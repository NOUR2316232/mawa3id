import React from 'react';
import { useAppStore } from '../store/appStore';
import toast from 'react-hot-toast';
import { CheckCircle, XCircle, Clock, Trash2 } from 'lucide-react';

const AppointmentsView = () => {
  const appointments = useAppStore((state) => state.appointments);
  const updateAppointmentStatus = useAppStore((state) => state.updateAppointmentStatus);
  const deleteAppointment = useAppStore((state) => state.deleteAppointment);
  const isLoading = useAppStore((state) => state.isLoading);

  const getStatusIcon = (status) => {
    switch (status) {
      case 'CONFIRMED':
        return <CheckCircle className="w-5 h-5 text-green-500" />;
      case 'CANCELLED':
        return <XCircle className="w-5 h-5 text-red-500" />;
      case 'NO_SHOW':
        return <XCircle className="w-5 h-5 text-orange-500" />;
      default:
        return <Clock className="w-5 h-5 text-yellow-500" />;
    }
  };

  const getStatusColor = (status) => {
    switch (status) {
      case 'CONFIRMED':
        return 'bg-green-100 text-green-800';
      case 'CANCELLED':
        return 'bg-red-100 text-red-800';
      case 'NO_SHOW':
        return 'bg-orange-100 text-orange-800';
      default:
        return 'bg-yellow-100 text-yellow-800';
    }
  };

  const handleStatusUpdate = async (id, newStatus) => {
    try {
      await updateAppointmentStatus(id, newStatus);
      toast.success(`Appointment marked as ${newStatus}`);
    } catch {
      toast.error('Failed to update appointment');
    }
  };

  const handleDelete = async (id) => {
    if (window.confirm('Are you sure?')) {
      try {
        await deleteAppointment(id);
        toast.success('Appointment deleted');
      } catch {
        toast.error('Failed to delete appointment');
      }
    }
  };

  return (
    <div>
      <h2 className="text-2xl font-bold text-gray-900 mb-6">Appointments</h2>

      <div className="bg-white rounded-lg shadow overflow-hidden">
        <table className="w-full">
          <thead className="bg-gray-50 border-b">
            <tr>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-700 uppercase">Customer</th>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-700 uppercase">Date & Time</th>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-700 uppercase">Phone</th>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-700 uppercase">Status</th>
              <th className="px-6 py-3 text-right text-xs font-medium text-gray-700 uppercase">Actions</th>
            </tr>
          </thead>
          <tbody className="divide-y">
            {appointments.map((appointment) => (
              <tr key={appointment.id} className="hover:bg-gray-50">
                <td className="px-6 py-4 text-sm text-gray-900">{appointment.customerName}</td>
                <td className="px-6 py-4 text-sm text-gray-900">
                  {new Date(appointment.appointmentDate).toLocaleDateString()} {appointment.startTime}
                </td>
                <td className="px-6 py-4 text-sm text-gray-900">{appointment.customerPhone}</td>
                <td className="px-6 py-4">
                  <span className={`px-3 py-1 rounded-full text-xs font-medium flex items-center gap-2 w-fit ${getStatusColor(appointment.status)}`}>
                    {getStatusIcon(appointment.status)}
                    {appointment.status}
                  </span>
                </td>
                <td className="px-6 py-4 text-right">
                  {appointment.status === 'PENDING' && (
                    <>
                      <button
                        onClick={() => handleStatusUpdate(appointment.id, 'CONFIRMED')}
                        className="text-green-600 hover:text-green-900 mr-3 text-sm font-medium"
                      >
                        Confirm
                      </button>
                      <button
                        onClick={() => handleStatusUpdate(appointment.id, 'CANCELLED')}
                        className="text-red-600 hover:text-red-900 mr-3 text-sm font-medium"
                      >
                        Cancel
                      </button>
                    </>
                  )}
                  <button
                    onClick={() => handleDelete(appointment.id)}
                    className="text-gray-600 hover:text-gray-900"
                  >
                    <Trash2 className="w-4 h-4" />
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
};

export default AppointmentsView;
