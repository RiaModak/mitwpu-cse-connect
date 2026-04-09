import api from './axiosConfig';

export const fileApi = {
  upload: (file, subfolder = 'general') => {
    const formData = new FormData();
    formData.append('file', file);
    formData.append('subfolder', subfolder);
    return api.post('/files/upload', formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    });
  },
};
