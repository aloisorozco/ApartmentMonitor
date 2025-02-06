from bs4 import BeautifulSoup
import requests
from proxy_scraper import ProxyScraper
import aiohttp
import asyncio
import ssl

class WebScraper:
  
  _TESTURL = "https://www.kijiji.ca/v-apartments-condos/winnipeg/2br-suite-in-character-building-in-the-heart-of-downtown/1700547336"
  _HEADERS = {"User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Safari/537.36"}
  _PROXY_SCRAPER = ProxyScraper()

  _SSL_CTX = ssl.create_default_context()
  _SSL_CTX.check_hostname = False
  _SSL_CTX.verify_mode = ssl.CERT_NONE

  def websrcape_url(self):
    
    proxies = WebScraper._PROXY_SCRAPER.proxy_scraping()
    try:  
      
      result = asyncio.run(WebScraper._fetch_listing_data(proxies, WebScraper._TESTURL))
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
  
  async def _fetch_listing_data(proxies, url):
    # disabling SSL for now
    async with aiohttp.ClientSession() as session:
      tasks = [asyncio.create_task(WebScraper._try_with_proxy(url, f'http://{proxy[0]}:{proxy[1]}', session)) for proxy in proxies]

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

  async def _try_with_proxy(url, proxy, session):
      print(f'Using Proxy {proxy}')
      async with session.get(url, proxy=proxy, allow_redirects=True, ssl=WebScraper._SSL_CTX, headers=WebScraper._HEADERS, timeout=aiohttp.ClientTimeout(total=20)) as response:
          if response.status != 200:
             raise Exception(f'failed to scrape with proxy {proxy}')
          elif response.status == 407:
            print(f'Proxy {proxy} requires authentication. Skipping it.')
            return None  # Skip this proxy
          else:
            return await response.text()


ws = WebScraper()
ws.websrcape_url()
      