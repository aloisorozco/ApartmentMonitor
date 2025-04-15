import React, { useState, useEffect } from 'react';
import Box from '@mui/material/Box';
import ApartmentListComponent from './ApartmentListComponent';
import ApartmentInputComponent from './ApartmentInputComponent';
import { useAuth } from '../AuthContext';

export default function MonitorPage() {
  const [apartmentListings, setApartmentListings] = useState([])
  const { userEmail } = useAuth()

  useEffect(() => {
    console.log("test")
      fetch(`http://localhost:8080/api/apartments/fetch_watchlist?email=${userEmail}`, {
        method: "GET",
        headers : { 'Content-Type' : 'application/json' },
      })
        .then(res => res.json())
        .then(data => {
          let listings_fetched = (data ?? []).map((listing) => {
            return {
              id: listing.listingID,
              title: listing.location,
              url: listing.url,
              targetPrice: listing.price_target,
              currentPrice: listing.price,
              image: listing.imageLink,
            };
          });
          setApartmentListings([
            ...apartmentListings,
            ...listings_fetched
          ]);
        })
        .catch(error => console.log(error));
  }, [userEmail]);

  return (
    <Box sx={{ flexGrow: 1 }}>
      <ApartmentInputComponent apartmentListings={apartmentListings} setApartmentListings={setApartmentListings} />
      <ApartmentListComponent apartmentListings={apartmentListings} setApartmentListings={setApartmentListings}/>
    </Box>
  );
}