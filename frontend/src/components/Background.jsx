import React, { useEffect, useState } from "react";
import "../styles/Background.css";
import bgStars from "../assets/images/stars.jpg";
import Dashboard from "../components/Dashboard";
import { fetchWithAuth } from "../api/fetchWithAuth";

function Background() {
  const [selectedSkinId, setSelectedSkinId] = useState(null);

useEffect(() => {
  const user = JSON.parse(localStorage.getItem("user"));
  const token = user?.token;
fetchWithAuth("https://unitygamewebserver.onrender.com/api/users/me")
  .then(res => {
    if (!res) return; 
    if (!res.ok) {
      return res.text().then(text => {
        throw new Error(`HTTP ${res.status}: ${text}`);
      });
    }
    return res.json();
  })
  .then(data => {
    if (!data) return;
    setSelectedSkinId(data?.selectedSkinId || null);
  })
  .catch(err => console.error("❌ Error fetching user:", err));


  const handler = () => {
    const updatedUser = JSON.parse(localStorage.getItem("user"));
    setSelectedSkinId(updatedUser?.selectedSkinId || null);
  };
  window.addEventListener("userUpdated", handler);
  return () => window.removeEventListener("userUpdated", handler);
}, []);

  return (
    <div className="background">
      <img src={bgStars} alt="" className="bg-layer blur top" />
      <img src={bgStars} alt="" className="bg-layer top" />
      <img src={bgStars} alt="" className="bg-layer blur bottom" />
      <img src={bgStars} alt="" className="bg-layer bottom" />

      {selectedSkinId ? (
        <div
          className="moon skin-circle"
          style={{
            backgroundImage: `url(https://unitygamewebserver.onrender.com/api/skins/${selectedSkinId}/image)`
          }}
        ></div>
      ) : (
        <div className="moon"></div> 
      )}

      <Dashboard />
    </div>
  );
}

export default Background;
