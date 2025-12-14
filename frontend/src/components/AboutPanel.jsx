import React, { useState } from "react";
import "../styles/Panel.css";

function AboutPanel({ onClose }) {
  const [closing, setClosing] = useState(false);

  const handleClose = () => {
    setClosing(true);
    setTimeout(() => onClose(), 400); 
  };

  return (
    <div className="panel-overlay">
      <div className={`panel ${closing ? "closing" : ""}`}>
        <button className="panel-close" onClick={handleClose}>×</button>
        <h2>About BLOON</h2>
        <p>
          BLOON is more than a platform — it’s a cosmic hub where skins, style,
          and community collide. Think of it as your personal gateway into a
          neon‑lit universe built for players who want to stand out.
        </p>
      </div>
    </div>
  );
}

export default AboutPanel;
