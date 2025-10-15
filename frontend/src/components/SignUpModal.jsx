import React, { useState } from "react";
import { motion } from "framer-motion";
import "../styles/SignUpAndInModal.css";
import closeIcon from "../assets/images/close-icon.png";
import bgImage from "../assets/images/stars-noised.png";
import { registerUser } from "../api/auth";

export default function SignUpModal({ onClose, onSwitchToSignIn }) {
  const [form, setForm] = useState({ username: "", email: "", password: "" });
  const [errors, setErrors] = useState({});

  const handleChange = e => {
    const { name, value } = e.target;
    setForm(prev => ({ ...prev, [name]: value }));
    setErrors(prev => ({ ...prev, [name]: null }));
  };

  const validate = () => {
    const e = {};
    if (!form.username || form.username.length < 3) {
      e.username = {
        short: "Min 3 chars",
        full: "Username must be at least 3 characters"
      };
    }
    if (!form.email || !form.email.includes("@")) {
      e.email = {
        short: "Invalid email",
        full: "Email must include '@' symbol"
      };
    }
    if (!form.password || form.password.length < 6) {
      e.password = {
        short: "Min 6 chars",
        full: "Password must be at least 6 characters"
      };
    }
    return e;
  };

  const handleSubmit = async e => {
    e.preventDefault();

    const newErrors = validate();
    if (Object.keys(newErrors).length > 0) {
      setErrors(newErrors);
      setForm(prev => ({
        ...prev,
        ...(newErrors.username ? { username: "" } : {}),
        ...(newErrors.email ? { email: "" } : {}),
        ...(newErrors.password ? { password: "" } : {})
      }));
      return;
    }

    try {
      const res = await registerUser(form);

      if (res.ok) {
        const data = await res.json();
        alert(data.message);

        localStorage.setItem("user", JSON.stringify({
          id: data.userId,
          email: data.email,
          username: data.username,  
          token: data.token
        }));

        onClose();
        window.location.reload();
      } else {
        let data;
        try {
          data = await res.json();
        } catch {
          data = { message: await res.text() };
        }

        if (data.error === "EMAIL_EXISTS") {
          if (window.confirm("This email is already registered. Do you want to sign in instead?")) {
            onSwitchToSignIn();
          }
        } else if (data.error === "USERNAME_EXISTS") {
          if (window.confirm("This username is already taken. Do you want to sign in instead?")) {
            onSwitchToSignIn();
          }
        } else {
          alert("Error: " + data.message);
        }
      }
    } catch (err) {
      console.error(err);
      alert("Server connection error");
    }
  };

  return (
    <motion.div
      className="auth-modal"
      initial={{ opacity: 0, scale: 0.9 }}
      animate={{ opacity: 1, scale: 1 }}
      exit={{ opacity: 0, scale: 0.9 }}
      transition={{ duration: 0.4 }}
    >
      <div className="auth-background" onClick={onClose} />

      <div className="auth-frame signup" style={{ backgroundImage: `url(${bgImage})` }}>
        <img
          src={closeIcon}
          alt="Close"
          className="auth-close"
          onClick={onClose}
        />

        <h2 className="auth-title">Sign up</h2>

        <form className="auth-form" onSubmit={handleSubmit} noValidate>
          <input
            name="username"
            placeholder={errors.username ? errors.username.short : "Username"}
            title={errors.username?.full || ""}
            autoComplete="username"
            value={form.username}
            onChange={handleChange}
            className={errors.username ? "input-error" : ""}
          />

          <input
            name="password"
            type="password"
            placeholder={errors.password ? errors.password.short : "Password"}
            title={errors.password?.full || ""}
            autoComplete="new-password"
            value={form.password}
            onChange={handleChange}
            className={errors.password ? "input-error" : ""}
          />

          <input
            name="email"
            type="email"
            placeholder={errors.email ? errors.email.short : "Email"}
            title={errors.email?.full || ""}
            autoComplete="email"
            value={form.email}
            onChange={handleChange}
            className={errors.email ? "input-error" : ""}
          />

          <button type="button" className="auth-google">Google</button>
          <button type="submit" className="auth-submit">Register</button>
        </form>
      </div>
    </motion.div>
  );
}
