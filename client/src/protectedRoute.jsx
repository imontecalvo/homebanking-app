import { Navigate, Outlet } from "react-router-dom";
// import { useAuth } from "./context/authContext";

export const ProtectedRoute = () => {
  const token = localStorage.getItem("token");

  if (!token) return <Navigate to="/login" replace />;
  return <Outlet />;

//   if (loading) return <h1>Loading...</h1>;
//   if (!isAuthenticated && !loading) return <Navigate to="/login" replace />;
//   return <Outlet />;
};


export const Landing = () => {
  const token = localStorage.getItem("token");

  if (!token) return <Navigate to="/login" replace />;
  return <Navigate to="/home" replace />
}