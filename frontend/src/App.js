import React from 'react'

import './App.css';
import Login from './Login/Login'
import HeaderComponent from './HeaderComponent';
import MonitorPage from './MonitorPage/MonitorPage';
import Register from './Login/Register';
import PrivateRoute from './PrivateRoute'
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';

function App() {
  return (
    <div className="App">
      <HeaderComponent />
      <Router>
        <Routes>
        <Route exact path='/' element={<PrivateRoute/>}>
            <Route exact path='/' element={<MonitorPage/>}/>
        </Route>
        <Route exact path='/login' element={<Login/>}/>
        <Route exact path='/register' element={<Register />} />
        </Routes>

       
    </Router>
    </div>
  );
}

export default App;
