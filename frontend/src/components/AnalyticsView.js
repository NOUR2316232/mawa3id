import React, { useEffect } from 'react';
import { useAppStore } from '../store/appStore';
import { BarChart, Bar, LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer } from 'recharts';

const AnalyticsView = () => {
  const analytics = useAppStore((state) => state.analytics);
  const fetchAnalytics = useAppStore((state) => state.fetchAnalytics);

  useEffect(() => {
    fetchAnalytics();
  }, [fetchAnalytics]);

  if (!analytics) {
    return <div className="text-center py-12">Loading analytics...</div>;
  }

  // Data for bar chart
  const appointmentData = [
    { name: 'Confirmed', value: analytics.confirmedAppointments },
    { name: 'Cancelled', value: analytics.cancelledAppointments },
    { name: 'No-Show', value: analytics.noShowAppointments },
    { name: 'Pending', value: analytics.pendingAppointments },
  ];

  // Data for rate chart
  const rateData = [
    { name: 'Confirmation Rate', value: Math.round(analytics.confirmationRate) },
    { name: 'No-Show Rate', value: Math.round(analytics.noShowRate) },
    { name: 'Cancellation Rate', value: Math.round(analytics.cancellationRate) },
  ];

  return (
    <div>
      <h2 className="text-2xl font-bold text-gray-900 mb-6">Analytics & Reports</h2>

      {/* Key Metrics */}
      <div className="grid grid-cols-1 md:grid-cols-2 gap-6 mb-6">
        <div className="bg-white rounded-lg shadow p-6">
          <h3 className="text-lg font-semibold text-gray-900 mb-4">Revenue Analysis</h3>
          <div className="space-y-4">
            <div>
              <p className="text-sm text-gray-600">Total Revenue (Confirmed)</p>
              <p className="text-2xl font-bold text-green-600">${analytics.totalRevenue?.toFixed(2) || '0'}</p>
            </div>
            <div>
              <p className="text-sm text-gray-600">Revenue Lost (No-Shows)</p>
              <p className="text-2xl font-bold text-red-600">${analytics.revenueLost?.toFixed(2) || '0'}</p>
            </div>
            <div>
              <p className="text-sm text-gray-600">Estimated Revenue Saved</p>
              <p className="text-2xl font-bold text-green-600">${analytics.revenueSaved?.toFixed(2) || '0'}</p>
            </div>
          </div>
        </div>

        <div className="bg-white rounded-lg shadow p-6">
          <h3 className="text-lg font-semibold text-gray-900 mb-4">Appointment Status</h3>
          <div className="space-y-3">
            <div className="flex justify-between">
              <span className="text-gray-600">Total</span>
              <span className="font-semibold">{analytics.totalAppointments}</span>
            </div>
            <div className="flex justify-between">
              <span className="text-gray-600">Confirmed</span>
              <span className="font-semibold text-green-600">{analytics.confirmedAppointments}</span>
            </div>
            <div className="flex justify-between">
              <span className="text-gray-600">Cancelled</span>
              <span className="font-semibold text-red-600">{analytics.cancelledAppointments}</span>
            </div>
            <div className="flex justify-between">
              <span className="text-gray-600">No-Shows</span>
              <span className="font-semibold text-orange-600">{analytics.noShowAppointments}</span>
            </div>
            <div className="flex justify-between border-t pt-3">
              <span className="text-gray-600">Pending</span>
              <span className="font-semibold">{analytics.pendingAppointments}</span>
            </div>
          </div>
        </div>
      </div>

      {/* Charts */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <div className="bg-white rounded-lg shadow p-6">
          <h3 className="text-lg font-semibold text-gray-900 mb-4">Appointment Distribution</h3>
          <ResponsiveContainer width="100%" height={300}>
            <BarChart data={appointmentData}>
              <CartesianGrid strokeDasharray="3 3" />
              <XAxis dataKey="name" />
              <YAxis />
              <Tooltip />
              <Bar dataKey="value" fill="#4f46e5" />
            </BarChart>
          </ResponsiveContainer>
        </div>

        <div className="bg-white rounded-lg shadow p-6">
          <h3 className="text-lg font-semibold text-gray-900 mb-4">Performance Rates (%)</h3>
          <ResponsiveContainer width="100%" height={300}>
            <LineChart data={rateData}>
              <CartesianGrid strokeDasharray="3 3" />
              <XAxis dataKey="name" />
              <YAxis domain={[0, 100]} />
              <Tooltip />
              <Line type="monotone" dataKey="value" stroke="#4f46e5" strokeWidth={2} />
            </LineChart>
          </ResponsiveContainer>
        </div>
      </div>

      {/* Summary */}
      <div className="mt-6 bg-gradient-to-r from-blue-50 to-indigo-50 rounded-lg shadow p-6">
        <h3 className="text-lg font-semibold text-gray-900 mb-4">Summary</h3>
        <p className="text-gray-700">
          Your no-show rate is <strong>{Math.round(analytics.noShowRate)}%</strong>, with an estimated 
          <strong> ${analytics.revenueSaved?.toFixed(2) || '0'}</strong> in revenue that can be saved 
          by implementing our reminder system. This represents approximately <strong>{Math.round(analytics.noShowRate * 2)}%</strong> 
          improvement potential when implemented with automated SMS reminders and confirmations.
        </p>
      </div>
    </div>
  );
};

export default AnalyticsView;
