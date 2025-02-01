import React, { useState } from 'react';
import { TextField, Button, Box, Typography, Paper } from '@mui/material';
import { Link } from 'react-router-dom';

const Login = () => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');

  const handleSubmit = (event) => {
    event.preventDefault();
    setEmail("");
    setPassword("");
    console.log('Email:', email);
    console.log('Password:', password);
  };

  return (
    <Box
      display="flex"
      justifyContent="center"
      alignItems="center"
      height="100vh"
      bgcolor="#f5f5f5"
      sx={{fontFamily:'Roboto'}}
    >
      <Paper
        elevation={3}
        sx={{
          padding: 4,
          width: 400,
          borderRadius: 3,
          boxShadow: 3,
          bgcolor: 'white',
          textAlign: 'center',
        }}
      >
        <Typography variant="h5" color="black" sx={{paddingBottom:'5px'}}>
          Log into Appartment Monitor
        </Typography>
        <form onSubmit={handleSubmit}>
          <TextField
            label="Email"
            type="email"
            variant="filled"
            fullWidth
            margin="normal"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
          />
          <TextField
            label="Password"
            type="password"
            variant="filled"
            fullWidth
            margin="normal"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
          />

          <Box sx={{ textAlign: 'center', width: '45%', margin: '0 auto' }}>
            <Button
              type="submit"
              variant="contained"
              fullWidth
              sx={{
                marginTop: 2,
                backgroundColor: '#00a400',
                color: 'white',
                fontWeight: 'bold',
                borderRadius:'8px'
              }}
            >
              Login
            </Button>

            <Typography variant="body2" sx={{ marginTop: 2 }}>
              <Link to={`/register`} underline="hover">
                Don't have an account?{' '}
              </Link>
            </Typography>
          </Box>
        </form>
      </Paper>
    </Box>
  );
};

export default Login;
