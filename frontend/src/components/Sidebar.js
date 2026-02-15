import React from 'react';
import { useNavigate } from 'react-router-dom';
import {
  BarChart,
  Calendar,
  Clock,
  Settings,
  Home,
  LogOut,
  Menu,
} from 'lucide-react';
import { useAuthStore } from '../store/authStore';

const Sidebar = ({ activeTab, setActiveTab }) => {
  const navigate = useNavigate();
  const logout = useAuthStore((state) => state.logout);
  const [isOpen, setIsOpen] = React.useState(true);

  const menuItems = [
    { id: 'overview', label: 'Overview', icon: Home },
    { id: 'appointments', label: 'Appointments', icon: Calendar },
    { id: 'services', label: 'Services', icon: Clock },
    { id: 'availability', label: 'Availability', icon: Clock },
    { id: 'analytics', label: 'Analytics', icon: BarChart },
  ];

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  return (
    <div className={`${isOpen ? 'w-64' : 'w-20'} bg-indigo-600 text-white transition-all duration-300`}>
      <div className="flex items-center justify-between p-4">
        <h1 className={`font-bold text-xl ${!isOpen && 'hidden'}`}>mawa3id</h1>
        <button
          onClick={() => setIsOpen(!isOpen)}
          className="p-1 hover:bg-indigo-700 rounded"
        >
          <Menu className="w-5 h-5" />
        </button>
      </div>

      <nav className="mt-8 space-y-2">
        {menuItems.map((item) => {
          const Icon = item.icon;
          return (
            <button
              key={item.id}
              onClick={() => setActiveTab(item.id)}
              className={`w-full flex items-center px-4 py-3 rounded transition ${
                activeTab === item.id
                  ? 'bg-indigo-700'
                  : 'hover:bg-indigo-700'
              }`}
              title={item.label}
            >
              <Icon className="w-5 h-5" />
              <span className={`ml-3 ${!isOpen && 'hidden'}`}>{item.label}</span>
            </button>
          );
        })}
      </nav>

      <div className="absolute bottom-0 w-full p-4 border-t border-indigo-500">
        <button
          onClick={() => navigate('/settings')}
          className="w-full flex items-center px-4 py-3 rounded hover:bg-indigo-700 transition mb-2"
          title="Settings"
        >
          <Settings className="w-5 h-5" />
          <span className={`ml-3 ${!isOpen && 'hidden'}`}>Settings</span>
        </button>
        <button
          onClick={handleLogout}
          className="w-full flex items-center px-4 py-3 rounded hover:bg-red-700 transition"
          title="Logout"
        >
          <LogOut className="w-5 h-5" />
          <span className={`ml-3 ${!isOpen && 'hidden'}`}>Logout</span>
        </button>
      </div>
    </div>
  );
};

export default Sidebar;
