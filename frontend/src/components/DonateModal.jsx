import React, { useCallback, useState } from "react";
import "../styles/DonateModal.css";
import closeIcon from "../assets/images/close-icon.png";
import coinIcon from "../assets/images/coin.png";
import { motion } from "framer-motion";

const packages = [
  { id: "price_1SI4ngFQqejofOUdvOz1Z8TU", coins: 50, price: "$5.00", stack: 1 },
  { id: "price_1SI4pCFQqejofOUdcQvZEed4", coins: 100, price: "$10.00", stack: 2 },
  { id: "price_1SI4pfFQqejofOUdXda56y5L", coins: 200, price: "$20.00", stack: 4 }
];

function DonateModal({ onClose }) {
  const [loadingId, setLoadingId] = useState(null);

  const handleBuy = useCallback(async (priceId) => {
    try {
      setLoadingId(priceId);
      const res = await fetch("http://localhost:8080/stripe/create-checkout-session", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ priceId })
      });
      const data = await res.json();
      if (data.url) {
        window.location.href = data.url;
      }
    } catch (err) {
      console.error("Error creating checkout session:", err);
    } finally {
      setLoadingId(null);
    }
  }, []);

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
          {packages.map((pkg) => (
            <div key={pkg.id} className="donate-card">
              <div className="donate-content">
                <div className="donate-coins-stack">
                  {[...Array(pkg.stack)].map((_, i) => (
                    <img
                      key={i}
                      src={coinIcon}
                      alt="coin"
                      className={`coin coin-${i}`}
                      loading="lazy"
                    />
                  ))}
                </div>
                <div className="donate-amount">{pkg.coins} Coins</div>
                <div className="donate-price">{pkg.price}</div>
              </div>
              <div
                className="donate-buy-wrapper"
                onClick={() => handleBuy(pkg.id)}
              >
                <div className="donate-buy-bg" />
                <span className="donate-buy-text">
                  {loadingId === pkg.id ? "Processing..." : "BUY"}
                </span>
              </div>
            </div>
          ))}
        </div>
      </div>
    </motion.div>
  );
}

export default DonateModal;
