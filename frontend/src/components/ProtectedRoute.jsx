import { Navigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

// Wrap any route that requires a logged-in user. Pass allowedRoles to restrict
// further, e.g. <ProtectedRoute allowedRoles={["PARENT"]}>.
export default function ProtectedRoute({ children, allowedRoles }) {
  const { user } = useAuth();

  if (!user) {
    return <Navigate to="/login" replace />;
  }

  if (allowedRoles && !allowedRoles.includes(user.role)) {
    return <Navigate to="/" replace />;
  }

  return children;
}
