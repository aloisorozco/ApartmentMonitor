from flask import Flask, request, jsonify, current_app
import hashlib
import uuid
from flask_cors import CORS
import firebase_admin
import time
from firebase_admin import credentials, firestore
from web_scraper import WebScraper
import smtplib
from email.mime.text import MIMEText
from email.mime.multipart import MIMEMultipart
import json
from apscheduler.schedulers.background import BackgroundScheduler
from apscheduler.triggers.interval import IntervalTrigger

# collection = db entry that contains nothing more than documents
# document = "end point" that contains properties and other documents/collections
class Server():

    cred: credentials.Certificate = credentials.Certificate("cred.json")
    firebase_admin.initialize_app(cred)
    db = firestore.client()
    email_address = ""
    email_password = ""

    with open("email_cred.json") as f:
        data = json.load(f)
        email_address = data['email_address']
        email_password = data['email_password']

    ws = None
    scheduler = BackgroundScheduler()
    scheduler.start()

    def __init__(self, app) -> None:
        self.app = app
        CORS(app)
        Server.ws = WebScraper()
        self.register_routes()

    # registering all the API routes here
    def register_routes(self):
        self.app.add_url_rule("/db_api/send_email", view_func=self.send_email, methods=['GET']) # TODO: test send email with arguments
        self.app.add_url_rule("/db_api/update_listing", view_func=self.update_listing, methods=['GET'])
        self.app.add_url_rule("/db_api/fetch_watchlist", view_func=self.fetch_watchlist, methods=['GET'])
        self.app.add_url_rule("/db_api/save_listing", view_func=self.save_listing, methods=['POST'])
        self.app.add_url_rule("/db_api/remove_listing", view_func=self.remove_listing, methods=['DELETE'])
        self.app.add_url_rule("/db_api/register_user", view_func=self.register_user, methods=['POST'])
        self.app.add_url_rule("/db_api/auth_user", view_func=self.auth_user, methods=['POST'])

    @scheduler.scheduled_job(IntervalTrigger(minutes=1000000))
    def scheduled_scraping():
        with Server.app.app_context():


            users_ref = Server.db.collection('users')
            users_snap = users_ref.stream()

            for user_snap in users_snap:
                # print("User: "+user_snap.get("email"))

                watchlist_ref = Server.db.collection('users').document(user_snap.id).collection('watchlist')
                watchlist_snap = watchlist_ref.stream()

                if watchlist_snap:
                    for watchlist_item in watchlist_snap:
                        apartment_id = watchlist_item.id

                        apartment_ref = Server.db.collection("apartments").document(apartment_id)
                        apartment_snap = apartment_ref.get()
                        if apartment_snap.exists:
                            # print("\tHas apartment: " 
                            #         + apartment_snap.get("description") + " in their watchlist")
                            url = apartment_snap.get("url")
                            if url:
                                listing_data = Server.ws.webscrape_url_premium_proxies(url)
                                price_curr = float(listing_data.get('price'))
                                if price_curr != float(apartment_snap.get("price")):
                                    Server.update_listing(apartment_id, price_curr)
                                    # print("Updated Listing for "+apartment_id)
                        
                


    def hash_data(data):
        return hashlib.sha256(data.encode()).hexdigest()

    def send_email(self, receiver_email, subject, body):
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
        mail_server.sendmail(Server.email_address,
                             receiver_email, msg.as_string())  # Send email

        response = jsonify({})
        response.status_code = 200 
        return response
    # TODO: TO TEST

    def update_listing(self):

        listing_id = ""
        price = 0.0

        try:
            listing_id = request.args.get('listing_id')
            price = float(request.args.get('price'))

            if len(listing_id) == 0:
                raise Exception("listing ID is empty")
        except ValueError as e:
            print(f'[ERROR] error when updating listing: {e}')
            response = jsonify({})
            response.status_code = 500
            return response
        
        # this is caused when user input is invalid
        except Exception as e:
            print(f'[ERROR] error when updating listing: {e}')
            response = jsonify({})
            response.status_code = 400
            return response


        print(listing_id)
        with current_app.app_context():
            # Query all users
            users_ref = Server.db.collection('users')
            users_snap = users_ref.stream()
            apartment_ref = Server.db.collection("apartments").document(listing_id)
            apartment_snap = apartment_ref.get()
            if apartment_snap.exists:
                if price <= float(apartment_snap.get("price_target")):
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
                                body = "The apartment: "+str(apartment_snap.get("description")+" has dropped below target price!")
                                Server.send_email(user_doc.get("email"), subject, body)

            # updating the target price - not the real price of the listing
            apartment_ref.update({"price_target" : price})
            response = jsonify({"ok": "item updated"})
            response.status_code = 200
            return response
        
    def fetch_watchlist(self):
        email =  request.args.get('email')
        email_hash = Server.hash_data(email)
        watchlist = Server.db.collection('users').document(email_hash).collection('watchlist').get()
        response = jsonify({})
        response.status_code = 200

        if len(watchlist) > 0:
            listings = []
            # querry each listing info - each listing is a DocumentSnapshot object from user - we need to requery each one
            # TODO: see if we did not mess up in our db design here, seems wierd we need to requerry so much
            for listing in watchlist:
                listing_id = listing.id 
                # .document(listing_id) will get OR create document with listing_id -> returns a document referance
                # doing .get() on the document referance -> gets the document snapshot itself; need to check if object itself exists in db or not
                listing_doc = Server.db.collection('apartments').document(listing_id).get()
                if listing_doc.exists:
                    listings.append(listing_doc.to_dict())

                
            response = jsonify({"listings" : listings})

        return response


    def save_listing(self):
        data = request.get_json()
        email = data.get('email')
        email_hash = Server.hash_data(email)

        price_target = float(data.get('target_price'))
        url = data.get('url')

        try:
            # Web scrape
            listing_data = Server.ws.webscrape_url_premium_proxies(url)
            price_curr = float(listing_data.get('price'))
            desc = listing_data.get('title')
            location = listing_data.get('location')
            image_link = listing_data.get('images')

        except Exception as e:
            print(f'[ERROR] error when parsing: {e}')
            response = jsonify({'error': 'parsing issue'})
            response.status_code = 500
            return response


        listing_id = str(uuid.uuid4())

        listing_data_processed = {
            "price": price_curr,
            "price_target": price_target,
            "location": location,
            "description": desc,
            "image_link": image_link,
            "url": url,
            "listing_id" : listing_id
        }

        watchlist_ref = Server.db.collection('users').document(
            email_hash).collection('watchlist')
        watchlist_ref.document(listing_id).set({
            "addedAt": time.time()
        })

        listing_docref = Server.db.collection(
            'apartments').document(listing_id)
        listing_docref.set(listing_data_processed)

        response = jsonify(listing_data_processed)
        response.status_code = 200
        return response

    
    def remove_listing(self):
        email = request.args.get('email')
        listing_id = request.args.get('listing_id')
        email_hash = Server.hash_data(email)

        watchlist_ref = Server.db.collection('users').document(email_hash).collection('watchlist')

        # no need to check if listing exists - delete() does nothing if the listing does not exist.
        watchlist_ref.document(listing_id).delete()
        Server.db.collection('apartments').document(listing_id).delete()

        response = jsonify({"ok": "listing removed"})
        response.status_code = 200
        return response

    def register_user(self):
        data = request.get_json()
        fname = data.get('fname')
        lname = data.get('lname')
        pword = data.get('password')

        # use the email as unqiue ID
        email = data.get('email')
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

    def auth_user(self):
        password = request.json.get('password')
        email = request.json.get('email')

        enterred_password = Server.hash_data(password)
        enterred_email = Server.hash_data(email)
        user_db = Server.db.collection("users").document(enterred_email).get()

        if not user_db.exists:
            response = jsonify(
                {"error": "User does not exist or provided information is wrong"})
            response.status_code = 400
            return response

        user_info = user_db.to_dict()
        password_db = user_info.get('password_hashed', "There was an error")
        if (enterred_password != password_db):
            response = jsonify({"error": "Password Invalid"})
            response.status_code = 400
            return response

        response = jsonify({"ok": "user authed"})
        response.status_code = 200
        return response


# letting flask know that all stuff it needs is in this dir
app = Flask(__name__)
server = Server(app)