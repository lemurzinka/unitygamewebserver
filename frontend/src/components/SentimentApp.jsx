import { useState } from "react";
import "../styles/SentimentApp.css"; 
import { fetchWithAuth } from "../api/fetchWithAuth";

function SentimentApp() {
   const API_URL = process.env.REACT_APP_API_URL;
  const [text, setText] = useState("");
  const [result, setResult] = useState(null);
  const [visible, setVisible] = useState(true);
  const [isClosing, setIsClosing] = useState(false);
  const [loading, setLoading] = useState(false); 

  const analyze = async () => {
    setLoading(true); 
    try {
const user = JSON.parse(localStorage.getItem("user"));
const token = user?.token;

const fetchFn = token ? fetchWithAuth : fetch;

const res = await fetchFn(`${API_URL}/api/nlp/analyze`, {
  method: "POST",
  headers: { "Content-Type": "application/json" },
  body: JSON.stringify({ text }),
});


if (!res) return;

      if (!res.ok) {
        const text = await res.text();
        console.error("NLP error:", res.status, text);
        return;
      }

      const data = await res.json();
      setResult(data);
    } catch (err) {
      console.error("Error analyzing text:", err.message);
    } finally {
      setLoading(false); 
    }
  };

  const handleClose = () => {
    setIsClosing(true); 
    setTimeout(() => setVisible(false), 400); 
  };

const renderMessage = () => {
  if (!result || !result.label) return null;

  const label = result.label.toUpperCase();

  if (label === "POSITIVE") {
    return (
      <div className="result-card positive">
        <p>Thank you for your feedback!</p>
      </div>
    );
  } else if (label === "NEGATIVE") {
    return (
      <div className="result-card negative">
        <p>We have taken your wishes</p>
      </div>
    );
  } else if (label === "NON_ENGLISH") {
    return (
      <div className="result-card warning">
        <p>Please write feedback in English only</p>
      </div>
    );
  }
};


  if (!visible) return null;

  return (
    <div className={`sentiment-widget ${isClosing ? "fade-out" : ""}`}>
      <button className="close-btn" onClick={handleClose}>×</button>
 <h2 className="title">Send feedback</h2>
<p className="subtitle">Your feedback is anonymous</p>
      <textarea
        className="input-box"
        placeholder="Type something..."
        value={text}
        onChange={(e) => setText(e.target.value)}
      />
      <button className="analyze-btn" onClick={analyze} disabled={loading}>
        {loading ? "Analyzing..." : "Analyze"}
      </button>
      {loading && <div className="loading-spinner"></div>} 
      {renderMessage()}
    </div>
  );
}

export default SentimentApp;
