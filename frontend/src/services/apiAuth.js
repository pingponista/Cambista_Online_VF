import apiClient from './apiClient';

/**
 * Servicio API de Autenticación para el Frontend.
 * Realiza peticiones HTTP REST estandarizadas hacia /api/v1/auth.
 */
export const apiAuth = {
  /**
   * Registro de usuario (Persona Natural / Persona Jurídica)
   * POST /api/v1/auth/register
   */
  register: async (payload) => {
    const response = await apiClient.post('/auth/register', payload);
    return response.data;
  },

  /**
   * Autenticación tradicional de usuario
   * POST /api/v1/auth/login
   */
  login: async (email, password) => {
    const response = await apiClient.post('/auth/login', { email, password });
    return response.data; // { accessToken, tokenType, expiresIn, mfaRequired, mfaSessionToken }
  },

  /**
   * Autenticación social con Google, GitHub o Facebook
   * POST /api/v1/auth/oauth/{provider}
   */
  loginOAuth: async (provider, code, redirectUri) => {
    const response = await apiClient.post(`/auth/oauth/${provider.toLowerCase()}`, { code, redirectUri });
    return response.data; // { accessToken, tokenType, expiresIn, mfaRequired, mfaSessionToken }
  },

  /**
   * Obtener perfil del usuario autenticado mediante JWT
   * GET /api/v1/auth/me
   */
  getMe: async () => {
    const response = await apiClient.get('/auth/me');
    return response.data; // ApiResponse<UserProfileResponse>
  },

  /**
   * Solicitar clave secreta y URI para QR (Google Authenticator)
   * POST /api/v1/auth/mfa/setup
   */
  setupMfa: async () => {
    const response = await apiClient.post('/auth/mfa/setup');
    return response.data; // ApiResponse<SetupMfaResponseDto>
  },

  /**
   * Confirmar código inicial y activar MFA
   * POST /api/v1/auth/mfa/enable
   */
  enableMfa: async (code) => {
    const response = await apiClient.post('/auth/mfa/enable', { code });
    return response.data;
  },

  /**
   * Verificar código TOTP durante el login cuando mfaRequired === true
   * POST /api/v1/auth/mfa/verify
   */
  verifyMfa: async (mfaSessionToken, code) => {
    const response = await apiClient.post('/auth/mfa/verify', { mfaSessionToken, code });
    return response.data; // { accessToken, tokenType, expiresIn }
  },

  /**
   * Desactivar MFA con el código TOTP actual
   * POST /api/v1/auth/mfa/disable
   */
  disableMfa: async (code) => {
    const response = await apiClient.post('/auth/mfa/disable', { code });
    return response.data;
  },
};

export default apiAuth;
