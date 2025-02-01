import React from 'react';
import Table from '@mui/material/Table';
import TableBody from '@mui/material/TableBody';
import TableCell from '@mui/material/TableCell';
import TableContainer from '@mui/material/TableContainer';
import TableHead from '@mui/material/TableHead';
import TableRow from '@mui/material/TableRow';
import Paper from '@mui/material/Paper';
import Link from '@mui/material/Link';

import {isNil} from "lodash";

export default function ApartmentListComponent(props) {
  const {apartmentListings} = props;

  if (isNil(apartmentListings) || apartmentListings.length === 0) {
    return null;
  }
  
  return (
    <TableContainer component={Paper}>
      <Table sx={{ minWidth: 650 }} aria-label="simple table">
        <TableHead>
          <TableRow>
            <TableCell>Title</TableCell>
            <TableCell align="right">Target Price</TableCell>
            <TableCell align="right"> Current Price</TableCell>
            <TableCell align="right">URL</TableCell>
          </TableRow>
        </TableHead>
        <TableBody>
          {apartmentListings.map((apartmentListing) => (
            <TableRow
              key={apartmentListing.title}
              sx={{ '&:last-child td, &:last-child th': { border: 0 } }}
            >
              <TableCell component="th" scope="row">
                {apartmentListing.title}
              </TableCell>
              <TableCell align="right">{apartmentListing.targetPrice}</TableCell>
              <TableCell align="right">{apartmentListing.currentPrice}</TableCell>
              <TableCell align="right"><Link href={apartmentListing.url}>Listing</Link></TableCell>
            </TableRow>
          ))}
        </TableBody>
      </Table>
    </TableContainer>
  );
}