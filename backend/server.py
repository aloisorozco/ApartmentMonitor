from flask import Flask, request, jsonify
import hashlib
import uuid
from flask_cors import CORS
import firebase_admin
import time
from firebase_admin import credentials, firestore
import smtplib
from email.mime.text import MIMEText
from email.mime.multipart import MIMEMultipart
import json



class Server():
    
    # letting flask know that all stuff it needs is in this dir
    app = Flask(__name__)
    CORS(app)
    cred: credentials.Certificate = credentials.Certificate("cred.json")
    firebase_admin.initialize_app(cred)
    db = firestore.client()
    email_address = ""
    email_password = ""

    with open("C:\\Users\\cszty\\Downloads\\email_cred.json") as f:
        data = json.load(f)
        email_address=data[email_address]
        email_password=data[email_password]
    

    
    def hash_data(data):
        return hashlib.sha256(data.encode()).hexdigest()

    # todo TO TEST
    @app.route('/db_api/send_email', methods=['GET'])
    def send_email(receiver_email):
        smtp_server = "smtp.gmail.com"
        smtp_port = 587  
        
        msg = MIMEMultipart()
        msg["From"] = Server.email_address
        msg["To"] = receiver_email
        msg["Subject"] = "Test Email"

        body = "Hello, this is a test email sent using Python."
        msg.attach(MIMEText(body, "plain"))


        mail_server = smtplib.SMTP(smtp_server, smtp_port)
        mail_server.starttls()  # Upgrade to a secure connection
        mail_server.login(Server.email_address, Server.email_password)  # Login
        mail_server.sendmail(Server.email_address, receiver_email, msg.as_string())  # Send email

        response = jsonify({})
        response.status_code = 200 
        return response

    # todo TO TEST
    @app.route('/db_api/update_listing', methods=['GET'])
    def update_listing(listing_id, price):
        apartment_ref = Server.db.collection("apartments").document(listing_id)
        apartment_ref.update({"price" : price})
        response = jsonify({})
        response.status_code = 200 
        return response
        
    @app.route('/db_api/fetch_watchlist', methods=['GET'])
    def fetch_watchlist():
        email = request.form.get('email')
        email_hash = Server.hash_data(email)
        watchlist = Server.db.collection('users').document(email_hash).collection('watchlist').get()
        response = jsonify({})
        response.status_code = 200

        if watchlist.exists:
            response =  jsonify(watchlist.to_dict())
        
        return response

    @app.route('/db_api/save_listing', methods=['POST'])
    def save_listing():
        email = request.form.get('fname')
        email_hash = Server.hash_data(email)

        price_curr = request.form.get('curr_price')
        pirce_target = request.form.get('target_price')
        desc = request.form.get('desc')

        listing_id = uuid.uuid4()

        watchlist_ref = Server.db.collection('users').document(email_hash).collection('watchlist')
        watchlist_ref.document(listing_id).set({
            "addedAt": time.time()
        })

        listing_docref = Server.db.collection('apartments').document(listing_id)
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
        email = request.form.get('fname')
        email_hash = Server.hash_data(email)
        listing_id = request.form.get('listing_id')

        watchlist_ref = Server.db.collection('users').document(email_hash).collection('watchlist')
        watchlist_ref.document(listing_id).delete()
        Server.db.collection('apartments').document(listing_id).delete()

        response = jsonify({"ok": "listing removed"})
        response.status_code = 200
        return response

    @app.route('/db_api/register_user', methods=['POST'])
    def register_user():
        fname = request.form.get('fname')
        lname = request.form.get('lname')
        pword = request.form.get('password')

        # use the email as unqiue ID
        email = request.form.get('email')
        email_encr = Server.hash_data(email) 
        password_encr = Server.hash_data(pword)

        # make sure email is not in the firebase db based on hash here
        email_docref = Server.db.collection('users').document(email_encr)

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

        response = jsonify({"ok": "user registered"})
        response.status_code = 200
        return response


    @app.route('/db_api/auth_user', methods=['GET'])
    def auth_user():
        password = request.form.get('password')
        email = request.form.get('email')

        enterred_password = Server.hash_data(password)
        enterred_email = Server.hash_data(email)
        user_db = Server.db.collection("users").document(enterred_email).get()

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

        response = jsonify({"ok": "user authed"})
        response.status_code = 200
        return response
