import os
import zipfile

class Proxy:
    # Defines the extension, permissions, and background script - the manifest tells chrome what permissions the extension we are injecting has.
    # we specify which script to use in "background" background.js
    _MANIFEST_JSON = """
        {
            "version": "1.0.0",
            "manifest_version": 2,
            "name": "Proxy",
            "permissions": [
                "proxy",
                "tabs",
                "unlimitedStorage",
                "storage",
                "<all_urls>",
                "webRequest",
                "webRequestBlocking"
            ],
            "background": {
                "scripts": ["background.js"]
            },
            "minimum_chrome_version":"22"
        }"""

    def __init__(self, id, usrname, password, ip, port):
        self.id = id
        self.usrname = usrname
        self.password = password
        self.ip = ip
        self.port = port

    def proxy_formatted(self):
        proxies = {
            'http': f'http://{self.usrname}:{self.password}@{self.ip}:{self.port}',
            'https': f'https://{self.usrname}:{self.password}@{self.ip}:{self.port}',
        }
        return proxies
    

    def __str__(self):
        return f'{self.ip}:{self.port}'
    
    def seleinium_proxy(self):

        # This is the injected extension's script, it effectively tells chrome to intersept any authentication
        # request it receives and use the proxys credentials.

        # config defines the config of the proxy - when to use proxy and for what resources to bypass (localhost in our case)
        # callbackFn returns a JSON with the username and password when chrome is required to authenticate the proxy
        # so when chrome gets the HTTP err 407 (because it initily tries the proxy without auth) it knows to supply authentication on the re-try
        # and it blocks selinium requests until auth is finished
        background_js = f"""
        var config = {{
                mode: "fixed_servers",
                rules: {{
                singleProxy: {{
                    scheme: "http",
                    host: "{self.ip}",
                    port: parseInt({self.por})
                }},
                bypassList: ["localhost"]
                }}
            }};

        chrome.proxy.settings.set({{value: config, scope: "regular"}}, function() {{}});

        function callbackFn(details) {{
            return {{
                authCredentials: {{
                    username: "{self.usrname}",
                    password: "{self.password}"
                }}
            }};
        }}

        chrome.webRequest.onAuthRequired.addListener(
            callbackFn,
            {{urls: ["<all_urls>"]}},
            ["blocking"]
        );"""

        # we need the extension to be passed to the chrome manager as a zip file with the manifest and js code
        # we write the zip to memory buffer and pass to chrome

        with zipfile.ZipFile("proxy_extension.zip", "w") as zip:
            zip.write("manifest.json", Proxy._MANIFEST_JSON)
            zip.write("background.js", background_js)
        
        return os.path.join(os.getcwd(), "proxy_extension.zip")