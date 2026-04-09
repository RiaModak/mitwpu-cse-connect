import { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useApi } from '../hooks/useApi';
import { dashboardApi } from '../api/dashboardApi';
import TopBar from '../components/layout/TopBar';
import StatsCard from '../components/ui/StatsCard';
import LoadingSpinner from '../components/ui/LoadingSpinner';
import Badge from '../components/ui/Badge';
import { Users, Trophy, Megaphone } from 'lucide-react';

export default function TeacherDashboard() {
  const { data: stats, loading, execute } = useApi(dashboardApi.getTeacherStats);
  const navigate = useNavigate();

  useEffect(() => { execute(); }, [execute]);

  if (loading || !stats) return <><TopBar title="Teacher Dashboard" /><LoadingSpinner className="py-24" size={32} /></>;

  return (
    <>
      <TopBar title="Teacher Dashboard" />
      <div className="p-6 space-y-6">
        <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
          <StatsCard title="Panel Students" value={stats.totalStudentsInPanel || 0} icon={Users} color="navy" />
          <StatsCard title="Pending Verifications" value={stats.pendingVerifications || 0} icon={Trophy} color="warning" />
          <StatsCard title="Announcements Posted" value={stats.announcementsPosted || 0} icon={Megaphone} color="accent" />
        </div>

        <div className="glass-card p-6">
          <h3 className="text-lg font-semibold text-gray-800 mb-4">My Panel Students</h3>
          {(stats.panelStudents || []).length === 0 ? (
            <p className="text-gray-400 text-sm">No students assigned yet.</p>
          ) : (
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-3">
              {(stats.panelStudents || []).slice(0, 9).map((s) => (
                <div
                  key={s.id}
                  onClick={() => navigate(`/students/${s.prn}`)}
                  className="p-4 rounded-xl bg-gray-50 hover:bg-navy-50 cursor-pointer transition-colors"
                >
                  <p className="font-medium text-gray-800">{s.fullName}</p>
                  <div className="flex items-center gap-2 mt-1">
                    <span className="text-xs text-gray-500">PRN: {s.prn}</span>
                    <Badge variant="info">Panel {s.panel}</Badge>
                  </div>
                  <div className="flex items-center gap-3 mt-2 text-xs text-gray-400">
                    <span>CGPA: {s.cgpa || '-'}</span>
                    <span>Attendance: {s.attendancePercent || '-'}%</span>
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>
      </div>
    </>
  );
}
