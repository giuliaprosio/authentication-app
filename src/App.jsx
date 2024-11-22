import React, { useState } from "react";
import { BrowserRouter as Router, Route, Routes } from "react-router-dom";
import "bootstrap/dist/css/bootstrap.min.css";
import "./App.css";  
import ProxyRequestComponent from "./components/ProxyRequestComponent";

function App() {
    const [isLightMode, setIsLightMode] = useState(false);

    const toggleLightMode = () => setIsLightMode(!isLightMode);

    return (
        <div className={`App ${isLightMode ? "light-mode" : "dark-mode"}`}>
            <Router>
                <button onClick={toggleLightMode} className="btn-toggle-mode">
                    Switch to {isLightMode ? "Dark" : "Light"} Mode
                </button>
                <Routes>
                    <Route path="/*" element={<ProxyRequestComponent />} />
                </Routes>
            </Router>
        </div>
    );
}

export default App;
