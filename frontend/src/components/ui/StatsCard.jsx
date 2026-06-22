import { motion } from 'framer-motion';

export default function StatsCard({ title, value, icon: Icon, color = 'navy', subtitle }) {
  const colorMap = {
    navy: 'from-navy-500 to-navy-700',
    accent: 'from-accent-400 to-accent-600',
    success: 'from-success-400 to-success-600',
    warning: 'from-warning-400 to-warning-600',
    danger: 'from-danger-400 to-danger-600',
    purple: 'from-purple-400 to-purple-600',
    teal: 'from-teal-400 to-teal-600',
  };

  return (
    <motion.div
      initial={{ opacity: 0, y: 20 }}
      animate={{ opacity: 1, y: 0 }}
      className="glass-card p-6"
    >
      <div className="flex items-center justify-between">
        <div>
          <p className="text-sm text-gray-500 font-medium">{title}</p>
          <p className="text-3xl font-bold text-gray-800 mt-1">{value}</p>
          {subtitle && <p className="text-xs text-gray-400 mt-1">{subtitle}</p>}
        </div>
        <div className={`p-3 rounded-xl bg-gradient-to-br ${colorMap[color] || colorMap.navy}`}>
          <Icon size={24} className="text-white" />
        </div>
      </div>
    </motion.div>
  );
}
