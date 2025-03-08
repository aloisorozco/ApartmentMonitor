import React from 'react';
import Table from '@mui/material/Table';
import TableBody from '@mui/material/TableBody';
import TableCell from '@mui/material/TableCell';
import TableContainer from '@mui/material/TableContainer';
import TableHead from '@mui/material/TableHead';
import TableRow from '@mui/material/TableRow';
import Paper from '@mui/material/Paper';
import Link from '@mui/material/Link';
import DeleteIcon from '@mui/icons-material/Delete';
import IconButton from '@mui/material/IconButton';
import { isNil } from 'lodash';
import { useAuth } from '../AuthContext';

export default function ApartmentListComponent(props) {
  const { apartmentListings, setApartmentListings } = props;
  const { userEmail } = useAuth();

  //call delete api
  const handleDelete = (listingId) => {
    fetch(`http://localhost:5500/db_api/remove_listing?email=${userEmail}&listing_id=${listingId}`, {
      method: "DELETE",
      headers: { 'Content-Type': 'application/json' },
    })
      .then(response => {
        if (!response.ok) {
          console.log("Error deleting the listing");
        } else {
          //visually update the listing
          setApartmentListings(prevListings =>
            prevListings.filter(listing => listing.id !== listingId)
          );
        }
      })
      .catch(error => console.log("Error:", error));
  };

  if (isNil(apartmentListings) || apartmentListings.length === 0) {
    return null;
  }

  return (
    <TableContainer component={Paper} 
    sx={{overflow: 'auto'}}
    >
      <Table sx={{ minWidth: 650 }} aria-label="simple table">
        <TableHead>
          <TableRow>
            <TableCell>Title</TableCell>
            <TableCell align="right">Target Price</TableCell>
            <TableCell align="right">Current Price</TableCell>
            <TableCell align="right">URL</TableCell>
            <TableCell align="right">Actions</TableCell>
          </TableRow>
        </TableHead>
        <TableBody>
          {apartmentListings.map((apartmentListing) => (
            <TableRow key={apartmentListing.id} sx={{ '&:last-child td, &:last-child th': { border: 0 } }}>
              <TableCell component="th" scope="row">
                {`${apartmentListing.title} - ${apartmentListing.id}`}
              </TableCell>
              <TableCell align="right">{apartmentListing.targetPrice}</TableCell>
              <TableCell align="right">{apartmentListing.currentPrice}</TableCell>
              <TableCell align="right">
                <Link href={apartmentListing.url}>Listing</Link>
              </TableCell>
              <TableCell align="right">
                <IconButton 
                  onClick={() => handleDelete(apartmentListing.id)} 
                  color="error"
                >
                <DeleteIcon />
                </IconButton>
              </TableCell>
            </TableRow>
          ))}
        </TableBody>
      </Table>
    </TableContainer>
  );
}
