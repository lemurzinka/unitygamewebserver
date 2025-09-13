import React, { useState } from "react";
import "../styles/Header.css";
import coinIcon from "../assets/images/coin.png";
import DonateModal from "./DonateModal";
import { AnimatePresence } from "framer-motion"; 

function Header() {
  const [showDonate, setShowDonate] = useState(false);

  return (
    <>
      <header className="header">
        <nav className="nav">
          <div className="nav-button">Main</div>
          <div className="nav-button">Skins</div>
          <div className="nav-button">Skins</div>
          <div className="donate-text" onClick={() => setShowDonate(true)}>
            <span className="donate-label">Buy</span>
            <img src={coinIcon} alt="Coin" className="coin-icon" />
          </div>
        </nav>
      </header>

      <AnimatePresence>
        {showDonate && (
          <DonateModal onClose={() => setShowDonate(false)} />
        )}
      </AnimatePresence>
    </>
  );
}

export default Header;
