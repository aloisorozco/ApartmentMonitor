from flask import Flask, request
import hashlib
from flask_cors import CORS
import firebase_admin
from firebase_admin import credentials, firestore

class Server():

    # letting flask know that all stuff it needs is in this dir
    app = Flask(__name__)
    CORS(app)

    def __init__(self, cred) -> None:
        self.cred: credentials.Certificate = cred
        firebase_admin.initialize_app(cred)
        self.db = firestore.client()
       
    @app.route('/db_api/fetch_watchlist', methods=['GET'])
    def fetch_watchlist(self):
        pass

    @app.route('/db_api/save_listing', methods=['POST'])
    def save_listing(self):
        pass

    @app.route('/db_api/remove_listing', methods=['DELETE'])
    def remove_listing(self):
        pass

    @app.route('/db_api/register_user', methods=['POST'])
    def register_user(self):
        fname = request.form.get('fname')
        lname = request.form.get('lname')
        pword = request.form.get('password')

        # use the email as unqiue ID
        email = request.form.get('email')
        email_encr = hash_data(email) 
        password_encr = hash_data(pword)

        # make sure email is not in the firebase db based on hash here
        email_ref = self.db.collection('users')


        # Check here if email already exists
        pass



    @app.route('/db_api/auth_user', methods=['GET'])
    def auth_user(self):
        pass
