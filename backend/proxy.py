class Proxy:

    def __init__(self, id, usrname, password, ip, port):
        self.id = id
        self.usrname = usrname
        self.password = password
        self.ip = ip
        self.port = port
    
    def proxy_formatted(self):
        proxies = {
            'http': f'http://{self.usrname}:{self.password}@{self.ip}:{self.port}',
        }
        return proxies
    
    def __str__(self):
        return f'{self.ip}:{self.port}'