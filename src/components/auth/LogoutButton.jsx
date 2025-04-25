import React from "react";
import { useNavigate } from "react-router-dom";

const LogoutButton = () => {
  const navigate = useNavigate(); 

  const handleLogout = () => {
    localStorage.clear()
    navigate("/login")
  }; 


  return (
    <button onClick={handleLogout} className="btn btn-logout">
      Logout
    </button>
  );
};

export default LogoutButton;
