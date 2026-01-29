import { useEffect, useMemo, useState } from "react";
import { Client } from "@stomp/stompjs";
import SockJS from "sockjs-client";
import { apiRequest, API_BASE } from "../lib/api";
import { useAuth } from "../context/AuthContext";

export default function InventoryDashboard() {
  const [summary, setSummary] = useState(null);
  const [stock, setStock] = useState([]);
  const [movements, setMovements] = useState([]);
  const [lowStock, setLowStock] = useState([]);
  const [error, setError] = useState("");
  const { user } = useAuth();

  const socketUrl = useMemo(() => `${API_BASE}/ws`, []);

  const refreshSnapshot = async () => {
    const [summaryData, stockData, movementData, lowStockData] = await Promise.all([
      apiRequest("/api/inventory/summary"),
      apiRequest("/api/inventory/stock"),
      apiRequest("/api/inventory/movements"),
      apiRequest("/api/inventory/low-stock"),
    ]);
    setSummary(summaryData);
    setStock(stockData);
    setMovements(movementData);
    setLowStock(lowStockData);
  };

  useEffect(() => {
    const load = async () => {
      try {
        await refreshSnapshot();
      } catch (err) {
        setError(err.message || "Failed to load inventory dashboard");
      }
    };

    load();
  }, []);

  useEffect(() => {
    const token = localStorage.getItem("token");
    const client = new Client({
      webSocketFactory: () => new SockJS(socketUrl),
      reconnectDelay: 5000,
      connectHeaders: token ? { Authorization: `Bearer ${token}` } : {},
      onConnect: () => {
        client.subscribe("/topic/stock", (message) => {
          const update = JSON.parse(message.body);
          setMovements((prev) => [update, ...prev].slice(0, 20));
          refreshSnapshot().catch(() => {});
        });
      },
    });

    client.activate();
    return () => client.deactivate();
  }, [socketUrl]);

  const roles = user?.roles || [];

  return (
    <div className="space-y-8">
      <header className="rounded-2xl bg-white p-6 shadow-sm">
        <h2 className="text-2xl font-semibold">Real-time Inventory</h2>
        <p className="text-slate-600">
          Live stock movements across warehouses with instant visibility.
        </p>
      </header>

      {error && (
        <div className="rounded-lg border border-rose-200 bg-rose-50 p-4 text-rose-600">
          {error}
        </div>
      )}

      <section className="grid gap-4 md:grid-cols-4">
        <SummaryCard label="Warehouses" value={summary?.warehouseCount ?? "-"} />
        <SummaryCard label="Products" value={summary?.productCount ?? "-"} />
        <SummaryCard label="Total Units" value={summary?.totalUnits ?? "-"} />
        <SummaryCard label="Role" value={roles.join(", ") || "-"} />
      </section>

      <section className="grid gap-6 lg:grid-cols-[1.1fr_0.9fr]">
        <div className="rounded-2xl bg-white p-6 shadow-sm">
          <h3 className="text-lg font-semibold">Stock by warehouse</h3>
          <div className="mt-4 overflow-x-auto">
            <table className="min-w-full text-left text-sm">
              <thead className="border-b border-slate-200 text-xs uppercase text-slate-400">
                <tr>
                  <th className="py-2">Warehouse</th>
                  <th className="py-2">Product</th>
                  <th className="py-2">SKU</th>
                  <th className="py-2">Qty</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100">
                {stock.map((item) => (
                  <tr key={item.id}>
                    <td className="py-3 font-medium text-slate-700">{item.warehouseName}</td>
                    <td className="py-3 text-slate-600">{item.productName}</td>
                    <td className="py-3 text-slate-500">{item.sku}</td>
                    <td className="py-3 text-slate-700">{item.quantity}</td>
                  </tr>
                ))}
              </tbody>
            </table>
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

      <section className="rounded-2xl bg-white p-6 shadow-sm">
        <h3 className="text-lg font-semibold">Latest movements</h3>
        <div className="mt-4 grid gap-3 md:grid-cols-2">
          {movements.slice(0, 8).map((movement) => (
            <div key={movement.id} className="rounded-lg border border-slate-100 p-3">
              <p className="text-sm font-semibold text-slate-700">
                {movement.type} · {movement.productName}
              </p>
              <p className="text-xs text-slate-500">
                {movement.fromWarehouseName || "-"} -> {movement.toWarehouseName || "-"} · {movement.quantity} units
              </p>
            </div>
          ))}
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
