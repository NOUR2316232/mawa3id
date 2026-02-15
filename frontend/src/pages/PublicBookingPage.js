import React, { useMemo, useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import toast from 'react-hot-toast';
import { publicBookingAPI } from '../api/client';

const dayNames = ['Sunday', 'Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday'];

const PublicBookingPage = () => {
  const { businessId } = useParams();

  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [profile, setProfile] = useState(null);

  const [form, setForm] = useState({
    serviceId: '',
    appointmentDate: '',
    startTime: '',
    customerName: '',
    customerPhone: '',
  });

  useEffect(() => {
    const load = async () => {
      try {
        const response = await publicBookingAPI.getProfile(businessId);
        setProfile(response.data);
      } catch (error) {
        toast.error(error.response?.data?.message || 'Could not load booking page');
      } finally {
        setLoading(false);
      }
    };

    load();
  }, [businessId]);

  const selectedService = useMemo(
    () => profile?.services?.find((service) => service.id === form.serviceId),
    [profile, form.serviceId]
  );

  const availableDayText = useMemo(() => {
    if (!profile?.availability?.length) {
      return 'No availability configured yet.';
    }

    return profile.availability
      .map((slot) => `${dayNames[slot.dayOfWeek]} ${slot.startTime} - ${slot.endTime}`)
      .join(' | ');
  }, [profile]);

  const handleChange = (event) => {
    const { name, value } = event.target;
    setForm((prev) => ({ ...prev, [name]: value }));
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    setSubmitting(true);

    try {
      await publicBookingAPI.createAppointment(businessId, {
        serviceId: form.serviceId,
        customerName: form.customerName,
        customerPhone: form.customerPhone,
        appointmentDate: form.appointmentDate,
        startTime: form.startTime,
      });

      toast.success('Appointment request sent successfully');
      setForm({
        serviceId: '',
        appointmentDate: '',
        startTime: '',
        customerName: '',
        customerPhone: '',
      });
    } catch (error) {
      toast.error(error.response?.data?.message || 'Could not create appointment');
    } finally {
      setSubmitting(false);
    }
  };

  if (loading) {
    return <div className="min-h-screen flex items-center justify-center">Loading booking page...</div>;
  }

  if (!profile) {
    return <div className="min-h-screen flex items-center justify-center">Booking page unavailable.</div>;
  }

  return (
    <div className="min-h-screen bg-gray-100 py-10 px-4">
      <div className="max-w-2xl mx-auto bg-white rounded-xl shadow p-6">
        <h1 className="text-3xl font-bold text-gray-900">Book An Appointment</h1>
        <p className="text-gray-600 mt-1">{profile.businessName}</p>
        <p className="text-gray-500 text-sm mt-1">Contact: {profile.phone}</p>

        <div className="mt-4 p-3 bg-gray-50 rounded border text-sm text-gray-700">
          <strong>Working hours:</strong> {availableDayText}
        </div>

        <form onSubmit={handleSubmit} className="mt-6 space-y-4">
          <div>
            <label className="block text-sm font-medium text-gray-700">Service</label>
            <select
              name="serviceId"
              value={form.serviceId}
              onChange={handleChange}
              required
              className="mt-1 w-full border border-gray-300 rounded-md px-3 py-2"
            >
              <option value="">Select a service</option>
              {(profile.services || []).map((service) => (
                <option key={service.id} value={service.id}>
                  {service.name} ({service.durationMinutes} min) - {service.price}
                </option>
              ))}
            </select>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div>
              <label className="block text-sm font-medium text-gray-700">Date</label>
              <input
                type="date"
                name="appointmentDate"
                value={form.appointmentDate}
                onChange={handleChange}
                required
                className="mt-1 w-full border border-gray-300 rounded-md px-3 py-2"
              />
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700">Start Time</label>
              <input
                type="time"
                name="startTime"
                value={form.startTime}
                onChange={handleChange}
                required
                className="mt-1 w-full border border-gray-300 rounded-md px-3 py-2"
              />
            </div>
          </div>

          {selectedService && (
            <p className="text-sm text-gray-500">
              Duration: {selectedService.durationMinutes} minutes (end time is calculated automatically)
            </p>
          )}

          <div>
            <label className="block text-sm font-medium text-gray-700">Your Name</label>
            <input
              type="text"
              name="customerName"
              value={form.customerName}
              onChange={handleChange}
              required
              className="mt-1 w-full border border-gray-300 rounded-md px-3 py-2"
            />
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700">Phone Number</label>
            <input
              type="tel"
              name="customerPhone"
              value={form.customerPhone}
              onChange={handleChange}
              required
              className="mt-1 w-full border border-gray-300 rounded-md px-3 py-2"
            />
          </div>

          <button
            type="submit"
            disabled={submitting}
            className="w-full bg-indigo-600 text-white py-2 rounded-md hover:bg-indigo-700 disabled:opacity-50"
          >
            {submitting ? 'Booking...' : 'Book Appointment'}
          </button>
        </form>
      </div>
    </div>
  );
};

export default PublicBookingPage;