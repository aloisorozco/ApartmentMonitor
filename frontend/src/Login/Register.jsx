import React, { useState } from 'react';
import { TextField, Button, Box, Typography, Paper } from '@mui/material';
import { Link } from 'react-router-dom';

const Register = () => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [firstName, setFirstName] = useState('');
  const [lastName, setLastName] = useState('');

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
        <Typography variant="h4" color="black" fontWeight="bold">
          Create a new account
        </Typography>
        <Typography variant="body1" color="grey" fontWeight="medium">
          It's quick and easy.
        </Typography>
        <form onSubmit={handleSubmit}>
          <TextField
            label="First Name"
            type="firstName"
            variant="filled"
            fullWidth
            margin="normal"
            value={firstName}
            onChange={(e) => setFirstName(e.target.value)}
          />
          <TextField
            label="Last Name"
            type="lastName"
            variant="filled"
            fullWidth
            margin="normal"
            value={lastName}
            onChange={(e) => setLastName(e.target.value)}
          />
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

         <Typography variant="caption" color="grey" fontStyle="oblique"> You may receive SMS Notifications from us and can opt out any time. </Typography>

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
              Sign Up
            </Button>

            <Typography variant="body2" sx={{ marginTop: 2 }}>
              <Link to={`/login`} underline="hover">
                Already have an account?{' '}
              </Link>
            </Typography>
          </Box>
        </form>
      </Paper>
    </Box>
  );
};

export default Register;
