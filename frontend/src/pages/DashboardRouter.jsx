import { useAuth } from '../context/AuthContext';
import AdminDashboard from './AdminDashboard';
import TeacherDashboard from './TeacherDashboard';
import StudentDashboard from './StudentDashboard';

export default function DashboardRouter() {
  const { isAdmin, isTeacher } = useAuth();

  if (isAdmin) return <AdminDashboard />;
  if (isTeacher) return <TeacherDashboard />;
  return <StudentDashboard />;
}
