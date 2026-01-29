import { useEffect, useMemo, useState } from "react";
import { Client } from "@stomp/stompjs";
import SockJS from "sockjs-client";
import { apiRequest, API_BASE } from "../lib/api";

export default function Alerts() {
  const [alerts, setAlerts] = useState([]);
  const [error, setError] = useState("");
  const socketUrl = useMemo(() => `${API_BASE}/ws`, []);

  const load = async () => {
    try {
      const data = await apiRequest("/api/notifications");
      setAlerts(data);
    } catch (err) {
      setError(err.message || "Failed to load alerts");
    }
  };

  useEffect(() => {
    load();
  }, []);

  useEffect(() => {
    const token = localStorage.getItem("token");
    const client = new Client({
      webSocketFactory: () => new SockJS(socketUrl),
      reconnectDelay: 5000,
      connectHeaders: token ? { Authorization: `Bearer ${token}` } : {},
      onConnect: () => {
        client.subscribe("/topic/alerts", (message) => {
          const update = JSON.parse(message.body);
          setAlerts((prev) => [update, ...prev].slice(0, 50));
        });
      },
    });
    client.activate();
    return () => client.deactivate();
  }, [socketUrl]);

  const markRead = async (id) => {
    try {
      await apiRequest(`/api/notifications/${id}/read`, { method: "POST" });
      setAlerts((prev) => prev.map((alert) => (alert.id === id ? { ...alert, readAt: new Date().toISOString() } : alert)));
    } catch (err) {
      setError(err.message || "Failed to mark read");
    }
  };

  return (
    <div className="space-y-6">
      <header>
        <h2 className="text-2xl font-semibold">Alerts</h2>
        <p className="text-slate-600">Real-time notifications and low stock warnings.</p>
      </header>

      {error && (
        <div className="rounded-lg border border-rose-200 bg-rose-50 p-4 text-rose-600">
          {error}
        </div>
      )}

      <div className="space-y-3">
        {alerts.map((alert) => (
          <div key={alert.id} className="rounded-2xl border border-slate-100 bg-white p-4 shadow-sm">
            <div className="flex items-center justify-between">
              <p className="text-sm font-semibold text-slate-700">
                {alert.title}
              </p>
              <span className={`text-xs ${alert.readAt ? "text-slate-400" : "text-amber-600"}`}>
                {alert.readAt ? "Read" : "New"}
              </span>
            </div>
            <p className="text-xs text-slate-500">{alert.message}</p>
            {!alert.readAt && (
              <button
                className="mt-2 text-xs text-blue-600"
                onClick={() => markRead(alert.id)}
              >
                Mark read
              </button>
            )}
          </div>
        ))}
      </div>
    </div>
  );
}
