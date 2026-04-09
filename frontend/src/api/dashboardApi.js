import api from './axiosConfig';

export const dashboardApi = {
  getAdminStats: () => api.get('/dashboard/admin'),
  getTeacherStats: () => api.get('/dashboard/teacher'),
  getStudentStats: () => api.get('/dashboard/student'),
};
