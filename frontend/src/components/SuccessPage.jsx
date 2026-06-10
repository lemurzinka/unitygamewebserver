import { useEffect } from "react";
import { fetchWithAuth } from "../api/fetchWithAuth";

function SuccessPage() {
  const API_URL = process.env.REACT_APP_API_URL;
  useEffect(() => {
    const user = JSON.parse(localStorage.getItem("user"));
    const token = user?.token; 

    if (!user || !token) {
      window.location.href = "/";
      return;
    }

    let attempts = 0;

    const checkBalance = () => {
      console.log("User from localStorage:", user);
      console.log("Token from user object:", token);

      fetchWithAuth(`${API_URL}/api/users/${user.id}/balance`)
        .then(res => {
          if (!res) return;
          if (!res.ok) {
            throw new Error(`HTTP error! status: ${res.status}`);
          }
          return res.json();
        })
        .then(data => {
          console.log(`Attempt ${attempts + 1}, balance from backend:`, data.balance);

          if (Number(data.balance) !== user.balance || attempts >= 5) {
  user.balance = Number(data.balance);
  localStorage.setItem("user", JSON.stringify(user));
  console.log("Balance updated:", user.balance);

  window.dispatchEvent(new Event("userUpdated"));

  
  setTimeout(() => {
    window.location.href = "/";
  }, 5000);
}
 else {
            attempts++;
            setTimeout(checkBalance, 3000); 
          }
        })
        .catch(err => {
          console.error("Error fetching balance:", err);
          window.location.href = "/";
        });
    };

    checkBalance();
  }, []);

  return <h2>Processing payment...</h2>;
}

export default SuccessPage;
