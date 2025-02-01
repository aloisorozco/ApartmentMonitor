from flask import Flask, request, jsonify
import hashlib
import uuid
from flask_cors import CORS
import firebase_admin
import time
from firebase_admin import credentials, firestore

class Server():
    
    # letting flask know that all stuff it needs is in this dir
    app = Flask(__name__)
    CORS(app)

    def __init__(self, cred) -> None:
        self.cred: credentials.Certificate = cred
        firebase_admin.initialize_app(cred)
        self.db = firestore.client()
    
    def hash_data(data):
        return hashlib.sha256(data.encode()).hexdigest()

    # todo
    @app.route('/db_api/send_msgd', methods=['GET'])
    def send_msgd(self):
        pass

    # todo
    @app.route('/db_api/send_email', methods=['GET'])
    def send_email(self):
        pass

    # todo
    @app.route('/db_api/update_listing', methods=['GET'])
    def update_listing(self):
        pass
        
    @app.route('/db_api/fetch_watchlist', methods=['GET'])
    def fetch_watchlist(self):
        email = request.form.get('email')
        email_hash = self.hash_data(email)
        wtachlist = self.db.collection('users').document(email_hash).collection('watchlist').get()

        if wtachlist.exists:
            return jsonify(wtachlist.to_dict())
        else:
            return jsonify({})
        

    @app.route('/db_api/save_listing', methods=['POST'])
    def save_listing(self):
        email = request.form.get('fname')
        email_hash = self.hash_data(email)

        price_curr = request.form.get('curr_price')
        pirce_target = request.form.get('target_price')
        desc = request.form.get('desc')

        listing_id = uuid.uuid4()

        watchlist_ref = self.db.collection('users').document(email_hash).collection('watchlist')
        watchlist_ref.document(listing_id).set({
            "addedAt": time.time()
        })

        listing_docref = self.db.collection('apartments').document(listing_id)
        listing_docref.set({
            "price": price_curr,
            "price_target": pirce_target,
            "description": desc
        })

    @app.route('/db_api/remove_listing', methods=['DELETE'])
    def remove_listing(self):
        email = request.form.get('fname')
        email_hash = self.hash_data(email)
        listing_id = request.form.get('listing_id')

        watchlist_ref = self.db.collection('users').document(email_hash).collection('watchlist')
        watchlist_ref.document(listing_id).delete()
        self.db.collection('apartments').document(listing_id).delete()

    @app.route('/db_api/register_user', methods=['POST'])
    def register_user(self):
        fname = request.form.get('fname')
        lname = request.form.get('lname')
        pword = request.form.get('password')

        # use the email as unqiue ID
        email = request.form.get('email')
        email_encr = self.hash_data(email) 
        password_encr = self.hash_data(pword)

        # make sure email is not in the firebase db based on hash here
        email_docref = self.db.collection('users').document(email_encr)

        doc = email_docref.get()

        # Check here if email already exists
        if doc.exists:
            response = jsonify({"error": "fuck you, your account already exists"})
            response.status_code = 400
            return response
        
        print("saving user")
        email_docref.set({
            "fname": fname,
            "lname": lname,
            "email": email,
            "password_hashed": password_encr,
            "createdAt": time.time()
        })


    @app.route('/db_api/auth_user', methods=['GET'])
    def auth_user(self):
        password = request.form.get('password')
        email = request.form.get('email')

        enterred_password = self.hash_data(password)
        enterred_email = self.hash_data(email)
        user_db = self.db.collection("users").document(enterred_email).get()

        if not user_db.exists:
            response = jsonify({"error": "User does not exist or provided information is wrong"})
            response.status_code = 400
            return response
        
        user_info = user_db.to_dict()
        password_db = user_info.get(password, "There was an error")
        if(enterred_password != password_db):
            response = jsonify({"error": "Password Invalid"})
            response.status_code = 400
            return response

