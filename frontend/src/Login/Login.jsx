import React, { useState } from 'react';
import { TextField, Button, Box, Typography, Paper } from '@mui/material';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../AuthContext';
import './Register.css';

const Login = () => {
  const { login } = useAuth();
  const navigate = useNavigate();
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [errors, setErrors] = useState({});

  //validates the fields
  const validateFields = () => {
    const newErrors = {};
    if (!email.trim()) newErrors.email = true;
    if (!password.trim()) newErrors.password = true;

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = (event) => {
    event.preventDefault(); //don't refresh the page

    if (!validateFields()) return; //if invalid field, don't POST

    fetch("http://localhost:8080/api/users/auth_user", {
      method: "POST",
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ email, password })
    })
      .then(response => {
        if (response.ok) {
          login(email); //pass in email into context
          navigate('/'); //navigate to main page
        } else {
          console.log("Invalid Credentials");
        }
      })
      .then(data => {
        console.log(data); //view response data
      })
      .catch(error => console.log(error));
  };

  return (
    <Box className="register-container">
      <Paper className="register-paper">
        <Typography variant="h5" color="black" sx={{ paddingBottom: '5px' }}>
          Log into Apartment Monitor
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
            error={!!errors.email}
            className="register-input"
          />
          <TextField
            label="Password"
            type="password"
            variant="filled"
            fullWidth
            margin="normal"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            error={!!errors.password}
            className="register-input"
          />

          <Box sx={{ textAlign: 'center', width: '100%', marginTop: 2 }}>
            <Button
              type="submit"
              variant="contained"
              fullWidth
              className="register-button"
            >
              Login
            </Button>

            <Typography variant="body2" className="register-link">
              <Link to="/register" underline="hover">
                Don't have an account?
              </Link>
            </Typography>
          </Box>
        </form>
      </Paper>
    </Box>
  );
};

export default Login;
