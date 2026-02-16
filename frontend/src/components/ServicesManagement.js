import React, { useMemo, useState } from 'react';
import { useAppStore } from '../store/appStore';
import { useAuthStore } from '../store/authStore';
import toast from 'react-hot-toast';
import { Plus, Trash2, Edit2, QrCode, Copy } from 'lucide-react';

const ServicesManagement = () => {
  const services = useAppStore((state) => state.services);
  const createService = useAppStore((state) => state.createService);
  const updateService = useAppStore((state) => state.updateService);
  const deleteService = useAppStore((state) => state.deleteService);
  const isLoading = useAppStore((state) => state.isLoading);
  const user = useAuthStore((state) => state.user);

  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingId, setEditingId] = useState(null);
  const [qrService, setQrService] = useState(null);
  const [formData, setFormData] = useState({
    name: '',
    durationMinutes: 30,
    price: '',
  });

  const bookingLink = useMemo(() => {
    if (!qrService || !user?.id) {
      return '';
    }

    return `${window.location.origin}/book/${user.id}?serviceId=${qrService.id}`;
  }, [qrService, user]);

  const qrImageUrl = useMemo(() => {
    if (!bookingLink) {
      return '';
    }

    return `https://api.qrserver.com/v1/create-qr-code/?size=280x280&data=${encodeURIComponent(bookingLink)}`;
  }, [bookingLink]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]: name === 'durationMinutes' ? parseInt(value, 10) : value,
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      if (editingId) {
        await updateService(editingId, formData);
        toast.success('Service updated successfully');
      } else {
        await createService(formData);
        toast.success('Service created successfully');
      }
      setFormData({ name: '', durationMinutes: 30, price: '' });
      setEditingId(null);
      setIsModalOpen(false);
    } catch {
      toast.error('Failed to save service');
    }
  };

  const handleEdit = (service) => {
    setEditingId(service.id);
    setFormData({
      name: service.name,
      durationMinutes: service.durationMinutes,
      price: service.price.toString(),
    });
    setIsModalOpen(true);
  };

  const handleDelete = async (id) => {
    if (window.confirm('Are you sure?')) {
      try {
        await deleteService(id);
        toast.success('Service deleted');
      } catch {
        toast.error('Failed to delete service');
      }
    }
  };

  const handleCopyLink = async () => {
    if (!bookingLink) {
      return;
    }

    try {
      await navigator.clipboard.writeText(bookingLink);
      toast.success('Booking link copied');
    } catch {
      toast.error('Could not copy link');
    }
  };

  return (
    <div>
      <div className="flex justify-between items-center mb-6">
        <h2 className="text-2xl font-bold text-gray-900">Services</h2>
        <button
          onClick={() => {
            setEditingId(null);
            setFormData({ name: '', durationMinutes: 30, price: '' });
            setIsModalOpen(true);
          }}
          className="flex items-center bg-indigo-600 text-white px-4 py-2 rounded-md hover:bg-indigo-700 transition"
        >
          <Plus className="w-4 h-4 mr-2" />
          Add Service
        </button>
      </div>

      <div className="bg-white rounded-lg shadow overflow-hidden">
        <table className="w-full">
          <thead className="bg-gray-50 border-b">
            <tr>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-700 uppercase">Name</th>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-700 uppercase">Duration</th>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-700 uppercase">Price</th>
              <th className="px-6 py-3 text-right text-xs font-medium text-gray-700 uppercase">Actions</th>
            </tr>
          </thead>
          <tbody className="divide-y">
            {services.map((service) => (
              <tr key={service.id} className="hover:bg-gray-50">
                <td className="px-6 py-4 text-sm text-gray-900">{service.name}</td>
                <td className="px-6 py-4 text-sm text-gray-900">{service.durationMinutes} min</td>
                <td className="px-6 py-4 text-sm text-gray-900">${parseFloat(service.price).toFixed(2)}</td>
                <td className="px-6 py-4 text-right">
                  <button
                    onClick={() => setQrService(service)}
                    className="text-emerald-600 hover:text-emerald-800 mr-4"
                    title="Show QR"
                  >
                    <QrCode className="w-4 h-4" />
                  </button>
                  <button
                    onClick={() => handleEdit(service)}
                    className="text-indigo-600 hover:text-indigo-900 mr-4"
                    title="Edit"
                  >
                    <Edit2 className="w-4 h-4" />
                  </button>
                  <button
                    onClick={() => handleDelete(service.id)}
                    className="text-red-600 hover:text-red-900"
                    title="Delete"
                  >
                    <Trash2 className="w-4 h-4" />
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      {isModalOpen && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
          <div className="bg-white rounded-lg p-6 max-w-md w-full mx-4">
            <h3 className="text-lg font-semibold mb-4">
              {editingId ? 'Edit Service' : 'Add New Service'}
            </h3>
            <form onSubmit={handleSubmit} className="space-y-4">
              <div>
                <label className="block text-sm font-medium text-gray-700">Service Name</label>
                <input
                  type="text"
                  name="name"
                  value={formData.name}
                  onChange={handleChange}
                  required
                  className="mt-1 w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-indigo-500 focus:border-indigo-500"
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700">Duration (minutes)</label>
                <input
                  type="number"
                  name="durationMinutes"
                  value={formData.durationMinutes}
                  onChange={handleChange}
                  required
                  min="1"
                  className="mt-1 w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-indigo-500 focus:border-indigo-500"
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700">Price ($)</label>
                <input
                  type="number"
                  name="price"
                  value={formData.price}
                  onChange={handleChange}
                  required
                  step="0.01"
                  className="mt-1 w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-indigo-500 focus:border-indigo-500"
                />
              </div>
              <div className="flex gap-2 pt-4">
                <button
                  type="button"
                  onClick={() => setIsModalOpen(false)}
                  className="flex-1 px-4 py-2 border border-gray-300 rounded-md text-gray-700 hover:bg-gray-50"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  disabled={isLoading}
                  className="flex-1 px-4 py-2 bg-indigo-600 text-white rounded-md hover:bg-indigo-700 disabled:opacity-50"
                >
                  {isLoading ? 'Saving...' : 'Save'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {qrService && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
          <div className="bg-white rounded-lg p-6 max-w-md w-full mx-4">
            <h3 className="text-lg font-semibold text-gray-900">Booking QR Code</h3>
            <p className="text-sm text-gray-600 mt-1">{qrService.name}</p>

            <div className="mt-4 flex justify-center">
              <img src={qrImageUrl} alt="Service booking QR code" className="w-64 h-64 border rounded" />
            </div>

            <div className="mt-4">
              <label className="block text-xs font-medium text-gray-600 mb-1">Booking Link</label>
              <input
                type="text"
                readOnly
                value={bookingLink}
                className="w-full text-xs px-3 py-2 border border-gray-300 rounded bg-gray-50"
              />
            </div>

            <div className="mt-4 flex gap-2">
              <button
                type="button"
                onClick={handleCopyLink}
                className="flex-1 inline-flex items-center justify-center px-4 py-2 border border-gray-300 rounded-md text-gray-700 hover:bg-gray-50"
              >
                <Copy className="w-4 h-4 mr-2" />
                Copy Link
              </button>
              <button
                type="button"
                onClick={() => setQrService(null)}
                className="flex-1 px-4 py-2 bg-indigo-600 text-white rounded-md hover:bg-indigo-700"
              >
                Close
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default ServicesManagement;