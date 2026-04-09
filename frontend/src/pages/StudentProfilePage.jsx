import { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { useApi } from '../hooks/useApi';
import { studentApi } from '../api/studentApi';
import { useAuth } from '../context/AuthContext';
import TopBar from '../components/layout/TopBar';
import LoadingSpinner from '../components/ui/LoadingSpinner';
import Badge from '../components/ui/Badge';
import Modal from '../components/ui/Modal';
import ConfirmDialog from '../components/ui/ConfirmDialog';
import toast from 'react-hot-toast';
import { useForm } from 'react-hook-form';
import { ArrowLeft, Edit, Trash2, Key, Github, Linkedin, Phone, Mail, BookOpen, Trophy, Building2, Calendar } from 'lucide-react';
import { format } from 'date-fns';

export default function StudentProfilePage() {
  const { prn } = useParams();
  const navigate = useNavigate();
  const { isAdmin, isTeacher } = useAuth();
  const { data: student, loading, execute } = useApi(studentApi.getByPrn);
  const [showEdit, setShowEdit] = useState(false);
  const [showDelete, setShowDelete] = useState(false);
  const [showResetPw, setShowResetPw] = useState(false);
  const [showAddRecord, setShowAddRecord] = useState(false);
  const { register, handleSubmit, reset, setValue } = useForm();
  const { register: regRecord, handleSubmit: handleRecordSubmit, reset: resetRecord } = useForm();

  useEffect(() => { execute(prn); }, [execute, prn]);

  useEffect(() => {
    if (student && showEdit) {
      Object.keys(student).forEach((key) => {
        if (student[key] !== null && student[key] !== undefined) setValue(key, student[key]);
      });
    }
  }, [student, showEdit, setValue]);

  const handleUpdate = async (data) => {
    try {
      await studentApi.update(prn, data);
      toast.success('Student updated');
      setShowEdit(false);
      execute(prn);
    } catch (err) {
      toast.error(err.response?.data?.message || 'Update failed');
    }
  };

  const handleDelete = async () => {
    try {
      await studentApi.delete(prn);
      toast.success('Student deleted');
      navigate('/students');
    } catch (err) {
      toast.error(err.response?.data?.message || 'Delete failed');
    }
  };

  const handleResetPassword = async (data) => {
    try {
      await studentApi.resetPassword(prn, data);
      toast.success('Password reset');
      setShowResetPw(false);
    } catch (err) {
      toast.error(err.response?.data?.message || 'Reset failed');
    }
  };

  const handleAddRecord = async (data) => {
    try {
      await studentApi.addAcademicRecord(prn, data);
      toast.success('Record added');
      setShowAddRecord(false);
      resetRecord();
      execute(prn);
    } catch (err) {
      toast.error(err.response?.data?.message || 'Failed');
    }
  };

  if (loading || !student) return <><TopBar title="Student Profile" /><LoadingSpinner className="py-24" size={32} /></>;

  const statusVariant = (s) => s === 'VERIFIED' ? 'success' : s === 'REJECTED' ? 'danger' : 'warning';

  return (
    <>
      <TopBar title="Student Profile" />
      <div className="p-6 space-y-6">
        <div className="flex items-center justify-between">
          <button onClick={() => navigate('/students')} className="flex items-center gap-2 text-gray-500 hover:text-gray-700"><ArrowLeft size={18} /> Back</button>
          {(isAdmin || isTeacher) && (
            <div className="flex gap-2">
              <button onClick={() => setShowEdit(true)} className="btn-secondary flex items-center gap-1"><Edit size={14} /> Edit</button>
              {isAdmin && <button onClick={() => setShowResetPw(true)} className="btn-secondary flex items-center gap-1"><Key size={14} /> Reset Password</button>}
              {isAdmin && <button onClick={() => setShowDelete(true)} className="btn-danger flex items-center gap-1"><Trash2 size={14} /> Delete</button>}
            </div>
          )}
        </div>

        <div className="glass-card p-6">
          <div className="flex items-start gap-6">
            <div className="w-20 h-20 bg-navy-100 rounded-2xl flex items-center justify-center text-2xl font-bold text-navy-600">
              {student.fullName?.charAt(0)}
            </div>
            <div className="flex-1">
              <h2 className="text-2xl font-bold text-gray-800">{student.fullName}</h2>
              <p className="text-gray-500">PRN: {student.prn}</p>
              <div className="flex flex-wrap gap-2 mt-2">
                <Badge variant="info">Panel {student.panel}</Badge>
                <Badge variant="purple">Year {student.year}</Badge>
                <Badge variant="teal">CGPA: {student.cgpa || '-'}</Badge>
                <Badge variant="accent">Attendance: {student.attendancePercent || 0}%</Badge>
              </div>
              <div className="flex flex-wrap gap-4 mt-4 text-sm text-gray-500">
                {student.email && <span className="flex items-center gap-1"><Mail size={14} /> {student.email}</span>}
                {student.phone && <span className="flex items-center gap-1"><Phone size={14} /> {student.phone}</span>}
                {student.githubUrl && <a href={student.githubUrl} target="_blank" rel="noopener noreferrer" className="flex items-center gap-1 text-navy-600 hover:underline"><Github size={14} /> GitHub</a>}
                {student.linkedinUrl && <a href={student.linkedinUrl} target="_blank" rel="noopener noreferrer" className="flex items-center gap-1 text-navy-600 hover:underline"><Linkedin size={14} /> LinkedIn</a>}
              </div>
              {student.bio && <p className="text-sm text-gray-600 mt-3">{student.bio}</p>}
              {student.skills && <div className="mt-2"><span className="text-xs text-gray-400">Skills: </span><span className="text-sm text-gray-600">{student.skills}</span></div>}
              {student.internshipCompany && (
                <div className="mt-3 p-3 bg-accent-50 rounded-xl">
                  <p className="text-sm font-medium text-accent-700">Internship: {student.internshipRole} at {student.internshipCompany}</p>
                  <p className="text-xs text-accent-500">{student.internshipStart} - {student.internshipEnd || 'Present'}</p>
                </div>
              )}
              {student.panelTeacherName && <p className="text-xs text-gray-400 mt-3">Panel Teacher: {student.panelTeacherName} ({student.panelTeacherEmail})</p>}
            </div>
          </div>
        </div>

        <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
          <div className="glass-card p-6">
            <div className="flex items-center justify-between mb-4">
              <h3 className="text-lg font-semibold text-gray-800 flex items-center gap-2"><Building2 size={18} /> Clubs</h3>
            </div>
            {(student.clubHistory || []).length === 0 ? <p className="text-gray-400 text-sm">No clubs</p> : (
              <div className="space-y-2">
                {(student.clubHistory || []).map((c) => (
                  <div key={c.id} className="flex items-center justify-between p-3 rounded-xl bg-gray-50">
                    <div>
                      <p className="font-medium text-sm text-gray-800">{c.clubName}</p>
                      <p className="text-xs text-gray-400">{c.startYear}{c.endYear ? ` - ${c.endYear}` : ''}</p>
                    </div>
                    <div className="flex items-center gap-2">
                      <Badge variant={c.isCurrent ? 'success' : 'gray'}>{c.isCurrent ? 'Active' : 'Past'}</Badge>
                      <Badge variant="info">{c.role}</Badge>
                    </div>
                  </div>
                ))}
              </div>
            )}
          </div>

          <div className="glass-card p-6">
            <div className="flex items-center justify-between mb-4">
              <h3 className="text-lg font-semibold text-gray-800 flex items-center gap-2"><Trophy size={18} /> Achievements</h3>
            </div>
            {(student.achievements || []).length === 0 ? <p className="text-gray-400 text-sm">No achievements</p> : (
              <div className="space-y-2">
                {(student.achievements || []).map((a) => (
                  <div key={a.id} className="flex items-center justify-between p-3 rounded-xl bg-gray-50">
                    <div>
                      <p className="font-medium text-sm text-gray-800">{a.title}</p>
                      <p className="text-xs text-gray-400">{a.issuingOrganization} | {a.dateOfAchievement}</p>
                    </div>
                    <Badge variant={statusVariant(a.status)}>{a.status}</Badge>
                  </div>
                ))}
              </div>
            )}
          </div>
        </div>

        <div className="glass-card p-6">
          <div className="flex items-center justify-between mb-4">
            <h3 className="text-lg font-semibold text-gray-800 flex items-center gap-2"><BookOpen size={18} /> Academic Records</h3>
            {(isAdmin || isTeacher) && <button onClick={() => setShowAddRecord(true)} className="btn-secondary text-sm">Add Record</button>}
          </div>
          {(student.academicRecords || []).length === 0 ? <p className="text-gray-400 text-sm">No records</p> : (
            <div className="overflow-x-auto">
              <table className="w-full text-sm">
                <thead><tr className="border-b border-gray-100">
                  <th className="px-3 py-2 text-left text-xs font-semibold text-gray-500">Year</th>
                  <th className="px-3 py-2 text-left text-xs font-semibold text-gray-500">Semester</th>
                  <th className="px-3 py-2 text-left text-xs font-semibold text-gray-500">SGPA</th>
                  <th className="px-3 py-2 text-left text-xs font-semibold text-gray-500">CGPA</th>
                  <th className="px-3 py-2 text-left text-xs font-semibold text-gray-500">Attendance</th>
                  <th className="px-3 py-2 text-left text-xs font-semibold text-gray-500">Backlogs</th>
                  <th className="px-3 py-2 text-left text-xs font-semibold text-gray-500">Remarks</th>
                </tr></thead>
                <tbody>
                  {(student.academicRecords || []).map((r) => (
                    <tr key={r.id} className="border-b border-gray-50">
                      <td className="px-3 py-2">{r.academicYear}</td>
                      <td className="px-3 py-2">{r.semester}</td>
                      <td className="px-3 py-2">{r.sgpa}</td>
                      <td className="px-3 py-2 font-medium">{r.cgpa}</td>
                      <td className="px-3 py-2">{r.attendancePercent}%</td>
                      <td className="px-3 py-2">{r.backlogs}</td>
                      <td className="px-3 py-2 text-gray-400">{r.remarks || '-'}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </div>
      </div>

      <Modal isOpen={showEdit} onClose={() => setShowEdit(false)} title="Edit Student" size="lg">
        <form onSubmit={handleSubmit(handleUpdate)} className="space-y-4">
          <div className="grid grid-cols-2 gap-4">
            <div><label className="block text-sm font-medium text-gray-600 mb-1">Full Name</label><input {...register('fullName')} className="input-field" /></div>
            <div><label className="block text-sm font-medium text-gray-600 mb-1">Panel</label><select {...register('panel')} className="input-field">{['A','B','C','D','E','F'].map(p=><option key={p} value={p}>{p}</option>)}</select></div>
            <div><label className="block text-sm font-medium text-gray-600 mb-1">Year</label><select {...register('year', { valueAsNumber: true })} className="input-field">{[1,2,3,4].map(y=><option key={y} value={y}>{y}</option>)}</select></div>
            <div><label className="block text-sm font-medium text-gray-600 mb-1">CGPA</label><input type="number" step="0.01" {...register('cgpa', { valueAsNumber: true })} className="input-field" /></div>
            <div><label className="block text-sm font-medium text-gray-600 mb-1">Attendance %</label><input type="number" step="0.1" {...register('attendancePercent', { valueAsNumber: true })} className="input-field" /></div>
            <div><label className="block text-sm font-medium text-gray-600 mb-1">Phone</label><input {...register('phone')} className="input-field" /></div>
            <div><label className="block text-sm font-medium text-gray-600 mb-1">GitHub URL</label><input {...register('githubUrl')} className="input-field" /></div>
            <div><label className="block text-sm font-medium text-gray-600 mb-1">LinkedIn URL</label><input {...register('linkedinUrl')} className="input-field" /></div>
          </div>
          <div><label className="block text-sm font-medium text-gray-600 mb-1">Skills</label><input {...register('skills')} className="input-field" /></div>
          <div><label className="block text-sm font-medium text-gray-600 mb-1">Bio</label><textarea {...register('bio')} className="input-field" rows={3} /></div>
          <div className="flex gap-3 justify-end pt-2">
            <button type="button" onClick={() => setShowEdit(false)} className="btn-secondary">Cancel</button>
            <button type="submit" className="btn-primary">Save Changes</button>
          </div>
        </form>
      </Modal>

      <Modal isOpen={showResetPw} onClose={() => setShowResetPw(false)} title="Reset Password" size="sm">
        <form onSubmit={handleSubmit(handleResetPassword)} className="space-y-4">
          <div><label className="block text-sm font-medium text-gray-600 mb-1">New Password</label><input type="password" {...register('newPassword', { required: true })} className="input-field" /></div>
          <div className="flex gap-3 justify-end"><button type="button" onClick={() => setShowResetPw(false)} className="btn-secondary">Cancel</button><button type="submit" className="btn-primary">Reset</button></div>
        </form>
      </Modal>

      <Modal isOpen={showAddRecord} onClose={() => setShowAddRecord(false)} title="Add Academic Record" size="md">
        <form onSubmit={handleRecordSubmit(handleAddRecord)} className="space-y-4">
          <div className="grid grid-cols-2 gap-4">
            <div><label className="block text-sm font-medium text-gray-600 mb-1">Academic Year *</label><input {...regRecord('academicYear', { required: true })} placeholder="2024-2025" className="input-field" /></div>
            <div><label className="block text-sm font-medium text-gray-600 mb-1">Semester *</label><input type="number" {...regRecord('semester', { required: true, valueAsNumber: true })} className="input-field" /></div>
            <div><label className="block text-sm font-medium text-gray-600 mb-1">SGPA *</label><input type="number" step="0.01" {...regRecord('sgpa', { required: true, valueAsNumber: true })} className="input-field" /></div>
            <div><label className="block text-sm font-medium text-gray-600 mb-1">CGPA *</label><input type="number" step="0.01" {...regRecord('cgpa', { required: true, valueAsNumber: true })} className="input-field" /></div>
            <div><label className="block text-sm font-medium text-gray-600 mb-1">Attendance %</label><input type="number" step="0.1" {...regRecord('attendancePercent', { valueAsNumber: true })} className="input-field" /></div>
            <div><label className="block text-sm font-medium text-gray-600 mb-1">Backlogs</label><input type="number" {...regRecord('backlogs', { valueAsNumber: true })} className="input-field" /></div>
          </div>
          <div><label className="block text-sm font-medium text-gray-600 mb-1">Remarks</label><input {...regRecord('remarks')} className="input-field" /></div>
          <div className="flex gap-3 justify-end"><button type="button" onClick={() => setShowAddRecord(false)} className="btn-secondary">Cancel</button><button type="submit" className="btn-primary">Add Record</button></div>
        </form>
      </Modal>

      <ConfirmDialog isOpen={showDelete} onClose={() => setShowDelete(false)} onConfirm={handleDelete} title="Delete Student" message={`Are you sure you want to delete ${student.fullName}? This action will soft-delete the student.`} confirmText="Delete" />
    </>
  );
}
