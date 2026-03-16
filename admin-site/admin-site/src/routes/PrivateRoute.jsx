// src/routes/PrivateRoute.jsx
import { Navigate, Outlet } from 'react-router-dom';
import { useMyState } from '../context/MyContext';

export default function PrivateRoute() {
  const { isAuthenticated } = useMyState();

  if (!isAuthenticated) {
    return <Navigate to="/login" replace />;
  }

  return <Outlet />;
}
