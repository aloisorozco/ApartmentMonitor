import json
from urllib.request import HTTPBasicAuthHandler
from bs4 import BeautifulSoup
import requests
from proxy_scraper import ProxyScraper
import aiohttp
import asyncio
import ssl
import queue
from proxy import Proxy
import re
from urllib.parse import urlparse
from urllib.parse import urljoin
from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.chrome.service import Service
from webdriver_manager.chrome import ChromeDriverManager
from selenium.webdriver.chrome.options import Options
import os

# TODO: make thread safe in the future - should be easy since we always juggle only one instance - in the futre we may need many insance for many users
# interacting with a share proxy list

# TODO: make dunder new and dunder init thread safe (use mutex)


class WebScraper:

    _HEADERS = {
        "User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Safari/537.36"}

    _sngleton_instance = None

    # dunder new is called at object creation (before dunder init) - here we are using dunder new to make a singleton instance
    # cls = class type where __new__ is FIRST called
    def __new__(cls):
        if cls._sngleton_instance is None:
            cls._sngleton_instance = super().__new__(cls)

        # we MUST return an instance - otherwise init wont be called
        return cls._sngleton_instance

    def __init__(self):
        if hasattr(self, "_has_init") and self._has_init:
            return

        self._has_init = True
        self._proxy_list = queue.Queue()

        self._proxy_scraper = ProxyScraper()

        self._ssl_ctx = ssl.create_default_context()
        self._ssl_ctx.check_hostname = False
        self._ssl_ctx.verify_mode = ssl.CERT_NONE

        with open("proxy_cred.json") as f:
            data = json.load(f)
            self._token = data["token"]

        self._get_premium_proxies()  # populate premium proxies list

    # need to find a way to auto rotate the proxies once in a while - maybe a counter since its a singleton?
    def _get_premium_proxies(self):
        response = requests.get(
            "https://proxy.webshare.io/api/v2/proxy/list/?mode=direct&page=1&page_size=25",
            headers={"Authorization": f'Token {self._token}'})

        for proxy in response.json()["results"]:
            self._proxy_list.put(Proxy(
                proxy['id'], proxy['username'], proxy['password'], proxy['proxy_address'], proxy['port']))

    # scraping with premium proxies - one by one, TODO: for not its ok, but in the future we need to find a way to manage concurency
    def webscrape_url_premium_proxies(self, target_url):

        # Iterate over all proxies - return on first working proxy result
        for _ in range(self._proxy_list.qsize()):

            proxy = self._proxy_list.get()
            print(f'using proxie: {proxy}')

            try:
                url_domain_name = urlparse(target_url).netloc.split('.')[-2]
                if url_domain_name == 'kijiji':
                    return self.webscrape_kijiji_page(target_url, proxy)
                elif url_domain_name == 'kamernet':
                    return self.webscrape_kamernet_page(target_url, proxy)
                raise Exception('Website not supported')

            except Exception as e:
                print(f'Error occurred while scraping url: {e}')
                # Put back the proxy at the end of queue even if it failed - rotating
                self._proxy_list.put(proxy)
                raise e

        raise Exception(
            "Ran out of proxies when parsing (time to ball out and buy our own proxies)")
    
    def webscrape_kamernet_page(self, target_url, proxy):
        result = requests.get(
                    url=target_url, proxies=proxy.proxy_formatted(), headers=WebScraper._HEADERS)
        doc = BeautifulSoup(result.text, "html.parser")

        # Put back the proxy at the end of queue - rotating
        self._proxy_list.put(proxy)

        # kamernet does something similar to marketplace to prevent webscraping so if this looks a little weird its because I couldnt directly find an element
        return {
            "url": target_url,
            "title": doc.find('title').string.split('|')[0],
            "price": doc.select('div[class*="PropertyDetails_price"]')[0].find("h6").string[2:].replace('.', ''),
            "location": doc.select('div[class*="Header_details"]')[0].find("a").string,
            "images": self.webscrape_kamernet_images(target_url, proxy)
        }
    
    def webscrape_kamernet_images(self, target_url, proxy: Proxy):

        proxie_extension_path = proxy.seleinium_proxy()
        options = Options()
        options.add_extension(proxie_extension_path) 

        # neede for headless mode (no GUI)
        options.add_argument("--headless")
        driver = webdriver.Chrome(service=Service(ChromeDriverManager().install()), options=options)
        driver.get(target_url)

        driver.find_element(By.XPATH, '//button[contains(@class,"Gallery_button")]').click()
        doc = BeautifulSoup(driver.page_source)

        images = []
        for img_container in doc.select('div[class*="Lightbox_imageWrapper"]'):
            images.append(urljoin(target_url, img_container.find("img").get("src")))

        driver.quit()
        os.remove(proxie_extension_path)
        return images
    
    def webscrape_kijiji_page(self, target_url, proxy):
        result = requests.get(
                    url=target_url, proxies=proxy.proxy_formatted(), headers=WebScraper._HEADERS)
        doc = BeautifulSoup(result.text, "html.parser")

        self._proxy_list.put(proxy)

        return {
            "url": target_url,
            "title": doc.find("h1", {"itemprop": "name"}).string,
            "price": doc.find("span", {"itemprop": "price"}).get("content"),
            "location": doc.find("span", {"itemprop": "address"}).get_text(),
            "images": doc.find("div", {"class": "mainImage"}).find("img", {"itemprop": "image"}).get("src") # TODO: replace what we have with the following: self.webscrape_kijiji_images(target_url, proxy)
        }
    
    def webscrape_kijiji_images(self, target_url, proxy):
        # TODO: integrate proxy rotation
        driver = webdriver.Firefox()
        driver.get(target_url)

        driver.find_element(By.XPATH, '//div[contains(@class,"generalOverlay")]').click()
        doc = BeautifulSoup(driver.page_source)

        images = []
        images_container = doc.select('ul[class*="slideList"]')[0]
        for img in images_container.find_all('img'):
            images.append(img.get("src"))

        driver.quit()
        return images

    # bad proxies - keep it in cases we need to rotate/ test bad proxies
    def webscrape_url_scrape_proxies(self, target_url):

        proxies = self._proxy_scraper.proxy_scraping()
        try:

            result = asyncio.run(self._fetch_listing_data(proxies, target_url))
            if result == None:
                print(f'Proxies failed - could not get any result')
                return

            doc = BeautifulSoup(result.text, "html.parser")

            return {
                "url": target_url,
                "title": doc.find("h1", {"itemprop": "name"}).string,
                "price": doc.find("span", {"itemprop": "price"}).get("content")
            }
        except Exception as e:
            print(f'Error occurred while scraping url: {e}')

    async def _fetch_listing_data(self, proxies, url):
        # disabling SSL for now
        async with aiohttp.ClientSession() as session:
            tasks = [asyncio.create_task(self._try_with_proxy(
                url, f'http://{proxy[0]}:{proxy[1]}', session)) for proxy in proxies]

            while tasks:
                done, pending = await asyncio.wait(tasks, return_when=asyncio.FIRST_COMPLETED)

                res = None
                # getting the first proxy that resolved sucesfully
                for task in done:
                    try:
                        # break if we have a proxy hit
                        res = task.result()
                        break
                    except Exception as e:
                        print(f"Task failed: {e}")

                # go over this again, and wait for whatever was already pending
                # asyncio.wait does not rescehule pending task on a subsequent .wait() call if we pass it the pending tasks
                tasks = list(pending)

            # Cancel all tasks if we have one that is successfull
            for task in pending:
                task.cancel()

            await asyncio.gather(*pending, return_exceptions=True)
            return res

    async def _try_with_proxy(self, url, proxy, session):
        print(f'Using Proxy {proxy}')
        async with session.get(url, proxy=proxy, allow_redirects=True, ssl=self._ssl_ctx, headers=WebScraper._HEADERS, timeout=aiohttp.ClientTimeout(total=20)) as response:
            if response.status != 200:
                raise Exception(f'failed to scrape with proxy {proxy}')
            elif response.status == 407:
                print(f'Proxy {proxy} requires authentication. Skipping it.')
                return None  # Skip this proxy
            else:
                return await response.text()


# Testing - singleton works
ws = WebScraper()
# w2 = WebScraper()

# print(ws is w2)
# test_url = "https://www.kijiji.ca/v-apartments-condos/winnipeg/2br-suite-in-character-building-in-the-heart-of-downtown/1700547336"
test_url_2 = "https://kamernet.nl/huren/kamer-amsterdam/pasubio/kamer-2285880"
# print(ws.webscrape_url_premium_proxies(test_url))
print(ws.webscrape_url_premium_proxies(test_url_2))
