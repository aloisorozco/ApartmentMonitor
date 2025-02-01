from flask import Flask, request

class Server():

    # letting flask know that all stuff it needs is in this dir
    app = Flask(__name__)

    def __init__(self) -> None:
        pass
       
    @app.route('/db_api/fetch_watchlist', methods=['GET'])
    def fetch_watchlist():
        pass

    @app.route('/db_api/save_listing', methods=['POST'])
    def save_listing():
        pass

    @app.route('/db_api/remove_listing', methods=['DELETE'])
    def remove_listing():
        pass

    @app.route('/db_api/register_user', methods=['GET'])
    def register_user():
        fname = request.form.get('param1')
        lname = request.form.get('param1')
        email = request.form.get('param1')

    @app.route('/db_api/auth_user', methods=['GET'])
    def auth_user():
        pass
