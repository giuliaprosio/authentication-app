import React, { useEffect, useState } from "react";
import { useNavigate, useLocation } from "react-router-dom";
import HomeComponent from "./HomeComponent";
import LoginComponent from "./LoginComponent";
import RegisterComponent from "./RegisterComponent";
import SpotifyCountriesComponent from "./SpotifyCountriesComponent"; 
import axiosConfig from "../api/axiosConfig";

const ProxyRequestComponent = () => {
    const [component, setComponent] = useState(null);
    const navigate = useNavigate();
    const location = useLocation();

    const componentMap = {
        home: <HomeComponent />,
        login: <LoginComponent />,
        register: <RegisterComponent />,
        "home/spotify/data": <SpotifyCountriesComponent/>
        };

    useEffect(() => {

        const fetchData = async () => {
            try {
                let uri = location.pathname; 
                const response = await axiosConfig.request(uri);
                
                if (typeof response.data === "string" && componentMap[response.data]) {
                    setComponent(componentMap[response.data]);
                } else {
                    navigate("/login");
                }
            } catch (error) {
                if (error.response && error.response.status === 404) {
                    navigate("/login");
                } else {
                    setComponent(<div>An error occurred: {error.message}</div>);
                }
            }
        };

        fetchData();
    }, [location, navigate]);

    return (
        <div>
            {component ? component : <p>Loading...</p>}
        </div>
    );
};

export default ProxyRequestComponent;
