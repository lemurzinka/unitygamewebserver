import React from "react";
import "../styles/Background.css";
import bgStars from "../assets/images/stars.jpg";

function Background() {
  return (
    <div className="background">
      <img src={bgStars} alt="Starry background" className="bg-layer blur" />
      <img src={bgStars} alt="Starry background" className="bg-layer" />
    </div>
  );
}

export default Background;
