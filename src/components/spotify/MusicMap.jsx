import { useEffect, useRef } from "react";
import L from "leaflet";
import "leaflet/dist/leaflet.css";

const MusicMap = ({ spotifyData }) => {
  const mapRef = useRef(null);

  useEffect(() => {
    if (!mapRef.current) {
      mapRef.current = L.map("map", {
        center: [39, -39],
        zoom: 2
      });

      L.tileLayer("https://tile.openstreetmap.org/{z}/{x}/{y}.png", {
        maxZoom: 23,
        attribution: '&copy; OpenStreetMap contributors',
      }).addTo(mapRef.current);

    }

    if (spotifyData) {
      fetch("https://raw.githubusercontent.com/johan/world.geo.json/master/countries.geo.json")
        .then(res => res.json())
        .then(geojsonData => {
          const countryMap = new Map();

          spotifyData.forEach(({ artist, country, img, name }) => {
            if (!countryMap.has(country)) {
              countryMap.set(country, []);
            }
            countryMap.get(country).push({ artist, img, name });
          });

          L.geoJSON(geojsonData, {
            style: (feature) => ({
              fillColor: countryMap.has(feature.id) ? "red" : "white",
              weight: 0.7,
              color: "green",
              fillOpacity: countryMap.has(feature.id) ? 0.95 : 1
            }),
            onEachFeature: (feature, layer) => {
              if (countryMap.has(feature.id)) {
                const content = countryMap.get(feature.id).map(
                  ({ artist, img, name }) => `
                  <div style="text-align: center;">
                    <h4>${artist}</h4>
                    <p>Song: ${name}</p>
                    <img src="${img}" alt="${name}" width="100px"/>
                  </div>
                `).join("<hr>");

                layer.bindPopup(`
                  <div style="max-height: 300px; overflow-y: auto;">
                    <h3>Artists from ${feature.properties.name}</h3>
                    ${content}
                  </div>
                `);
              }
            }
          }).addTo(mapRef.current);
        });
    }
  }, [spotifyData]);

  return <div id="map" style={{ height: "400px", width: "100%" }}></div>;
};

export default MusicMap;
