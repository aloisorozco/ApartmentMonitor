import React, { useState, useEffect } from 'react';

const ApartmentsList = () => {
  // State to hold apartments data
  const [apartments, setApartments] = useState([]);
  
  // Fetch apartments data from the backend when the component mounts
  useEffect(() => {
    fetch("http://localhost:8080/api/apartments/")  // Backend API URL
      .then((response) => response.json())  // Parse JSON data
      .then((data) => setApartments(data))  // Store data in state
      .catch((error) => console.error("Error fetching apartments:", error));
  }, []); // Empty dependency array means this runs once when the component mounts
  
  return (
    <div>
      <h1>Apartments</h1>
      <ul>
        {apartments.map((apartment) => (
          <li key={apartment.id}>{apartment.name}</li>  // Render each apartment's name
        ))}
      </ul>
    </div>
  );
}

export default ApartmentsList;