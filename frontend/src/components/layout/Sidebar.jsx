import { NavLink } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import {
  LayoutDashboard, Users, GraduationCap, Trophy, Megaphone,
  Building2, Shield, LogOut, ChevronLeft, ChevronRight
} from 'lucide-react';
import { useState } from 'react';

export default function Sidebar() {
  const { user, logout, isAdmin, isTeacher, isStudent } = useAuth();
  const [collapsed, setCollapsed] = useState(false);

  const navItems = [];

  if (isAdmin) {
    navItems.push(
      { to: '/dashboard', icon: LayoutDashboard, label: 'Dashboard' },
      { to: '/students', icon: Users, label: 'Students' },
      { to: '/teachers', icon: GraduationCap, label: 'Teachers' },
      { to: '/clubs', icon: Building2, label: 'Clubs' },
      { to: '/achievements', icon: Trophy, label: 'Achievements' },
      { to: '/announcements', icon: Megaphone, label: 'Announcements' },
      { to: '/admin/audit-logs', icon: Shield, label: 'Audit Logs' }
    );
  } else if (isTeacher) {
    navItems.push(
      { to: '/dashboard', icon: LayoutDashboard, label: 'Dashboard' },
      { to: '/students', icon: Users, label: 'Students' },
      { to: '/clubs', icon: Building2, label: 'Clubs' },
      { to: '/achievements', icon: Trophy, label: 'Achievements' },
      { to: '/announcements', icon: Megaphone, label: 'Announcements' }
    );
  } else if (isStudent) {
    navItems.push(
      { to: '/dashboard', icon: LayoutDashboard, label: 'Dashboard' },
      { to: '/clubs', icon: Building2, label: 'Clubs' },
      { to: '/achievements', icon: Trophy, label: 'My Achievements' },
      { to: '/announcements', icon: Megaphone, label: 'Announcements' }
    );
  }

  return (
    <aside className={`fixed left-0 top-0 h-screen bg-navy-950 text-white z-40 transition-all duration-300 flex flex-col ${collapsed ? 'w-16' : 'w-64'}`}>
      <div className="flex items-center gap-3 p-4 border-b border-navy-800">
        {!collapsed && (
          <div className="flex-1 min-w-0">
            <h1 className="font-bold text-lg truncate">CSE Connect</h1>
            <p className="text-xs text-navy-300 truncate">MITWPU</p>
          </div>
        )}
        <button
          onClick={() => setCollapsed(!collapsed)}
          className="p-1.5 hover:bg-navy-800 rounded-lg transition-colors flex-shrink-0"
        >
          {collapsed ? <ChevronRight size={18} /> : <ChevronLeft size={18} />}
        </button>
      </div>

      <nav className="flex-1 py-4 overflow-y-auto">
        {navItems.map((item) => (
          <NavLink
            key={item.to}
            to={item.to}
            className={({ isActive }) =>
              `flex items-center gap-3 px-4 py-2.5 mx-2 rounded-xl transition-all duration-200 ${
                isActive
                  ? 'bg-navy-700 text-white shadow-md'
                  : 'text-navy-300 hover:text-white hover:bg-navy-800/50'
              }`
            }
          >
            <item.icon size={20} className="flex-shrink-0" />
            {!collapsed && <span className="text-sm font-medium truncate">{item.label}</span>}
          </NavLink>
        ))}
      </nav>

      <div className="border-t border-navy-800 p-4">
        {!collapsed && (
          <div className="mb-3 px-2">
            <p className="text-sm font-medium truncate">{user?.name}</p>
            <p className="text-xs text-navy-400 truncate">{user?.role}</p>
          </div>
        )}
        <button
          onClick={logout}
          className="flex items-center gap-3 w-full px-3 py-2 rounded-xl text-navy-300 hover:text-white hover:bg-danger-600/20 transition-all"
        >
          <LogOut size={18} />
          {!collapsed && <span className="text-sm">Logout</span>}
        </button>
      </div>
    </aside>
  );
}
