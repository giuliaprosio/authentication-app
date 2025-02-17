import React, {useEffect, useState, useRef} from "react";
import { useLocation } from "react-router-dom";
import L from "leaflet";
import "leaflet/dist/leaflet.css";

import axiosConfig from "../api/axiosConfig";

const SpotifyCountriesComponent = () => {

    const location = useLocation(); 

    let username = location.state.username;

    const [message, setMessage] = useState(""); 
    const [spotifyData, setSpotifyData] = useState(null); 
    const mapRef = useRef(null);

    useEffect(() => {
    
        const handleSpotifyAnalytics = async () => {
            try{
                const response = await axiosConfig.spotifydata(username); 
                console.log("username ", username, "hello")
                if(response.status === 200) {
                  console.log(response.data); 
                  setSpotifyData(response.data); 
                }else{
                    console.log("error"); 
                }
            } catch(error) {
                setMessage(error.response?.status === 401 ? "Invalid credentials. Try again." : "An error occurred"); 
            }
        }   

        handleSpotifyAnalytics(); 
    }, [username]); 


    useEffect(() => {
        if (!mapRef.current) {
            mapRef.current = L.map("map", {
                center: [35, -35],
                zoom: 3
            });

            L.tileLayer("https://tile.openstreetmap.org/{z}/{x}/{y}.png", {
                maxZoom: 19,
                attribution: '&copy; <a href="http://www.openstreetmap.org/copyright">OpenStreetMap</a>',
            }).addTo(mapRef.current);

            setTimeout(() => {
                mapRef.current.invalidateSize();
            }, 500); 
        }

        if(spotifyData){

            fetch("https://raw.githubusercontent.com/johan/world.geo.json/master/countries.geo.json")
            .then((res) => res.json())
            .then((geojsonData) => {
                const countryDataMap = new Map();
        
                spotifyData.forEach(({ artist, country, img, name }) => {
                    if (!countryDataMap.has(country)) {
                        countryDataMap.set(country, []);
                    }
                    countryDataMap.get(country).push({ artist, img, name });
                });
        
                L.geoJSON(geojsonData, {
                    style: (feature) => ({
                        fillColor: countryDataMap.has(feature.id) ? "red" : "white",
                        weight: 0.5,
                        color: "green",
                        fillOpacity: countryDataMap.has(feature.id) ? 0.7 : 0.3
                    }),
                    onEachFeature: (feature, layer) => {
                        if (countryDataMap.has(feature.id)) {
                            const artistsData = countryDataMap.get(feature.id);
        
                            const popupContent = artistsData.map(({ artist, img, name }) => `
                                <div style="text-align: center; margin-bottom: 10px;">
                                    <h4>${artist}</h4>
                                    <p>Song: ${name}</p>
                                    <img src="${img}" alt="${name}" width="100px" />
                                </div>
                            `).join("<hr>"); 
        
                            layer.bindPopup(`
                                <div style="max-height: 300px; overflow-y: auto; text-align: center;">
                                    <h3>Artists from ${feature.properties.name}</h3>
                                    ${popupContent}
                                </div>
                            `);
                        }
                    }
                }).addTo(mapRef.current);
            })
            .catch((error) => console.error("Error loading GeoJSON:", error));
    }
}, [spotifyData]);

    return (
        <div className="home-card">
            <div className="card-header text-center">
                <h2>Hello, {username}!</h2>
                <h3>Top 10 Songs</h3>
            </div>
            
            <div id="map" style={{ height: "500px", width: "100%" }}></div>

            {!spotifyData && (
                <div className="spinner-overlay">
                    <div className="spinner"></div>
                </div>
            )}

        </div>
    );
};
   

export default SpotifyCountriesComponent; 