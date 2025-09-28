import React, { useState } from "react";
import "../styles/Header.css";
import coinIcon from "../assets/images/coin.png";
import DonateModal from "./DonateModal";
import { AnimatePresence } from "framer-motion"; 
import SignUpModal from "./SignUpModal";
import SignInModal from "./SignInModal";

function Header() {
  const [modal, setModal] = useState(null); 
  const user = JSON.parse(localStorage.getItem("user"));

  const handleLogout = () => {
    localStorage.removeItem("user");
    window.location.reload();
  };

  return (
    <>
      <header className="header">
        <nav className="nav">
          <div className="nav-button">Main</div>
          <div className="nav-button">Skins</div>

         {user ? (
  <>
    <div className="nav-button">{user.username}</div>
    <div className="nav-button" onClick={handleLogout}>Logout</div>
  </>
) : (
  <>
    <div className="nav-button" onClick={() => setModal("signup")}>Sign up</div>
    <div className="nav-button" onClick={() => setModal("signin")}>Sign in</div>
  </>
)}

          <div className="donate-text" onClick={() => setModal("donate")}>
            <span className="donate-label">Buy</span>
            <img src={coinIcon} alt="Coin" className="coin-icon" />
          </div>
        </nav>
      </header>

      <AnimatePresence>
        {modal === "donate" && (
          <DonateModal onClose={() => setModal(null)} />
        )}
        {modal === "signup" && (
          <SignUpModal 
            onClose={() => setModal(null)} 
            onSwitchToSignIn={() => setModal("signin")} 
          />
        )}
        {modal === "signin" && (
          <SignInModal 
            onClose={() => setModal(null)} 
            onSwitchToSignUp={() => setModal("signup")} 
          />
        )}
      </AnimatePresence>
    </>
  );
}

export default Header;
