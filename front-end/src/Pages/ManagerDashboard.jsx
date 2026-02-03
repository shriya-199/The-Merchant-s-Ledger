import { useEffect, useState } from "react";
import { apiRequest } from "../lib/api";

export default function ManagerDashboard() {
  const [summary, setSummary] = useState(null);
  const [movements, setMovements] = useState([]);
  const [lowStock, setLowStock] = useState([]);
  const [error, setError] = useState("");

  useEffect(() => {
    const load = async () => {
      try {
        const [summaryData, movementData, lowStockData] = await Promise.all([
          apiRequest("/api/inventory/summary"),
          apiRequest("/api/inventory/movements"),
          apiRequest("/api/inventory/low-stock"),
        ]);
        setSummary(summaryData);
        setMovements(movementData);
        setLowStock(lowStockData);
      } catch (err) {
        setError(err.message || "Failed to load manager dashboard");
      }
    };

    load();
  }, []);

  return (
    <div className="space-y-8">
      <header className="rounded-2xl bg-white p-6 shadow-sm">
        <h2 className="text-2xl font-semibold">Manager Dashboard</h2>
        <p className="text-slate-600">Monitor movements, alerts, and activity health.</p>
      </header>

      {error && (
        <div className="rounded-lg border border-rose-200 bg-rose-50 p-4 text-rose-600">
          {error}
        </div>
      )}

      <section className="grid gap-4 md:grid-cols-3">
        <SummaryCard label="Warehouses" value={summary?.warehouseCount ?? "-"} />
        <SummaryCard label="Products" value={summary?.productCount ?? "-"} />
        <SummaryCard label="Total Units" value={summary?.totalUnits ?? "-"} />
      </section>

      <section className="grid gap-6 lg:grid-cols-[1fr_1fr]">
        <div className="rounded-2xl bg-white p-6 shadow-sm">
          <h3 className="text-lg font-semibold">Recent movements</h3>
          <div className="mt-4 space-y-3">
            {movements.slice(0, 8).map((movement) => (
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
          <h3 className="text-lg font-semibold">Low stock alerts</h3>
          <div className="mt-4 space-y-3">
            {lowStock.length === 0 && (
              <p className="text-sm text-slate-500">All items above reorder level.</p>
            )}
            {lowStock.map((item) => (
              <div key={`${item.warehouseId}-${item.productId}`} className="rounded-lg border border-amber-100 bg-amber-50 p-3">
                <p className="text-sm font-semibold text-amber-700">
                  {item.productName} · {item.sku}
                </p>
                <p className="text-xs text-amber-700/80">
                  {item.warehouseName} · {item.quantity} / reorder {item.reorderLevel}
                </p>
              </div>
            ))}
          </div>
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

