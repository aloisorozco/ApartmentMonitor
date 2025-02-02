import React, { createContext, useState, useContext } from 'react';

//global authentication tracker
const AuthContext = createContext();


export const AuthProvider = ({ children }) => {
  const [auth, setAuth] = useState(false); 

  const login = () => setAuth(true);
  const logout = () => setAuth(false);

  return (
    <AuthContext.Provider value={{ auth, login, logout }}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  return useContext(AuthContext);
};
