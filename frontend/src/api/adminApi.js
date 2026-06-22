import api from './axiosConfig';

export const adminApi = {
  getAuditLogs: (params) => api.get('/admin/audit-logs', { params }),
  resetStudentPassword: (prn, data) => api.put(`/admin/students/${prn}/reset-password`, data),
  resetTeacherPassword: (id, data) => api.put(`/admin/teachers/${id}/reset-password`, data),
};
