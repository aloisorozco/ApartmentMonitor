import React from 'react';
import Box from '@mui/material/Box';
import Button from '@mui/material/Button';
import TextField from '@mui/material/TextField';

export default function ApartmentInputComponent() {
  return (
    <Box sx={{ flexGrow: 1 }}>
      <TextField
        label="Apartment URL"
        variant="outlined"
        fullWidth
        margin="normal"
      />
      <Button
        variant="contained"
        color="primary"
        type="submit"
      >
        Add to watchlist
      </Button>
    </Box>
  );
}