import React from 'react';
import '../styles/Polygon.css'; 
import polygonShape from '../assets/images/Polygon.svg';

function Polygon() {
  return (
    <img
      src={polygonShape}
      alt="Polygon"
      className="polygon"
    />
  );
}

export default Polygon;