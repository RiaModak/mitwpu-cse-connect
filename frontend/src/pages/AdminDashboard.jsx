import { useEffect } from 'react';
import { useApi } from '../hooks/useApi';
import { dashboardApi } from '../api/dashboardApi';
import TopBar from '../components/layout/TopBar';
import StatsCard from '../components/ui/StatsCard';
import LoadingSpinner from '../components/ui/LoadingSpinner';
import Badge from '../components/ui/Badge';
import { Users, GraduationCap, Building2, Trophy, Shield, Clock } from 'lucide-react';
import { BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, PieChart, Pie, Cell } from 'recharts';
import { format } from 'date-fns';

const COLORS = ['#4263eb', '#ff9800', '#4caf50', '#f44336', '#9c27b0', '#009688', '#ffc107'];

export default function AdminDashboard() {
  const { data: stats, loading, execute } = useApi(dashboardApi.getAdminStats);

  useEffect(() => { execute(); }, [execute]);

  if (loading || !stats) return <><TopBar title="Admin Dashboard" /><LoadingSpinner className="py-24" size={32} /></>;

  const yearData = stats.studentsByYear ? Object.entries(stats.studentsByYear).map(([k, v]) => ({ name: k, count: v })) : [];
  const panelData = stats.studentsByPanel ? Object.entries(stats.studentsByPanel).map(([k, v]) => ({ name: k, count: v })) : [];
  const categoryData = stats.achievementsByCategory ? Object.entries(stats.achievementsByCategory).map(([k, v]) => ({ name: k, value: v })) : [];

  return (
    <>
      <TopBar title="Admin Dashboard" />
      <div className="p-6 space-y-6">
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
          <StatsCard title="Total Students" value={stats.totalStudents || 0} icon={Users} color="navy" />
          <StatsCard title="Total Teachers" value={stats.totalTeachers || 0} icon={GraduationCap} color="teal" />
          <StatsCard title="Active Clubs" value={stats.activeClubs || 0} icon={Building2} color="accent" />
          <StatsCard title="Achievements" value={stats.totalAchievements || 0} icon={Trophy} color="success" subtitle={`${stats.pendingVerifications || 0} pending`} />
        </div>

        <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
          <div className="glass-card p-6">
            <h3 className="text-lg font-semibold text-gray-800 mb-4">Students by Year</h3>
            <ResponsiveContainer width="100%" height={250}>
              <BarChart data={yearData}>
                <CartesianGrid strokeDasharray="3 3" stroke="#f0f0f0" />
                <XAxis dataKey="name" tick={{ fontSize: 12 }} />
                <YAxis tick={{ fontSize: 12 }} />
                <Tooltip />
                <Bar dataKey="count" fill="#4263eb" radius={[6, 6, 0, 0]} />
              </BarChart>
            </ResponsiveContainer>
          </div>

          <div className="glass-card p-6">
            <h3 className="text-lg font-semibold text-gray-800 mb-4">Achievements by Category</h3>
            <ResponsiveContainer width="100%" height={250}>
              <PieChart>
                <Pie data={categoryData} cx="50%" cy="50%" outerRadius={80} dataKey="value" label={({ name, value }) => `${name}: ${value}`}>
                  {categoryData.map((_, i) => <Cell key={i} fill={COLORS[i % COLORS.length]} />)}
                </Pie>
                <Tooltip />
              </PieChart>
            </ResponsiveContainer>
          </div>
        </div>

        <div className="glass-card p-6">
          <div className="flex items-center justify-between mb-4">
            <h3 className="text-lg font-semibold text-gray-800">Recent Audit Logs</h3>
            <Badge variant="info">{stats.auditEntriesToday || 0} today</Badge>
          </div>
          <div className="space-y-3">
            {(stats.recentAuditLogs || []).map((log) => (
              <div key={log.id} className="flex items-start gap-3 p-3 rounded-xl bg-gray-50/80">
                <div className="p-2 bg-navy-100 rounded-lg"><Shield size={14} className="text-navy-600" /></div>
                <div className="flex-1 min-w-0">
                  <p className="text-sm text-gray-700">{log.description}</p>
                  <div className="flex items-center gap-2 mt-1">
                    <span className="text-xs text-gray-400">{log.actorName || 'System'}</span>
                    <span className="text-xs text-gray-300">|</span>
                    <span className="text-xs text-gray-400 flex items-center gap-1">
                      <Clock size={10} />{log.createdAt ? format(new Date(log.createdAt), 'MMM dd, HH:mm') : ''}
                    </span>
                  </div>
                </div>
                <Badge variant={log.action === 'DELETE' ? 'danger' : log.action === 'CREATE' ? 'success' : 'info'}>{log.action}</Badge>
              </div>
            ))}
          </div>
        </div>
      </div>
    </>
  );
}
