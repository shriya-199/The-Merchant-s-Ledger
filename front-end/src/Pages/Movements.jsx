import { useEffect, useState } from "react";
import { apiRequest } from "../lib/api";

const emptyForm = {
  type: "RECEIVE",
  productId: "",
  fromWarehouseId: "",
  toWarehouseId: "",
  quantity: "",
  adjustmentDirection: "INCREASE",
  transactionId: "",
  idempotencyKey: "",
  correlationId: "",
  referenceType: "",
  referenceId: "",
  reasonCode: "",
  sourceLocation: "",
  destinationLocation: "",
  performedVia: "API",
  metadataJson: "",
  referenceNote: "",
};
const movementTypes = [
  "RECEIVE",
  "SHIP",
  "RESERVE",
  "RELEASE",
  "TRANSFER",
  "ADJUST",
  "RETURN",
  "DAMAGE",
];

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
      const payload = {
        type: form.type,
        productId: Number(form.productId),
        fromWarehouseId: form.fromWarehouseId ? Number(form.fromWarehouseId) : null,
        toWarehouseId: form.toWarehouseId ? Number(form.toWarehouseId) : null,
        quantity: Number(form.quantity),
        adjustmentDirection: form.adjustmentDirection || null,
        transactionId: form.transactionId || null,
        idempotencyKey: form.idempotencyKey || null,
        correlationId: form.correlationId || null,
        referenceType: form.referenceType || null,
        referenceId: form.referenceId || null,
        reasonCode: form.reasonCode || null,
        sourceLocation: form.sourceLocation || null,
        destinationLocation: form.destinationLocation || null,
        performedVia: form.performedVia || null,
        metadataJson: form.metadataJson || null,
        referenceNote: form.referenceNote || null,
      };

      await apiRequest("/api/inventory/movements", {
        method: "POST",
        body: JSON.stringify(payload),
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
        <h2 className="text-2xl font-semibold">Inventory Ledger</h2>
        <p className="text-slate-600">Immutable movement log for stock traceability.</p>
      </header>

      <section className="grid gap-6 lg:grid-cols-[1fr_1.5fr]">
        <form onSubmit={handleSubmit} className="rounded-2xl bg-white p-6 shadow-sm">
          <h3 className="text-lg font-semibold">New ledger entry</h3>
          <div className="mt-6 space-y-4">
            <label className="block">
              <span className="text-sm font-medium text-slate-600">Movement type</span>
              <select
                name="type"
                value={form.type}
                onChange={handleChange}
                className="mt-2 w-full rounded-lg border border-slate-200 bg-white px-3 py-2 text-sm"
              >
                {movementTypes.map((type) => (
                  <option key={type} value={type}>
                    {type}
                  </option>
                ))}
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
            {form.type === "ADJUST" && (
              <label className="block">
                <span className="text-sm font-medium text-slate-600">Adjustment direction</span>
                <select
                  name="adjustmentDirection"
                  value={form.adjustmentDirection}
                  onChange={handleChange}
                  className="mt-2 w-full rounded-lg border border-slate-200 bg-white px-3 py-2 text-sm"
                >
                  <option value="INCREASE">Increase</option>
                  <option value="DECREASE">Decrease</option>
                </select>
              </label>
            )}
            <label className="block">
              <span className="text-sm font-medium text-slate-600">Reference note</span>
              <input
                name="referenceNote"
                value={form.referenceNote}
                onChange={handleChange}
                className="mt-2 w-full rounded-lg border border-slate-200 px-3 py-2 text-sm"
              />
            </label>
            <label className="block">
              <span className="text-sm font-medium text-slate-600">Reference type</span>
              <input
                name="referenceType"
                value={form.referenceType}
                onChange={handleChange}
                placeholder="ORDER / PO / TRANSFER / CYCLE_COUNT"
                className="mt-2 w-full rounded-lg border border-slate-200 px-3 py-2 text-sm"
              />
            </label>
            <label className="block">
              <span className="text-sm font-medium text-slate-600">Reference ID</span>
              <input
                name="referenceId"
                value={form.referenceId}
                onChange={handleChange}
                className="mt-2 w-full rounded-lg border border-slate-200 px-3 py-2 text-sm"
              />
            </label>
            <label className="block">
              <span className="text-sm font-medium text-slate-600">Reason code</span>
              <input
                name="reasonCode"
                value={form.reasonCode}
                onChange={handleChange}
                placeholder="DAMAGE / RECON / RETURN / SHRINKAGE"
                className="mt-2 w-full rounded-lg border border-slate-200 px-3 py-2 text-sm"
              />
            </label>
            <label className="block">
              <span className="text-sm font-medium text-slate-600">Source location</span>
              <input
                name="sourceLocation"
                value={form.sourceLocation}
                onChange={handleChange}
                placeholder="Aisle-2/Bin-14"
                className="mt-2 w-full rounded-lg border border-slate-200 px-3 py-2 text-sm"
              />
            </label>
            <label className="block">
              <span className="text-sm font-medium text-slate-600">Destination location</span>
              <input
                name="destinationLocation"
                value={form.destinationLocation}
                onChange={handleChange}
                placeholder="Receiving/Bin-02"
                className="mt-2 w-full rounded-lg border border-slate-200 px-3 py-2 text-sm"
              />
            </label>
            <label className="block">
              <span className="text-sm font-medium text-slate-600">Transaction ID</span>
              <input
                name="transactionId"
                value={form.transactionId}
                onChange={handleChange}
                placeholder="Auto-generated if blank"
                className="mt-2 w-full rounded-lg border border-slate-200 px-3 py-2 text-sm"
              />
            </label>
            <label className="block">
              <span className="text-sm font-medium text-slate-600">Idempotency key</span>
              <input
                name="idempotencyKey"
                value={form.idempotencyKey}
                onChange={handleChange}
                placeholder="Prevents duplicate writes"
                className="mt-2 w-full rounded-lg border border-slate-200 px-3 py-2 text-sm"
              />
            </label>
            <label className="block">
              <span className="text-sm font-medium text-slate-600">Correlation ID</span>
              <input
                name="correlationId"
                value={form.correlationId}
                onChange={handleChange}
                placeholder="Trace across services"
                className="mt-2 w-full rounded-lg border border-slate-200 px-3 py-2 text-sm"
              />
            </label>
            <label className="block">
              <span className="text-sm font-medium text-slate-600">Performed via</span>
              <input
                name="performedVia"
                value={form.performedVia}
                onChange={handleChange}
                placeholder="API / SCAN / SYSTEM"
                className="mt-2 w-full rounded-lg border border-slate-200 px-3 py-2 text-sm"
              />
            </label>
            <label className="block">
              <span className="text-sm font-medium text-slate-600">Metadata JSON</span>
              <textarea
                name="metadataJson"
                value={form.metadataJson}
                onChange={handleChange}
                rows={3}
                placeholder='{"scanId":"SCN123","deviceId":"DV-10"}'
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
          <h3 className="text-lg font-semibold">Recent immutable entries</h3>
          <div className="mt-4 overflow-x-auto">
            <table className="min-w-full text-left text-sm">
              <thead className="border-b border-slate-200 text-xs uppercase text-slate-400">
                <tr>
                  <th className="py-2">TX</th>
                  <th className="py-2">Type</th>
                  <th className="py-2">Product</th>
                  <th className="py-2">From</th>
                  <th className="py-2">To</th>
                  <th className="py-2">Qty</th>
                  <th className="py-2">Reason</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100">
                {movements.map((movement) => (
                  <tr key={movement.id}>
                    <td className="py-3 text-slate-500">{movement.transactionId || "-"}</td>
                    <td className="py-3 font-medium text-slate-700">{movement.type}</td>
                    <td className="py-3 text-slate-600">{movement.productName}</td>
                    <td className="py-3 text-slate-500">{movement.fromWarehouseName || "-"}</td>
                    <td className="py-3 text-slate-500">{movement.toWarehouseName || "-"}</td>
                    <td className="py-3 text-slate-700">{movement.quantity}</td>
                    <td className="py-3 text-slate-500">{movement.reasonCode || "-"}</td>
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
