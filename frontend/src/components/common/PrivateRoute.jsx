import { Navigate } from "react-router-dom";
import { useAuth } from "../../context/AuthContext"; 

function PrivateRoute({ children }) {
    const { userIsAuthenticated } = useAuth(); 
    return userIsAuthenticated() ? children : <Navigate to="/login" replace />; 
  }

export default PrivateRoute