import React from 'react';
import AppBar from '@mui/material/AppBar';
import Box from '@mui/material/Box';
import Toolbar from '@mui/material/Toolbar';
import Typography from '@mui/material/Typography';
import Button from '@mui/material/Button';
import { useAuth } from './AuthContext'


export default function HeaderComponent() {
  const { logout } = useAuth();

  const handleLogout = () => {
      logout();
  }


  return (
    <Box >
      <AppBar position="static">
        <Toolbar>
          <Box sx={{ flexGrow: 1, display: "flex", justifyContent: "center" }}>
            <Typography variant="h6" component="div">
              Apartment Monitor
            </Typography>
          </Box>


            <Button color="inherit" onClick={handleLogout}>
              Logout
            </Button>

        </Toolbar>
      </AppBar>
    </Box>
  );
}