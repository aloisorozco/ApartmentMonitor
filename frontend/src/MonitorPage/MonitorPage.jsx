import React, { useState, useEffect } from 'react';
import Box from '@mui/material/Box';
import ApartmentListComponent from './ApartmentListComponent';
import ApartmentInputComponent from './ApartmentInputComponent';
import { useAuth } from '../AuthContext';

export default function MonitorPage() {
  const [apartmentListings, setApartmentListings] = useState([]);
  const { userEmail } = useAuth()

  useEffect(() => {
    fetch(`http://localhost:8080/api/apartments/fetch_watchlist?email=${userEmail}`, {
      method: "GET",
      headers : { 'Content-Type' : 'application/json'},
      data : {}
    })
    .then(res => res.json())
    .then(data => {
      let listings_fetched = (data.listings ?? []).map((listing) => {
        return {
          id: listing.listing_id,
          title: listing.location,
          url: listing.url,
          targetPrice: listing.price_target,
          currentPrice: listing.price,
          image: listing.image_link,
        }
      })
      // This should cause the table re-render and set all items in the user watchlist
      setApartmentListings([
        ...apartmentListings,
        ...listings_fetched
      ])
    })
    .catch(error => console.log(error))
    // passing empty array here so useEffect is called once on mount, and never again - without this we are in a infinite render loop
    // since we fetch listings, set state, and then re-render causing another fetch listing, and set state and so on
  }, []) 

  return (
    <Box sx={{ flexGrow: 1 }}>
      <ApartmentInputComponent apartmentListings={apartmentListings} setApartmentListings={setApartmentListings} />
      <ApartmentListComponent apartmentListings={apartmentListings} setApartmentListings={setApartmentListings}/>
    </Box>
  );
}