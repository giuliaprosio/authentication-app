import React, {useEffect, useState} from "react"; 
import axiosConfig from "../api/axiosConfig";

const HomeComponent = () => {
    const [isButtonVisible, setIsButtonVisible] = useState(true);
    const username = localStorage.getItem("username");
    const [message, setMessage] = useState(""); 

    const handleConnect = async (e) => {
        e.preventDefault(); 
        setIsButtonVisible(false);
        localStorage.setItem("isButtonVisible", "false"); 

        try{
            const response = await axiosConfig.connect(username); 
            if(response.status === 200) {
                window.location.replace(response.data); 
            }
        } catch(error) {
            setMessage(error.response?.status === 401 ? "Invalid credentials. Try again." : "An error occurred"); 
            setIsButtonVisible(true); 
            localStorage.setItem("isButtonVisible", "true");
        }
    };

    useEffect(() => {

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
                    setMessage("Top Track: " + response.data);
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
                {message && <h4 className="response-message"> {message}</h4>}
            </div>
        </div>  
    )
}

export default HomeComponent; 