import { Routes, Route, Navigate } from 'react-router-dom';
import { useAuth } from './context/AuthContext';
import Layout from './components/layout/Layout';
import ProtectedRoute from './components/layout/ProtectedRoute';
import LoginPage from './pages/LoginPage';
import DashboardRouter from './pages/DashboardRouter';
import StudentsListPage from './pages/StudentsListPage';
import StudentProfilePage from './pages/StudentProfilePage';
import TeachersListPage from './pages/TeachersListPage';
import ClubsListPage from './pages/ClubsListPage';
import ClubDetailPage from './pages/ClubDetailPage';
import AchievementsPage from './pages/AchievementsPage';
import AnnouncementsPage from './pages/AnnouncementsPage';
import AuditLogsPage from './pages/AuditLogsPage';

export default function App() {
  const { isAuthenticated } = useAuth();

  return (
    <Routes>
      <Route path="/login" element={isAuthenticated ? <Navigate to="/dashboard" /> : <LoginPage />} />
      <Route path="/dashboard" element={<ProtectedRoute><Layout><DashboardRouter /></Layout></ProtectedRoute>} />
      <Route path="/students" element={<ProtectedRoute roles={['ADMIN', 'TEACHER']}><Layout><StudentsListPage /></Layout></ProtectedRoute>} />
      <Route path="/students/:prn" element={<ProtectedRoute><Layout><StudentProfilePage /></Layout></ProtectedRoute>} />
      <Route path="/teachers" element={<ProtectedRoute roles={['ADMIN']}><Layout><TeachersListPage /></Layout></ProtectedRoute>} />
      <Route path="/clubs" element={<ProtectedRoute><Layout><ClubsListPage /></Layout></ProtectedRoute>} />
      <Route path="/clubs/:id" element={<ProtectedRoute><Layout><ClubDetailPage /></Layout></ProtectedRoute>} />
      <Route path="/achievements" element={<ProtectedRoute><Layout><AchievementsPage /></Layout></ProtectedRoute>} />
      <Route path="/announcements" element={<ProtectedRoute><Layout><AnnouncementsPage /></Layout></ProtectedRoute>} />
      <Route path="/admin/audit-logs" element={<ProtectedRoute roles={['ADMIN']}><Layout><AuditLogsPage /></Layout></ProtectedRoute>} />
      <Route path="*" element={<Navigate to={isAuthenticated ? '/dashboard' : '/login'} />} />
    </Routes>
  );
}
