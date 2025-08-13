# ApartmentMonitor 🏠

React App we are developing for a friend to help him land an appartment.
The website allows users to login and apparment listing URLs they wish to monitor, and notifies users when a listing becomes avaialble or listing price fluctuates.

## Tech Stack 🧰
- Python (Backend) #Deprecated
- Java Spring Boot (Backend)
- JS (Frontend)
- Flask #Deprecated
- MySQL
- React
- Firestore (NoSQL) #Deprecated

## Databse
We are using a NoSQL firestore databse to store all the information about the user's and listings.

## Backend
- Using beatifullsoup, we webscrape the provided URL to retrvie all the key information.
- We schedule periodic webscraping to check if the listing became avaialable. 
- To prevent from gettign our IPs banned, we implmented a proxy rotation algorithm - allowing us to use a new IP for every webscrape we perform.
- Server runing using Flask.

## Frontend
- React frontend, following as closely as we can industry standards for authentication, and session managment.

## How To Run locally (because I am trying to steal your project)?
- Make sure you have React and Python on your device.
- pull this repo.
- split your terminal and cd into `backend` and `frontend`
- in `backend` run `pip install -r dependencies.txt` to download all the python dependencies.
- run `python main.py` to start the flask server.
- in `frontend` run `npm install` for the dependencies and then `npm run` to run the react app.
  
## Future plans (TODO)
- add support for websrcaping other webistes.
- add auth0
- add concurency to flask (I know, I know, we should have this already - we were focusing on making a prototype first and then handeling concurency)
