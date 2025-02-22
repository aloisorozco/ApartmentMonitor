import React, { useState } from 'react';
import { TextField, Button, Box, Typography, Paper } from '@mui/material';
import { Link, useNavigate } from 'react-router-dom';
import './Register.css';

const Register = () => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [firstName, setFirstName] = useState('');
  const [lastName, setLastName] = useState('');
  const [errors, setErrors] = useState({});

  const navigate = useNavigate();

  const validateFields = () => {
    const newErrors = {};
    if (!firstName.trim()) newErrors.firstName = true;
    if (!lastName.trim()) newErrors.lastName = true;
    if (!email.trim()) newErrors.email = true;
    if (!password.trim()) newErrors.password = true;

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = (event) => {
    event.preventDefault();

    if (!validateFields()) return;

    fetch("http://localhost:5500/db_api/register_user", {
      method: "POST",
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        email,
        password,
        fname: firstName,
        lname: lastName
      })
    })
    .then(response => response.json())
    .then(() => {
      navigate('/');
    })
    .catch(error => {
      console.log(error);
    });
  };

  return (
    <Box className="register-container">
      <Paper className="register-paper">
        <Typography variant="h4" color="black" fontWeight="bold">
          Create a new account
        </Typography>
        <Typography variant="body1" color="grey" fontWeight="medium">
          It's quick and easy.
        </Typography>
        <form onSubmit={handleSubmit}>
          <Box className="register-input-group">
            <TextField
              label="First Name"
              variant="outlined"
              fullWidth
              margin="normal"
              value={firstName}
              onChange={(e) => setFirstName(e.target.value)}
              error={!!errors.firstName}
              className="register-input"
            />
            <TextField
              label="Last Name"
              variant="outlined"
              fullWidth
              margin="normal"
              value={lastName}
              onChange={(e) => setLastName(e.target.value)}
              error={!!errors.lastName}
              className="register-input"
            />
          </Box>
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

          <Typography variant="caption" className="register-caption">
            You may receive SMS Notifications from us and can opt out any time.
          </Typography>

          <Box sx={{ textAlign: 'center', width: '60%', margin: '0 auto' }}>
            <Button type="submit" variant="contained" fullWidth className="register-button">
              Sign Up
            </Button>

            <Typography variant="body2" className="register-link">
              <Link to={`/login`} underline="hover">
                Already have an account?
              </Link>
            </Typography>
          </Box>
        </form>
      </Paper>
    </Box>
  );
};

export default Register;
