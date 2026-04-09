import { useEffect, useState, useCallback } from 'react';
import { usePaginatedApi } from '../hooks/useApi';
import { adminApi } from '../api/adminApi';
import TopBar from '../components/layout/TopBar';
import DataTable from '../components/ui/DataTable';
import Badge from '../components/ui/Badge';
import { format } from 'date-fns';

export default function AuditLogsPage() {
  const { data, loading, execute } = usePaginatedApi(adminApi.getAuditLogs);
  const [page, setPage] = useState(0);

  const fetchLogs = useCallback(() => {
    execute({ page, size: 30 });
  }, [execute, page]);

  useEffect(() => { fetchLogs(); }, [fetchLogs]);

  const actionVariant = (action) => {
    switch (action) {
      case 'CREATE': return 'success';
      case 'DELETE': return 'danger';
      case 'UPDATE': return 'info';
      case 'LOGIN': case 'LOGOUT': return 'purple';
      case 'VERIFY': return 'success';
      case 'REJECT': return 'danger';
      case 'BULK_IMPORT': return 'accent';
      case 'PASSWORD_RESET': return 'warning';
      default: return 'gray';
    }
  };

  const columns = [
    { key: 'createdAt', label: 'Timestamp', render: (row) => row.createdAt ? format(new Date(row.createdAt), 'MMM dd, yyyy HH:mm:ss') : '-' },
    { key: 'actorName', label: 'Actor', render: (row) => <span>{row.actorName || 'System'} <span className="text-xs text-gray-400">({row.actorRole})</span></span> },
    { key: 'action', label: 'Action', render: (row) => <Badge variant={actionVariant(row.action)}>{row.action}</Badge> },
    { key: 'entityName', label: 'Entity', render: (row) => <span>{row.entityName} {row.entityId ? `#${row.entityId}` : ''}</span> },
    { key: 'description', label: 'Description' },
    { key: 'ipAddress', label: 'IP' },
  ];

  return (
    <>
      <TopBar title="Audit Logs" />
      <div className="p-6">
        <div className="glass-card">
          <DataTable
            columns={columns}
            data={data.content || []}
            loading={loading}
            page={page}
            totalPages={data.totalPages || 0}
            onPageChange={setPage}
            emptyMessage="No audit logs"
          />
        </div>
      </div>
    </>
  );
}
