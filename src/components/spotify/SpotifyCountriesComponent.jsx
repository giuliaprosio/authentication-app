import React, {useEffect, useState, useRef} from "react";
import { useNavigate, useLocation } from "react-router-dom";
import { Tab, Tabs, TabList, TabPanel } from 'react-tabs';
import { BarChart, Bar, XAxis, YAxis, Tooltip, CartesianGrid, ResponsiveContainer } from 'recharts';
import 'react-tabs/style/react-tabs.css';
import L from "leaflet";
import "leaflet/dist/leaflet.css";
import LogoutButton from "../auth/LogoutButton";


import axiosConfig from "../../api/axiosConfig";

const prepareCountryBarData = (data) => {
    const countryCount = {};
    data.forEach(({ country }) => {
        if(country !== "") {
            console.log(country); 
            countryCount[country] = (countryCount[country] || 0) + 1;
        }
    });
  
    return Object.entries(countryCount).map(([country, count]) => ({
      country,
      count,
    }));
  };

const SpotifyCountriesComponent = () => {

    const location = useLocation(); 

    let username = location.state.username;

    const [message, setMessage] = useState(""); 
    const [spotifyData, setSpotifyData] = useState(null); 
    const mapRef = useRef(null);
    const navigate = useNavigate();

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
                    navigate("/login");
                }
            } catch(error) {
                setMessage(error.response?.status === 401 ? "Invalid credentials. Try again." : "An error occurred"); 
                navigate("/login");
            }
        }   

        handleSpotifyAnalytics(); 
    }, [username]); 


    useEffect(() => {
        if (!mapRef.current) {
            mapRef.current = L.map("map", {
                center: [39, -39],
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
                        weight: 0.7,
                        color: "green",
                        fillOpacity: countryDataMap.has(feature.id) ? 0.95 : 1
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
        <div>
        <div className="home-card">
            <div className="card-header text-center">
                <h2>Hello, {username}!</h2>
                <h3>Top 10 Songs</h3>
            </div>

            <div style={{ gridArea: 'map' }}>
            <div id="map" style={{ height: "100%", width: "100%" }}></div>

            {!spotifyData && (
                <div className="spinner-overlay">
                    <div className="spinner"></div>
                </div>
            )}
            </div>


            <div style={{ gridArea: 'chart' }}>
            <div id="graph" style={{ width: '100%', height: '85%', marginTop: '2rem' }}>
                {spotifyData && (
                    <ResponsiveContainer width="100%" height="100%">
                    <BarChart data={prepareCountryBarData(spotifyData)} margin={{ top: 20, right: 30, left: 0, bottom: 5 }}>
                        <CartesianGrid strokeDasharray="3 3" />
                        <XAxis dataKey="country" />
                        <YAxis allowDecimals={false} />
                        <Tooltip />
                        <Bar dataKey="count" fill="#8884d8" />
                    </BarChart>
                    </ResponsiveContainer>
                )}
                </div>
            </div>

            <div style={{ gridArea: 'cards' }}>
            <div id="song-cards" style={{ display: 'flex', flexWrap: 'wrap', gap: '1rem' }}>
  {spotifyData && spotifyData.map(({ name, artist, img, album }, index) => (
            <div 
            key={index} 
            style={{
                border: '1px solid #ccc',
                borderRadius: '10px',
                padding: '1rem',
                width: '130px',
                textAlign: 'center',
                boxShadow: '0 2px 5px rgba(0,0,0,0.1)'
            }}
            >
            <img 
                src={img} 
                alt={`${album} cover`} 
                style={{ width: '100%', borderRadius: '8px', marginBottom: '0.5rem' }} 
            />
            <h4 style={{ margin: '0.5rem 0' }}>{name}</h4>
            <p style={{ margin: 0 }}><strong>Artist:</strong> {artist}</p>
            </div>
        ))}
        </div> 
            </div>   
        </div>
        <LogoutButton />  
        </div>
    );
};
   

export default SpotifyCountriesComponent; 