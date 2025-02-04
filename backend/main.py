from server import Server

if __name__ == "__main__":
    HOST = "0.0.0.0"
    PORT = "5500"

    server = Server()
    server.app.run(host=HOST, port=PORT)
    Server.update_listing("c9b6a4fc-0967-43a6-8bc9-0510c3482b5e", 0)