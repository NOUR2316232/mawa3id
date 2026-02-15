import { create } from 'zustand';
import { servicesAPI, availabilityAPI, appointmentsAPI, analyticsAPI } from '../api/client';

export const useAppStore = create((set, get) => ({
  services: [],
  availability: [],
  appointments: [],
  analytics: null,
  isLoading: false,
  error: null,

  // Services
  fetchServices: async () => {
    set({ isLoading: true, error: null });
    try {
      const response = await servicesAPI.getAll();
      set({ services: response.data, isLoading: false });
    } catch (error) {
      set({ error: 'Failed to fetch services', isLoading: false });
    }
  },

  createService: async (data) => {
    set({ isLoading: true, error: null });
    try {
      const response = await servicesAPI.create(data);
      set((state) => ({
        services: [...state.services, response.data],
        isLoading: false,
      }));
      return response.data;
    } catch (error) {
      set({ error: 'Failed to create service', isLoading: false });
      throw error;
    }
  },

  updateService: async (id, data) => {
    set({ isLoading: true, error: null });
    try {
      const response = await servicesAPI.update(id, data);
      set((state) => ({
        services: state.services.map((s) => (s.id === id ? response.data : s)),
        isLoading: false,
      }));
      return response.data;
    } catch (error) {
      set({ error: 'Failed to update service', isLoading: false });
      throw error;
    }
  },

  deleteService: async (id) => {
    set({ isLoading: true, error: null });
    try {
      await servicesAPI.delete(id);
      set((state) => ({
        services: state.services.filter((s) => s.id !== id),
        isLoading: false,
      }));
    } catch (error) {
      set({ error: 'Failed to delete service', isLoading: false });
      throw error;
    }
  },

  // Availability
  fetchAvailability: async () => {
    set({ isLoading: true, error: null });
    try {
      const response = await availabilityAPI.getAll();
      set({ availability: response.data, isLoading: false });
    } catch (error) {
      set({ error: 'Failed to fetch availability', isLoading: false });
    }
  },

  createAvailability: async (data) => {
    set({ isLoading: true, error: null });
    try {
      const response = await availabilityAPI.create(data);
      set((state) => ({
        availability: [...state.availability, response.data],
        isLoading: false,
      }));
      return response.data;
    } catch (error) {
      set({ error: 'Failed to create availability', isLoading: false });
      throw error;
    }
  },

  deleteAvailability: async (id) => {
    set({ isLoading: true, error: null });
    try {
      await availabilityAPI.delete(id);
      set((state) => ({
        availability: state.availability.filter((a) => a.id !== id),
        isLoading: false,
      }));
    } catch (error) {
      set({ error: 'Failed to delete availability', isLoading: false });
      throw error;
    }
  },

  // Appointments
  fetchAppointments: async () => {
    set({ isLoading: true, error: null });
    try {
      const response = await appointmentsAPI.getAll();
      set({ appointments: response.data, isLoading: false });
    } catch (error) {
      set({ error: 'Failed to fetch appointments', isLoading: false });
    }
  },

  createAppointment: async (data) => {
    set({ isLoading: true, error: null });
    try {
      const response = await appointmentsAPI.create(data);
      set((state) => ({
        appointments: [...state.appointments, response.data],
        isLoading: false,
      }));
      return response.data;
    } catch (error) {
      set({ error: 'Failed to create appointment', isLoading: false });
      throw error;
    }
  },

  updateAppointmentStatus: async (id, status) => {
    set({ isLoading: true, error: null });
    try {
      const response = await appointmentsAPI.updateStatus(id, status);
      set((state) => ({
        appointments: state.appointments.map((a) =>
          a.id === id ? { ...a, status } : a
        ),
        isLoading: false,
      }));
      return response.data;
    } catch (error) {
      set({ error: 'Failed to update appointment', isLoading: false });
      throw error;
    }
  },

  deleteAppointment: async (id) => {
    set({ isLoading: true, error: null });
    try {
      await appointmentsAPI.delete(id);
      set((state) => ({
        appointments: state.appointments.filter((a) => a.id !== id),
        isLoading: false,
      }));
    } catch (error) {
      set({ error: 'Failed to delete appointment', isLoading: false });
      throw error;
    }
  },

  // Analytics
  fetchAnalytics: async () => {
    set({ isLoading: true, error: null });
    try {
      const response = await analyticsAPI.getAnalytics();
      set({ analytics: response.data, isLoading: false });
    } catch (error) {
      set({ error: 'Failed to fetch analytics', isLoading: false });
    }
  },
}));
