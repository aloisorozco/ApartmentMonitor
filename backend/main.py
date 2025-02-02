from server import Server

if __name__ == "__main__":
    HOST = "0.0.0.0"
    PORT = "5500"

    server = Server()
    server.app.run(host=HOST, port=PORT)
    Server.update_listing("ebd239ff-88c8-489a-996d-47ef65396991", 0)