import { useEffect } from "react";
import axiosConfig from "../api/axiosConfig";

export const useAuthRedirect = ({ username, navigate, setMessage }) => {
    useEffect(() => {
        const queryParams = new URLSearchParams(window.location.search);
        const code = queryParams.get("code");
        const state = queryParams.get("state");

        if (!code || !state) {
            return;
        }
        
        const handleRedirect = async () => {

            const params = {};
            queryParams.forEach((value, key) => {
                params[key] = value;
            });

            try {
                const response = await axiosConfig.redirect(params);
                if (response.status === 200) {
                    navigate("/dashboard/spotify/data", {
                        state: { username }
                    });
                }
            } catch (error) {
                setMessage("An error occurred while processing the redirect.");
            }
        }
        handleRedirect();
    }, [navigate, setMessage, username]); 
}; 