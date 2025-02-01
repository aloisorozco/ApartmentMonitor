from flask import Flask, json, request, jsonify
import hashlib
import uuid
from flask_cors import CORS
import firebase_admin
import time
from firebase_admin import credentials, firestore
import os


class Server():

    # letting flask know that all stuff it needs is in this dir
    app = Flask(__name__)
    CORS(app)
    cred: credentials.Certificate = credentials.Certificate(
        os.path.expanduser("~/Downloads/cred.json"))
    firebase_admin.initialize_app(cred)
    db = firestore.client()

    def hash_data(data):
        return hashlib.sha256(data.encode()).hexdigest()

    # todo
    @app.route('/db_api/send_msgd', methods=['GET'])
    def send_msgd():
        response = jsonify({})
        response.status_code = 200
        return response

    # todo
    @app.route('/db_api/send_email', methods=['GET'])
    def send_email():
        response = jsonify({})
        response.status_code = 200
        return response

    # todo
    @app.route('/db_api/update_listing', methods=['GET'])
    def update_listing():
        response = jsonify({})
        response.status_code = 200
        return response

    @app.route('/db_api/fetch_watchlist', methods=['GET'])
    def fetch_watchlist():
        email = request.args.get('email')
        email_hash = Server.hash_data(email)
        wtachlist = Server.db.collection('users').document(
            email_hash).collection('watchlist')
        response = jsonify({})
        response.status_code = 200

        docs = wtachlist.stream()

        if any(docs):
            documents = [doc.to_dict() for doc in docs]
            response = json.dumps(documents, indent=2)

        return response

    @app.route('/db_api/save_listing', methods=['POST'])
    def save_listing():
        email = request.form.get('email')
        email_hash = Server.hash_data(email)

        price_curr = request.form.get('curr_price')
        pirce_target = request.form.get('target_price')
        desc = request.form.get('desc')

        listing_id = uuid.uuid4().__str__()

        watchlist_ref = Server.db.collection('users').document(
            email_hash).collection('watchlist')
        watchlist_ref.document(listing_id).set({
            "addedAt": time.time()
        })

        listing_docref = Server.db.collection(
            'apartments').document(listing_id)
        listing_docref.set({
            "price": price_curr,
            "price_target": pirce_target,
            "description": desc
        })

        response = jsonify({"ok": "listing saved"})
        response.status_code = 200
        return response

    @app.route('/db_api/remove_listing', methods=['DELETE'])
    def remove_listing():
        email = request.args.get('email')
        email_hash = Server.hash_data(email)
        listing_id = request.args.get('listing_id')

        watchlist_ref = Server.db.collection('users').document(
            email_hash).collection('watchlist')
        watchlist_ref.document(listing_id).delete()
        Server.db.collection('apartments').document(listing_id).delete()

        response = jsonify({"ok": "listing removed"})
        response.status_code = 200
        return response

    @app.route('/db_api/register_user', methods=['POST'])
    def register_user():
        form_data = request.get_json()
        print(form_data)
        fname = form_data.get('fname')
        lname = form_data.get('lname')
        pword = form_data.get('password')

        # use the email as unqiue ID
        email = form_data.get('email')
        email_encr = Server.hash_data(email)
        password_encr = Server.hash_data(pword)

        # make sure email is not in the firebase db based on hash here
        email_docref = Server.db.collection('users').document(email_encr)

        doc = email_docref.get()

        # Check here if email already exists
        if doc.exists:
            response = jsonify(
                {"error": "fuck you, your account already exists"})
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

        response = jsonify({"ok": "user registered"})
        response.status_code = 200
        return response

    @app.route('/db_api/auth_user', methods=['POST'])
    def auth_user():
        data = request.get_json()
        password = data.get('password')
        email = data.get('email')

        enterred_password = Server.hash_data(password)
        enterred_email = Server.hash_data(email)
        user_db = Server.db.collection("users").document(enterred_email).get()

        if not user_db.exists:
            response = jsonify(
                {"error": "User does not exist or provided information is wrong"})
            response.status_code = 400
            return response

        user_info = user_db.to_dict()
        password_db = user_info.get(password, "There was an error")
        if (enterred_password != password_db):
            response = jsonify({"error": "Password Invalid"})
            response.status_code = 400
            return response

        response = jsonify({"ok": "user authed"})
        response.status_code = 200
        return response
