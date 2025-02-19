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
    })
    .then(res => res.json())
    .then(data => {
      console.log(data)
    })
    .catch(error => console.log(error))
  })

  return (
    <Box sx={{ flexGrow: 1 }}>
      <ApartmentInputComponent apartmentListings={apartmentListings} setApartmentListings={setApartmentListings} />
      <ApartmentListComponent apartmentListings={apartmentListings} />
    </Box>
  );
}