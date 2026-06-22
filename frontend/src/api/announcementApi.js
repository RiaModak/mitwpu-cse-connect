import api from './axiosConfig';

export const announcementApi = {
  getVisible: () => api.get('/announcements'),
  create: (data) => api.post('/announcements', data),
  delete: (id) => api.delete(`/announcements/${id}`),
};
