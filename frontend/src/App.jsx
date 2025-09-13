import React from "react";
import "./styles/app.css";
import Background from "./components/Background";
import Header from "./components/Header";
import './styles/fonts.css';
import PolygonBackground from './components/PolygonBackground';

function App() {
  return (
    <div className="app-wrapper">
      <PolygonBackground />
      <Background />
      <Header />
      <main className="main-content">
        {/* Контент */}
      </main>
    </div>
  );
}
export default App;
