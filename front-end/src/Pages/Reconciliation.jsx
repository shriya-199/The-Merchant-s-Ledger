import { useEffect, useMemo, useState } from "react";
import { apiRequest } from "../lib/api";
import { useAuth } from "../context/AuthContext";
import { canRunReconciliation } from "../lib/roles";

const emptySession = { name: "", warehouseId: "" };
const emptyLine = { productId: "", countedQty: "", sourceLocation: "", reasonCode: "CYCLE_COUNT" };

export default function Reconciliation() {
  const { user } = useAuth();
  const [warehouses, setWarehouses] = useState([]);
  const [products, setProducts] = useState([]);
  const [sessions, setSessions] = useState([]);
  const [sessionForm, setSessionForm] = useState(emptySession);
  const [lineForm, setLineForm] = useState(emptyLine);
  const [activeSessionId, setActiveSessionId] = useState(null);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  const activeSession = useMemo(
    () => sessions.find((session) => session.id === activeSessionId) || null,
    [sessions, activeSessionId]
  );

  const load = async () => {
    const [warehouseData, productData, sessionData] = await Promise.all([
      apiRequest("/api/warehouses"),
      apiRequest("/api/products"),
      apiRequest("/api/reconciliation/sessions"),
    ]);
    setWarehouses(warehouseData);
    setProducts(productData);
    setSessions(sessionData);
    if (!activeSessionId && sessionData.length > 0) {
      setActiveSessionId(sessionData[0].id);
    }
  };

  useEffect(() => {
    load().catch((err) => setError(err.message || "Failed to load reconciliation data"));
  }, []);

  const createSession = async (event) => {
    event.preventDefault();
    setError("");
    setSuccess("");
    try {
      const created = await apiRequest("/api/reconciliation/sessions", {
        method: "POST",
        body: JSON.stringify({ name: sessionForm.name, warehouseId: Number(sessionForm.warehouseId) }),
      });
      setSessions((prev) => [created, ...prev]);
      setActiveSessionId(created.id);
      setSessionForm(emptySession);
      setSuccess("Cycle count session created");
    } catch (err) {
      setError(err.message || "Failed to create session");
    }
  };

  const addLine = async (event) => {
    event.preventDefault();
    if (!activeSessionId) {
      setError("Select a cycle count session first");
      return;
    }
    setError("");
    setSuccess("");
    try {
      const updated = await apiRequest(`/api/reconciliation/sessions/${activeSessionId}/lines`, {
        method: "POST",
        body: JSON.stringify({
          productId: Number(lineForm.productId),
          countedQty: Number(lineForm.countedQty),
          sourceLocation: lineForm.sourceLocation || null,
          reasonCode: lineForm.reasonCode || null,
        }),
      });
      setSessions((prev) => prev.map((session) => (session.id === updated.id ? updated : session)));
      setLineForm(emptyLine);
      setSuccess("Count line added");
    } catch (err) {
      setError(err.message || "Failed to add line");
    }
  };

  const submitSession = async () => {
    if (!activeSessionId) return;
    setError("");
    setSuccess("");
    try {
      const updated = await apiRequest(`/api/reconciliation/sessions/${activeSessionId}/submit`, { method: "POST" });
      setSessions((prev) => prev.map((session) => (session.id === updated.id ? updated : session)));
      setSuccess("Session submitted for approval");
    } catch (err) {
      setError(err.message || "Failed to submit session");
    }
  };

  const approveSession = async () => {
    if (!activeSessionId) return;
    setError("");
    setSuccess("");
    try {
      const updated = await apiRequest(`/api/reconciliation/sessions/${activeSessionId}/approve`, { method: "POST" });
      setSessions((prev) => prev.map((session) => (session.id === updated.id ? updated : session)));
      setSuccess("Session approved and variance adjustments posted");
    } catch (err) {
      setError(err.message || "Failed to approve session");
    }
  };

  const canApprove = canRunReconciliation(user?.roles || []);

  return (
    <div className="space-y-8">
      <header>
        <h2 className="text-2xl font-semibold">Reconciliation & Cycle Count</h2>
        <p className="text-slate-600">Plan count sessions, post variances, and keep audit trails clean.</p>
      </header>

      {(error || success) && (
        <div className={`rounded-lg border p-4 ${error ? "border-rose-200 bg-rose-50 text-rose-600" : "border-emerald-200 bg-emerald-50 text-emerald-700"}`}>
          {error || success}
        </div>
      )}

      <section className="grid gap-6 lg:grid-cols-[1fr_1fr]">
        <form onSubmit={createSession} className="rounded-2xl bg-white p-6 shadow-sm space-y-4">
          <h3 className="text-lg font-semibold">Create Cycle Count Session</h3>
          <Field label="Session name" name="name" value={sessionForm.name} onChange={(e) => setSessionForm((p) => ({ ...p, name: e.target.value }))} required />
          <label className="block">
            <span className="text-sm font-medium text-slate-600">Warehouse</span>
            <select
              value={sessionForm.warehouseId}
              onChange={(e) => setSessionForm((p) => ({ ...p, warehouseId: e.target.value }))}
              className="mt-2 w-full rounded-lg border border-slate-200 bg-white px-3 py-2 text-sm"
              required
            >
              <option value="">Select warehouse</option>
              {warehouses.map((warehouse) => (
                <option key={warehouse.id} value={warehouse.id}>
                  {warehouse.name}
                </option>
              ))}
            </select>
          </label>
          <button type="submit" className="rounded-lg bg-slate-900 px-4 py-2 text-sm font-semibold text-white">
            Create Session
          </button>
        </form>

        <div className="rounded-2xl bg-white p-6 shadow-sm space-y-3">
          <h3 className="text-lg font-semibold">Sessions</h3>
          <div className="space-y-2 max-h-72 overflow-auto pr-1">
            {sessions.map((session) => (
              <button
                type="button"
                key={session.id}
                onClick={() => setActiveSessionId(session.id)}
                className={`w-full rounded-lg border px-3 py-2 text-left text-sm ${
                  activeSessionId === session.id ? "border-slate-900 bg-slate-50" : "border-slate-200"
                }`}
              >
                <p className="font-medium text-slate-800">{session.name}</p>
                <p className="text-xs text-slate-500">{session.warehouseName} · {session.status}</p>
              </button>
            ))}
            {sessions.length === 0 && <p className="text-sm text-slate-500">No cycle count sessions yet.</p>}
          </div>
        </div>
      </section>

      <section className="grid gap-6 lg:grid-cols-[1fr_1.4fr]">
        <form onSubmit={addLine} className="rounded-2xl bg-white p-6 shadow-sm space-y-4">
          <h3 className="text-lg font-semibold">Add Count Line</h3>
          <label className="block">
            <span className="text-sm font-medium text-slate-600">Product</span>
            <select
              value={lineForm.productId}
              onChange={(e) => setLineForm((p) => ({ ...p, productId: e.target.value }))}
              className="mt-2 w-full rounded-lg border border-slate-200 bg-white px-3 py-2 text-sm"
              required
            >
              <option value="">Select product</option>
              {products.map((product) => (
                <option key={product.id} value={product.id}>
                  {product.name} ({product.sku})
                </option>
              ))}
            </select>
          </label>
          <Field
            label="Counted quantity"
            name="countedQty"
            type="number"
            min="0"
            value={lineForm.countedQty}
            onChange={(e) => setLineForm((p) => ({ ...p, countedQty: e.target.value }))}
            required
          />
          <Field
            label="Source location (Zone/Aisle/Rack/Shelf/Bin)"
            name="sourceLocation"
            value={lineForm.sourceLocation}
            onChange={(e) => setLineForm((p) => ({ ...p, sourceLocation: e.target.value }))}
          />
          <Field
            label="Reason code"
            name="reasonCode"
            value={lineForm.reasonCode}
            onChange={(e) => setLineForm((p) => ({ ...p, reasonCode: e.target.value }))}
          />
          <button type="submit" className="rounded-lg bg-slate-900 px-4 py-2 text-sm font-semibold text-white">
            Add Line
          </button>
        </form>

        <div className="rounded-2xl bg-white p-6 shadow-sm">
          <div className="flex items-center justify-between">
            <h3 className="text-lg font-semibold">Session Details</h3>
            {activeSession && (
              <div className="flex gap-2">
                {activeSession.status === "OPEN" && (
                  <button type="button" onClick={submitSession} className="rounded-lg border border-slate-300 px-3 py-1.5 text-xs">
                    Submit
                  </button>
                )}
                {canApprove && activeSession.status === "SUBMITTED" && (
                  <button type="button" onClick={approveSession} className="rounded-lg bg-slate-900 px-3 py-1.5 text-xs text-white">
                    Approve
                  </button>
                )}
              </div>
            )}
          </div>
          {!activeSession && <p className="mt-4 text-sm text-slate-500">Select a session to inspect lines.</p>}
          {activeSession && (
            <div className="mt-4 overflow-x-auto">
              <table className="min-w-full text-left text-sm">
                <thead className="border-b border-slate-200 text-xs uppercase text-slate-400">
                  <tr>
                    <th className="py-2">Product</th>
                    <th className="py-2">Expected</th>
                    <th className="py-2">Counted</th>
                    <th className="py-2">Variance</th>
                    <th className="py-2">Location</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-slate-100">
                  {activeSession.lines.map((line) => (
                    <tr key={line.id}>
                      <td className="py-2">{line.productName}</td>
                      <td className="py-2">{line.expectedQty}</td>
                      <td className="py-2">{line.countedQty}</td>
                      <td className={`py-2 ${line.variance === 0 ? "text-slate-700" : line.variance > 0 ? "text-emerald-700" : "text-rose-700"}`}>
                        {line.variance}
                      </td>
                      <td className="py-2 text-slate-500">{line.sourceLocation || "-"}</td>
                    </tr>
                  ))}
                  {activeSession.lines.length === 0 && (
                    <tr>
                      <td colSpan={5} className="py-3 text-slate-500">No lines added for this session.</td>
                    </tr>
                  )}
                </tbody>
              </table>
            </div>
          )}
        </div>
      </section>
    </div>
  );
}

function Field({ label, name, value, onChange, required = false, type = "text", min }) {
  return (
    <label className="block">
      <span className="text-sm font-medium text-slate-600">{label}</span>
      <input
        name={name}
        type={type}
        min={min}
        value={value}
        onChange={onChange}
        required={required}
        className="mt-2 w-full rounded-lg border border-slate-200 px-3 py-2 text-sm"
      />
    </label>
  );
}
