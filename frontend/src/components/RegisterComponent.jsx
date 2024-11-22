import React, { useState } from "react";
import { useNavigate, Link } from "react-router-dom";
import axiosConfig  from "../api/axiosConfig";
import "bootstrap/dist/css/bootstrap.min.css";

const RegisterComponent = () => {
    const [id, setId] = useState("");
    const [username, setUsername] = useState("");
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [secondPassword, setSecondPassword] = useState("");
    const [message, setMessage] = useState("");
    const navigate = useNavigate();

    const register = async (e) => {
        e.preventDefault();
        console.log({ username, email, password, secondPassword });
        const response = await axiosConfig.register({
            username,
            email,
            password,
            secondPassword,
        });
        setMessage(response.data);
        if (response.data === "added") {
            navigate("/login");
        }
        console.log(response.data);

        setId("");
        setUsername("");
        setEmail("");
        setPassword("");
        setSecondPassword("");
    };

    return (
        <div className="login-card">
            <div className="card-header text-center">
                <h3>Register</h3>
            </div>
            <div className="card-body">
                {message && <div className="alert alert-danger">{message}</div>}
                <form onSubmit={register}>
                    <div className="form-group mb-3">
                        <label htmlFor="username" className="form-label">Username</label>
                        <input
                            type="text"
                            className="form-control"
                            id="username"
                            placeholder="Enter username"
                            value={username}
                            onChange={(e) => setUsername(e.target.value)}
                        />
                    </div>
                    <div className="form-group mb-3">
                        <label htmlFor="email" className="form-label">Email</label>
                        <input
                            type="email"
                            className="form-control"
                            id="email"
                            placeholder="Enter email"
                            value={email}
                            onChange={(e) => setEmail(e.target.value)}
                        />
                    </div>
                    <div className="form-group mb-3">
                        <label htmlFor="password" className="form-label">Password</label>
                        <input
                            type="password"
                            className="form-control"
                            id="password"
                            placeholder="Enter password"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                        />
                    </div>
                    <div className="form-group mb-4">
                        <label htmlFor="secondPassword" className="form-label">Confirm Password</label>
                        <input
                            type="password"
                            className="form-control"
                            id="secondPassword"
                            placeholder="Confirm password"
                            value={secondPassword}
                            onChange={(e) => setSecondPassword(e.target.value)}
                        />
                    </div>
                    <button type="submit" className="btn btn-primary w-100 btn-login">
                        Register
                    </button>
                </form>
                <div className="text-center mt-3">
                    <span>Already registered? <Link to="/login" className="link-primary">Login here</Link></span>
                </div>
            </div>
        </div>
    );
};

export default RegisterComponent;
