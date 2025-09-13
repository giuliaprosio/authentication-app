import React, {createContext, useContext, useState, useEffect } from "react";
import { Navigate } from "react-router-dom"

const AuthContext = createContext(); 

export const AuthProvider = ({ children }) => {
    const [user, setUser] = useState(null); 

    useEffect(() => {
        const token = localStorage.getItem('jwt'); 
        if(token){
            const storedUser = localStorage.getItem("username"); 
            setUser(storedUser); 
        } 
    }, []);

    const userIsAuthenticated = () => {
        let token = localStorage.getItem('jwt')

        return !!token; 
    }

    const userLogout = () => {
        localStorage.clear();
        setUser(null);
        <Navigate path="/login"/>
    }

    const contextValue = {
        user, 
        userIsAuthenticated, 
        userLogout
    }

    return (
        <AuthContext.Provider value={contextValue}>
            {children}
        </AuthContext.Provider>
    )
}

export const useAuth = () => useContext(AuthContext);

