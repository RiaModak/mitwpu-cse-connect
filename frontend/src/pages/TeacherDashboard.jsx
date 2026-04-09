import { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useApi } from '../hooks/useApi';
import { dashboardApi } from '../api/dashboardApi';
import { clubApi } from '../api/clubApi';
import TopBar from '../components/layout/TopBar';
import StatsCard from '../components/ui/StatsCard';
import LoadingSpinner from '../components/ui/LoadingSpinner';
import Badge from '../components/ui/Badge';
import toast from 'react-hot-toast';
import { Users, Trophy, Megaphone, Building2, CheckCircle, XCircle } from 'lucide-react';
import { format } from 'date-fns';

export default function TeacherDashboard() {
  const { data: stats, loading, execute } = useApi(dashboardApi.getTeacherStats);
  const navigate = useNavigate();

  useEffect(() => { execute(); }, [execute]);

  const handleApprove = async (requestId) => {
    try {
      await clubApi.approveJoinRequest(requestId);
      toast.success('Join request approved!');
      execute();
    } catch (err) {
      toast.error(err.response?.data?.message || 'Failed to approve');
    }
  };

  const handleReject = async (requestId) => {
    try {
      await clubApi.rejectJoinRequest(requestId);
      toast.success('Join request rejected');
      execute();
    } catch (err) {
      toast.error(err.response?.data?.message || 'Failed to reject');
    }
  };

  if (loading || !stats) return <><TopBar title="Teacher Dashboard" /><LoadingSpinner className="py-24" size={32} /></>;

  return (
    <>
      <TopBar title="Teacher Dashboard" />
      <div className="p-6 space-y-6">
        <div className="grid grid-cols-1 sm:grid-cols-4 gap-4">
          <StatsCard title="Panel Students" value={stats.totalStudentsInPanel || 0} icon={Users} color="navy" />
          <StatsCard title="Pending Verifications" value={stats.pendingVerifications || 0} icon={Trophy} color="warning" />
          <StatsCard title="Club Join Requests" value={stats.pendingJoinRequests || 0} icon={Building2} color="teal" />
          <StatsCard title="Announcements Posted" value={stats.announcementsPosted || 0} icon={Megaphone} color="accent" />
        </div>

        {(stats.joinRequests || []).length > 0 && (
          <div className="glass-card p-6">
            <h3 className="text-lg font-semibold text-gray-800 mb-4 flex items-center gap-2">
              <Building2 size={18} /> Pending Club Join Requests
              <Badge variant="warning">{stats.joinRequests.length}</Badge>
            </h3>
            <div className="space-y-3">
              {(stats.joinRequests || []).map((req) => (
                <div key={req.id} className="flex items-center justify-between p-4 rounded-xl bg-gray-50">
                  <div>
                    <p className="font-medium text-gray-800">{req.studentName}</p>
                    <p className="text-xs text-gray-500">PRN: {req.studentPrn} | Panel {req.studentPanel} | Year {req.studentYear}</p>
                    <p className="text-sm text-gray-600 mt-1">
                      Wants to join <span className="font-medium text-navy-600">{req.clubName}</span>
                      <span className="text-xs text-gray-400 ml-2">({req.clubCategory})</span>
                    </p>
                    <p className="text-xs text-gray-400 mt-1">
                      Requested: {req.createdAt ? format(new Date(req.createdAt), 'MMM dd, yyyy HH:mm') : ''}
                    </p>
                  </div>
                  <div className="flex gap-2">
                    <button
                      onClick={() => handleApprove(req.id)}
                      className="btn-primary text-sm py-2 px-4 flex items-center gap-1"
                    >
                      <CheckCircle size={14} /> Approve
                    </button>
                    <button
                      onClick={() => handleReject(req.id)}
                      className="btn-danger text-sm py-2 px-4 flex items-center gap-1"
                    >
                      <XCircle size={14} /> Reject
                    </button>
                  </div>
                </div>
              ))}
            </div>
          </div>
        )}

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
