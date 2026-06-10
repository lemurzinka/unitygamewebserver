import React, { useEffect, useState } from "react";
import "./styles/app.css";
import Background from "./components/Background";
import Header from "./components/Header";
import "./styles/fonts.css";
import PolygonBackground from "./components/PolygonBackground";
import SentimentApp from "./components/SentimentApp";
import ProjectTitle from "./components/ProjectTitle";
import Polygon from "./components/Polygon";
import CurrentSkin from "./components/CurrentSkin";
import SkinName from "./components/SkinName";
import Footer from "./components/Footer";
import SuccessPage from "./components/SuccessPage";
import SystemMessageModal from "./components/SystemMessageModal";
import SignInModal from "./components/SignInModal";
import SignUpModal from "./components/SignUpModal";
import { BrowserRouter, Routes, Route } from "react-router-dom";

function App() {
  const API_URL = process.env.REACT_APP_API_URL;

  console.log("API_URL:", API_URL);
  console.log("Google Client ID:", process.env.REACT_APP_GOOGLE_CLIENT_ID);

  const [isSignInOpen, setIsSignInOpen] = useState(false);
  const [isSignUpOpen, setIsSignUpOpen] = useState(false);
  const [selectedSkinName, setSelectedSkinName] = useState(null);
  const [isHeaderOpen, setIsHeaderOpen] = useState(false);
  const [sessionExpired, setSessionExpired] = useState(false);

  useEffect(() => {
    const handler = () => setSessionExpired(true);
    window.addEventListener("sessionExpired", handler);
    return () => window.removeEventListener("sessionExpired", handler);
  }, []);

  const updateSelectedSkinName = () => {
    const user = JSON.parse(localStorage.getItem("user"));
    const token = user?.token;

    if (!user || !token) {
      setSelectedSkinName(null);
      return;
    }

    fetch(`${API_URL}/api/users/me`, {
      headers: { Authorization: `Bearer ${token}` },
    })
      .then((res) => {
        if (!res.ok) throw new Error(`HTTP ${res.status}`);
        return res.json();
      })
      .then((data) => {
        if (data.selectedSkinId) {
          fetch(`${API_URL}/api/skins`)
            .then((res) => {
              if (!res.ok) throw new Error(`HTTP ${res.status}`);
              return res.json();
            })
            .then((skins) => {
              const skin = skins.find((s) => s.skinId === data.selectedSkinId);
              if (skin) setSelectedSkinName(skin.name);
            });
        } else {
          setSelectedSkinName(null);
        }
      })
      .catch((err) => console.error("Error loading selected skin:", err));
  };

  useEffect(() => {
    const user = JSON.parse(localStorage.getItem("user"));
    if (user?.token) {
      updateSelectedSkinName();
      const handler = () => updateSelectedSkinName();
      window.addEventListener("userUpdated", handler);
      return () => window.removeEventListener("userUpdated", handler);
    }
  }, []);

  const MainPage = () => (
    <div className="app-wrapper">
      <PolygonBackground />
      <Background />
      <Polygon />
      <ProjectTitle />
      <Header />

      {sessionExpired && (
        <SystemMessageModal
          title="Session expired"
          message="Your session has ended. Please log in again."
          onClose={() => {
            setSessionExpired(false);
            localStorage.removeItem("user");
            window.location.href = "/login";
          }}
        />
      )}

      <button className="menu-toggle" onClick={() => setIsHeaderOpen(true)}>
        ☰
      </button>

      {isHeaderOpen && (
        <div className="header-overlay">
          <Header />
          <button className="menu-close" onClick={() => setIsHeaderOpen(false)}>
            ✕
          </button>
        </div>
      )}

      <CurrentSkin />
      <SkinName name={selectedSkinName || "No skin selected"} />
      <SentimentApp />
      <Footer />

      {isSignInOpen && (
        <SignInModal
          onClose={() => setIsSignInOpen(false)}
          onSwitchToSignUp={() => {
            setIsSignInOpen(false);
            setIsSignUpOpen(true);
          }}
        />
      )}

      {isSignUpOpen && (
        <SignUpModal
          onClose={() => setIsSignUpOpen(false)}
          onSwitchToSignIn={() => {
            setIsSignUpOpen(false);
            setIsSignInOpen(true);
          }}
        />
      )}
    </div>
  );

  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<MainPage />} />
        <Route path="/success" element={<SuccessPage />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;
