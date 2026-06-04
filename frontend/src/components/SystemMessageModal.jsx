import React from "react";
import "../styles/SystemMessageModal.css";

function SystemMessageModal({ title, message, onClose }) {
  return (
    <div className="system-message-modal">
      <div className="system-message-content">
        <h3>{title}</h3>
        <p>{message}</p>
        <button onClick={onClose}>Close</button>
      </div>
    </div>
  );
}

export default SystemMessageModal;
