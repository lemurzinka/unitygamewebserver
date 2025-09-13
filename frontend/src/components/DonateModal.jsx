import React from "react";
import "../styles/DonateModal.css";
import closeIcon from "../assets/images/close-icon.png";
import coinIcon from "../assets/images/coin.png";
import { motion } from "framer-motion";

function DonateModal({ onClose }) {
  return (
    <motion.div
      className="donate-modal"
      initial={{ opacity: 0, scale: 0.9 }}
      animate={{ opacity: 1, scale: 1 }}
      exit={{ opacity: 0, scale: 0.9 }}
      transition={{ duration: 0.4 }}
    >
      <div className="donate-background" />
      <div className="donate-frame">
        <img
          src={closeIcon}
          alt="Close"
          className="donate-close"
          onClick={onClose}
        />
        <h1 className="donate-title">How many coins will we get?</h1>

        <div className="donate-sections">
  
          {/* 50 Coins */}
<div className="donate-card">
  <div className="donate-content">
    <div className="donate-coins-stack">
      <img src={coinIcon} alt="coin" className="coin coin-0" />
    </div>
    <div className="donate-amount">50 Coins</div>
    <div className="donate-price">$ 5.00</div>
  </div>
  <div className="donate-buy-wrapper">
    <div className="donate-buy-bg" />
    <span className="donate-buy-text">BUY</span>
  </div>
</div>
          {/* 100 Coins */}
          <div className="donate-card">
            <div className="donate-content">
              <div className="donate-coins-stack">
                <img src={coinIcon} alt="coin" className="coin coin-1" />
                <img src={coinIcon} alt="coin" className="coin coin-2" />
              </div>
              <div className="donate-amount">100 Coins</div>
              <div className="donate-price">$ 10.00</div>
            </div>
            <div className="donate-buy-wrapper">
              <div className="donate-buy-bg" />
              <span className="donate-buy-text">BUY</span>
            </div>
          </div>

          {/* 200 Coins */}
          <div className="donate-card">
            <div className="donate-content">
              <div className="donate-coins-stack">
                <img src={coinIcon} alt="coin" className="coin coin-1" />
                <img src={coinIcon} alt="coin" className="coin coin-2" />
                <img src={coinIcon} alt="coin" className="coin coin-3" />
                <img src={coinIcon} alt="coin" className="coin coin-4" />
              </div>
              <div className="donate-amount">200 Coins</div>
              <div className="donate-price">$ 20.00</div>
            </div>
            <div className="donate-buy-wrapper">
              <div className="donate-buy-bg" />
              <span className="donate-buy-text">BUY</span>
            </div>
          </div>
        </div>
      </div>
    </motion.div>
  );
}

export default DonateModal;
