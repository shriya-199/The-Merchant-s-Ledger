import { useEffect, useState } from "react";
import { apiRequest } from "../lib/api";

const emptyForm = {
  type: "INBOUND",
  productId: "",
  fromWarehouseId: "",
  toWarehouseId: "",
  quantity: "",
  referenceNote: "",
};


export default function Movements() {
  const [movements, setMovements] = useState([]);
  const [products, setProducts] = useState([]);
  const [warehouses, setWarehouses] = useState([]);
  const [form, setForm] = useState(emptyForm);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  const load = async () => {
    try {
      const [movementData, productData, warehouseData] = await Promise.all([
        apiRequest("/api/inventory/movements"),
        apiRequest("/api/products"),
        apiRequest("/api/warehouses"),
      ]);
      setMovements(movementData);
      setProducts(productData);
      setWarehouses(warehouseData);
    } catch (err) {
      setError(err.message || "Failed to load movements");
    }
  };

  useEffect(() => {
    load();
  }, []);

  const handleChange = (event) => {
    const { name, value } = event.target;
    setForm((prev) => ({ ...prev, [name]: value }));
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    setError("");
    setSuccess("");

    try {
      await apiRequest("/api/inventory/movements", {
        method: "POST",
        body: JSON.stringify({
          type: form.type,
          productId: Number(form.productId),
          fromWarehouseId: form.fromWarehouseId ? Number(form.fromWarehouseId) : null,
          toWarehouseId: form.toWarehouseId ? Number(form.toWarehouseId) : null,
          quantity: Number(form.quantity),
          referenceNote: form.referenceNote,
        }),
      });
      setForm(emptyForm);
      setSuccess("Movement recorded");
      load();
    } catch (err) {
      setError(err.message || "Failed to record movement");
    }
  };

  return (
    <div className="space-y-8">
      <header>
        <h2 className="text-2xl font-semibold">Stock Movements</h2>
        <p className="text-slate-600">Record inbound, outbound, and transfer events.</p>
      </header>

      <section className="grid gap-6 lg:grid-cols-[1fr_1.5fr]">
        <form onSubmit={handleSubmit} className="rounded-2xl bg-white p-6 shadow-sm">
          <h3 className="text-lg font-semibold">New movement</h3>
          <div className="mt-6 space-y-4">
            <label className="block">
              <span className="text-sm font-medium text-slate-600">Type</span>
              <select
                name="type"
                value={form.type}
                onChange={handleChange}
                className="mt-2 w-full rounded-lg border border-slate-200 bg-white px-3 py-2 text-sm"
              >
                <option value="INBOUND">Inbound</option>
                <option value="OUTBOUND">Outbound</option>
                <option value="TRANSFER">Transfer</option>
              </select>
            </label>
            <label className="block">
              <span className="text-sm font-medium text-slate-600">Product</span>
              <select
                name="productId"
                value={form.productId}
                onChange={handleChange}
                required
                className="mt-2 w-full rounded-lg border border-slate-200 bg-white px-3 py-2 text-sm"
              >
                <option value="">Select product</option>
                {products.map((product) => (
                  <option key={product.id} value={product.id}>
                    {product.name} ({product.sku})
                  </option>
                ))}
              </select>
            </label>
            <label className="block">
              <span className="text-sm font-medium text-slate-600">From warehouse</span>
              <select
                name="fromWarehouseId"
                value={form.fromWarehouseId}
                onChange={handleChange}
                className="mt-2 w-full rounded-lg border border-slate-200 bg-white px-3 py-2 text-sm"
              >
                <option value="">-</option>
                {warehouses.map((warehouse) => (
                  <option key={warehouse.id} value={warehouse.id}>
                    {warehouse.name}
                  </option>
                ))}
              </select>
            </label>
            <label className="block">
              <span className="text-sm font-medium text-slate-600">To warehouse</span>
              <select
                name="toWarehouseId"
                value={form.toWarehouseId}
                onChange={handleChange}
                className="mt-2 w-full rounded-lg border border-slate-200 bg-white px-3 py-2 text-sm"
              >
                <option value="">-</option>
                {warehouses.map((warehouse) => (
                  <option key={warehouse.id} value={warehouse.id}>
                    {warehouse.name}
                  </option>
                ))}
              </select>
            </label>
            <label className="block">
              <span className="text-sm font-medium text-slate-600">Quantity</span>
              <input
                name="quantity"
                type="number"
                value={form.quantity}
                onChange={handleChange}
                required
                min="1"
                className="mt-2 w-full rounded-lg border border-slate-200 px-3 py-2 text-sm"
              />
            </label>
            <label className="block">
              <span className="text-sm font-medium text-slate-600">Reference note</span>
              <input
                name="referenceNote"
                value={form.referenceNote}
                onChange={handleChange}
                className="mt-2 w-full rounded-lg border border-slate-200 px-3 py-2 text-sm"
              />
            </label>
          </div>

          <button
            type="submit"
            className="mt-6 w-full rounded-lg bg-slate-900 px-4 py-2.5 text-sm font-semibold text-white"
          >
            Record movement
          </button>

          {(error || success) && (
            <p className={`mt-4 text-sm ${error ? "text-rose-600" : "text-emerald-600"}`}>
              {error || success}
            </p>
          )}
        </form>

        <div className="rounded-2xl bg-white p-6 shadow-sm">
          <h3 className="text-lg font-semibold">Recent movements</h3>
          <div className="mt-4 overflow-x-auto">
            <table className="min-w-full text-left text-sm">
              <thead className="border-b border-slate-200 text-xs uppercase text-slate-400">
                <tr>
                  <th className="py-2">Type</th>
                  <th className="py-2">Product</th>
                  <th className="py-2">From</th>
                  <th className="py-2">To</th>
                  <th className="py-2">Qty</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100">
                {movements.map((movement) => (
                  <tr key={movement.id}>
                    <td className="py-3 font-medium text-slate-700">{movement.type}</td>
                    <td className="py-3 text-slate-600">{movement.productName}</td>
                    <td className="py-3 text-slate-500">{movement.fromWarehouseName || "-"}</td>
                    <td className="py-3 text-slate-500">{movement.toWarehouseName || "-"}</td>
                    <td className="py-3 text-slate-700">{movement.quantity}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      </section>
    </div>
  );
}
