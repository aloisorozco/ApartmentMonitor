import React, { useState } from 'react';
import { TextField, Button, Box, Typography, Paper } from '@mui/material';
<<<<<<< Updated upstream
import { Link } from 'react-router-dom'

=======
import { Link } from 'react-router-dom';
>>>>>>> Stashed changes

const Login = () => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');

  const handleSubmit = (event) => {
    event.preventDefault();
<<<<<<< Updated upstream
    // Handle form submission (e.g., validation, API call, etc.)
    setEmail("")
    setPassword("")
=======
    setEmail("");
    setPassword("");
>>>>>>> Stashed changes
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
<<<<<<< Updated upstream
    >
      <Paper elevation={3} sx={{ padding: 3, width: 300 }}>
        <Typography variant="h5" align="center" gutterBottom>
          Login
=======
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
>>>>>>> Stashed changes
        </Typography>
        <form onSubmit={handleSubmit}>
          <TextField
            label="Email"
            type="email"
<<<<<<< Updated upstream
            variant="outlined"
=======
            variant="filled"
>>>>>>> Stashed changes
            fullWidth
            margin="normal"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
          />
          <TextField
            label="Password"
            type="password"
<<<<<<< Updated upstream
            variant="outlined"
=======
            variant="filled"
>>>>>>> Stashed changes
            fullWidth
            margin="normal"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
          />
<<<<<<< Updated upstream
          <Button
            type="submit"
            variant="contained"
            color="primary"
            fullWidth
            sx={{ marginTop: 2 }}
          >
            Log In
          </Button>
        </form>

        <Box sx={{ textAlign: 'center', marginTop: 2 }}>
          <Typography variant="body2">
            Don't have an account?{' '}
            <Link to={`/register`} underline="hover">
              Register Now
            </Link>
          </Typography>
        </Box>

        

=======

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
>>>>>>> Stashed changes
      </Paper>
    </Box>
  );
};

export default Login;
