import React, { useState } from 'react';
import Box from '@mui/material/Box';
import Button from '@mui/material/Button';
import TextField from '@mui/material/TextField';

import {isEmpty} from "lodash";

export default function ApartmentInputComponent(props) {
  const [url, setUrl] = useState("");
  const {apartmentListings, setApartmentListings} = props;

  const handleAdd = () => {
    if (isEmpty(url)) {
      return;
    }
    setApartmentListings([...apartmentListings, {
      title: url,
      url: url,
      targetPrice: 600,
      currentPrice: 900
    }])
    setUrl("")
  }

  return (
    <Box sx={{ flexGrow: 1 }}>
      <TextField
        label="Apartment URL"
        variant="outlined"
        fullWidth
        margin="normal"
        value={url}
        onChange={(e) => {
          setUrl(e.target.value)
        }}
      />
      <Button
        variant="contained"
        color="primary"
        disabled={isEmpty(url)}
        onClick={handleAdd}
      >
        Add to watchlist
      </Button>
    </Box>
  );
}