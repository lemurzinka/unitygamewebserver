import React, { useEffect, useState } from "react";
import "./styles/app.css";
import Background from "./components/Background";
import Header from "./components/Header";
import './styles/fonts.css';
import PolygonBackground from './components/PolygonBackground';
import SentimentApp from "./components/SentimentApp";
import ProjectTitle from "./components/ProjectTitle";
import Polygon from "./components/Polygon";
import CurrentSkin from "./components/CurrentSkin";
import SkinName from "./components/SkinName";
import Footer from "./components/Footer";
import SuccessPage from "./components/SuccessPage";

function App() {
  const [selectedSkinName, setSelectedSkinName] = useState(null);


  const updateSelectedSkinName = () => {
    const user = JSON.parse(localStorage.getItem("user"));
    const token = user?.token;
    if (!token) return;

    fetch("http://localhost:8080/api/users/me", {
      headers: { "Authorization": `Bearer ${token}` }
    })
      .then(res => res.json())
      .then(data => {
        if (data.selectedSkinId) {
          fetch("http://localhost:8080/api/skins")
            .then(res => res.json())
            .then(skins => {
              const skin = skins.find(s => s.skinId === data.selectedSkinId);
              if (skin) setSelectedSkinName(skin.name);
            });
        } else {
          setSelectedSkinName(null);
        }
      })
      .catch(err => console.error("❌ Error loading selected skin:", err));
  };

  useEffect(() => {
    
    updateSelectedSkinName();

 
    const handler = () => updateSelectedSkinName();
    window.addEventListener("userUpdated", handler);

    return () => window.removeEventListener("userUpdated", handler);
  }, []);

  const path = window.location.pathname;
  if (path === "/success") {
    return <SuccessPage />;
  }

  return (
    <div className="app-wrapper">
      <PolygonBackground />
      <Background />
      <Polygon />
      <ProjectTitle />
      <Header />
      <CurrentSkin />
      <SkinName name={selectedSkinName || "No skin selected"} />
      <SentimentApp />
      <Footer />
    </div>
  );
}

export default App;
