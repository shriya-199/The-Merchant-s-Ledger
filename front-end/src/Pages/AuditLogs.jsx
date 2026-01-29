import { useEffect, useState } from "react";
import { apiRequest } from "../lib/api";

export default function AuditLogs() {
  const [logs, setLogs] = useState([]);
  const [error, setError] = useState("");

  useEffect(() => {
    const load = async () => {
      try {
        const data = await apiRequest("/api/audit");
        setLogs(data);
      } catch (err) {
        setError(err.message || "Failed to load audit logs");
      }
    };

    load();
  }, []);

  return (
    <div className="space-y-6">
      <header>
        <h2 className="text-2xl font-semibold">Audit Logs</h2>
        <p className="text-slate-600">Trace activity and events across the system.</p>
      </header>

      {error && (
        <div className="rounded-lg border border-rose-200 bg-rose-50 p-4 text-rose-600">
          {error}
        </div>
      )}

      <div className="rounded-2xl bg-white p-6 shadow-sm">
        <table className="min-w-full text-left text-sm">
          <thead className="border-b border-slate-200 text-xs uppercase text-slate-400">
            <tr>
              <th className="py-2">Action</th>
              <th className="py-2">Actor</th>
              <th className="py-2">Entity</th>
              <th className="py-2">Meta</th>
              <th className="py-2">Time</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-slate-100">
            {logs.map((log) => (
              <tr key={log.id}>
                <td className="py-3 font-medium text-slate-700">{log.action}</td>
                <td className="py-3 text-slate-500">{log.actorEmail}</td>
                <td className="py-3 text-slate-500">{log.entityType} {log.entityId}</td>
                <td className="py-3 text-slate-400">{log.metadata || "-"}</td>
                <td className="py-3 text-slate-400">{log.createdAt ? new Date(log.createdAt).toLocaleString() : "-"}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}
