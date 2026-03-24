import React, { useState, useEffect } from "react";
import "../styles/Header.css";
import coinIcon from "../assets/images/coin.png";
import DonateModal from "./DonateModal";
import { AnimatePresence } from "framer-motion"; 
import SignUpModal from "./SignUpModal";
import SignInModal from "./SignInModal";
import SkinsModal from "./SkinsModal";

function Header() {
  const [modal, setModal] = useState(null); 
  const [user, setUser] = useState(JSON.parse(localStorage.getItem("user")));

  useEffect(() => {
    const handler = () => {
      setUser(JSON.parse(localStorage.getItem("user")));
    };
    window.addEventListener("userUpdated", handler);
    return () => window.removeEventListener("userUpdated", handler);
  }, []);

  const handleLogout = () => {
    localStorage.removeItem("user");
    localStorage.removeItem("token");
    window.location.reload();
  };

  return (
    <>
      <header className="header">
        <nav className="nav">
          <div className="nav-button">Main</div>
          <div 
            className="nav-button" 
            onClick={() => {
              if (user && user.username) {
                setModal("skins");
              } else {
                setModal("authRequired");
              }
            }}
          >
            Skins
          </div>

          {user && user.username ? (
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
            {user && user.balance !== undefined && (
              <span className="balance-text">Balance: {user.balance}</span>
            )}
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
        {modal === "skins" && (
          <SkinsModal onClose={() => setModal(null)} />
        )}
        {modal === "authRequired" && (
          <div className="auth-required-modal">
            <div className="auth-required-content">
              <h3>Access denied</h3>
              <p>You need to sign in or register to access Skins.</p>
              <button onClick={() => setModal("signin")}>Sign in</button>
              <button onClick={() => setModal("signup")}>Sign up</button>
              <button onClick={() => setModal(null)}>Close</button>
            </div>
          </div>
        )}
      </AnimatePresence>
    </>
  );
}

export default Header;
