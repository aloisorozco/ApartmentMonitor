import requests

TOKEN = "hell na"
 
response = requests.get(
    "https://proxy.webshare.io/api/v2/proxy/list/?mode=direct&page=1&page_size=25",
    headers={"Authorization": f'Token {TOKEN}'}
)
 
response = response.json()
