import React, { useState } from "react";
import "../styles/Footer.css";
import AboutPanel from "./AboutPanel";
import TermsPanel from "./TermsPanel";
import PrivacyPanel from "./PrivacyPanel";

function Footer() {
  const [panel, setPanel] = useState(null);

  return (
    <footer className="footer">
      <div className="footer-left">
        <span className="footer-logo">BLOON</span>
        <span className="footer-copy">© 2025 BLOON. All rights reserved.</span>
      </div>
      <div className="footer-right">
<div className="footer-right">
  <button className="footer-link" onClick={() => setPanel("about")}>✦ Our Story</button>
  <button className="footer-link" onClick={() => setPanel("terms")}>✦ Rules of Play</button>
  <button className="footer-link" onClick={() => setPanel("privacy")}>✦ Your Space</button>
</div>

      </div>

      {panel === "about" && <AboutPanel onClose={() => setPanel(null)} />}
      {panel === "terms" && <TermsPanel onClose={() => setPanel(null)} />}
      {panel === "privacy" && <PrivacyPanel onClose={() => setPanel(null)} />}
    </footer>
  );
}

export default Footer;
