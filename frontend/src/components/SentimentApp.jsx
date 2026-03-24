import { useState } from "react";
import "../styles/SentimentApp.css"; 

function SentimentApp() {
  const [text, setText] = useState("");
  const [result, setResult] = useState(null);
  const [visible, setVisible] = useState(true);
  const [isClosing, setIsClosing] = useState(false);

  const analyze = async () => {
const res = await fetch("http://localhost:8080/api/nlp/analyze", {
  method: "POST",
  headers: { "Content-Type": "application/json" },
  body: JSON.stringify({ text }),
});

if (!res.ok) {
  const text = await res.text();
  console.error("❌ NLP error:", res.status, text);
  return;
}

const data = await res.json();
setResult(data);

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
        <p>😊 Thank you for your feedback!</p>
      </div>
    );
  } else {
    return (
      <div className="result-card negative">
        <p>🙏 We have taken your wishes</p>
      </div>
    );
  }
};


  if (!visible) return null;

  return (
    <div className={`sentiment-widget ${isClosing ? "fade-out" : ""}`}>
      <button className="close-btn" onClick={handleClose}>×</button>
      <h2 className="title">Send feedback</h2>
      <textarea
        className="input-box"
        placeholder="Type something..."
        value={text}
        onChange={(e) => setText(e.target.value)}
      />
      <button className="analyze-btn" onClick={analyze}>
        Analyze
      </button>
      {renderMessage()}
    </div>
  );
}

export default SentimentApp;
