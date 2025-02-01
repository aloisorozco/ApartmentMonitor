import React from 'react';
import Box from '@mui/material/Box';
import ApartmentListComponent from './ApartmentListComponent';
import ApartmentInputComponent from './ApartmentInputComponent';

export default function MonitorPage() {
  return (
    <Box sx={{ flexGrow: 1 }}>
      <ApartmentInputComponent />
      <ApartmentListComponent />
    </Box>
  );
}