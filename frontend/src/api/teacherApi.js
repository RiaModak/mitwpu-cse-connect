import api from './axiosConfig';

export const teacherApi = {
  getAll: () => api.get('/teachers'),
  getById: (id) => api.get(`/teachers/${id}`),
  create: (data) => api.post('/teachers', data),
  update: (id, data) => api.put(`/teachers/${id}`, data),
  delete: (id) => api.delete(`/teachers/${id}`),
  assignPanel: (data) => api.post('/teachers/assign-panel', data),
  getMyStudents: () => api.get('/teachers/my-students'),
  getStudentsByPanel: (panel) => api.get(`/teachers/panel/${panel}/students`),
};
