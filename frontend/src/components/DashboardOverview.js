import React, { useEffect } from 'react';
import { useAppStore } from '../store/appStore';
import { TrendingUp, Users, Calendar, AlertCircle } from 'lucide-react';

const DashboardOverview = () => {
  const analytics = useAppStore((state) => state.analytics);
  const fetchAnalytics = useAppStore((state) => state.fetchAnalytics);

  useEffect(() => {
    fetchAnalytics();
  }, [fetchAnalytics]);

  if (!analytics) {
    return <div className="text-center py-12">Loading analytics...</div>;
  }

  const stats = [
    {
      label: 'Total Appointments',
      value: analytics.totalAppointments,
      icon: Calendar,
      color: 'bg-blue-500',
    },
    {
      label: 'Confirmed',
      value: analytics.confirmedAppointments,
      icon: Users,
      color: 'bg-green-500',
    },
    {
      label: 'No-Shows',
      value: analytics.noShowAppointments,
      icon: AlertCircle,
      color: 'bg-red-500',
    },
    {
      label: 'Revenue Saved',
      value: `$${analytics.revenueSaved?.toFixed(2) || 0}`,
      icon: TrendingUp,
      color: 'bg-purple-500',
    },
  ];

  return (
    <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-6">
      {stats.map((stat, index) => {
        const Icon = stat.icon;
        return (
          <div key={index} className="bg-white rounded-lg shadow p-6">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-gray-600 text-sm font-medium">{stat.label}</p>
                <p className="text-3xl font-bold text-gray-900 mt-2">{stat.value}</p>
              </div>
              <div className={`${stat.color} p-3 rounded-lg`}>
                <Icon className="w-6 h-6 text-white" />
              </div>
            </div>
          </div>
        );
      })}
    </div>
  );
};

export default DashboardOverview;
