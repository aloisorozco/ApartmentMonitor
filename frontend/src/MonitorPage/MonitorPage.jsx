import React, { useState } from 'react';
import Box from '@mui/material/Box';
import ApartmentListComponent from './ApartmentListComponent';
import ApartmentInputComponent from './ApartmentInputComponent';

export default function MonitorPage() {
  const [apartmentListings, setApartmentListings] = useState([]);

  return (
    <Box sx={{ flexGrow: 1 }}>
      <ApartmentInputComponent apartmentListings={apartmentListings} setApartmentListings={setApartmentListings} />
      <ApartmentListComponent apartmentListings={apartmentListings} />
    </Box>
  );
}