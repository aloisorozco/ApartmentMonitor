from flask import Flask, json, request, jsonify
import hashlib
import uuid
from flask_cors import CORS
import firebase_admin
import time
from firebase_admin import credentials, firestore
import os
from web_scraper import WebScraper
import smtplib
from email.mime.text import MIMEText
from email.mime.multipart import MIMEMultipart
import json



class Server():

    # letting flask know that all stuff it needs is in this dir
    app = Flask(__name__)
    CORS(app)
    cred: credentials.Certificate = credentials.Certificate(
        os.path.expanduser("~/Downloads/cred.json"))
    firebase_admin.initialize_app(cred)
    db = firestore.client()
    email_address = ""
    email_password = ""

    with open("email_cred.json") as f:
        data = json.load(f)
        email_address=data['email_address']
        email_password=data['email_password']
        

    
    ws = None

    def __init__(self) -> None:
        Server.ws = WebScraper()

    def hash_data(data):
        return hashlib.sha256(data.encode()).hexdigest()

    @app.route('/db_api/send_email', methods=['GET'])
    def send_email(receiver_email, subject, body):
        smtp_server = "smtp.gmail.com"
        smtp_port = 587  
        
        msg = MIMEMultipart()
        msg["From"] = Server.email_address
        msg["To"] = receiver_email
        msg["Subject"] = subject

        msg.attach(MIMEText(body, "plain"))


        mail_server = smtplib.SMTP(smtp_server, smtp_port)
        mail_server.starttls()  # Upgrade to a secure connection
        mail_server.login(Server.email_address, Server.email_password)  # Login
        mail_server.sendmail(Server.email_address, receiver_email, msg.as_string())  # Send email

        print("Sent mail to "+receiver_email)

        response = jsonify({})
        response.status_code = 200
        return response
    
    
    # todo TO TEST
    @app.route('/db_api/update_listing', methods=['GET'])
    def update_listing(listing_id, price):
            # Query all users
        users_ref = Server.db.collection('users')
        users_snap = users_ref.stream()
        apartment_ref = Server.db.collection("apartments").document(listing_id)
        apartment_snap = apartment_ref.get()
        if apartment_snap.exists:
            if price <= int(apartment_snap.get("price_target")):
                # print("Apartment "+str(listing_id)+" has dropped in price to "+str(price)+" which is below the target price of "+str(apartment_snap.get("price_target")))
                # Iterate over each user
                for user_doc in users_snap:
                    user_id = user_doc.id
                    # Query the user's watchlist to see if the apartment is there
                    watchlist_ref = Server.db.collection('users').document(user_id).collection('watchlist')
                    watchlist_snap = watchlist_ref.stream()

                    for watchlist_item in watchlist_snap:
                        apartment_id = watchlist_item.id
                        if apartment_id == listing_id:
                            print("Apartment "+str(listing_id)+" found in watchlist of user "+str(user_id))
                            subject = "Price Drop Alert!"
                            body = "The appartment: "+str(apartment_snap.get("description")+" has dropped below target price!")
                            Server.send_email(user_doc.get("email"), subject, body)

        apartment_ref.update({"price" : price})

        response = jsonify({"ok": "item updated"})
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

    # need to fix alois code to test this one TODO
    @app.route('/db_api/save_listing', methods=['POST'])
    def save_listing():
        email = request.form.get('email')
        email_hash = Server.hash_data(email)

        price_curr = request.form.get('curr_price')
        price_target = request.form.get('target_price')
        location = request.form.get('location')
        desc = request.form.get('desc')
        image_link = request.form.get('image_link')

        listing_id = str(uuid.uuid4())

        watchlist_ref = Server.db.collection('users').document(email_hash).collection('watchlist')
        user_listing = watchlist_ref.document(listing_id).get()
        if user_listing.exists:
            response = jsonify({"error":"what you tryna do, one time is more than enough, bitch trying to add same listing multiple times"})
        else:

            listing_docref = Server.db.collection('apartments').document(listing_id)
            listing_docref.set({
                "price": price_curr,
                "price_target": price_target,
                "location" : location,
                "description" : desc,
                "image_link" : image_link

            })

            watchlist_ref.document(listing_id).set({
                "addedAt": time.time()
            })

            listing_docref = Server.db.collection('apartments').document(listing_id)
            listing_docref.set({
                "price": price,
                "price_target": pirce_target,
                "description": desc,
                "listing_url": listing_url
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
            response = jsonify({"error": "Account already exists"})
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
        password_db = user_info.get("password_hashed", "There was an error")
        if (enterred_password != password_db):
            response = jsonify({"error": "Password Invalid"})
            response.status_code = 400
            return response

        response = jsonify({"ok": "user authed"})
        response.status_code = 200
        return response
    
    # TODO
    def refresh_listings():
        listings = Server.db.collection('apartments').stream()
        
        for listing in listings:
            listing_dict = listing.to_dict()



