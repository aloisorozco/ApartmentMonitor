import React, { useState, useEffect } from 'react';
import Box from '@mui/material/Box';
import ApartmentListComponent from './ApartmentListComponent';
import ApartmentInputComponent from './ApartmentInputComponent';
import { useAuth } from '../AuthContext';

export default function MonitorPage() {
  const [apartmentListings, setApartmentListings] = useState([]);
  const { userEmail } = useAuth()

  useEffect(() => {
    fetch(`http://localhost:5500/db_api/fetch_watchlist?email=${userEmail}`, {
      method: "GET",
      headers : { 'Content-Type' : 'application/json'},
      data : {}
    })
    .then(res => res.json())
    .then(data => {
      console.log(data)
      let listings_fetched = data.listings.map((listing) => {
        return {
          id: listing.listing_id,
          title: listing.location,
          url: listing.url,
          targetPrice: listing.price_target,
          currentPrice: listing.price,
          image: listing.image_link,
        }
      })
        .then(res => res.json())
        .then(data => {
          let listings_fetched = (data ?? []).map((listing) => {
            console.log(listing)
            return {
              id: listing.apartment_id,
              title: listing.apartment_description,
              url: listing.apartment_url,
              targetPrice: null,
              currentPrice: listing.apartment_price,
              image: listing.apartment_image_link,
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
})
}
