import React, { useState } from 'react';
import Box from '@mui/material/Box';
import Button from '@mui/material/Button';
import TextField from '@mui/material/TextField';
import { isEmpty } from 'lodash';
import { useAuth } from '../AuthContext';

export default function ApartmentInputComponent(props) {
  const [url, setUrl] = useState('');
  const { userEmail } = useAuth();
  const { apartmentListings, setApartmentListings } = props;

  const handleAdd = () => {
    if (isEmpty(url)) {
      return;
    }

    let email = userEmail
    let targetPrice =  600
    
    fetch("http://localhost:5500/db_api/save_listing", {
      method: "POST",
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ email: email, target_Price : targetPrice, url: url})
    })
      .then(response => {
        if (response.ok) {
          return response.json()
        } else {
          console.log("Invalid Credentials");
        }
      }).then(data => {
        setApartmentListings([
          ...apartmentListings,
          { 
            // set here the img + description
            title: data.location,
            url: url,
            targetPrice: 600,
            currentPrice: data.price_curr,
          },
        ]);
        setUrl('');
      })
      .catch(error => console.log(error));
  };

  return (
    <Box sx={{ width: '100%', padding: 2 }}>
      <TextField
        label="Apartment URL"
        variant="outlined"
        fullWidth
        value={url}
        onChange={(e) => setUrl(e.target.value)}
      />
      <Button variant="contained" color="primary" disabled={isEmpty(url)} onClick={handleAdd} sx={{ mt: 1 }}>
        Add to watchlist
      </Button>
    </Box>
  );
}
