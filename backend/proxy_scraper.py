from bs4 import BeautifulSoup
import requests

class ProxyScraper:
  
  _PROXIES_URL = "https://free-proxy-list.net/"

  def __init__(self):
    self.headers = {
        "User-Agent": "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:109.0) Gecko/20100101 Firefox/117.0"
    }

  def proxy_scraping(self):
      
    try:  
      result = requests.get(ProxyScraper._PROXIES_URL, headers=self.headers, timeout=3)
      doc = BeautifulSoup(result.text, "html.parser")

      table = doc.find("table", {"class" : "table table-striped table-bordered"}).find_all("tr")[1:] # firs thing is a header we dont need
      proxies = []

      for row in table:
        ip_info = row.find_all("td")
        ip = ip_info[0].string
        port = int(ip_info[1].string)
        proxies.append((ip, port))

        # we can extract other things like location and stuff - for now we just keep this

      return proxies
    except Exception as e:
      print(f'Error occurred while scraping url: {e}')
      return []