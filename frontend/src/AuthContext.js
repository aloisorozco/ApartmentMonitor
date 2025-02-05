import React, { createContext, useState, useContext, useEffect } from "react";

// Global authentication tracker
const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
  const [auth, setAuth] = useState(null);

  // On component mount, check sessionStorage for the auth status
  useEffect(() => {
    const storedAuthStatus = sessionStorage.getItem("authStatus");
    if (storedAuthStatus === "true") {
      setAuth(true);
    } else {
      setAuth(false);
    }
  }, []);

  // Login function to set auth and store in sessionStorage
  const login = () => {
    setAuth(true);
    sessionStorage.setItem("authStatus", "true");
    console.log(
      "authStatus stored in sessionStorage: ",
      sessionStorage.getItem("authStatus")
    );
  };

  // Logout function to reset auth and remove from sessionStorage
  const logout = () => {
    setAuth(false);
    sessionStorage.removeItem("authStatus");
  };

  return (
    <AuthContext.Provider value={{ auth, login, logout }}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  return useContext(AuthContext);
};
