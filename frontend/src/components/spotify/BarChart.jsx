import { ResponsiveContainer, BarChart, Bar, XAxis, YAxis, Tooltip, CartesianGrid } from "recharts";
import prepareCountryBarData from "./prepareCountryBarData";

const BarChartComponent = ({ spotifyData }) => {
  if (!spotifyData) return null;

  const chartData = prepareCountryBarData(spotifyData);

  return (
    <div style={{ width: "100%", height: "400px", marginTop: "2rem" }}>
      <ResponsiveContainer width="100%" height="100%">
        <BarChart data={chartData} margin={{ top: 20, right: 30, left: 0, bottom: 5 }}>
          <CartesianGrid strokeDasharray="3 3" />
          <XAxis dataKey="country" />
          <YAxis allowDecimals={false} />
          <Tooltip />
          <Bar dataKey="count" fill="#8884d8" />
        </BarChart>
      </ResponsiveContainer>
    </div>
  );
};

export default BarChartComponent;
