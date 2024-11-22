import React, { useEffect, useState } from "react";
import { useNavigate, useLocation } from "react-router-dom";
import HomeComponent from "./HomeComponent";
import LoginComponent from "./LoginComponent";
import RegisterComponent from "./RegisterComponent";
import axiosConfig from "../api/axiosConfig";

const ProxyRequestComponent = () => {
    const [component, setComponent] = useState(null);
    const navigate = useNavigate();
    const location = useLocation();

    const componentMap = {
        home: <HomeComponent />,
        login: <LoginComponent />,
        register: <RegisterComponent />
    };

    useEffect(() => {
        const fetchData = async () => {
            try {
                const response = await axiosConfig.request(location.pathname);

                if (typeof response.data === "string" && componentMap[response.data]) {
                    setComponent(componentMap[response.data]);
                } else {
                    navigate("/index.html");
                }
            } catch (error) {
                if (error.response && error.response.status === 404) {
                    navigate("/index.html");
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
