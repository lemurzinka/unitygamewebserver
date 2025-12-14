import React, { useState } from "react";
import "../styles/Panel.css";

function TermsPanel({ onClose }) {
  const [closing, setClosing] = useState(false);

  const handleClose = () => {
    setClosing(true);
    setTimeout(() => onClose(), 400); 
  };

  return (
    <div className="panel-overlay">
      <div className={`panel ${closing ? "closing" : ""}`}>
        <button className="panel-close" onClick={handleClose}>×</button>
        <h2>Rules of Play</h2>
        <p>
          BLOON is a playground, not a battlefield. Respect the vibe, respect the
          community. No toxicity, no stealing, no breaking the flow.
        </p>
        <p>
          By joining, you agree to keep the universe alive and thriving. We may update
          the rules as the galaxy grows — staying here means you’re on board with the
          mission.
        </p>
      </div>
    </div>
  );
}

export default TermsPanel;
