from bs4 import BeautifulSoup
import requests
import requests
import queue
import concurrent.futures

class WebScraper:
  
  TESTURL = "https://ipinfo.io/json"


  def __init__(self, url):
    self.headers = {
        "User-Agent": "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:109.0) Gecko/20100101 Firefox/117.0"
    }
    self.url = url
    self.proxie_queue = queue.Queue()
  
    with open("proxies.txt", 'r') as f:
      proxies = f.read().split("\n")
      # we are using ThreadPoolExecutor because the bottleneck are the network call opps
      with concurrent.futures.ThreadPoolExecutor() as executor:
        executor.map(self.test_proxy, proxies)


  def test_proxy(self, proxy):
    try:
        response = requests.get(WebScraper.TESTURL, proxies={"http" : proxy, "https" : proxy}, timeout=1)
        if response.status_code == 200:
          print(f'Good proxy: {proxy}')
          self.proxie_queue.put(proxy)
        else:
          print(f'Proxy Faile: {proxy}')
    except:
        print(f'Proxy Failed: {proxy}')


  def webscrapeKijijiPage(self):
    try:
      proxy = self.proxie_queue.get()
      while(self.test_proxy(proxy) is False):
        if(self.proxie_queue.empty):
          print("Ran out of proxies")
          return
        
        proxy = self.proxie_queue.get()
      
      print(f'Proxy used: {proxy}')
      result = requests.get(url, headers=self.headers, proxies={"http": proxy, "https": proxy})
      self.proxie_queue.put(proxy)
      doc = BeautifulSoup(result.text, "html.parser")

      return {
        "url": url,
        "title": doc.find("h1", {"itemprop" : "name"}).string,
        "price": doc.find("span", {"itemprop" : "price"}).get("content")
      }
    except:
      print("Error occurred while scraping url")
      return {
        "err": True
      }


url = "https://www.kijiji.ca/v-apartments-condos/winnipeg/2br-suite-in-character-building-in-the-heart-of-downtown/1700547336"
ws = WebScraper(url)
print(ws.webscrapeKijijiPage())