import { useEffect, useMemo, useState } from "react";
import { apiRequest } from "../lib/api";

const DASHBOARD_CONFIG = {
  "system-admin": {
    title: "System Admin Dashboard",
    subtitle: "Platform-wide control, tenant health, and service operations.",
    theme: "bg-indigo-50 text-indigo-700",
    focus: ["Tenant Governance", "Security Controls", "Service Uptime"],
  },
  "support-agent": {
    title: "Support Dashboard",
    subtitle: "Handle disputes and troubleshoot merchant transaction traces.",
    theme: "bg-sky-50 text-sky-700",
    focus: ["Dispute Queue", "User Support", "Traceability"],
  },
  "automation-bot": {
    title: "Automation Dashboard",
    subtitle: "Monitor scheduled checks, anomaly detections, and auto-actions.",
    theme: "bg-fuchsia-50 text-fuchsia-700",
    focus: ["Reconciliation Jobs", "Anomaly Alerts", "Auto Restock Rules"],
  },
  "merchant-admin": {
    title: "Merchant Admin Dashboard",
    subtitle: "Full merchant view across inventory, settlements, and audits.",
    theme: "bg-emerald-50 text-emerald-700",
    focus: ["Inventory Control", "Settlement Visibility", "Audit Health"],
  },
  "merchant-finance": {
    title: "Merchant Finance Dashboard",
    subtitle: "Track credits/debits, settlement flow, and fee transparency.",
    theme: "bg-amber-50 text-amber-700",
    focus: ["Financial Ledger", "Payout Reconciliation", "Fee Monitoring"],
  },
  "merchant-operations": {
    title: "Merchant Operations Dashboard",
    subtitle: "Operational command center for stock, transfers, and replenishment.",
    theme: "bg-cyan-50 text-cyan-700",
    focus: ["Live Stock", "Transfers", "Replenishment"],
  },
  "merchant-viewer": {
    title: "Merchant Viewer Dashboard",
    subtitle: "Read-only visibility into business performance and stock health.",
    theme: "bg-slate-100 text-slate-700",
    focus: ["Snapshots", "Reports", "Alerts"],
  },
  "warehouse-manager": {
    title: "Warehouse Manager Dashboard",
    subtitle: "Oversee receiving, picking, dispatch, and zone performance.",
    theme: "bg-orange-50 text-orange-700",
    focus: ["Queue Balancing", "Capacity Oversight", "Shift Performance"],
  },
  "inventory-auditor": {
    title: "Inventory Auditor Dashboard",
    subtitle: "Cycle count planning, variance detection, and approvals.",
    theme: "bg-violet-50 text-violet-700",
    focus: ["Cycle Count", "Variance Approval", "Audit Trail"],
  },
  "picker-packer": {
    title: "Picker/Packer Dashboard",
    subtitle: "Mobile-first task view for pick-pack-dispatch compliance.",
    theme: "bg-teal-50 text-teal-700",
    focus: ["Pick Tasks", "Scan Validation", "Exception Flags"],
  },
  receiver: {
    title: "Receiver / GRN Dashboard",
    subtitle: "Manage receiving, barcode verification, and bin putaway.",
    theme: "bg-lime-50 text-lime-700",
    focus: ["Inbound GRN", "Putaway Rules", "Receiving Accuracy"],
  },
  admin: {
    title: "Legacy Admin Dashboard",
    subtitle: "Administrative view for user and operational oversight.",
    theme: "bg-red-50 text-red-700",
    focus: ["User Control", "Audit Oversight", "Critical Alerts"],
  },
  manager: {
    title: "Legacy Manager Dashboard",
    subtitle: "Team-level oversight for execution and stock safety.",
    theme: "bg-blue-50 text-blue-700",
    focus: ["Execution", "Team Queue", "Low Stock"],
  },
  staff: {
    title: "Legacy Staff Dashboard",
    subtitle: "Daily stock status and immediate action items.",
    theme: "bg-green-50 text-green-700",
    focus: ["Daily Tasks", "Low Stock", "Escalations"],
  },
  user: {
    title: "User Dashboard",
    subtitle: "Read-only business and inventory snapshots.",
    theme: "bg-gray-100 text-gray-700",
    focus: ["Overview", "Recent Activity", "Notifications"],
  },
};

export default function RoleDashboard({ roleKey = "merchant-operations" }) {
  const config = DASHBOARD_CONFIG[roleKey] || DASHBOARD_CONFIG["merchant-operations"];
  const [data, setData] = useState({
    summary: null,
    lowStock: [],
    movements: [],
    sessions: [],
    ledger: [],
  });
  const [error, setError] = useState("");

  useEffect(() => {
    const load = async () => {
      const [summary, lowStock, movements, sessions, ledger] = await Promise.allSettled([
        apiRequest("/api/inventory/summary"),
        apiRequest("/api/inventory/low-stock"),
        apiRequest("/api/inventory/movements"),
        apiRequest("/api/reconciliation/sessions"),
        apiRequest("/api/ledger"),
      ]);

      setData({
        summary: summary.status === "fulfilled" ? summary.value : null,
        lowStock: lowStock.status === "fulfilled" ? lowStock.value : [],
        movements: movements.status === "fulfilled" ? movements.value : [],
        sessions: sessions.status === "fulfilled" ? sessions.value : [],
        ledger: ledger.status === "fulfilled" ? ledger.value : [],
      });

      if (
        summary.status === "rejected" &&
        lowStock.status === "rejected" &&
        movements.status === "rejected" &&
        sessions.status === "rejected" &&
        ledger.status === "rejected"
      ) {
        setError("Dashboard data unavailable for your role.");
      }
    };

    load().catch((err) => setError(err.message || "Failed to load dashboard"));
  }, [roleKey]);

  const topMovements = useMemo(() => data.movements.slice(0, 5), [data.movements]);
  const topLedger = useMemo(() => data.ledger.slice(0, 5), [data.ledger]);

  return (
    <div className="space-y-8">
      <header className="rounded-2xl bg-white p-6 shadow-sm">
        <h2 className="text-2xl font-semibold">{config.title}</h2>
        <p className="text-slate-600">{config.subtitle}</p>
        <div className="mt-4 flex flex-wrap gap-2">
          {config.focus.map((item) => (
            <span key={item} className={`rounded-full px-3 py-1 text-xs font-semibold ${config.theme}`}>
              {item}
            </span>
          ))}
        </div>
      </header>

      {error && (
        <div className="rounded-lg border border-rose-200 bg-rose-50 p-4 text-rose-600">
          {error}
        </div>
      )}

      <section className="grid gap-4 md:grid-cols-4">
        <MetricCard label="Warehouses" value={data.summary?.warehouseCount ?? "-"} />
        <MetricCard label="Products" value={data.summary?.productCount ?? "-"} />
        <MetricCard label="Units" value={data.summary?.totalUnits ?? "-"} />
        <MetricCard label="Low Stock SKUs" value={data.lowStock.length} />
      </section>

      <section className="grid gap-6 lg:grid-cols-[1fr_1fr]">
        <div className="rounded-2xl bg-white p-6 shadow-sm">
          <h3 className="text-lg font-semibold">Recent Inventory Activity</h3>
          <div className="mt-4 space-y-3">
            {topMovements.length === 0 && <p className="text-sm text-slate-500">No recent movement data.</p>}
            {topMovements.map((movement) => (
              <div key={movement.id} className="rounded-lg border border-slate-100 p-3">
                <p className="text-sm font-semibold text-slate-700">
                  {movement.type} · {movement.productName}
                </p>
                <p className="text-xs text-slate-500">
                  {movement.fromWarehouseName || "-"} {"->"} {movement.toWarehouseName || "-"} {"·"} {movement.quantity} units
                </p>
              </div>
            ))}
          </div>
        </div>

        <div className="rounded-2xl bg-white p-6 shadow-sm">
          <h3 className="text-lg font-semibold">Role-Relevant Workload</h3>
          <div className="mt-4 space-y-3">
            <WorkloadRow label="Open Reconciliation Sessions" value={data.sessions.filter((s) => s.status !== "APPROVED").length} />
            <WorkloadRow label="Pending Approvals" value={data.sessions.filter((s) => s.status === "SUBMITTED").length} />
            <WorkloadRow label="Low Stock Alerts" value={data.lowStock.length} />
            <WorkloadRow label="Ledger Entries (Recent)" value={data.ledger.length} />
          </div>
        </div>
      </section>

      <section className="rounded-2xl bg-white p-6 shadow-sm">
        <h3 className="text-lg font-semibold">Latest Financial Activity</h3>
        <div className="mt-4 overflow-x-auto">
          <table className="min-w-full text-left text-sm">
            <thead className="border-b border-slate-200 text-xs uppercase text-slate-400">
              <tr>
                <th className="py-2">Customer</th>
                <th className="py-2">Type</th>
                <th className="py-2">Amount</th>
                <th className="py-2">Reference</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100">
              {topLedger.map((entry) => (
                <tr key={entry.id}>
                  <td className="py-2 text-slate-700">{entry.customerName || "-"}</td>
                  <td className="py-2 text-slate-600">{entry.type}</td>
                  <td className="py-2 text-slate-700">${Number(entry.amount || 0).toLocaleString()}</td>
                  <td className="py-2 text-slate-500">{entry.referenceType || "-"} {entry.referenceId || ""}</td>
                </tr>
              ))}
              {topLedger.length === 0 && (
                <tr>
                  <td colSpan={4} className="py-3 text-slate-500">No financial entries available for this role.</td>
                </tr>
              )}
            </tbody>
          </table>
        </div>
      </section>
    </div>
  );
}

function MetricCard({ label, value }) {
  return (
    <div className="rounded-2xl bg-white p-6 shadow-sm">
      <p className="text-sm text-slate-500">{label}</p>
      <p className="mt-3 text-2xl font-semibold text-slate-900">{value}</p>
    </div>
  );
}

function WorkloadRow({ label, value }) {
  return (
    <div className="flex items-center justify-between rounded-lg border border-slate-100 px-3 py-2 text-sm">
      <span className="text-slate-600">{label}</span>
      <span className="font-semibold text-slate-800">{value}</span>
    </div>
  );
}
