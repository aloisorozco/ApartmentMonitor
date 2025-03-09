import React from 'react';
import { Box, Card, CardContent, CardMedia, Typography, IconButton, Button, Grid, Grid2 } from '@mui/material';
import DeleteIcon from '@mui/icons-material/Delete';
import { useAuth } from '../AuthContext';

export default function ApartmentListComponent({ apartmentListings, setApartmentListings }) {
  const { userEmail } = useAuth();

  const handleDelete = (listingId) => {
    fetch(`http://localhost:5500/db_api/remove_listing?email=${userEmail}&listing_id=${listingId}`, {
      method: "DELETE",
      headers: { 'Content-Type': 'application/json' },
    })
    .then(response => {
      if (response.ok) {
        setApartmentListings(prevListings => prevListings.filter(listing => listing.id !== listingId));
      } else {
        console.log("Error deleting the listing");
      }
    })
    .catch(error => console.log("Error:", error));
  };

  if (apartmentListings.length === 0) {
    return <Typography align="center" variant="h6" sx={{ mt: 4 }}>No listings found.</Typography>;
  }

  return (
    <Box sx={{ p: 2, display: "flex", justifyContent: "center" }}>
      <Grid2 container spacing={3} justifyContent="center">
        {apartmentListings.map((listing) => (
          <Grid2 item xs={12} sm={6} md={4} lg={3} key={listing.id}>
            <Card sx={{ width: "100%", borderRadius: 4, boxShadow: 3, overflow: "hidden" }}>
              <CardMedia
                component="img"
                height="200"
                width="100%"
                image={listing.image}
                alt={listing.title}
                sx={{ objectFit: "cover" }}
              />
              <CardContent >
                <Typography variant="h6" fontWeight="bold">{listing.title}</Typography>
                <Typography color="text.secondary" fontSize={14}>ID: {listing.id}</Typography>
                <Typography variant="body1" sx={{ mt: 1 }}>
                  💰 <b>Target Price:</b> ${listing.targetPrice}
                </Typography>
                <Typography variant="body1">
                  🔥 <b>Current Price:</b> ${listing.currentPrice}
                </Typography>
                <Box sx={{ mt: 2, display: "flex", justifyContent: "space-between", alignItems: "center" }}>
                  <Button
                    variant="contained"
                    size="small"
                    href={listing.url}
                    target="_blank"
                    sx={{ backgroundColor: "#1976d2", color: "white" }}
                  >
                    View Listing
                  </Button>
                  <IconButton onClick={() => handleDelete(listing.id)} color="error">
                    <DeleteIcon />
                  </IconButton>
                </Box>
              </CardContent>
            </Card>
          </Grid2>
        ))}
      </Grid2>
    </Box>
  );
}
