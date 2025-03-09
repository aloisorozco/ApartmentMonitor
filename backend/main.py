from server import Server

if __name__ == "__main__":
    HOST = "0.0.0.0"
    PORT = "5500"

    server = Server()
    server.app.run(host=HOST, port=PORT, threaded=True)