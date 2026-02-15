import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuthStore } from '../store/authStore';
import { useAppStore } from '../store/appStore';
import Sidebar from '../components/Sidebar';
import DashboardOverview from '../components/DashboardOverview';
import ServicesManagement from '../components/ServicesManagement';
import AvailabilityManagement from '../components/AvailabilityManagement';
import AppointmentsView from '../components/AppointmentsView';
import AnalyticsView from '../components/AnalyticsView';

const DashboardPage = () => {
  const navigate = useNavigate();
  const user = useAuthStore((state) => state.user);
  const logout = useAuthStore((state) => state.logout);
  const fetchServices = useAppStore((state) => state.fetchServices);
  const fetchAvailability = useAppStore((state) => state.fetchAvailability);
  const fetchAppointments = useAppStore((state) => state.fetchAppointments);
  const fetchAnalytics = useAppStore((state) => state.fetchAnalytics);

  const [activeTab, setActiveTab] = useState('overview');

  useEffect(() => {
    fetchServices();
    fetchAvailability();
    fetchAppointments();
    fetchAnalytics();
  }, [fetchServices, fetchAvailability, fetchAppointments, fetchAnalytics]);

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  return (
    <div className="flex h-screen bg-gray-100">
      <Sidebar activeTab={activeTab} setActiveTab={setActiveTab} />

      <div className="flex-1 flex flex-col overflow-hidden">
        {/* Header */}
        <header className="bg-white shadow">
          <div className="flex items-center justify-between px-6 py-4">
            <div>
              <h1 className="text-2xl font-bold text-gray-900">
                {user?.businessName || 'Dashboard'}
              </h1>
              <p className="text-sm text-gray-500">{user?.email}</p>
            </div>
            <button
              onClick={handleLogout}
              className="bg-red-600 hover:bg-red-700 text-white px-4 py-2 rounded-md text-sm font-medium transition"
            >
              Logout
            </button>
          </div>
        </header>

        {/* Content */}
        <main className="flex-1 overflow-auto">
          <div className="p-6">
            {activeTab === 'overview' && <DashboardOverview />}
            {activeTab === 'services' && <ServicesManagement />}
            {activeTab === 'availability' && <AvailabilityManagement />}
            {activeTab === 'appointments' && <AppointmentsView />}
            {activeTab === 'analytics' && <AnalyticsView />}
          </div>
        </main>
      </div>
    </div>
  );
};

export default DashboardPage;
