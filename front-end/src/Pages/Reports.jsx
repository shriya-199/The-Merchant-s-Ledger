import { useEffect, useState } from "react";
import { apiRequest, API_BASE } from "../lib/api";

export default function Reports() {
  const [analytics, setAnalytics] = useState(null);
  const [error, setError] = useState("");

  useEffect(() => {
    const load = async () => {
      try {
        const data = await apiRequest("/api/analytics");
        setAnalytics(data);
      } catch (err) {
        setError(err.message || "Failed to load analytics");
      }
    };

    load();
  }, []);

  const download = async (path, filename) => {
    const token = localStorage.getItem("token");
    const response = await fetch(`${API_BASE}${path}`, {
      headers: token ? { Authorization: `Bearer ${token}` } : {},
    });
    const blob = await response.blob();
    const url = URL.createObjectURL(blob);
    const link = document.createElement("a");
    link.href = url;
    link.download = filename;
    link.click();
    URL.revokeObjectURL(url);
  };

  return (
    <div className="space-y-8">
      <header>
        <h2 className="text-2xl font-semibold">Analytics & Reports</h2>
        <p className="text-slate-600">Decision-ready insights for inventory performance.</p>
      </header>

      {error && (
        <div className="rounded-lg border border-rose-200 bg-rose-50 p-4 text-rose-600">
          {error}
        </div>
      )}

      <section className="grid gap-4 md:grid-cols-4">
        <SummaryCard label="Total Movements" value={analytics?.totalMovements ?? "-"} />
        <SummaryCard label="Inbound" value={analytics?.inboundCount ?? "-"} />
        <SummaryCard label="Outbound" value={analytics?.outboundCount ?? "-"} />
        <SummaryCard label="Low Stock" value={analytics?.lowStockCount ?? "-"} />
      </section>

      <section className="rounded-2xl bg-white p-6 shadow-sm">
        <h3 className="text-lg font-semibold">Movement trend (7 days)</h3>
        <div className="mt-4 grid grid-cols-7 gap-3 text-xs text-slate-500">
          {analytics?.dailyMovements?.map((day) => (
            <div key={day.day} className="flex flex-col items-center gap-2">
              <div
                className="w-full rounded-md bg-slate-900/80"
                style={{ height: `${Math.max(day.count * 8, 8)}px` }}
              />
              <span>{day.day.slice(5)}</span>
            </div>
          ))}
        </div>
      </section>

      <section className="rounded-2xl bg-white p-6 shadow-sm">
        <h3 className="text-lg font-semibold">Exports</h3>
        <div className="mt-4 flex flex-wrap gap-3">
          <button
            className="rounded-lg border border-slate-200 px-4 py-2 text-sm"
            onClick={() => download("/api/exports/stock.csv", "stock.csv")}
          >
            Download Stock CSV
          </button>
          <button
            className="rounded-lg border border-slate-200 px-4 py-2 text-sm"
            onClick={() => download("/api/exports/movements.csv", "movements.csv")}
          >
            Download Movements CSV
          </button>
        </div>
      </section>
    </div>
  );
}

function SummaryCard({ label, value }) {
  return (
    <div className="rounded-2xl bg-white p-6 shadow-sm">
      <p className="text-sm text-slate-500">{label}</p>
      <p className="mt-3 text-2xl font-semibold text-slate-900">{value}</p>
    </div>
  );
}
