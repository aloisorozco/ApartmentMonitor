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
    
    fetch("http://localhost:8080/api/apartments/insert", {
      method: "POST",
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ email: email, url: url})
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
            currentPrice: data.price,
            image: data.imageLink,
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
