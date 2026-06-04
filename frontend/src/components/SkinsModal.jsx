import React, { useState, useEffect } from "react";
import { motion } from "framer-motion";
import "../styles/SkinsModal.css";
import binIcon from "../assets/images/bin.png";
import { fetchWithAuth } from "../api/fetchWithAuth";


function SkinsModal({ onClose }) {
  const [filter, setFilter] = useState("All");
  const [skins, setSkins] = useState([]);
  const [showForm, setShowForm] = useState(false);
  const [newSkin, setNewSkin] = useState({ name: "", rarity: "Common", price: "", file: null });
  const [selectedSkin, setSelectedSkin] = useState(null);
  const [user, setUser] = useState(JSON.parse(localStorage.getItem("user")));

  useEffect(() => {
  const token = user?.token;
  if (!token) return;

 
  Promise.all([
    fetchWithAuth("https://unitygamewebserver.onrender.com/api/users/me")
  .then(res => {
    if (!res) return;
    if (!res.ok) throw new Error(`HTTP ${res.status}`);
    return res.json();
  }),
    fetchWithAuth("https://unitygamewebserver.onrender.com/api/skins").then(res => {
    if (!res) return;
    if (!res.ok) throw new Error(`HTTP ${res.status}`);
    return res.json();
  })
  ])
    .then(([userData, skinsData]) => {
      const updatedUser = { ...user, ...userData, token };
      localStorage.setItem("user", JSON.stringify(updatedUser));
      setUser(updatedUser);

      const selectedId = updatedUser.selectedSkinId;
      const ownedIds = updatedUser.ownedSkinIds || [];

      const updatedSkins = skinsData.map(s => ({
        ...s,
        owned: s.price === 0 || ownedIds.includes(s.skinId),
        selected: selectedId === s.skinId
      }));

      setSkins(updatedSkins);
    })
    .catch(err => console.error("Error fetching user/skins:", err));
}, []);



  const filteredSkins = filter === "All" ? skins : skins.filter(s => s.rarity === filter);

  const handleBuySkin = async (skin) => {
    const token = user?.token;
    if (!token) {
      console.error("No token found. User must be logged in.");
      return;
    }

    try {
      const res = await fetchWithAuth(`https://unitygamewebserver.onrender.com/api/skins/${skin.skinId}/buy`, {
        method: "POST",

      });
if (!res) return; 
      if (!res.ok) {
        const text = await res.text();
        throw new Error(`HTTP ${res.status}: ${text}`);
      }

      const data = await res.json();
      if (data.success) {
  const updatedUser = { ...user, balance: data.newBalance };
  localStorage.setItem("user", JSON.stringify(updatedUser));
  setUser(updatedUser);

  const updatedSkin = { ...skin, owned: true };
  setSkins(skins.map(s => s.skinId === skin.skinId ? updatedSkin : s));
  setSelectedSkin(updatedSkin);

  window.dispatchEvent(new Event("userUpdated"));
}


    } catch (err) {
      console.error("Error buying skin:", err.message);
    }
  };
  const fetchUserFromBackend = async (token) => {
  try {
    const res = await fetchWithAuth("https://unitygamewebserver.onrender.com/api/users/me");
    if (!res) return; 
    if (!res.ok) throw new Error(`HTTP ${res.status}`);
    const data = await res.json();
    const updatedUser = { ...user, ...data, token }; 
    localStorage.setItem("user", JSON.stringify(updatedUser));
    setUser(updatedUser);
    return updatedUser;
  } catch (err) {
    console.error("Error fetching user:", err.message);
    return null;
  }
};


 const handleSelectSkin = async (skin) => {
  const token = user?.token;
  if (!token) {
    console.error("No token found. User must be logged in.");
    return;
  }

  if (!skin.owned) {
    console.warn("Cannot select skin you don't own.");
    return;
  }

  try {
    const res = await fetchWithAuth(`https://unitygamewebserver.onrender.com/api/skins/${skin.skinId}/select`, {
      method: "POST",
    });
if (!res) return; 
    if (!res.ok) {
      const text = await res.text();
      throw new Error(`HTTP ${res.status}: ${text}`);
    }

    const data = await res.json();
    if (data.success) {
      const updatedUser = await fetchUserFromBackend(token);
      if (!updatedUser) return;

      setSkins(skins.map(s => ({
        ...s,
        selected: s.skinId === skin.skinId
      })));
      setSelectedSkin({ ...skin, selected: true });

     
      window.dispatchEvent(new Event("userUpdated"));
    }
  } catch (err) {
    console.error("Error selecting skin:", err.message);
  }
};



const [deleting, setDeleting] = useState(false);

const handleDeleteSkin = async (skinId) => {
  setDeleting(true); 
  try {
    const res = await fetchWithAuth(`https://unitygamewebserver.onrender.com/api/skins/${skinId}`, {
      method: "DELETE",
    });
    if (!res) return;
if (!res.ok) {
  const text = await res.text();
  throw new Error(`HTTP ${res.status}: ${text}`);
}
const data = await res.json();

    if (data.success) {
      
      setSkins(skins.filter(s => s.skinId !== skinId));
     
      setSelectedSkin(null);
    } else {
      console.error("Error deleting skin:", data.error);
    }
  } catch (err) {
    console.error("Error deleting skin:", err);
  } finally {
    setDeleting(false); 
  }
};


  const handleCloseForm = () => {
    setShowForm(false);
    setNewSkin({ name: "", rarity: "Common", price: "", file: null });
  };

  const handleImageUpload = (e) => {
    const file = e.target.files[0];
    if (file) {
      setNewSkin({ ...newSkin, file });
    }
  };

  const handleAddSkin = async (e) => {
    e.preventDefault();
    if (!newSkin.file || !newSkin.name) return;

    const skinRarity = filter === "All" ? newSkin.rarity : filter;

    const formData = new FormData();
    formData.append("file", newSkin.file);
    formData.append("name", newSkin.name);
    formData.append("rarity", skinRarity);
    formData.append("price", newSkin.price || 0);

    try {
      const res = await fetchWithAuth("https://unitygamewebserver.onrender.com/api/skins/upload", {
        method: "POST",
        body: formData
      });
      if (!res) return;
if (!res.ok) {
  const text = await res.text();
  throw new Error(`HTTP ${res.status}: ${text}`);
}
      const savedSkin = await res.json();
      setSkins([...skins, {
        ...savedSkin,
        owned: savedSkin.price === 0,
        selected: false
      }]);
      setNewSkin({ name: "", rarity: "Common", price: "", file: null });
      setShowForm(false);
    } catch (err) {
      console.error("Error saving skin:", err);
    }
  };

  return (
    <motion.div className="skins-overlay" initial={{ opacity: 0 }} animate={{ opacity: 1 }} exit={{ opacity: 0 }}>
      <motion.div className="skins-frame" initial={{ scale: 0.9 }} animate={{ scale: 1 }} exit={{ scale: 0.9 }} transition={{ duration: 0.3 }}>
        <button className="skins-close" onClick={onClose}></button>
        <h1 className="skins-title">Choose Your Skin</h1>

        <div className="skins-body">
          <div className="skins-sidebar">
            <h3>Rarity:</h3>
            {["All", "Common", "Rare", "Epic", "Legendary"].map(r => (
              <button key={r} className={`rarity-btn ${filter === r ? "active" : ""}`} onClick={() => setFilter(r)}>
                {r}
              </button>
            ))}
          </div>

          <div className="skins-main">
            <div className="skins-content">
              {filteredSkins.map(skin => (
                <div
                  key={skin.skinId}
                  className={`skin-card compact ${skin.selected ? "active-skin" : ""} ${skin.owned ? "owned-skin" : ""}`}
                  onClick={() => setSelectedSkin(skin)}
                >
                  <div className="skin-img-wrapper spinning">
                    <img src={`https://unitygamewebserver.onrender.com/api/skins/${skin.skinId}/image`} alt={skin.name} className="skin-img" />
                  </div>
                </div>
              ))}
              {user?.isAdmin && (
  <div className="skin-card add-card" onClick={() => {
    setNewSkin({ ...newSkin, rarity: filter === "All" ? "Common" : filter });
    setShowForm(true);
  }}>
    <div className="plus-icon">+</div>
  </div>
)}

            </div>
          </div>
        </div>
      </motion.div>

      {showForm && (
        <motion.div className="add-form-overlay" initial={{ opacity: 0 }} animate={{ opacity: 1 }} exit={{ opacity: 0 }}>
          <motion.div className="add-form" initial={{ scale: 0.9 }} animate={{ scale: 1 }} exit={{ scale: 0.9 }} transition={{ duration: 0.3 }}>
            <button className="skins-close" onClick={handleCloseForm}>×</button>
            <h3>Add New Skin</h3>
            <form onSubmit={handleAddSkin}>
              <input type="text" placeholder="Skin name" value={newSkin.name} onChange={(e) => setNewSkin({ ...newSkin, name: e.target.value })} required />
              <input type="number" placeholder="Price (coins)" value={newSkin.price} onChange={(e) => setNewSkin({ ...newSkin, price: e.target.value })} />
              <select value={newSkin.rarity} onChange={(e) => setNewSkin({ ...newSkin, rarity: e.target.value })} disabled={filter !== "All"}>
                <option>Common</option>
                <option>Rare</option>
                <option>Epic</option>
                <option>Legendary</option>
              </select>
              <input type="file" accept="image/*" onChange={handleImageUpload} />
              {newSkin.file && (
                <div className="preview-circle">
                  <img src={URL.createObjectURL(newSkin.file)} alt="preview" />
                </div>
              )}
              <button type="submit" className="save-btn">Save</button>
            </form>
          </motion.div>
        </motion.div>
      )}

           {selectedSkin && (
        <motion.div className="skin-detail-overlay" initial={{ opacity: 0 }} animate={{ opacity: 1 }} exit={{ opacity: 0 }}>
          <motion.div className="skin-detail" initial={{ scale: 0.9 }} animate={{ scale: 1 }} exit={{ scale: 0.9 }}>
            <button className="close-inside" onClick={() => setSelectedSkin(null)}>×</button>
            <div className="skin-img-wrapper">
              <img
                src={`https://unitygamewebserver.onrender.com/api/skins/${selectedSkin.skinId}/image`}
                alt={selectedSkin.name}
                className="skin-img"
              />
            </div>
            <h4>{selectedSkin.name}</h4>
            <p className="rarity">{selectedSkin.rarity}</p>
            <p className="price">{selectedSkin.price ? `${selectedSkin.price} coins` : "Free"}</p>

{user?.isAdmin && (
  <button 
    className="delete-btn" 
    onClick={() => handleDeleteSkin(selectedSkin.skinId)} 
    disabled={deleting}
  >
    {deleting ? "Deleting..." : <img src={binIcon} alt="Delete" className="delete-icon" />}
  </button>
)}

      
            {selectedSkin.owned ? (
              selectedSkin.selected ? (
                <button className="selected-btn" disabled>Selected</button>
              ) : (
                <button className="select-btn" onClick={() => handleSelectSkin(selectedSkin)}>Select</button>
              )
            ) : (
              <button
                className="buy-btn"
                onClick={() => handleBuySkin(selectedSkin)}
                disabled={user?.balance < selectedSkin.price}
              >
                Buy
              </button>
            )}
          </motion.div>
        </motion.div>
      )}
    </motion.div>
  );
}

export default SkinsModal;