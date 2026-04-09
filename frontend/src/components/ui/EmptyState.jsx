import { Inbox } from 'lucide-react';

export default function EmptyState({ title = 'No data', message = 'Nothing to show here yet.', icon: Icon = Inbox }) {
  return (
    <div className="flex flex-col items-center justify-center py-16 text-gray-400">
      <Icon size={48} className="mb-4 opacity-50" />
      <h3 className="text-lg font-medium text-gray-500">{title}</h3>
      <p className="text-sm mt-1">{message}</p>
    </div>
  );
}
