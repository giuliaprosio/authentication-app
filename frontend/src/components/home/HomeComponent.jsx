import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import axiosConfig from "../../api/axiosConfig";
import { useAuthRedirect } from "../../hooks/useAuthRedirect";
import LogoutButton from "../auth/LogoutButton";

const HomeComponent = () => {
    const username = localStorage.getItem("username");
    const [message, setMessage] = useState("");
    const [imageUrl, setImageUrl] = useState("");
    const navigate = useNavigate();

    const handleConnect = async (e) => {
        e.preventDefault();

        try {
            const response = await axiosConfig.connect(username);
            if (response.status === 200) {
                window.location.replace(response.data);
            } else if (response.status === 204) {
                navigate("/dashboard/spotify/data", {
                    state: {
                        username: username,
                    }
                });
            }
        } catch (error) {
            console.log(error)
            setMessage(error.response);
        }
    };

    useAuthRedirect({ username, navigate, setMessage });

    return (
        <div>
        <div className="home-card">

            <div className="card-header text-center">
                <h2>Hello, {username}!</h2>
                <h3>Analytics Dashboard</h3>

                <button className="btn btn-connect" id="connectToSpotifty" onClick={handleConnect}>
                    <img src="./img/spotify-logo-png-7069.png" alt="Logo" />
                    &emsp;Connect to Spotify</button>

                {message && (
                    <div>
                        <h4 className="response-message">{message}</h4>
                    </div>
                )}
                {imageUrl && (
                    <div>
                        <img src={imageUrl} alt="Fetched Content" />
                    </div>
                )}
            </div>
        </div>
        <LogoutButton />
        </div>
    )
}

export default HomeComponent; 