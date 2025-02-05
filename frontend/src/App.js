import React from "react";
import "./App.css";
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import { AuthProvider } from "./AuthContext";
import Login from "./Login/Login";
import HeaderComponent from "./HeaderComponent";
import MonitorPage from "./MonitorPage/MonitorPage";
import Register from "./Login/Register";
import PrivateRoute from "./PrivateRoute";

function App() {
  return (
    <Router>
      <AuthProvider>
        <div className="App">
          <HeaderComponent />
          <Routes>
            <Route path="/" element={<PrivateRoute />}>
              <Route path="/" element={<MonitorPage />} />
            </Route>
            <Route path="/login" element={<Login />} />
            <Route path="/register" element={<Register />} />
          </Routes>
        </div>
      </AuthProvider>
    </Router>
  );
}

export default App;
