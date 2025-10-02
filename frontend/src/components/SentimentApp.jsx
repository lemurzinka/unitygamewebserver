import { useState } from "react";
import "../styles/SentimentApp.css"; 

function SentimentApp() {
  const [text, setText] = useState("");
  const [result, setResult] = useState(null);

  const analyze = async () => {
    const res = await fetch("http://localhost:8080/api/nlp/analyze", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ text }),
    });
    const data = await res.json();
    setResult(data);
  };

const renderMessage = () => {
  if (!result || result.length === 0) return null;

  const first = Array.isArray(result[0]) ? result[0][0] : result[0];
  if (!first || !first.label) return null;

  const label = first.label.toUpperCase();

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


  return (
    <div className="sentiment-widget">
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
