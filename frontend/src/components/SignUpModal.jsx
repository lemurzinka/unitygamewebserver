import React, { useState } from "react";
import { motion } from "framer-motion";
import "../styles/SignUpModal.css";
import closeIcon from "../assets/images/close-icon.png";
import bgImage from "../assets/images/stars-noised.png";
import { registerUser } from "../api/auth"; 

export default function SignUpModal({ onClose }) {
  const [form, setForm] = useState({ username: "", email: "", password: "" });

  const handleChange = e => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const handleSubmit = async e => {
    e.preventDefault();

    try {
      const res = await registerUser(form);

      if (res.ok) {
        alert("Registration is successful!");
        onClose();
      } else {
        const text = await res.text();
        alert("Error: " + text);
      }
    } catch (err) {
      console.error(err);
      alert("Server connection error");
    }
  };

  return (
    <motion.div
      className="signup-modal"
      initial={{ opacity: 0, scale: 0.9 }}
      animate={{ opacity: 1, scale: 1 }}
      exit={{ opacity: 0, scale: 0.9 }}
      transition={{ duration: 0.4 }}
    >
      <div className="signup-background" onClick={onClose} />

      <div className="signup-frame" style={{ backgroundImage: `url(${bgImage})` }}>
        <img
          src={closeIcon}
          alt="Close"
          className="signup-close"
          onClick={onClose}
        />

        <h2 className="signup-title">Sign up</h2>

        <form className="signup-form" onSubmit={handleSubmit}>
          <input
            name="username"
            placeholder="Username"
            autoComplete="username"
            required
            onChange={handleChange}
          />
          <input
            name="password"
            type="password"
            placeholder="Password"
            autoComplete="new-password"
            required
            minLength={6}
            onChange={handleChange}
          />
          <input
            name="email"
            type="email"
            placeholder="Email"
            autoComplete="email"
            required
            onChange={handleChange}
          />

          <button type="button" className="signup-google">Google</button>
          <button type="submit" className="signup-register">Register</button>
        </form>
      </div>
    </motion.div>
  );
}
