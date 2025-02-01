import firebase_admin
from firebase_admin import credentials
from firebase_admin import firestore

# Use a service account
cred = credentials.Certificate('C:\\Users\\Alessio\\Downloads\\cred.json')

# Initialize DB
app = firebase_admin.initialize_app(cred)

# Connect DB
db = firestore.client()

# Setup tables
user_table = db.collection('users').document('userID')
apartment_table = db.collection('apartments').document('listingID')

# Set user data
user_table.set({
    "username": "",
    "email": "",
    "password": "",
    "createdAt": ""
})

apartment_table.set({
    "price": "", "location": "", 
    "description": "", 
    "ownerID": user_table
})

watchlist_sub = user_table.collection('watchlist').document('dummy_apartment_ID')
watchlist_sub.set({
    "addedAt": firestore.SERVER_TIMESTAMP
})