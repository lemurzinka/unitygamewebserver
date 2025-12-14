import React, { useEffect, useState } from "react";
import "../styles/Background.css";
import bgStars from "../assets/images/stars.jpg";
import Dashboard from "../components/Dashboard";

function Background() {
  const [selectedSkinId, setSelectedSkinId] = useState(null);

useEffect(() => {
  const user = JSON.parse(localStorage.getItem("user"));
  const token = user?.token;
  if (token) {
    fetch("http://localhost:8080/api/users/me", {
      headers: { "Authorization": `Bearer ${token}` }
    })
      .then(res => res.json())
      .then(data => setSelectedSkinId(data.selectedSkinId || null));
  }

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
            backgroundImage: `url(http://localhost:8080/api/skins/${selectedSkinId}/image)`
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
