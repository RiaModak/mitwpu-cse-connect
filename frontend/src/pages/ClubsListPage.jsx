import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useApi } from '../hooks/useApi';
import { clubApi } from '../api/clubApi';
import { studentApi } from '../api/studentApi';
import { useAuth } from '../context/AuthContext';
import TopBar from '../components/layout/TopBar';
import LoadingSpinner from '../components/ui/LoadingSpinner';
import Badge from '../components/ui/Badge';
import Modal from '../components/ui/Modal';
import SearchInput from '../components/ui/SearchInput';
import toast from 'react-hot-toast';
import { useForm } from 'react-hook-form';
import { Plus, Users, Crown, UserPlus, Clock, CheckCircle } from 'lucide-react';

export default function ClubsListPage() {
  const { isAdmin, isStudent, user } = useAuth();
  const navigate = useNavigate();
  const { data: clubs, loading, execute } = useApi(clubApi.getAll);
  const [search, setSearch] = useState('');
  const [category, setCategory] = useState('');
  const [showCreate, setShowCreate] = useState(false);
  const { register, handleSubmit, reset } = useForm();
  const [myClubs, setMyClubs] = useState([]);
  const [myPendingRequests, setMyPendingRequests] = useState([]);

  useEffect(() => {
    execute({ category: category || undefined, search: search || undefined });
  }, [execute, category, search]);

  // Fetch student's clubs and pending requests
  useEffect(() => {
    if (isStudent && user?.studentPrn) {
      Promise.all([
        studentApi.getClubHistory(user.studentPrn).catch(() => ({ data: { data: [] } })),
        clubApi.getMyJoinRequests().catch(() => ({ data: { data: [] } })),
      ]).then(([clubsRes, requestsRes]) => {
        const clubData = clubsRes.data?.data || clubsRes.data || [];
        setMyClubs(Array.isArray(clubData) ? clubData : []);
        const reqData = requestsRes.data?.data || requestsRes.data || [];
        setMyPendingRequests(Array.isArray(reqData) ? reqData.filter(r => r.status === 'PENDING') : []);
      });
    }
  }, [isStudent, user?.studentPrn]);

  const handleCreate = async (data) => {
    try {
      await clubApi.create(data);
      toast.success('Club created');
      setShowCreate(false);
      reset();
      execute({});
    } catch (err) {
      toast.error(err.response?.data?.message || 'Failed');
    }
  };

  const handleRequestJoin = async (club) => {
    try {
      await clubApi.requestJoinClub(club.id);
      toast.success(`Join request sent for ${club.name}! Awaiting teacher approval.`);
      setMyPendingRequests(prev => [...prev, { clubId: club.id, clubName: club.name, clubCategory: club.category, status: 'PENDING' }]);
    } catch (err) {
      toast.error(err.response?.data?.message || 'Failed to request');
    }
  };

  // Quick lookup sets
  const myClubIds = new Set((myClubs || []).filter(c => c.isCurrent).map(c => c.clubId));
  const pendingClubIds = new Set((myPendingRequests || []).map(r => r.clubId));

  // Filter out clubs the student already joined
  const filteredClubs = isStudent
    ? (clubs || []).filter(club => !myClubIds.has(club.id))
    : (clubs || []);

  const roleVariant = (role) => {
    switch (role) {
      case 'PRESIDENT': return 'purple';
      case 'SECRETARY': return 'accent';
      case 'VICE_PRESIDENT': return 'teal';
      default: return 'info';
    }
  };

  if (loading && !clubs) return <><TopBar title="Clubs" /><LoadingSpinner className="py-24" size={32} /></>;

  return (
    <>
      <TopBar title="Clubs" />
      <div className="p-6 space-y-6">
        {/* My Clubs Section */}
        {isStudent && (myClubs || []).filter(c => c.isCurrent).length > 0 && (
          <div>
            <h3 className="text-lg font-semibold text-gray-800 mb-3">My Clubs</h3>
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
              {(myClubs || []).filter(c => c.isCurrent).map((membership) => (
                <div
                  key={membership.membershipId || membership.clubId}
                  onClick={() => navigate(`/clubs/${membership.clubId}`)}
                  className="glass-card p-5 cursor-pointer hover:shadow-xl transition-all duration-200 border-l-4 border-navy-500"
                >
                  <div className="flex items-start gap-4">
                    <div className="w-12 h-12 bg-navy-100 rounded-xl flex items-center justify-center text-lg font-bold text-navy-600">
                      {membership.clubName?.charAt(0)}
                    </div>
                    <div className="flex-1 min-w-0">
                      <h3 className="font-semibold text-gray-800 truncate">{membership.clubName}</h3>
                      <p className="text-xs text-gray-400">{membership.clubCategory} | {membership.startYear}</p>
                    </div>
                  </div>
                  <div className="flex items-center justify-between mt-3 pt-3 border-t border-gray-100">
                    <Badge variant={roleVariant(membership.role)}>{membership.role}</Badge>
                    <div className="flex items-center gap-1 text-xs text-success-600">
                      <CheckCircle size={14} /> Member
                    </div>
                  </div>
                </div>
              ))}
            </div>
          </div>
        )}

        {/* Pending Requests Section */}
        {isStudent && (myPendingRequests || []).length > 0 && (
          <div>
            <h3 className="text-lg font-semibold text-gray-800 mb-3">Pending Requests</h3>
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
              {(myPendingRequests || []).map((req) => (
                <div key={req.id || req.clubId} className="glass-card p-5 border-l-4 border-warning-400 opacity-75">
                  <div className="flex items-start gap-4">
                    <div className="w-12 h-12 bg-warning-50 rounded-xl flex items-center justify-center text-lg font-bold text-warning-600">
                      {req.clubName?.charAt(0)}
                    </div>
                    <div className="flex-1 min-w-0">
                      <h3 className="font-semibold text-gray-800 truncate">{req.clubName}</h3>
                      <p className="text-xs text-gray-400">{req.clubCategory || 'Club'}</p>
                    </div>
                  </div>
                  <div className="flex items-center justify-end mt-3 pt-3 border-t border-gray-100">
                    <span className="flex items-center gap-1 text-xs text-warning-600 bg-warning-50 px-3 py-1 rounded-full">
                      <Clock size={12} /> Awaiting Approval
                    </span>
                  </div>
                </div>
              ))}
            </div>
          </div>
        )}

        {/* Available Clubs */}
        <div>
          {isStudent && <h3 className="text-lg font-semibold text-gray-800 mb-3">Available Clubs</h3>}
          <div className="flex flex-wrap items-center gap-3 mb-4">
            <div className="flex-1 min-w-64">
              <SearchInput value={search} onChange={setSearch} placeholder="Search clubs..." />
            </div>
            <select value={category} onChange={(e) => setCategory(e.target.value)} className="input-field w-44">
              <option value="">All Categories</option>
              {['Technical', 'Cultural', 'Social', 'Sports', 'Entrepreneurship'].map(c => <option key={c} value={c}>{c}</option>)}
            </select>
            {isAdmin && (
              <button onClick={() => { reset(); setShowCreate(true); }} className="btn-primary flex items-center gap-2"><Plus size={16} /> Add Club</button>
            )}
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
            {filteredClubs.map((club) => (
              <div
                key={club.id}
                onClick={() => navigate(`/clubs/${club.id}`)}
                className="glass-card p-5 cursor-pointer hover:shadow-xl transition-all duration-200"
              >
                <div className="flex items-start gap-4">
                  <div className="w-12 h-12 bg-navy-100 rounded-xl flex items-center justify-center text-lg font-bold text-navy-600">
                    {club.name?.charAt(0)}
                  </div>
                  <div className="flex-1 min-w-0">
                    <h3 className="font-semibold text-gray-800 truncate">{club.name}</h3>
                    <p className="text-xs text-gray-400">{club.category} | Founded {club.foundedYear}</p>
                    <p className="text-sm text-gray-500 mt-1 line-clamp-2">{club.description}</p>
                  </div>
                </div>
                <div className="flex items-center justify-between mt-4 pt-3 border-t border-gray-100">
                  <div className="flex items-center gap-1 text-xs text-gray-400">
                    <Users size={14} /> {club.currentMemberCount} members
                  </div>
                  {club.presidentName && (
                    <div className="flex items-center gap-1 text-xs text-accent-600">
                      <Crown size={14} /> {club.presidentName}
                    </div>
                  )}
                </div>
                <div className="mt-2 flex items-center justify-between">
                  <Badge variant={club.isActive ? 'success' : 'gray'}>{club.isActive ? 'Active' : 'Inactive'}</Badge>
                  {isStudent && club.isActive && (
                    pendingClubIds.has(club.id) ? (
                      <span className="flex items-center gap-1 text-xs text-warning-600 bg-warning-50 px-3 py-1 rounded-full">
                        <Clock size={12} /> Request Pending
                      </span>
                    ) : (
                      <button
                        onClick={(e) => {
                          e.stopPropagation();
                          handleRequestJoin(club);
                        }}
                        className="btn-primary text-xs py-1 px-3 flex items-center gap-1"
                      >
                        <UserPlus size={12} /> Request to Join
                      </button>
                    )
                  )}
                </div>
              </div>
            ))}
            {isStudent && filteredClubs.length === 0 && (
              <p className="text-gray-400 text-sm col-span-3">You are a member of all available clubs!</p>
            )}
          </div>
        </div>
      </div>

      <Modal isOpen={showCreate} onClose={() => setShowCreate(false)} title="Create Club" size="md">
        <form onSubmit={handleSubmit(handleCreate)} className="space-y-4">
          <div className="grid grid-cols-2 gap-4">
            <div><label className="block text-sm font-medium text-gray-600 mb-1">Name *</label><input {...register('name', { required: true })} className="input-field" /></div>
            <div>
              <label className="block text-sm font-medium text-gray-600 mb-1">Category *</label>
              <select {...register('category', { required: true })} className="input-field">
                <option value="">Select</option>
                {['Technical', 'Cultural', 'Social', 'Sports', 'Entrepreneurship'].map(c => <option key={c} value={c}>{c}</option>)}
              </select>
            </div>
            <div><label className="block text-sm font-medium text-gray-600 mb-1">Founded Year</label><input type="number" {...register('foundedYear', { valueAsNumber: true })} className="input-field" /></div>
            <div><label className="block text-sm font-medium text-gray-600 mb-1">Faculty Advisor</label><input {...register('facultyAdvisor')} className="input-field" /></div>
          </div>
          <div><label className="block text-sm font-medium text-gray-600 mb-1">Description</label><textarea {...register('description')} className="input-field" rows={3} /></div>
          <div><label className="block text-sm font-medium text-gray-600 mb-1">Logo URL</label><input {...register('logoUrl')} className="input-field" /></div>
          <div className="flex gap-3 justify-end"><button type="button" onClick={() => setShowCreate(false)} className="btn-secondary">Cancel</button><button type="submit" className="btn-primary">Create</button></div>
        </form>
      </Modal>
    </>
  );
}
