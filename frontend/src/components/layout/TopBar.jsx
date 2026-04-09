import { useAuth } from '../../context/AuthContext';
import { Bell, User } from 'lucide-react';

export default function TopBar({ title }) {
  const { user } = useAuth();

  return (
    <header className="h-16 bg-white/80 backdrop-blur-lg border-b border-gray-100 flex items-center justify-between px-6 sticky top-0 z-30">
      <h2 className="text-xl font-semibold text-gray-800">{title}</h2>
      <div className="flex items-center gap-4">
        <button className="p-2 hover:bg-gray-100 rounded-xl transition-colors relative">
          <Bell size={20} className="text-gray-500" />
        </button>
        <div className="flex items-center gap-3 pl-4 border-l border-gray-200">
          <div className="w-8 h-8 bg-navy-100 rounded-full flex items-center justify-center">
            <User size={16} className="text-navy-600" />
          </div>
          <div className="hidden sm:block">
            <p className="text-sm font-medium text-gray-700">{user?.name}</p>
            <p className="text-xs text-gray-400">{user?.role}</p>
          </div>
        </div>
      </div>
    </header>
  );
}
