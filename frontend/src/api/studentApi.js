import api from './axiosConfig';

export const studentApi = {
  getAll: (params) => api.get('/students', { params }),
  getByPrn: (prn) => api.get(`/students/${prn}`),
  create: (data) => api.post('/students', data),
  update: (prn, data) => api.put(`/students/${prn}`, data),
  updateSelf: (data) => api.put('/students/me', data),
  delete: (prn) => api.delete(`/students/${prn}`),
  resetPassword: (prn, data) => api.put(`/students/${prn}/reset-password`, data),
  getClubHistory: (prn) => api.get(`/students/${prn}/clubs`),
  getAchievements: (prn) => api.get(`/students/${prn}/achievements`),
  getTimeline: (prn) => api.get(`/students/${prn}/timeline`),
  bulkImport: (file) => {
    const formData = new FormData();
    formData.append('file', file);
    return api.post('/students/bulk-import', formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    });
  },
  addAcademicRecord: (prn, data) => api.post(`/students/${prn}/academic-records`, data),
};
