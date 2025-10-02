import React from "react";
import "./styles/app.css";
import Background from "./components/Background";
import Header from "./components/Header";
import './styles/fonts.css';
import PolygonBackground from './components/PolygonBackground';
import SentimentApp from "./components/SentimentApp";

function App() {
  return (
    <div className="app-wrapper">
      <PolygonBackground />
      <Background />
      <Header />
      <main className="main-content">
        {/* Тут вставляємо компонент */}
        <SentimentApp />
      </main>
    </div>
  );
}

export default App;
