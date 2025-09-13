import { useEffect, useState } from "react";
import { useNavigate, useLocation } from "react-router-dom";
//import LogoutButton from "../auth/LogoutButton";
import axiosConfig from "../../api/axiosConfig";
import SpotifyMap from "./MusicMap";
import BarChartComponent from "./BarChart";
import SongCards from "./SongCards";
import "./Spotify.css"; 

const SpotifyCountriesComponent = () => {
  const location = useLocation();
  const navigate = useNavigate();
  const username = location?.state?.username || "";

  const [spotifyData, setSpotifyData] = useState(null);

  useEffect(() => {
    const fetchData = async () => {
      try {
        const response = await axiosConfig.spotifydata(username);
        if (response.status === 200) {
          setSpotifyData(response.data);
        } else {
          navigate("/login");
        }
      } catch (err) {
        navigate("/login");
      }
    };

    fetchData();
  }, [username, navigate]);

  return (
    <div>
                <div className="home-card">
        <div className="card-header text-center">
            <h2>Hello, {username}!</h2>
            <h3>Here some stats on your top 10 listens of the month</h3>
        </div>

        <div className="content-grid">
            <div className="map-container">
                <SpotifyMap spotifyData={spotifyData} />
            </div>
            <div className="chart-container">
                <BarChartComponent spotifyData={spotifyData} />
            </div>
            <div className="cards-container">
                <SongCards spotifyData={spotifyData} />
            </div>
       
        </div>
    </div>
          

    </div>
                
  );
};

export default SpotifyCountriesComponent;
