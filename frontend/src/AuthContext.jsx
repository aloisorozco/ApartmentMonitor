import React, { createContext, useState, useContext, useEffect } from "react";

// Global authentication tracker
const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
  const [auth, setAuth] = useState(null);
  const [userEmail, setUserEmail] = useState(null);

  // On component mount, check sessionStorage for the auth status
  useEffect(() => {
    const storedAuthStatus = sessionStorage.getItem("authStatus");
    const storedEmail = sessionStorage.getItem("userEmail");

    //if auth status and email are already in storage, set them to that, else set them to default value.
    if (storedAuthStatus === "true" && storedEmail) {
      setAuth(true);
      setUserEmail(storedEmail);
    } else {
      setAuth(false);
      setUserEmail(null);
    }
  }, []);

  // Login function to set auth and store in sessionStorage
  const login = (email) => {
    setAuth(true);
    setUserEmail(email);
    sessionStorage.setItem("authStatus", "true");
    sessionStorage.setItem("userEmail", email);
    console.log("authStatus and email stored in sessionStorage");
  };

  // Logout function to reset auth and remove from sessionStorage
  const logout = () => {
    setAuth(false);
    setUserEmail(null);
    sessionStorage.removeItem("authStatus");
    sessionStorage.removeItem("userEmail");
  };

  return (
    <AuthContext.Provider value={{ auth, userEmail, login, logout }}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  return useContext(AuthContext);
};
