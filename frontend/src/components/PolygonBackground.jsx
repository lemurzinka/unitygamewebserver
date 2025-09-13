import React from 'react';
import '../styles/PolygonBackground.css'; 
import polygonShape from '../assets/images/bg-poligon.svg';

function PolygonBackground() {
  return (
    <img
      src={polygonShape}
      alt="Polygon background"
      className="polygon-bg"
    />
  );
}

export default PolygonBackground;