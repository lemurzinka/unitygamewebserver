import { useEffect } from "react";

function SuccessPage() {
  useEffect(() => {
    const user = JSON.parse(localStorage.getItem("user"));
    const token = user?.token; 

    if (!user || !token) {
      window.location.href = "/";
      return;
    }

    let attempts = 0;

    const checkBalance = () => {
      console.log("📦 User from localStorage:", user);
      console.log("📦 Token from user object:", token);

      fetch(`http://localhost:8080/api/users/${user.id}/balance`, {
        headers: { "Authorization": `Bearer ${token}` }
      })
        .then(res => {
          if (!res.ok) {
            throw new Error(`HTTP error! status: ${res.status}`);
          }
          return res.json();
        })
        .then(data => {
          console.log(`🔍 Attempt ${attempts + 1}, balance from backend:`, data.balance);

          if (Number(data.balance) > 0 || attempts >= 5) {
            user.balance = Number(data.balance);
            localStorage.setItem("user", JSON.stringify(user));
            console.log("✅ Balance updated:", user.balance);
            window.location.href = "/";
          } else {
            attempts++;
            setTimeout(checkBalance, 3000); 
          }
        })
        .catch(err => {
          console.error("❌ Error fetching balance:", err);
          window.location.href = "/";
        });
    };

    checkBalance();
  }, []);

  return <h2>Processing payment...</h2>;
}

export default SuccessPage;
