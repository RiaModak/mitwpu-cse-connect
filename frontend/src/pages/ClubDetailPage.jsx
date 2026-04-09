import { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { useApi } from '../hooks/useApi';
import { clubApi } from '../api/clubApi';
import { useAuth } from '../context/AuthContext';
import TopBar from '../components/layout/TopBar';
import LoadingSpinner from '../components/ui/LoadingSpinner';
import Badge from '../components/ui/Badge';
import Modal from '../components/ui/Modal';
import ConfirmDialog from '../components/ui/ConfirmDialog';
import toast from 'react-hot-toast';
import { useForm } from 'react-hook-form';
import { ArrowLeft, Plus, Edit, Trash2, Users, Crown, MessageSquare, UserPlus } from 'lucide-react';
import { format } from 'date-fns';

export default function ClubDetailPage() {
  const { id } = useParams();
  const navigate = useNavigate();
  const { isAdmin, isTeacher, isStudent, user } = useAuth();
  const { data: club, loading, execute } = useApi(clubApi.getById);
  const [showAddMember, setShowAddMember] = useState(false);
  const [showPostNotice, setShowPostNotice] = useState(false);
  const [showEdit, setShowEdit] = useState(false);
  const [showDeactivate, setShowDeactivate] = useState(false);
  const { register, handleSubmit, reset } = useForm();
  const { register: regNotice, handleSubmit: handleNoticeSubmit, reset: resetNotice } = useForm();
  const { register: regEdit, handleSubmit: handleEditSubmit, reset: resetEdit, setValue } = useForm();

  useEffect(() => { execute(id); }, [execute, id]);

  useEffect(() => {
    if (club && showEdit) {
      setValue('name', club.name);
      setValue('description', club.description);
      setValue('category', club.category);
      setValue('logoUrl', club.logoUrl);
      setValue('foundedYear', club.foundedYear);
      setValue('facultyAdvisor', club.facultyAdvisor);
    }
  }, [club, showEdit, setValue]);

  const handleAddMember = async (data) => {
    try {
      await clubApi.addMember(id, data);
      toast.success('Member added');
      setShowAddMember(false);
      reset();
      execute(id);
    } catch (err) {
      toast.error(err.response?.data?.message || 'Failed');
    }
  };

  const handlePostNotice = async (data) => {
    try {
      await clubApi.postNotice(id, data);
      toast.success('Notice posted');
      setShowPostNotice(false);
      resetNotice();
      execute(id);
    } catch (err) {
      toast.error(err.response?.data?.message || 'Failed');
    }
  };

  const handleUpdate = async (data) => {
    try {
      await clubApi.update(id, data);
      toast.success('Club updated');
      setShowEdit(false);
      execute(id);
    } catch (err) {
      toast.error(err.response?.data?.message || 'Failed');
    }
  };

  const handleDeactivate = async () => {
    try {
      await clubApi.deactivate(id);
      toast.success('Club deactivated');
      navigate('/clubs');
    } catch (err) {
      toast.error(err.response?.data?.message || 'Failed');
    }
  };

  const handleRemoveMember = async (membershipId) => {
    try {
      await clubApi.removeMember(id, membershipId);
      toast.success('Member removed');
      execute(id);
    } catch (err) {
      toast.error(err.response?.data?.message || 'Failed');
    }
  };

  const handleRequestJoin = async () => {
    try {
      await clubApi.requestJoinClub(id);
      toast.success('Join request submitted! Awaiting teacher approval.');
      execute(id);
    } catch (err) {
      toast.error(err.response?.data?.message || 'Failed to submit join request');
    }
  };

  if (loading || !club) return <><TopBar title="Club Details" /><LoadingSpinner className="py-24" size={32} /></>;

  const roleVariant = (role) => {
    switch (role) {
      case 'PRESIDENT': return 'purple';
      case 'SECRETARY': return 'accent';
      case 'VICE_PRESIDENT': return 'teal';
      default: return 'info';
    }
  };

  return (
    <>
      <TopBar title="Club Details" />
      <div className="p-6 space-y-6">
        <div className="flex items-center justify-between">
          <button onClick={() => navigate('/clubs')} className="flex items-center gap-2 text-gray-500 hover:text-gray-700"><ArrowLeft size={18} /> Back</button>
          <div className="flex gap-2">
            {isAdmin && <button onClick={() => setShowEdit(true)} className="btn-secondary flex items-center gap-1"><Edit size={14} /> Edit</button>}
            {isAdmin && <button onClick={() => setShowAddMember(true)} className="btn-primary flex items-center gap-1"><Plus size={14} /> Add Member</button>}
            {isStudent && (() => {
              const isMember = (club.currentMembers || []).some(m => m.prn === user?.studentPrn);
              const myMembership = (club.currentMembers || []).find(m => m.prn === user?.studentPrn);
              const isClubHead = myMembership && ['PRESIDENT', 'SECRETARY', 'VICE_PRESIDENT'].includes(myMembership.role);
              return (
                <>
                  {!isMember && (
                    <button onClick={handleRequestJoin} className="btn-primary flex items-center gap-1"><UserPlus size={14} /> Request to Join</button>
                  )}
                  {isClubHead && (
                    <button onClick={() => setShowPostNotice(true)} className="btn-accent flex items-center gap-1"><MessageSquare size={14} /> Post Notice</button>
                  )}
                </>
              );
            })()}
            {isAdmin && <button onClick={() => setShowDeactivate(true)} className="btn-danger flex items-center gap-1"><Trash2 size={14} /> Deactivate</button>}
          </div>
        </div>

        <div className="glass-card p-6">
          <div className="flex items-start gap-6">
            <div className="w-16 h-16 bg-navy-100 rounded-2xl flex items-center justify-center text-2xl font-bold text-navy-600">{club.name?.charAt(0)}</div>
            <div>
              <h2 className="text-2xl font-bold text-gray-800">{club.name}</h2>
              <div className="flex items-center gap-2 mt-1">
                <Badge variant="info">{club.category}</Badge>
                <Badge variant={club.isActive ? 'success' : 'gray'}>{club.isActive ? 'Active' : 'Inactive'}</Badge>
              </div>
              <p className="text-gray-600 mt-2">{club.description}</p>
              <div className="flex flex-wrap gap-4 mt-3 text-sm text-gray-500">
                <span>Founded: {club.foundedYear}</span>
                <span>Advisor: {club.facultyAdvisor || 'N/A'}</span>
                <span className="flex items-center gap-1"><Users size={14} /> {club.currentMemberCount} members</span>
                {club.presidentName && <span className="flex items-center gap-1"><Crown size={14} /> President: {club.presidentName}</span>}
              </div>
            </div>
          </div>
        </div>

        <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
          <div className="glass-card p-6">
            <h3 className="text-lg font-semibold text-gray-800 mb-4">Current Members ({(club.currentMembers || []).length})</h3>
            <div className="space-y-2">
              {(club.currentMembers || []).map((m) => (
                <div key={m.membershipId} className="flex items-center justify-between p-3 rounded-xl bg-gray-50">
                  <div>
                    <p className="font-medium text-sm text-gray-800">{m.studentName}</p>
                    <p className="text-xs text-gray-400">PRN: {m.prn} | Panel {m.panel} | Year {m.year}</p>
                  </div>
                  <div className="flex items-center gap-2">
                    <Badge variant={roleVariant(m.role)}>{m.role}</Badge>
                    {isAdmin && (
                      <button onClick={() => handleRemoveMember(m.membershipId)} className="text-danger-400 hover:text-danger-600"><Trash2 size={14} /></button>
                    )}
                  </div>
                </div>
              ))}
              {(club.currentMembers || []).length === 0 && <p className="text-gray-400 text-sm">No current members</p>}
            </div>
          </div>

          <div className="glass-card p-6">
            <h3 className="text-lg font-semibold text-gray-800 mb-4">Club Notices</h3>
            <div className="space-y-3">
              {(club.recentNotices || []).map((n) => (
                <div key={n.id} className="p-3 rounded-xl bg-gray-50">
                  <p className="font-medium text-sm text-gray-800">{n.title}</p>
                  <p className="text-sm text-gray-600 mt-1">{n.body}</p>
                  <p className="text-xs text-gray-400 mt-2">{n.postedByName} | {n.createdAt ? format(new Date(n.createdAt), 'MMM dd, yyyy') : ''}</p>
                </div>
              ))}
              {(club.recentNotices || []).length === 0 && <p className="text-gray-400 text-sm">No notices yet</p>}
            </div>
          </div>
        </div>
      </div>

      <Modal isOpen={showAddMember} onClose={() => setShowAddMember(false)} title="Add Member" size="md">
        <form onSubmit={handleSubmit(handleAddMember)} className="space-y-4">
          <div><label className="block text-sm font-medium text-gray-600 mb-1">Student PRN *</label><input {...register('studentPrn', { required: true })} className="input-field" placeholder="e.g. 1032220000" /></div>
          <div>
            <label className="block text-sm font-medium text-gray-600 mb-1">Role *</label>
            <select {...register('role', { required: true })} className="input-field">
              {['MEMBER', 'SECRETARY', 'VICE_PRESIDENT', 'PRESIDENT'].map(r => <option key={r} value={r}>{r}</option>)}
            </select>
          </div>
          <div><label className="block text-sm font-medium text-gray-600 mb-1">Start Year *</label><input {...register('startYear', { required: true })} placeholder="2024-2025" className="input-field" /></div>
          <div>
            <label className="block text-sm font-medium text-gray-600 mb-1">Joined Via *</label>
            <select {...register('joinedVia', { required: true })} className="input-field">
              {['DIRECT', 'INTERVIEW', 'REFERRAL', 'EVENT'].map(j => <option key={j} value={j}>{j}</option>)}
            </select>
          </div>
          <div><label className="block text-sm font-medium text-gray-600 mb-1">Notes</label><input {...register('notes')} className="input-field" /></div>
          <div className="flex gap-3 justify-end"><button type="button" onClick={() => setShowAddMember(false)} className="btn-secondary">Cancel</button><button type="submit" className="btn-primary">Add</button></div>
        </form>
      </Modal>

      <Modal isOpen={showPostNotice} onClose={() => setShowPostNotice(false)} title="Post Notice" size="md">
        <form onSubmit={handleNoticeSubmit(handlePostNotice)} className="space-y-4">
          <div><label className="block text-sm font-medium text-gray-600 mb-1">Title *</label><input {...regNotice('title', { required: true })} className="input-field" /></div>
          <div><label className="block text-sm font-medium text-gray-600 mb-1">Body *</label><textarea {...regNotice('body', { required: true })} className="input-field" rows={4} /></div>
          <div className="flex gap-3 justify-end"><button type="button" onClick={() => setShowPostNotice(false)} className="btn-secondary">Cancel</button><button type="submit" className="btn-accent">Post</button></div>
        </form>
      </Modal>

      <Modal isOpen={showEdit} onClose={() => setShowEdit(false)} title="Edit Club" size="md">
        <form onSubmit={handleEditSubmit(handleUpdate)} className="space-y-4">
          <div className="grid grid-cols-2 gap-4">
            <div><label className="block text-sm font-medium text-gray-600 mb-1">Name</label><input {...regEdit('name')} className="input-field" /></div>
            <div><label className="block text-sm font-medium text-gray-600 mb-1">Category</label><select {...regEdit('category')} className="input-field">{['Technical','Cultural','Social','Sports','Entrepreneurship'].map(c=><option key={c} value={c}>{c}</option>)}</select></div>
            <div><label className="block text-sm font-medium text-gray-600 mb-1">Founded Year</label><input type="number" {...regEdit('foundedYear', { valueAsNumber: true })} className="input-field" /></div>
            <div><label className="block text-sm font-medium text-gray-600 mb-1">Faculty Advisor</label><input {...regEdit('facultyAdvisor')} className="input-field" /></div>
          </div>
          <div><label className="block text-sm font-medium text-gray-600 mb-1">Description</label><textarea {...regEdit('description')} className="input-field" rows={3} /></div>
          <div className="flex gap-3 justify-end"><button type="button" onClick={() => setShowEdit(false)} className="btn-secondary">Cancel</button><button type="submit" className="btn-primary">Save</button></div>
        </form>
      </Modal>

      <ConfirmDialog isOpen={showDeactivate} onClose={() => setShowDeactivate(false)} onConfirm={handleDeactivate} title="Deactivate Club" message={`Are you sure you want to deactivate ${club.name}?`} confirmText="Deactivate" />
    </>
  );
}
