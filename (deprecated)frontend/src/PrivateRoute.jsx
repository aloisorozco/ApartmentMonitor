import React from "react";
import { Navigate, Outlet } from "react-router-dom";
import { useAuth } from "./AuthContext";
import { CircularProgress } from "@mui/material";

const PrivateRoute = () => {
  const { auth } = useAuth();

  // loading screen for fetching the authStatus during refresh
  if (auth === null) {
    return <CircularProgress />;
  }

  return auth ? <Outlet /> : <Navigate to="/login" />;
};

export default PrivateRoute;
