import React, { useState, useEffect } from "react";
import "./App.css";
import { BrowserRouter as Router, Routes, Route, useLocation } from "react-router-dom";
import { AuthProvider } from "./AuthContext";
import Login from "./Login/Login";
import HeaderComponent from "./HeaderComponent";
import MonitorPage from "./MonitorPage/MonitorPage";
import Register from "./Login/Register";
import PrivateRoute from "./PrivateRoute";

function App() {
  const [noScroll, setNoScroll] = useState(false); //set scroll condition as state
  const location = useLocation(); //get path
  
  //if in login/register, css no scroll
  useEffect(() => {
    const isAuthPage = location.pathname === "/login" || location.pathname === "/register";
    setNoScroll(isAuthPage);
  }, [location.pathname]); //updates on pathname update

  return (
    <div className={`App ${noScroll ? "no-scroll" : ""}`}>
      <HeaderComponent />
      <Routes>
        <Route path="/" element={<PrivateRoute />}>
          <Route path="/" element={<MonitorPage />} />
        </Route>
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />
      </Routes>
      

    </div>
  );
}

//App will now be part of router
function RouterApp() {
  return (
    <Router>
      <AuthProvider>
        <App />
      </AuthProvider>
    </Router>
  );
}

export default RouterApp;
