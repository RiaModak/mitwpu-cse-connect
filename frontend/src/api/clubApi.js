import api from './axiosConfig';

export const clubApi = {
  getAll: (params) => api.get('/clubs', { params }),
  getById: (id) => api.get(`/clubs/${id}`),
  create: (data) => api.post('/clubs', data),
  update: (id, data) => api.put(`/clubs/${id}`, data),
  deactivate: (id) => api.delete(`/clubs/${id}`),
  requestJoinClub: (clubId) => api.post(`/clubs/${clubId}/join`),
  getMyJoinRequests: () => api.get('/clubs/join-requests/my'),
  getPendingJoinRequests: () => api.get('/clubs/join-requests/pending'),
  approveJoinRequest: (requestId) => api.put(`/clubs/join-requests/${requestId}/approve`),
  rejectJoinRequest: (requestId, reason) => api.put(`/clubs/join-requests/${requestId}/reject`, null, { params: { reason } }),
  addMember: (clubId, data) => api.post(`/clubs/${clubId}/members`, data),
  updateMember: (clubId, membershipId, data) => api.put(`/clubs/${clubId}/members/${membershipId}`, data),
  removeMember: (clubId, membershipId) => api.delete(`/clubs/${clubId}/members/${membershipId}`),
  postNotice: (clubId, data) => api.post(`/clubs/${clubId}/notices`, data),
  getNotices: (clubId) => api.get(`/clubs/${clubId}/notices`),
};
