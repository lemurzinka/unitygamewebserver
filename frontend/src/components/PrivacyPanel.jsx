import React, { useState } from "react";
import "../styles/Panel.css";

function PrivacyPanel({ onClose }) {
  const [closing, setClosing] = useState(false);

  const handleClose = () => {
    setClosing(true);
    setTimeout(() => onClose(), 400);
  };

  return (
    <div className="panel-overlay">
      <div className={`panel ${closing ? "closing" : ""}`}>
        <button className="panel-close" onClick={handleClose}>×</button>
        <h2>Your Space</h2>
        <p>
          Your data is yours. BLOON only keeps what’s needed to keep the engines
          running — nothing more. We don’t sell, we don’t trade, we don’t betray.
        </p>
        <p>
          Anonymous stats may help us improve the orbit, but your identity stays locked
          in your own cockpit. You’re always in control of your presence here.
        </p>
      </div>
    </div>
  );
}

export default PrivacyPanel;
