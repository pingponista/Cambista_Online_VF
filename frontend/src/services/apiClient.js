import axios from 'axios';

const apiClient = axios.create({
  baseURL: import.meta.env.VITE_API_URL || import.meta.env.VITE_API_BASE_URL || '/api/v1',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
});

apiClient.interceptors.request.use((config) => {
  const token = localStorage.getItem('cambista_jwt_token');
  if (token && token !== 'null' && token !== 'undefined') {
    config.headers.Authorization = `Bearer ${token}`;
  }

  // Generar o propagar X-Correlation-Id para trazabilidad distribuida extremo a extremo
  if (!config.headers['X-Correlation-Id']) {
    const correlationId = 'web-' + Math.random().toString(36).substring(2, 11) + '-' + Date.now();
    config.headers['X-Correlation-Id'] = correlationId;
  }

  return config;
});

apiClient.interceptors.response.use(
  (response) => {
    // Capturar Correlation ID devuelto por el Gateway si estuviera presente
    const incomingCid = response.headers['x-correlation-id'];
    if (incomingCid) {
      sessionStorage.setItem('last_correlation_id', incomingCid);
    }
    return response;
  },
  (error) => {
    if (error.response && error.response.status === 401) {
      console.warn('Unauthorized request - Clearing invalid token');
      localStorage.removeItem('cambista_jwt_token');
    }
    return Promise.reject(error);
  }
);

export default apiClient;
