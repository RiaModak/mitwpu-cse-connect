import { useEffect, useState } from 'react';
import { useApi } from '../hooks/useApi';
import { teacherApi } from '../api/teacherApi';
import { useAuth } from '../context/AuthContext';
import TopBar from '../components/layout/TopBar';
import LoadingSpinner from '../components/ui/LoadingSpinner';
import Badge from '../components/ui/Badge';
import Modal from '../components/ui/Modal';
import ConfirmDialog from '../components/ui/ConfirmDialog';
import toast from 'react-hot-toast';
import { useForm } from 'react-hook-form';
import { Plus, Edit, Trash2, Key } from 'lucide-react';

export default function TeachersListPage() {
  const { isAdmin } = useAuth();
  const { data: teachers, loading, execute } = useApi(teacherApi.getAll);
  const [showCreate, setShowCreate] = useState(false);
  const [showEdit, setShowEdit] = useState(null);
  const [showDelete, setShowDelete] = useState(null);
  const [showAssignPanel, setShowAssignPanel] = useState(false);
  const { register, handleSubmit, reset, setValue } = useForm();
  const { register: regPanel, handleSubmit: handlePanelSubmit, reset: resetPanel } = useForm();

  useEffect(() => { execute(); }, [execute]);

  const handleCreate = async (data) => {
    try {
      const cleaned = Object.fromEntries(
        Object.entries(data).map(([k, v]) => [k, v === '' ? null : v])
      );
      await teacherApi.create(cleaned);
      toast.success('Teacher created');
      setShowCreate(false);
      reset();
      execute();
    } catch (err) {
      toast.error(err.response?.data?.message || 'Failed');
    }
  };

  const handleUpdate = async (data) => {
    try {
      await teacherApi.update(showEdit.id, data);
      toast.success('Teacher updated');
      setShowEdit(null);
      execute();
    } catch (err) {
      toast.error(err.response?.data?.message || 'Failed');
    }
  };

  const handleDelete = async () => {
    try {
      await teacherApi.delete(showDelete.id);
      toast.success('Teacher deleted');
      setShowDelete(null);
      execute();
    } catch (err) {
      toast.error(err.response?.data?.message || 'Failed');
    }
  };

  const handleAssignPanel = async (data) => {
    try {
      await teacherApi.assignPanel(data);
      toast.success('Panel assigned');
      setShowAssignPanel(false);
      resetPanel();
      execute();
    } catch (err) {
      toast.error(err.response?.data?.message || 'Failed');
    }
  };

  if (loading && !teachers) return <><TopBar title="Teachers" /><LoadingSpinner className="py-24" size={32} /></>;

  return (
    <>
      <TopBar title="Teachers" />
      <div className="p-6 space-y-4">
        {isAdmin && (
          <div className="flex gap-3">
            <button onClick={() => { reset(); setShowCreate(true); }} className="btn-primary flex items-center gap-2"><Plus size={16} /> Add Teacher</button>
            <button onClick={() => { resetPanel(); setShowAssignPanel(true); }} className="btn-secondary flex items-center gap-2">Assign Panel</button>
          </div>
        )}

        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
          {(teachers || []).map((t) => (
            <div key={t.id} className="glass-card p-5">
              <div className="flex items-start justify-between">
                <div>
                  <h3 className="font-semibold text-gray-800">{t.fullName}</h3>
                  <p className="text-sm text-gray-500">{t.designation}</p>
                  <p className="text-xs text-gray-400 mt-1">{t.email}</p>
                  {t.phone && <p className="text-xs text-gray-400">{t.phone}</p>}
                  <p className="text-xs text-gray-400">Emp ID: {t.employeeId}</p>
                </div>
                {isAdmin && (
                  <div className="flex gap-1">
                    <button onClick={() => { setShowEdit(t); setValue('fullName', t.fullName); setValue('designation', t.designation); setValue('phone', t.phone); }} className="p-1.5 hover:bg-gray-100 rounded-lg"><Edit size={14} /></button>
                    <button onClick={() => setShowDelete(t)} className="p-1.5 hover:bg-danger-50 rounded-lg text-danger-500"><Trash2 size={14} /></button>
                  </div>
                )}
              </div>
              <div className="flex flex-wrap gap-1 mt-3">
                {(t.currentPanels || []).map((p) => <Badge key={p} variant="info">Panel {p}</Badge>)}
                {(t.currentPanels || []).length === 0 && <span className="text-xs text-gray-400">No panels assigned</span>}
              </div>
            </div>
          ))}
        </div>
      </div>

      <Modal isOpen={showCreate} onClose={() => setShowCreate(false)} title="Add Teacher" size="md">
        <form onSubmit={handleSubmit(handleCreate)} className="space-y-4">
          <div className="grid grid-cols-2 gap-4">
            <div><label className="block text-sm font-medium text-gray-600 mb-1">Full Name *</label><input {...register('fullName', { required: true })} className="input-field" /></div>
            <div><label className="block text-sm font-medium text-gray-600 mb-1">Employee ID *</label><input {...register('employeeId', { required: true })} className="input-field" /></div>
            <div><label className="block text-sm font-medium text-gray-600 mb-1">Email *</label><input type="email" {...register('email', { required: true })} className="input-field" /></div>
            <div><label className="block text-sm font-medium text-gray-600 mb-1">Password *</label><input type="password" {...register('password', { required: true })} className="input-field" /></div>
            <div><label className="block text-sm font-medium text-gray-600 mb-1">Designation</label><input {...register('designation')} className="input-field" /></div>
            <div><label className="block text-sm font-medium text-gray-600 mb-1">Phone</label><input {...register('phone')} className="input-field" /></div>
          </div>
          <div className="flex gap-3 justify-end"><button type="button" onClick={() => setShowCreate(false)} className="btn-secondary">Cancel</button><button type="submit" className="btn-primary">Create</button></div>
        </form>
      </Modal>

      <Modal isOpen={!!showEdit} onClose={() => setShowEdit(null)} title="Edit Teacher" size="md">
        <form onSubmit={handleSubmit(handleUpdate)} className="space-y-4">
          <div className="grid grid-cols-2 gap-4">
            <div><label className="block text-sm font-medium text-gray-600 mb-1">Full Name</label><input {...register('fullName')} className="input-field" /></div>
            <div><label className="block text-sm font-medium text-gray-600 mb-1">Designation</label><input {...register('designation')} className="input-field" /></div>
            <div><label className="block text-sm font-medium text-gray-600 mb-1">Phone</label><input {...register('phone')} className="input-field" /></div>
          </div>
          <div className="flex gap-3 justify-end"><button type="button" onClick={() => setShowEdit(null)} className="btn-secondary">Cancel</button><button type="submit" className="btn-primary">Save</button></div>
        </form>
      </Modal>

      <Modal isOpen={showAssignPanel} onClose={() => setShowAssignPanel(false)} title="Assign Panel to Teacher" size="sm">
        <form onSubmit={handlePanelSubmit(handleAssignPanel)} className="space-y-4">
          <div>
            <label className="block text-sm font-medium text-gray-600 mb-1">Teacher *</label>
            <select {...regPanel('teacherId', { required: true, valueAsNumber: true })} className="input-field">
              <option value="">Select Teacher</option>
              {(teachers || []).map((t) => <option key={t.id} value={t.id}>{t.fullName}</option>)}
            </select>
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-600 mb-1">Panel *</label>
            <select {...regPanel('panel', { required: true })} className="input-field">
              <option value="">Select</option>
              {['A','B','C','D','E','F'].map(p=><option key={p} value={p}>{p}</option>)}
            </select>
          </div>
          <div><label className="block text-sm font-medium text-gray-600 mb-1">Academic Year *</label><input {...regPanel('academicYear', { required: true })} placeholder="2024-2025" className="input-field" /></div>
          <div className="flex gap-3 justify-end"><button type="button" onClick={() => setShowAssignPanel(false)} className="btn-secondary">Cancel</button><button type="submit" className="btn-primary">Assign</button></div>
        </form>
      </Modal>

      <ConfirmDialog isOpen={!!showDelete} onClose={() => setShowDelete(null)} onConfirm={handleDelete} title="Delete Teacher" message={`Are you sure you want to delete ${showDelete?.fullName}?`} confirmText="Delete" />
    </>
  );
}
