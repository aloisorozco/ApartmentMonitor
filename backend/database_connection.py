import firebase_admin
from firebase_admin import credentials, firestore

# Initialize Firebase
cred = credentials.Certificate('C:\\Users\\cszty\\Downloads\\cred.json')
firebase_admin.initialize_app(cred)
db = firestore.client()

# Function to create a new user
def create_user(fname, lname, email, password_hashed):
    user_ref = db.collection('users').add({
        "fname": fname,
        "lname": lname,
        "email": email,
        "password_hashed": password_hashed,
        "createdAt": firestore.SERVER_TIMESTAMP
    })
    return user_ref[1].id  # Returns the generated user ID

# Function to create an apartment listing
def create_apartment(price, location, description):
    apartment_ref = db.collection('apartments').add({
            "price": 0,
            "price_target": 0,
            "description": ""
    })
    return apartment_ref[1].id  # Returns the generated listing ID

# Function to add an apartment to a user's watchlist
def add_to_watchlist(user_id, apartment_id):
    # Fetch the apartment reference using the listing ID
    apartment_ref = db.collection('apartments').document(apartment_id)
    
    # Add the apartment reference to the user's watchlist
    watchlist_ref = db.collection('users').document(user_id).collection('watchlist').document(apartment_id)
    watchlist_ref.set({
        "apartmentRef": apartment_ref,  # Store the reference to the listing
        "addedAt": firestore.SERVER_TIMESTAMP
    })

# Example Usage:
user_id = create_user("Test", "User", "john@example.com", "hashedpassword")
apartment_id = create_apartment(0, "Example Location", "Example Description")

# User adds the apartment to their watchlist
add_to_watchlist(user_id, apartment_id)
