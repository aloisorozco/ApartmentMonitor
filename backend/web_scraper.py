from bs4 import BeautifulSoup
import requests

headers = {
    "User-Agent": "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:109.0) Gecko/20100101 Firefox/117.0"
}

#test:
url = "https://www.kijiji.ca/v-apartments-condos/winnipeg/2br-suite-in-character-building-in-the-heart-of-downtown/1700547336"

def webscrapeKijijiPage(url):
  try:
    result = requests.get(url, headers=headers)
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

webscrapeKijijiPage(url)