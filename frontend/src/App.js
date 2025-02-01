import React from 'react'

import './App.css';
import SignIn from './LoginPage/SignIn'
import HeaderComponent from './HeaderComponent';
import MonitorPage from './MonitorPage/MonitorPage';

function App() {
  return (
    <div className="App">
      <SignIn />
      <HeaderComponent />
      <MonitorPage />
    </div>
  );
}

export default App;
