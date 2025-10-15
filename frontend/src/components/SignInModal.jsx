import React, { useState } from "react";
import { motion } from "framer-motion";
import "../styles/SignUpAndInModal.css";
import closeIcon from "../assets/images/close-icon.png";
import bgImage from "../assets/images/stars-noised.png";
import { loginUser } from "../api/auth";

export default function SignInModal({ onClose, onSwitchToSignUp }) {
  const [form, setForm] = useState({ email: "", password: "" });
  const [errors, setErrors] = useState({});

  const handleChange = e => {
    const { name, value } = e.target;
    setForm(prev => ({ ...prev, [name]: value }));
    setErrors(prev => ({ ...prev, [name]: null }));
  };

  const validate = () => {
    const e = {};
    if (!form.email || !form.email.includes("@")) {
      e.email = { short: "Invalid email", full: "Invalid email format" };
    }
    if (!form.password || form.password.length < 6) {
      e.password = { short: "Min 6 chars", full: "Password must be at least 6 characters" };
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
        ...(newErrors.email ? { email: "" } : {}),
        ...(newErrors.password ? { password: "" } : {})
      }));
      return;
    }

    try {
      const res = await loginUser(form);
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
        try { data = await res.json(); } catch { data = { message: await res.text() }; }
        if (data.error === "USER_NOT_FOUND") {
          alert("User not found. Please sign up.");
          onSwitchToSignUp();
        } else if (data.error === "INVALID_PASSWORD") {
          setErrors({ password: { short: "Wrong pass", full: "Invalid password" } });
          setForm(prev => ({ ...prev, password: "" }));
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

      <div className="auth-frame signin" style={{ backgroundImage: `url(${bgImage})` }}>
        <img
          src={closeIcon}
          alt="Close"
          className="auth-close"
          onClick={onClose}
        />

        <h2 className="auth-title">Sign in</h2>

        <form className="auth-form" onSubmit={handleSubmit} noValidate>
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

          <input
            name="password"
            type="password"
            placeholder={errors.password ? errors.password.short : "Password"}
            title={errors.password?.full || ""}
            autoComplete="current-password"
            value={form.password}
            onChange={handleChange}
            className={errors.password ? "input-error" : ""}
          />

          <button type="button" className="auth-google">Google</button>
          <button type="submit" className="auth-submit">Sign in</button>
        </form>
      </div>
    </motion.div>
  );
}
