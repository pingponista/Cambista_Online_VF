import React from 'react';
import { Routes, Route, Navigate } from 'react-router-dom';
import { Calculator } from '../features/calculator/Calculator';
import { ExchangeFlow } from '../features/exchange/ExchangeFlow';
import { LoginForm } from '../features/auth/LoginForm';
import { RegisterForm } from '../features/auth/RegisterForm';
import { UserDashboard } from '../features/dashboard/UserDashboard';
import { AboutPage } from '../features/about/AboutPage';
import { useAuthStore } from '../store/useAuthStore';

const ProtectedRoute = ({ children }) => {
  const { isAuthenticated } = useAuthStore();
  return isAuthenticated ? children : <Navigate to="/login" replace />;
};

export const AppRoutes = () => {
  const { isAuthenticated } = useAuthStore();

  return (
    <Routes>
      <Route
        path="/"
        element={
          <ProtectedRoute>
            <Calculator />
          </ProtectedRoute>
        }
      />
      <Route
        path="/exchange"
        element={
          <ProtectedRoute>
            <ExchangeFlow />
          </ProtectedRoute>
        }
      />
      <Route path="/nosotros" element={<AboutPage />} />
      <Route path="/empresas" element={<Navigate to="/register" replace />} />
      <Route path="/login" element={<LoginForm />} />
      <Route path="/register" element={<RegisterForm />} />
      <Route
        path="/dashboard"
        element={
          <ProtectedRoute>
            <UserDashboard />
          </ProtectedRoute>
        }
      />
      <Route path="*" element={<Navigate to={isAuthenticated ? "/" : "/login"} replace />} />
    </Routes>
  );
};
