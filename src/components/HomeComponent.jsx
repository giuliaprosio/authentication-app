import React, {useEffect, useState} from "react"; 
import { useNavigate, Link } from "react-router-dom";
import axiosConfig from "../api/axiosConfig";


const HomeComponent = () => {
    const [isButtonVisible, setIsButtonVisible] = useState(true);
    const username = localStorage.getItem("username");
    const [message, setMessage] = useState(""); 
    const [imageUrl, setImageUrl] = useState("");
    const navigate = useNavigate();

    const handleConnect = async (e) => {
        e.preventDefault(); 
        setIsButtonVisible(false);
        localStorage.setItem("isButtonVisible", "false"); 
        try{
            const response = await axiosConfig.connect(username); 
            console.log("username ", username, "hello")
            if(response.status === 200) { 
                console.log(response)
                window.location.replace(response.data); 
            }else if (response.status === 204) {
                navigate("home/spotify/data", {
                    state: {
                        username: username,
                    }
                });
            }else{
                console.log("error"); 
            }
        } catch(error) {
            console.log(error)
            setMessage(error.response?.status === 401 ? "Invalid credentials. Try again." : "An error occurred"); 
            setIsButtonVisible(true); 
            localStorage.setItem("isButtonVisible", "true");
        }
    };

    useEffect(() => {
        const queryParams = new URLSearchParams(window.location.search);
        const code = queryParams.get("code");
        const state = queryParams.get("state");

        if (!code || !state) {
            return;
        }
        const handleRedirect = async () => {
            
            const queryParams = new URLSearchParams(window.location.search); 
            if(!queryParams.toString()) {
                return; 
            }
            const params = {}; 
            queryParams.forEach((value, key) => {
                params[key] = value; 
            }); 
    
            try{
                const response = await axiosConfig.redirect(params); 
                if (response.status === 200) {
                    navigate("home/spotify/data", {
                        state: {
                            username: username,
                        }
                    }); 
                }
            } catch (error) {
                setMessage("An error occurred while processing the redirect.");
            }
        }
    handleRedirect();
    }, []); 
    
    return (
        <div className="home-card">

            <div className="card-header text-center">
                <h2>Hello, {username}!</h2>
                <h3>Analytics Dashboard</h3>
                <button className="btn btn-connect" id="connectToSpotifty" onClick={handleConnect}>
                    <img src="src/img/spotify-logo-png-7069.png" alt="Logo" />
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
    )
}

export default HomeComponent; 