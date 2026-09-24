import apiAuth from './apiAuth';

export const DEMO_CREDENTIALS = {
  email: 'demo@cambistaonline.pe',
  password: 'demo1234',
};

const handleAuthSuccess = async (token) => {
  localStorage.setItem('cambista_jwt_token', token);

  let userProfile = null;
  try {
    const meResponse = await apiAuth.getMe();
    userProfile = meResponse.data;
  } catch (meErr) {
    console.warn('Could not fetch user profile from /me, using token data', meErr);
  }

  const name = userProfile
    ? (userProfile.firstName ? `${userProfile.firstName} ${userProfile.lastName}` : (userProfile.companyName || userProfile.email))
    : 'Usuario';

  return {
    token: token,
    userId: userProfile ? userProfile.id : 'user-id',
    email: userProfile ? userProfile.email : '',
    fullNameOrCompany: name,
    profileType: userProfile ? (userProfile.role === 'J' ? 'JURIDICA' : 'NATURAL') : 'NATURAL',
    mfaEnabled: userProfile ? userProfile.mfaEnabled : false,
    authProvider: userProfile ? userProfile.authProvider : 'LOCAL',
  };
};

export const authService = {
  login: async (email, password) => {
    try {
      const tokenResponse = await apiAuth.login(email, password);

      // Si el backend solicita segundo factor MFA
      if (tokenResponse.mfaRequired) {
        return {
          mfaRequired: true,
          mfaSessionToken: tokenResponse.mfaSessionToken,
          email: email,
        };
      }

      return await handleAuthSuccess(tokenResponse.accessToken);
    } catch (err) {
      console.error('Error during login:', err);
      throw err;
    }
  },

  loginOAuth: async (provider, code, redirectUri) => {
    try {
      const tokenResponse = await apiAuth.loginOAuth(provider, code, redirectUri);

      // Si el backend solicita segundo factor MFA
      if (tokenResponse.mfaRequired) {
        return {
          mfaRequired: true,
          mfaSessionToken: tokenResponse.mfaSessionToken,
          provider: provider,
        };
      }

      return await handleAuthSuccess(tokenResponse.accessToken);
    } catch (err) {
      console.error('Error during OAuth login:', err);
      throw err;
    }
  },

  verifyMfa: async (mfaSessionToken, code) => {
    try {
      const tokenResponse = await apiAuth.verifyMfa(mfaSessionToken, code);
      return await handleAuthSuccess(tokenResponse.accessToken);
    } catch (err) {
      console.error('Error during MFA verification:', err);
      throw err;
    }
  },

  setupMfa: async () => {
    const res = await apiAuth.setupMfa();
    return res.data; // { secret, qrCodeUri, manualKey }
  },

  enableMfa: async (code) => {
    const res = await apiAuth.enableMfa(code);
    return res.data;
  },

  disableMfa: async (code) => {
    const res = await apiAuth.disableMfa(code);
    return res.data;
  },

  register: async (registerData) => {
    try {
      await apiAuth.register(registerData);
      return await authService.login(registerData.email, registerData.password);
    } catch (err) {
      console.error('Error during registration:', err);
      throw err;
    }
  },
};

export default authService;
