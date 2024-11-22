import React, { useState } from "react";
import { useNavigate, Link } from "react-router-dom";
import axiosConfig from "../api/axiosConfig";
import "bootstrap/dist/css/bootstrap.min.css";

const LoginComponent = () => {
    const [username, setUsername] = useState(""); 
    const [password, setPassword] = useState("");
    const [message, setMessage] = useState(""); 
    const navigate = useNavigate(); 

    const handleLogin = async (e) => {
        e.preventDefault();

        try {
            const response = await axiosConfig.login({ username, password });
            if (response.status === 200) {
                navigate("/home");
            }
        } catch (error) {
            setMessage(error.response?.status === 401 ? "Invalid credentials. Try again." : "An error occurred.");
        }
    };

    return (
        <div className="login-card">
            <div className="card-header text-center">
                <h3>Login</h3>
            </div>
            <div className="card-body">
                {message && <div className="alert alert-danger">{message}</div>}
                <form onSubmit={handleLogin}>
                    <div className="form-group mb-3">
                        <label htmlFor="username" className="form-label">Username</label>
                        <input
                            type="text"
                            className="form-control"
                            id="username"
                            placeholder="Enter username"
                            value={username}
                            onChange={(e) => setUsername(e.target.value)}
                            required
                        />
                    </div>
                    <div className="form-group mb-4">
                        <label htmlFor="password" className="form-label">Password</label>
                        <input
                            type="password"
                            className="form-control"
                            id="password"
                            placeholder="Enter password"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                            required
                        />
                    </div>
                    <button type="submit" className="btn btn-primary w-100 btn-login">
                        Login
                    </button>
                </form>
                <div className="text-center mt-3">
                    <span>Not registered? <Link to="/register" className="link-primary">Register here</Link></span>
                </div>
            </div>
        </div>
    );
};

export default LoginComponent;
