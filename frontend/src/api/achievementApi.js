import api from './axiosConfig';

export const achievementApi = {
  getAll: (params) => api.get('/achievements', { params }),
  getById: (id) => api.get(`/achievements/${id}`),
  getByStudent: (prn) => api.get(`/achievements/student/${prn}`),
  submit: (data, proofFile) => {
    const formData = new FormData();
    Object.keys(data).forEach((key) => {
      if (data[key] !== null && data[key] !== undefined) {
        formData.append(key, data[key]);
      }
    });
    if (proofFile) {
      formData.append('proofFile', proofFile);
    }
    return api.post('/achievements', formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    });
  },
  verify: (id, data) => api.put(`/achievements/${id}/verify`, data),
  delete: (id) => api.delete(`/achievements/${id}`),
};
