import axios from "axios";
import qs from "qs";

const API_BASE_URL = "http://localhost:9090";

const instance = axios.create({
    baseURL: API_BASE_URL,
});

instance.interceptors.request.use((config) => {
    const token = localStorage.getItem("jwtToken");
    if (token) {
        config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
}, (error) => Promise.reject(error));

instance.interceptors.response.use(
    (response) => response,
    (error) => {
        if (error.response && error.response.status === 401) {
            localStorage.removeItem("jwtToken");
            window.location.href = "/login";
        }
        return Promise.reject(error);
    }
);

const axiosConfig = {

    async login(credentials) {
        const response = await instance.post(
            "/api/login", 
            qs.stringify(credentials), 
            { headers: { "Content-Type": "application/x-www-form-urlencoded" } }
        );
        if (response.status === 200 && response.headers.authorization) {
            const token = response.headers.authorization.split(" ")[1];
            localStorage.setItem("jwtToken", token);
        }
        return response;
    },

    async register(user) {
        return instance.post("/api/register", user, {
            headers: { "Content-Type": "application/json" },
        });
    },

    async connect(username) {
        const formData = new FormData(); 
        formData.append('username', username);
        return instance.post("/api/home/connect",
            formData, {
            headers: { "Content-Type": "multipart/form-data" }
        });
    },

    async redirect(params) {
        const formData = new FormData(); 
        Object.entries(params).forEach(([key, value]) => formData.append(key, value)); 

        return instance.post("/api/home/redirect", formData, {
            headers: {"Content-Type": "multipart/form-data"},
        }); 
    },

    async spotifydata(username){
        const formData = new FormData(); 
        formData.append('username', username);
        return instance.post("/api/dashboard/spotify/data",
            formData, {
            headers: { "Content-Type": "multipart/form-data" }
        });
    }
    
};

export default axiosConfig;
