import axios from "axios";
import qs from "qs";

const API_BASE_URL = "http://localhost:8080";

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

    async request(endpoint, method = "get", data = {}) {
        const contentType = method === "post" ? "application/json" : "application/x-www-form-urlencoded";
        return instance({
            url: "/api" + endpoint,
            method,
            data: contentType === "application/json" ? data : qs.stringify(data),
            headers: { "Content-Type": contentType },
        });
    },
};

export default axiosConfig;
