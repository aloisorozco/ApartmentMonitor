from urllib.request import HTTPBasicAuthHandler
from bs4 import BeautifulSoup
import requests
from proxy_scraper import ProxyScraper
import aiohttp
import asyncio
import ssl
import queue
from proxy import Proxy

# TODO: make thread safe in the future - should be easy since we always juggle only one instance - in the futre we may need many insance for many users
# interacting with a share proxy list

# TODO: make dunder new and dunder init thread safe (use mutex)
class WebScraper:
  
  # TODO: make singleton
  _TESTURL = "https://www.kijiji.ca/v-apartments-condos/winnipeg/2br-suite-in-character-building-in-the-heart-of-downtown/1700547336"
  _HEADERS = {"User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Safari/537.36"}

  _sngleton_instance = None

  # dunder new is called at object creation (before dunder init) - here we are using dunder new to make a singleton instance
  # cls = class type where __new__ is FIRST called
  def __new__(cls):
    if cls._sngleton_instance is None:
      cls._sngleton_instance = super().__new__(cls) 
    
    return cls._sngleton_instance # we MUST return an instance - otherwise init wont be called
  
  def __init__(self):
    if hasattr(self, "_has_init") and self._has_init:
      return
    
    self._has_init = True
    self._proxy_list = queue.Queue()

    self._proxy_scraper = ProxyScraper()

    self._ssl_ctx = ssl.create_default_context()
    self._ssl_ctx.check_hostname = False
    self._ssl_ctx.verify_mode = ssl.CERT_NONE
    self._token = "I want an job offer (200k + comp) pls"
    self._get_premium_proxies() # populate premium proxies list

  # need to find a way to auto rotate the proxies once in a while - maybe a counter since its a singleton?
  def _get_premium_proxies(self):
    response = requests.get(
      "https://proxy.webshare.io/api/v2/proxy/list/?mode=direct&page=1&page_size=25",
      headers={"Authorization": f'Token {self._token}'})

    for proxy in response.json()["results"]:
      self._proxy_list.put(Proxy(proxy['id'], proxy['username'], proxy['password'],proxy['proxy_address'], proxy['port']))

  # scraping with premium proxies - one by one, TODO: for not its ok, but in the future we need to find a way to manage concurency
  def websrcape_url_premium_proxies(self):

    # Iterate over all proxies - return on first working proxy result
    for _ in range(self._proxy_list.qsize()):
      
      proxy = self._proxy_list.get()
      print(f'using proxie: {proxy}')

      try:
        result = requests.get(url=WebScraper._TESTURL, proxies=proxy.proxy_formatted())      
        doc = BeautifulSoup(result.text, "html.parser")

        # Put back the proxy at the end of queue - rotating
        self._proxy_list.put(proxy)

        return {
          "url": WebScraper._TESTURL,
          "title": doc.find("h1", {"itemprop" : "name"}).string,
          "price": doc.find("span", {"itemprop" : "price"}).get("content")
        }
      except Exception as e:
        print(f'Error occurred while scraping url: {e}')
        # Put back the proxy at the end of queue even if it failed - rotating
        self._proxy_list.put(proxy)

  # bad proxies - keep it in cases we need to rotate/ test bad proxies
  def websrcape_url_scrape_proxies(self):
    
    proxies = self._proxy_scraper.proxy_scraping()
    try:  
      
      result = asyncio.run(self._fetch_listing_data(proxies, WebScraper._TESTURL))
      if result == None:
         print(f'Proxies failed - could not get any result')
         return
      
      doc = BeautifulSoup(result.text, "html.parser")

      return {
        "url": WebScraper._TESTURL,
        "title": doc.find("h1", {"itemprop" : "name"}).string,
        "price": doc.find("span", {"itemprop" : "price"}).get("content")
      }
    except Exception as e:
      print(f'Error occurred while scraping url: {e}')
  

  async def _fetch_listing_data(self, proxies, url):
    # disabling SSL for now
    async with aiohttp.ClientSession() as session:
      tasks = [asyncio.create_task(self._try_with_proxy(url, f'http://{proxy[0]}:{proxy[1]}', session)) for proxy in proxies]

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
# ws = WebScraper()
# w2 = WebScraper()

# print(ws is w2)

# print(ws.websrcape_url_premium_proxies())
      