import { create } from 'zustand';
import { authAPI } from '../api/client';

export const useAuthStore = create((set) => ({
  user: null,
  token: localStorage.getItem('token'),
  isLoading: false,
  error: null,

  setUser: (user) => set({ user }),
  setToken: (token) => {
    if (token) {
      localStorage.setItem('token', token);
    } else {
      localStorage.removeItem('token');
    }
    set({ token });
  },
  setError: (error) => set({ error }),
  setLoading: (loading) => set({ isLoading: loading }),

  register: async (data) => {
    set({ isLoading: true, error: null });
    try {
      const response = await authAPI.register(data);
      const { token, refreshToken, user } = response.data;
      set({
        token,
        user,
        isLoading: false,
      });
      localStorage.setItem('token', token);
      localStorage.setItem('refreshToken', refreshToken);
      return response.data;
    } catch (error) {
      const errorMsg = error.response?.data?.message || 'Registration failed';
      set({ error: errorMsg, isLoading: false });
      throw error;
    }
  },

  login: async (data) => {
    set({ isLoading: true, error: null });
    try {
      const response = await authAPI.login(data);
      const { token, refreshToken, user } = response.data;
      set({
        token,
        user,
        isLoading: false,
      });
      localStorage.setItem('token', token);
      localStorage.setItem('refreshToken', refreshToken);
      return response.data;
    } catch (error) {
      const errorMsg = error.response?.data?.message || 'Login failed';
      set({ error: errorMsg, isLoading: false });
      throw error;
    }
  },

  logout: () => {
    localStorage.removeItem('token');
    localStorage.removeItem('refreshToken');
    set({ user: null, token: null });
  },

  fetchProfile: async () => {
    set({ isLoading: true });
    try {
      const response = await authAPI.getProfile();
      set({ user: response.data, isLoading: false });
      return response.data;
    } catch (error) {
      set({ error: 'Failed to fetch profile', isLoading: false });
      throw error;
    }
  },

  updateProfile: async (data) => {
    set({ isLoading: true, error: null });
    try {
      const response = await authAPI.updateProfile(data);
      set({ user: response.data, isLoading: false });
      return response.data;
    } catch (error) {
      set({ error: 'Failed to update profile', isLoading: false });
      throw error;
    }
  },

  isAuthenticated: () => {
    const token = localStorage.getItem('token');
    return !!token;
  },
}));
