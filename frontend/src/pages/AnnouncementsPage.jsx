import { useEffect, useState } from 'react';
import { useApi } from '../hooks/useApi';
import { announcementApi } from '../api/announcementApi';
import { useAuth } from '../context/AuthContext';
import TopBar from '../components/layout/TopBar';
import LoadingSpinner from '../components/ui/LoadingSpinner';
import Badge from '../components/ui/Badge';
import Modal from '../components/ui/Modal';
import ConfirmDialog from '../components/ui/ConfirmDialog';
import toast from 'react-hot-toast';
import { useForm } from 'react-hook-form';
import { Plus, Trash2, Pin, Megaphone } from 'lucide-react';
import { format } from 'date-fns';

export default function AnnouncementsPage() {
  const { isAdmin, isTeacher } = useAuth();
  const { data: announcements, loading, execute } = useApi(announcementApi.getVisible);
  const [showCreate, setShowCreate] = useState(false);
  const [showDelete, setShowDelete] = useState(null);
  const { register, handleSubmit, reset } = useForm();

  useEffect(() => { execute(); }, [execute]);

  const handleCreate = async (data) => {
    try {
      await announcementApi.create({
        ...data,
        targetClubId: data.targetClubId ? Number(data.targetClubId) : null,
        isPinned: data.isPinned === 'true' || data.isPinned === true,
      });
      toast.success('Announcement created');
      setShowCreate(false);
      reset();
      execute();
    } catch (err) {
      toast.error(err.response?.data?.message || 'Failed');
    }
  };

  const handleDelete = async () => {
    try {
      await announcementApi.delete(showDelete.id);
      toast.success('Announcement deleted');
      setShowDelete(null);
      execute();
    } catch (err) {
      toast.error(err.response?.data?.message || 'Failed');
    }
  };

  if (loading && !announcements) return <><TopBar title="Announcements" /><LoadingSpinner className="py-24" size={32} /></>;

  return (
    <>
      <TopBar title="Announcements" />
      <div className="p-6 space-y-4">
        {(isAdmin || isTeacher) && (
          <button onClick={() => { reset(); setShowCreate(true); }} className="btn-primary flex items-center gap-2"><Plus size={16} /> New Announcement</button>
        )}

        <div className="space-y-4">
          {(announcements || []).map((a) => (
            <div key={a.id} className={`glass-card p-5 ${a.isPinned ? 'border-l-4 border-accent-500' : ''}`}>
              <div className="flex items-start justify-between">
                <div className="flex-1">
                  <div className="flex items-center gap-2">
                    <h3 className="font-semibold text-gray-800">{a.title}</h3>
                    {a.isPinned && <Pin size={14} className="text-accent-500" />}
                  </div>
                  <p className="text-gray-600 mt-2 whitespace-pre-wrap">{a.body}</p>
                  <div className="flex flex-wrap items-center gap-3 mt-3">
                    <span className="text-xs text-gray-400">
                      {a.postedByName} ({a.postedByRole}) | {a.createdAt ? format(new Date(a.createdAt), 'MMM dd, yyyy HH:mm') : ''}
                    </span>
                    <Badge variant="info">{a.targetAudience}</Badge>
                    {a.targetPanel && <Badge variant="purple">Panel {a.targetPanel}</Badge>}
                    {a.targetClubName && <Badge variant="teal">{a.targetClubName}</Badge>}
                    {a.expiresAt && <span className="text-xs text-gray-400">Expires: {format(new Date(a.expiresAt), 'MMM dd, yyyy')}</span>}
                  </div>
                </div>
                {(isAdmin || isTeacher) && (
                  <button onClick={() => setShowDelete(a)} className="p-1.5 hover:bg-danger-50 rounded-lg text-danger-400"><Trash2 size={16} /></button>
                )}
              </div>
            </div>
          ))}
          {(announcements || []).length === 0 && (
            <div className="text-center py-16 text-gray-400">
              <Megaphone size={48} className="mx-auto mb-4 opacity-50" />
              <p>No announcements yet</p>
            </div>
          )}
        </div>
      </div>

      <Modal isOpen={showCreate} onClose={() => setShowCreate(false)} title="New Announcement" size="lg">
        <form onSubmit={handleSubmit(handleCreate)} className="space-y-4">
          <div><label className="block text-sm font-medium text-gray-600 mb-1">Title *</label><input {...register('title', { required: true })} className="input-field" /></div>
          <div><label className="block text-sm font-medium text-gray-600 mb-1">Body *</label><textarea {...register('body', { required: true })} className="input-field" rows={4} /></div>
          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="block text-sm font-medium text-gray-600 mb-1">Target Audience *</label>
              <select {...register('targetAudience', { required: true })} className="input-field">
                {['ALL', 'STUDENTS', 'TEACHERS', 'CLUB'].map(t => <option key={t} value={t}>{t}</option>)}
              </select>
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-600 mb-1">Target Panel</label>
              <select {...register('targetPanel')} className="input-field">
                <option value="ALL">All Panels</option>
                {['A', 'B', 'C', 'D', 'E', 'F'].map(p => <option key={p} value={p}>Panel {p}</option>)}
              </select>
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-600 mb-1">Pinned</label>
              <select {...register('isPinned')} className="input-field">
                <option value="false">No</option>
                <option value="true">Yes</option>
              </select>
            </div>
            <div><label className="block text-sm font-medium text-gray-600 mb-1">Expires At</label><input type="datetime-local" {...register('expiresAt')} className="input-field" /></div>
          </div>
          <div className="flex gap-3 justify-end"><button type="button" onClick={() => setShowCreate(false)} className="btn-secondary">Cancel</button><button type="submit" className="btn-primary">Post</button></div>
        </form>
      </Modal>

      <ConfirmDialog isOpen={!!showDelete} onClose={() => setShowDelete(null)} onConfirm={handleDelete} title="Delete Announcement" message={`Are you sure you want to delete "${showDelete?.title}"?`} confirmText="Delete" />
    </>
  );
}
