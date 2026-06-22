import { useEffect, useState, useCallback } from 'react';
import { usePaginatedApi, useApi } from '../hooks/useApi';
import { achievementApi } from '../api/achievementApi';
import { useAuth } from '../context/AuthContext';
import TopBar from '../components/layout/TopBar';
import DataTable from '../components/ui/DataTable';
import Badge from '../components/ui/Badge';
import Modal from '../components/ui/Modal';
import toast from 'react-hot-toast';
import { useForm } from 'react-hook-form';
import { Plus, CheckCircle, XCircle, Eye, ExternalLink } from 'lucide-react';
import { format } from 'date-fns';

export default function AchievementsPage() {
  const { isAdmin, isTeacher, isStudent, user } = useAuth();
  const { data, loading, execute } = usePaginatedApi(achievementApi.getAll);
  const { data: myAchievements, loading: myLoading, execute: fetchMy } = useApi(achievementApi.getByStudent);
  const [page, setPage] = useState(0);
  const [category, setCategory] = useState('');
  const [status, setStatus] = useState('');
  const [showSubmit, setShowSubmit] = useState(false);
  const [showVerify, setShowVerify] = useState(null);
  const [showDetail, setShowDetail] = useState(null);
  const [proofFile, setProofFile] = useState(null);
  const { register, handleSubmit, reset } = useForm();
  const { register: regVerify, handleSubmit: handleVerifySubmit, reset: resetVerify } = useForm();

  const fetchData = useCallback(() => {
    if (isStudent && user?.studentPrn) {
      fetchMy(user.studentPrn);
    } else if (!isStudent) {
      execute({ page, size: 20, category: category || undefined, status: status || undefined });
    }
  }, [execute, fetchMy, isStudent, user?.studentPrn, page, category, status]);

  useEffect(() => { fetchData(); }, [fetchData]);

  const handleSubmitAchievement = async (formData) => {
    try {
      await achievementApi.submit(formData, proofFile);
      toast.success('Achievement submitted for verification');
      setShowSubmit(false);
      reset();
      setProofFile(null);
      fetchData();
    } catch (err) {
      toast.error(err.response?.data?.message || 'Failed');
    }
  };

  const handleVerify = async (formData) => {
    try {
      await achievementApi.verify(showVerify.id, formData);
      toast.success('Achievement updated');
      setShowVerify(null);
      resetVerify();
      fetchData();
    } catch (err) {
      toast.error(err.response?.data?.message || 'Failed');
    }
  };

  const handleDelete = async (id) => {
    try {
      await achievementApi.delete(id);
      toast.success('Achievement deleted');
      fetchData();
    } catch (err) {
      toast.error(err.response?.data?.message || 'Failed');
    }
  };

  const statusVariant = (s) => s === 'VERIFIED' ? 'success' : s === 'REJECTED' ? 'danger' : 'warning';

  const achievementList = isStudent
    ? (myAchievements || []).map((a, i) => ({ ...a, _idx: i }))
    : (data.content || []);
  const isLoading = isStudent ? myLoading : loading;

  const columns = [
    { key: 'title', label: 'Title', render: (row) => <span className="font-medium">{row.title}</span> },
    ...(!isStudent ? [{ key: 'studentName', label: 'Student', render: (row) => <span>{row.studentName} ({row.studentPrn})</span> }] : []),
    { key: 'category', label: 'Category', render: (row) => <Badge variant="info">{row.category}</Badge> },
    { key: 'dateOfAchievement', label: 'Date', render: (row) => row.dateOfAchievement || '-' },
    { key: 'issuingOrganization', label: 'Organization' },
    { key: 'status', label: 'Status', render: (row) => <Badge variant={statusVariant(row.status)}>{row.status}</Badge> },
    { key: 'actions', label: 'Actions', render: (row) => (
      <div className="flex items-center gap-1">
        <button onClick={(e) => { e.stopPropagation(); setShowDetail(row); }} className="p-1 hover:bg-gray-100 rounded"><Eye size={14} /></button>
        {(isAdmin || isTeacher) && row.status === 'PENDING' && (
          <button onClick={(e) => { e.stopPropagation(); setShowVerify(row); }} className="p-1 hover:bg-success-50 rounded text-success-500"><CheckCircle size={14} /></button>
        )}
      </div>
    )},
  ];

  const categories = ['HACKATHON', 'CERTIFICATION', 'PROJECT', 'PAPER_PUBLICATION', 'COMPETITION', 'INTERNSHIP', 'OTHER'];

  return (
    <>
      <TopBar title="Achievements" />
      <div className="p-6 space-y-4">
        <div className="flex flex-wrap items-center gap-3">
          <select value={category} onChange={(e) => { setCategory(e.target.value); setPage(0); }} className="input-field w-44">
            <option value="">All Categories</option>
            {categories.map(c => <option key={c} value={c}>{c}</option>)}
          </select>
          <select value={status} onChange={(e) => { setStatus(e.target.value); setPage(0); }} className="input-field w-36">
            <option value="">All Status</option>
            {['PENDING', 'VERIFIED', 'REJECTED'].map(s => <option key={s} value={s}>{s}</option>)}
          </select>
          {isStudent && (
            <button onClick={() => { reset(); setShowSubmit(true); }} className="btn-primary flex items-center gap-2 ml-auto"><Plus size={16} /> Submit Achievement</button>
          )}
        </div>

        <div className="glass-card">
          <DataTable
            columns={columns}
            data={achievementList}
            loading={isLoading}
            page={isStudent ? 0 : page}
            totalPages={isStudent ? 1 : (data.totalPages || 0)}
            onPageChange={isStudent ? undefined : setPage}
            emptyMessage={isStudent ? "You have no achievements yet" : "No achievements found"}
          />
        </div>
      </div>

      <Modal isOpen={showSubmit} onClose={() => setShowSubmit(false)} title="Submit Achievement" size="lg">
        <form onSubmit={handleSubmit(handleSubmitAchievement)} className="space-y-4">
          <div className="grid grid-cols-2 gap-4">
            <div><label className="block text-sm font-medium text-gray-600 mb-1">Title *</label><input {...register('title', { required: true })} className="input-field" /></div>
            <div>
              <label className="block text-sm font-medium text-gray-600 mb-1">Category *</label>
              <select {...register('category', { required: true })} className="input-field">
                <option value="">Select</option>
                {categories.map(c => <option key={c} value={c}>{c}</option>)}
              </select>
            </div>
            <div><label className="block text-sm font-medium text-gray-600 mb-1">Date *</label><input type="date" {...register('dateOfAchievement', { required: true })} className="input-field" /></div>
            <div><label className="block text-sm font-medium text-gray-600 mb-1">Issuing Organization *</label><input {...register('issuingOrganization', { required: true })} className="input-field" /></div>
          </div>
          <div><label className="block text-sm font-medium text-gray-600 mb-1">Description</label><textarea {...register('description')} className="input-field" rows={3} /></div>
          <div><label className="block text-sm font-medium text-gray-600 mb-1">Proof URL</label><input {...register('proofExternalUrl')} className="input-field" placeholder="https://..." /></div>
          <div><label className="block text-sm font-medium text-gray-600 mb-1">Proof File (PDF/JPG/PNG)</label><input type="file" accept=".pdf,.jpg,.jpeg,.png" onChange={(e) => setProofFile(e.target.files[0])} className="input-field" /></div>
          <div className="flex gap-3 justify-end"><button type="button" onClick={() => setShowSubmit(false)} className="btn-secondary">Cancel</button><button type="submit" className="btn-primary">Submit</button></div>
        </form>
      </Modal>

      <Modal isOpen={!!showVerify} onClose={() => setShowVerify(null)} title="Verify Achievement" size="md">
        <div className="mb-4 p-3 bg-gray-50 rounded-xl">
          <p className="font-medium">{showVerify?.title}</p>
          <p className="text-sm text-gray-500">By: {showVerify?.studentName} | {showVerify?.issuingOrganization}</p>
        </div>
        <form onSubmit={handleVerifySubmit(handleVerify)} className="space-y-4">
          <div>
            <label className="block text-sm font-medium text-gray-600 mb-1">Decision *</label>
            <select {...regVerify('status', { required: true })} className="input-field">
              <option value="">Select</option>
              <option value="VERIFIED">Verify</option>
              <option value="REJECTED">Reject</option>
            </select>
          </div>
          <div><label className="block text-sm font-medium text-gray-600 mb-1">Rejection Reason (if rejecting)</label><textarea {...regVerify('rejectionReason')} className="input-field" rows={2} /></div>
          <div className="flex gap-3 justify-end"><button type="button" onClick={() => setShowVerify(null)} className="btn-secondary">Cancel</button><button type="submit" className="btn-primary">Submit</button></div>
        </form>
      </Modal>

      <Modal isOpen={!!showDetail} onClose={() => setShowDetail(null)} title="Achievement Details" size="md">
        {showDetail && (
          <div className="space-y-3">
            <div className="flex items-center justify-between">
              <h3 className="font-semibold text-lg">{showDetail.title}</h3>
              <Badge variant={statusVariant(showDetail.status)}>{showDetail.status}</Badge>
            </div>
            <p className="text-sm text-gray-600">{showDetail.description}</p>
            <div className="grid grid-cols-2 gap-2 text-sm">
              <div><span className="text-gray-400">Student:</span> {showDetail.studentName}</div>
              <div><span className="text-gray-400">PRN:</span> {showDetail.studentPrn}</div>
              <div><span className="text-gray-400">Category:</span> {showDetail.category}</div>
              <div><span className="text-gray-400">Date:</span> {showDetail.dateOfAchievement}</div>
              <div><span className="text-gray-400">Organization:</span> {showDetail.issuingOrganization}</div>
              {showDetail.verifiedByName && <div><span className="text-gray-400">Verified By:</span> {showDetail.verifiedByName}</div>}
            </div>
            {showDetail.proofExternalUrl && <a href={showDetail.proofExternalUrl} target="_blank" rel="noopener noreferrer" className="flex items-center gap-1 text-navy-600 text-sm hover:underline"><ExternalLink size={14} /> View Proof</a>}
            {showDetail.rejectionReason && <div className="p-3 bg-danger-50 rounded-xl text-sm text-danger-700"><strong>Rejection Reason:</strong> {showDetail.rejectionReason}</div>}
          </div>
        )}
      </Modal>
    </>
  );
}
