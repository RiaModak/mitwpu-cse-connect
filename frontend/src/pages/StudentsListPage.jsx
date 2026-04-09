import { useEffect, useState, useCallback } from 'react';
import { useNavigate } from 'react-router-dom';
import { usePaginatedApi } from '../hooks/useApi';
import { studentApi } from '../api/studentApi';
import { useAuth } from '../context/AuthContext';
import TopBar from '../components/layout/TopBar';
import DataTable from '../components/ui/DataTable';
import SearchInput from '../components/ui/SearchInput';
import Badge from '../components/ui/Badge';
import Modal from '../components/ui/Modal';
import toast from 'react-hot-toast';
import { Plus, Upload } from 'lucide-react';
import { useForm } from 'react-hook-form';

export default function StudentsListPage() {
  const { isAdmin, isTeacher } = useAuth();
  const navigate = useNavigate();
  const { data, loading, execute } = usePaginatedApi(studentApi.getAll);
  const [page, setPage] = useState(0);
  const [search, setSearch] = useState('');
  const [panel, setPanel] = useState('');
  const [year, setYear] = useState('');
  const [showCreate, setShowCreate] = useState(false);
  const [showImport, setShowImport] = useState(false);
  const { register, handleSubmit, reset } = useForm();

  const fetchStudents = useCallback(() => {
    const params = { page, size: 20, search: search || undefined, panel: panel || undefined, year: year || undefined };
    execute(params);
  }, [execute, page, search, panel, year]);

  useEffect(() => { fetchStudents(); }, [fetchStudents]);

  const handleCreate = async (formData) => {
    try {
      // Clean empty strings to null so backend validation passes for optional fields
      const cleaned = Object.fromEntries(
        Object.entries(formData).map(([k, v]) => [k, v === '' || (typeof v === 'number' && isNaN(v)) ? null : v])
      );
      await studentApi.create(cleaned);
      toast.success('Student created');
      setShowCreate(false);
      reset();
      fetchStudents();
    } catch (err) {
      toast.error(err.response?.data?.message || 'Failed to create');
    }
  };

  const handleImport = async (e) => {
    const file = e.target.files[0];
    if (!file) return;
    try {
      const res = await studentApi.bulkImport(file);
      const result = res.data?.data || res.data;
      toast.success(`Imported: ${result.successCount} success, ${result.failureCount} failed`);
      setShowImport(false);
      fetchStudents();
    } catch (err) {
      toast.error(err.response?.data?.message || 'Import failed');
    }
  };

  const columns = [
    { key: 'prn', label: 'PRN' },
    { key: 'fullName', label: 'Name' },
    { key: 'panel', label: 'Panel', render: (row) => <Badge variant="info">{row.panel}</Badge> },
    { key: 'year', label: 'Year' },
    { key: 'cgpa', label: 'CGPA', render: (row) => row.cgpa || '-' },
    { key: 'attendancePercent', label: 'Attendance', render: (row) => row.attendancePercent ? `${row.attendancePercent}%` : '-' },
    { key: 'activeClubName', label: 'Club', render: (row) => row.activeClubName || '-' },
    { key: 'achievementsCount', label: 'Achievements', render: (row) => row.achievementsCount || 0 },
  ];

  return (
    <>
      <TopBar title="Students" />
      <div className="p-6 space-y-4">
        <div className="flex flex-wrap items-center gap-3">
          <div className="flex-1 min-w-64">
            <SearchInput value={search} onChange={(v) => { setSearch(v); setPage(0); }} placeholder="Search students..." />
          </div>
          <select value={panel} onChange={(e) => { setPanel(e.target.value); setPage(0); }} className="input-field w-36">
            <option value="">All Panels</option>
            {['A', 'B', 'C', 'D', 'E', 'F'].map((p) => <option key={p} value={p}>Panel {p}</option>)}
          </select>
          <select value={year} onChange={(e) => { setYear(e.target.value); setPage(0); }} className="input-field w-32">
            <option value="">All Years</option>
            {[1, 2, 3, 4].map((y) => <option key={y} value={y}>Year {y}</option>)}
          </select>
          {isAdmin && (
            <>
              <button onClick={() => setShowCreate(true)} className="btn-primary flex items-center gap-2">
                <Plus size={16} /> Add Student
              </button>
              <button onClick={() => setShowImport(true)} className="btn-secondary flex items-center gap-2">
                <Upload size={16} /> Import CSV
              </button>
            </>
          )}
        </div>

        <div className="glass-card">
          <DataTable
            columns={columns}
            data={data.content || []}
            loading={loading}
            page={page}
            totalPages={data.totalPages || 0}
            onPageChange={setPage}
            onRowClick={(row) => navigate(`/students/${row.prn}`)}
            emptyMessage="No students found"
          />
        </div>
      </div>

      <Modal isOpen={showCreate} onClose={() => setShowCreate(false)} title="Add New Student" size="lg">
        <form onSubmit={handleSubmit(handleCreate)} className="space-y-4">
          <div className="grid grid-cols-2 gap-4">
            <div><label className="block text-sm font-medium text-gray-600 mb-1">PRN *</label><input {...register('prn', { required: true })} className="input-field" /></div>
            <div><label className="block text-sm font-medium text-gray-600 mb-1">Full Name *</label><input {...register('fullName', { required: true })} className="input-field" /></div>
            <div><label className="block text-sm font-medium text-gray-600 mb-1">Email *</label><input type="email" {...register('email', { required: true })} className="input-field" /></div>
            <div><label className="block text-sm font-medium text-gray-600 mb-1">Password *</label><input type="password" {...register('password', { required: true })} className="input-field" /></div>
            <div>
              <label className="block text-sm font-medium text-gray-600 mb-1">Panel *</label>
              <select {...register('panel', { required: true })} className="input-field">
                <option value="">Select</option>
                {['A', 'B', 'C', 'D', 'E', 'F'].map((p) => <option key={p} value={p}>{p}</option>)}
              </select>
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-600 mb-1">Year *</label>
              <select {...register('year', { required: true, valueAsNumber: true })} className="input-field">
                <option value="">Select</option>
                {[1, 2, 3, 4].map((y) => <option key={y} value={y}>{y}</option>)}
              </select>
            </div>
            <div><label className="block text-sm font-medium text-gray-600 mb-1">CGPA</label><input type="number" step="0.01" {...register('cgpa', { valueAsNumber: true })} className="input-field" /></div>
            <div><label className="block text-sm font-medium text-gray-600 mb-1">Attendance %</label><input type="number" step="0.1" {...register('attendancePercent', { valueAsNumber: true })} className="input-field" /></div>
            <div><label className="block text-sm font-medium text-gray-600 mb-1">Phone</label><input {...register('phone')} className="input-field" /></div>
            <div><label className="block text-sm font-medium text-gray-600 mb-1">GitHub URL</label><input {...register('githubUrl')} className="input-field" /></div>
          </div>
          <div className="flex gap-3 justify-end pt-4">
            <button type="button" onClick={() => setShowCreate(false)} className="btn-secondary">Cancel</button>
            <button type="submit" className="btn-primary">Create Student</button>
          </div>
        </form>
      </Modal>

      <Modal isOpen={showImport} onClose={() => setShowImport(false)} title="Bulk Import Students" size="sm">
        <p className="text-sm text-gray-600 mb-4">Upload a CSV file with columns: PRN, Full Name, Panel, Year, Email, Password, CGPA, Attendance</p>
        <input type="file" accept=".csv" onChange={handleImport} className="input-field" />
      </Modal>
    </>
  );
}
