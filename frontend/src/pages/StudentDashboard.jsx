import { useEffect } from 'react';
import { useApi } from '../hooks/useApi';
import { dashboardApi } from '../api/dashboardApi';
import TopBar from '../components/layout/TopBar';
import StatsCard from '../components/ui/StatsCard';
import LoadingSpinner from '../components/ui/LoadingSpinner';
import Badge from '../components/ui/Badge';
import { Trophy, Building2, BookOpen, TrendingUp, Megaphone } from 'lucide-react';
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer } from 'recharts';
import { format } from 'date-fns';

export default function StudentDashboard() {
  const { data: stats, loading, execute } = useApi(dashboardApi.getStudentStats);

  useEffect(() => { execute(); }, [execute]);

  if (loading || !stats) return <><TopBar title="My Dashboard" /><LoadingSpinner className="py-24" size={32} /></>;

  const cgpaData = (stats.academicRecords || []).slice().reverse().map((r) => ({
    name: `Sem ${r.semester}`,
    sgpa: r.sgpa,
    cgpa: r.cgpa,
  }));

  return (
    <>
      <TopBar title="My Dashboard" />
      <div className="p-6 space-y-6">
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
          <StatsCard title="Current CGPA" value={stats.cgpa || '-'} icon={TrendingUp} color="navy" />
          <StatsCard title="Attendance" value={`${stats.attendancePercent || 0}%`} icon={BookOpen} color="teal" />
          <StatsCard title="Active Clubs" value={stats.activeClubsCount || 0} icon={Building2} color="accent" />
          <StatsCard title="Achievements" value={stats.achievementsCount || 0} icon={Trophy} color="success" subtitle={`${stats.verifiedAchievementsCount || 0} verified`} />
        </div>

        {cgpaData.length > 0 && (
          <div className="glass-card p-6">
            <h3 className="text-lg font-semibold text-gray-800 mb-4">Academic Progress</h3>
            <ResponsiveContainer width="100%" height={250}>
              <LineChart data={cgpaData}>
                <CartesianGrid strokeDasharray="3 3" stroke="#f0f0f0" />
                <XAxis dataKey="name" tick={{ fontSize: 12 }} />
                <YAxis domain={[0, 10]} tick={{ fontSize: 12 }} />
                <Tooltip />
                <Line type="monotone" dataKey="sgpa" stroke="#ff9800" strokeWidth={2} dot={{ r: 4 }} name="SGPA" />
                <Line type="monotone" dataKey="cgpa" stroke="#4263eb" strokeWidth={2} dot={{ r: 4 }} name="CGPA" />
              </LineChart>
            </ResponsiveContainer>
          </div>
        )}

        <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
          <div className="glass-card p-6">
            <h3 className="text-lg font-semibold text-gray-800 mb-4">My Clubs</h3>
            {(stats.currentClubs || []).length === 0 ? (
              <p className="text-gray-400 text-sm">Not a member of any club yet.</p>
            ) : (
              <div className="space-y-3">
                {(stats.currentClubs || []).map((c) => (
                  <div key={c.id} className="flex items-center justify-between p-3 rounded-xl bg-gray-50">
                    <div>
                      <p className="font-medium text-gray-800">{c.clubName}</p>
                      <p className="text-xs text-gray-400">{c.startYear}</p>
                    </div>
                    <Badge variant={c.role === 'PRESIDENT' ? 'purple' : c.role === 'SECRETARY' ? 'accent' : 'info'}>{c.role}</Badge>
                  </div>
                ))}
              </div>
            )}
          </div>

          <div className="glass-card p-6">
            <h3 className="text-lg font-semibold text-gray-800 mb-4">Recent Announcements</h3>
            {(stats.recentAnnouncements || []).length === 0 ? (
              <p className="text-gray-400 text-sm">No announcements.</p>
            ) : (
              <div className="space-y-3">
                {(stats.recentAnnouncements || []).map((a) => (
                  <div key={a.id} className="p-3 rounded-xl bg-gray-50">
                    <div className="flex items-start justify-between">
                      <p className="font-medium text-gray-800 text-sm">{a.title}</p>
                      {a.isPinned && <Badge variant="warning">Pinned</Badge>}
                    </div>
                    <p className="text-xs text-gray-500 mt-1 line-clamp-2">{a.body}</p>
                    <p className="text-xs text-gray-400 mt-2">
                      {a.postedByName} | {a.createdAt ? format(new Date(a.createdAt), 'MMM dd, yyyy') : ''}
                    </p>
                  </div>
                ))}
              </div>
            )}
          </div>
        </div>
      </div>
    </>
  );
}
