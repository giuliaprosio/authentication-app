import React, { useState } from "react";
import { BrowserRouter as Router, Route, Routes, Navigate } from "react-router-dom";
import "bootstrap/dist/css/bootstrap.min.css";
import "./App.css";  
import LoginComponent from "./components/auth/LoginComponent";
import RegisterComponent from "./components/auth/RegisterComponent";
import HomeComponent from "./components/home/HomeComponent";
import SpotifyCountriesComponent from "./components/spotify/SpotifyCountriesComponent";
import PrivateRoute from "./components/common/PrivateRoute";
import { AuthProvider } from "./context/AuthContext";

function App() {
    const [isLightMode, setIsLightMode] = useState(false);

    const toggleLightMode = () => setIsLightMode(!isLightMode);

    return (
        <div className={`App ${isLightMode ? "light-mode" : "dark-mode"}`}>
            <AuthProvider>
            <Router>
                <div id="buttons">
                    <button onClick={toggleLightMode} className="btn-toggle-mode">
                        Switch to {isLightMode ? "Dark" : "Light"} Mode
                    </button>
                </div>
                
                <Routes>
                    <Route path="/" element={<LoginComponent />} />
                    <Route path="/login" element={<LoginComponent />} />
                    <Route path="/register" element={<RegisterComponent />} />
                    <Route path="/home" element={<PrivateRoute><HomeComponent/></PrivateRoute>} />
                    <Route path="/dashboard/spotify/data" element={<PrivateRoute><SpotifyCountriesComponent/></PrivateRoute>} />
                    <Route path="*" element={ <Navigate to="/"/>} />
                </Routes>
            </Router>
            </AuthProvider>   
        </div>
    );
}

export default App;
