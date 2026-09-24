import { create } from 'zustand';

const getSavedUser = () => {
  try {
    const saved = localStorage.getItem('cambista_user');
    return saved ? JSON.parse(saved) : null;
  } catch (e) {
    return null;
  }
};

export const useAuthStore = create((set) => ({
  user: getSavedUser(),
  token: localStorage.getItem('cambista_jwt_token') || null,
  isAuthenticated: !!localStorage.getItem('cambista_jwt_token'),
  profileType: getSavedUser()?.profileType || 'NATURAL',

  setProfileType: (type) => set({ profileType: type }),

  setAuth: (authData) => {
    const userObj = {
      id: authData.userId,
      email: authData.email,
      name: authData.fullNameOrCompany,
      profileType: authData.profileType,
    };

    localStorage.setItem('cambista_jwt_token', authData.token);
    localStorage.setItem('cambista_user', JSON.stringify(userObj));

    set({
      user: userObj,
      token: authData.token,
      isAuthenticated: true,
      profileType: authData.profileType,
    });
  },

  logout: () => {
    localStorage.removeItem('cambista_jwt_token');
    localStorage.removeItem('cambista_user');
    set({ user: null, token: null, isAuthenticated: false, profileType: 'NATURAL' });
  },
}));
