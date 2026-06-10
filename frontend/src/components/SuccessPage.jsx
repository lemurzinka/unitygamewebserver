import { useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { fetchWithAuth } from "../api/fetchWithAuth";

function SuccessPage() {
  const API_URL = process.env.REACT_APP_API_URL;
  const navigate = useNavigate();

  useEffect(() => {
    const user = JSON.parse(localStorage.getItem("user"));
    const token = user?.token; 

    if (!user || !token) {
      navigate("/");
      return;
    }

    let attempts = 0;

    const checkBalance = () => {
      fetchWithAuth(`${API_URL}/api/users/${user.id}/balance`)
        .then(res => {
          if (!res) return;
          if (!res.ok) throw new Error(`HTTP error! status: ${res.status}`);
          return res.json();
        })
        .then(data => {
          if (Number(data.balance) !== user.balance || attempts >= 5) {
            user.balance = Number(data.balance);
            localStorage.setItem("user", JSON.stringify(user));
            window.dispatchEvent(new Event("userUpdated"));

            
            navigate("/");
          } else {
            attempts++;
            setTimeout(checkBalance, 3000); 
          }
        })
        .catch(err => {
          console.error("Error fetching balance:", err);
          navigate("/");
        });
    };

    checkBalance();
  }, [API_URL, navigate]);

  return <h2>Processing payment...</h2>;
}

export default SuccessPage;
