import React, { useEffect, useState } from "react";
import { Bar } from "react-chartjs-2";
import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  BarElement,
  Title,
  Tooltip,
  Legend,
} from "chart.js";
import "../styles/Dashboard.css";

ChartJS.register(CategoryScale, LinearScale, BarElement, Title, Tooltip, Legend);

function Dashboard() {
  const [chartData, setChartData] = useState(null);

  useEffect(() => {
    fetch("http://localhost:8080/api/logs/logins")
      .then((res) => res.json())
      .then((data) => {
        setChartData({
          labels: data.labels,
          datasets: [
            {
              label: "Logins per Day",
              data: data.data,
              backgroundColor: "rgba(83, 41, 182, 0.7)",
              borderRadius: 6,
            },
          ],
        });
      });
  }, []);

  if (!chartData) return <p className="loading-text">Loading chart...</p>;

  return (
    <div className="dashboard-widget">
      <div className="dashboard-content">
        <div className="chart-section">
          <h2 className="dashboard-title">📊 User Activity</h2>
          <Bar 
            data={chartData} 
            options={{
              responsive: true,
              plugins: {
                legend: { labels: { color: "#fff" } },
                title: {
                  display: true,
                  text: "Logins per Day",
                  color: "#fff",
                  font: { size: 18 }
                }
              },
              scales: {
                x: {
                  ticks: { color: "#aaa" },
                  grid: { color: "rgba(255,255,255,0.1)" }
                },
                y: {
                  beginAtZero: true,
                  ticks: {
                    stepSize: 1,
                    precision: 0,
                    color: "#aaa"
                  },
                  suggestedMax: Math.max(...chartData.datasets[0].data) + 1, 
                  grid: { color: "rgba(255,255,255,0.1)" }
                }
              }
            }} 
          />
        </div>

        <div className="dashboard-note">
          <h3 className="note-title">💜 Thank You!</h3>
            <p>
    We truly appreciate every single one of you who logs in day after day, showing up not just as players, 
    but as the heartbeat of this entire world we are building together. Every click, every login, every moment 
    you spend with us is proof that what we are creating matters. You are not just users — you are pioneers, 
    dreamers, and the driving force that keeps this universe alive.
  </p>

  <p>
    Your daily presence is more than a statistic on a chart — it’s a spark that fuels our passion, a reminder 
    that behind every number there is a real person who believes in what we do. And because of you, we refuse 
    to settle for “good enough.” We push harder, we dream bigger, and we build stronger.
  </p>

  <p>
    Every time you log in, you’re not only enjoying the game — you’re shaping its future. You’re part of a 
    community that grows stronger with every sunrise, a family that celebrates victories together and overcomes 
    challenges side by side. Your activity is the proof that this project is alive, breathing, and unstoppable.
  </p>

  <p>
    So here’s our promise: we will keep improving, keep innovating, and keep surprising you. We will transform 
    every ounce of your support into features that excite you, worlds that inspire you, and experiences that 
    stay with you long after you log out.
  </p>

  <p>
    You are the reason we wake up with fire in our hearts and ambition in our minds. Together, we are not just 
    building a game — we are building a legacy. And this is only the beginning. 🚀🔥🌍
  </p>
        </div>
      </div>
    </div>
  );
}

export default Dashboard;
