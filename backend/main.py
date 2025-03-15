from server import app

if __name__ == "__main__":
    HOST = "0.0.0.0"
    PORT = "5500"
    app.run(host=HOST, port=PORT, threaded=True)