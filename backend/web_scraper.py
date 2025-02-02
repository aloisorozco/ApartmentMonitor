from bs4 import BeautifulSoup
import requests
import requests
import concurrent.futures

class WebScraper:
  
  TESTURL = "https://www.kijiji.ca/v-apartments-condos/winnipeg/2br-suite-in-character-building-in-the-heart-of-downtown/1700547336"

  def __init__(self, url):
    self.headers = {
        "User-Agent": "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:109.0) Gecko/20100101 Firefox/117.0"
    }

    # with open("proxies.txt", 'r') as f:
    #   proxies = f.read().split("\n")
    #   # we are using ThreadPoolExecutor because the bottleneck are the network call opps
    #   with concurrent.futures.ThreadPoolExecutor() as executor:
    #     executor.map(self.webscrapeKijijiPage, proxies)


  # def webscrapeKijijiPage_iprotation(self, proxy):
    
  #   print(f'Proxy used: {proxy}')
      
  #   try:  
  #     result = requests.get(WebScraper.TESTURL, headers=self.headers, proxies={"http": proxy, "https": proxy}, timeout=3)
  #     doc = BeautifulSoup(result.text, "html.parser")

  #     return {
  #       "url": WebScraper.TESTURL,
  #       "title": doc.find("h1", {"itemprop" : "name"}).string,
  #       "price": doc.find("span", {"itemprop" : "price"}).get("content")
  #     }
  #   except:
  #     print("Error occurred while scraping url")
  
  # print("No proxies worked - I am going to loose it")
        
  def webscrapeKijijiPage_iprotation(self, url):
      
    try:  
      result = requests.get(url, headers=self.headers, timeout=3)
      doc = BeautifulSoup(result.text, "html.parser")

      return {
        "url": WebScraper.TESTURL,
        "title": doc.find("h1", {"itemprop" : "name"}).string,
        "price": doc.find("span", {"itemprop" : "price"}).get("content")
      }
    except:
      print("Error occurred while scraping url")

# print(ws.webscrapeKijijiPage())