import { useEffect, useState } from "react";
import { apiRequest } from "../lib/api";

const emptyForm = {
  customerId: "",
  type: "SALE_CREDIT",
  amount: "",
  transactionId: "",
  idempotencyKey: "",
  correlationId: "",
  referenceType: "",
  referenceId: "",
  relatedMovementId: "",
  description: "",
};
const ledgerTypes = [
  "SALE_CREDIT",
  "MANUAL_CREDIT",
  "COD_SETTLEMENT",
  "CHARGEBACK_RELEASE",
  "ADJUSTMENT_CREDIT",
  "PAYOUT_DEBIT",
  "REFUND_DEBIT",
  "COMMISSION_FEE",
  "SHIPPING_FEE",
  "STORAGE_FEE",
  "CHARGEBACK_HOLD",
  "ADJUSTMENT_DEBIT",
];

export default function Ledger() {
  const [entries, setEntries] = useState([]);
  const [customers, setCustomers] = useState([]);
  const [form, setForm] = useState(emptyForm);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  const loadData = async () => {
    try {
      const [ledgerData, customerData] = await Promise.all([
        apiRequest("/api/ledger"),
        apiRequest("/api/customers"),
      ]);
      setEntries(ledgerData);
      setCustomers(customerData);
    } catch (err) {
      setError(err.message || "Failed to load ledger");
    }
  };

  useEffect(() => {
    loadData();
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
        ...form,
        customerId: Number(form.customerId),
        amount: Number(form.amount),
        transactionId: form.transactionId || null,
        idempotencyKey: form.idempotencyKey || null,
        correlationId: form.correlationId || null,
        referenceType: form.referenceType || null,
        referenceId: form.referenceId || null,
        relatedMovementId: form.relatedMovementId ? Number(form.relatedMovementId) : null,
        description: form.description || null,
      };

      await apiRequest("/api/ledger", {
        method: "POST",
        body: JSON.stringify(payload),
      });
      setForm(emptyForm);
      setSuccess("Entry added to ledger");
      loadData();
    } catch (err) {
      setError(err.message || "Failed to add entry");
    }
  };

  return (
    <div className="space-y-8">
      <header>
        <h2 className="text-2xl font-semibold">Financial Ledger</h2>
        <p className="text-slate-600">Charges, payouts, refunds, and settlement trail.</p>
      </header>

      <section className="grid gap-6 lg:grid-cols-[1fr_1.4fr]">
        <form onSubmit={handleSubmit} className="rounded-2xl bg-white p-6 shadow-sm">
          <h3 className="text-lg font-semibold">New entry</h3>
          <div className="mt-6 space-y-4">
            <label className="block">
              <span className="text-sm font-medium text-slate-600">Customer</span>
              <select
                name="customerId"
                value={form.customerId}
                onChange={handleChange}
                required
                className="mt-2 w-full rounded-lg border border-slate-200 bg-white px-3 py-2 text-sm"
              >
                <option value="">Select customer</option>
                {customers.map((customer) => (
                  <option key={customer.id} value={customer.id}>
                    {customer.name}
                  </option>
                ))}
              </select>
            </label>
            <label className="block">
              <span className="text-sm font-medium text-slate-600">Type</span>
              <select
                name="type"
                value={form.type}
                onChange={handleChange}
                className="mt-2 w-full rounded-lg border border-slate-200 bg-white px-3 py-2 text-sm"
              >
                {ledgerTypes.map((type) => (
                  <option key={type} value={type}>
                    {type}
                  </option>
                ))}
              </select>
            </label>
            <label className="block">
              <span className="text-sm font-medium text-slate-600">Amount</span>
              <input
                name="amount"
                type="number"
                step="0.01"
                value={form.amount}
                onChange={handleChange}
                required
                className="mt-2 w-full rounded-lg border border-slate-200 px-3 py-2 text-sm"
              />
            </label>
            <label className="block">
              <span className="text-sm font-medium text-slate-600">Description</span>
              <input
                name="description"
                value={form.description}
                onChange={handleChange}
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
                className="mt-2 w-full rounded-lg border border-slate-200 px-3 py-2 text-sm"
              />
            </label>
            <label className="block">
              <span className="text-sm font-medium text-slate-600">Correlation ID</span>
              <input
                name="correlationId"
                value={form.correlationId}
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
                placeholder="ORDER / SETTLEMENT / RETURN"
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
              <span className="text-sm font-medium text-slate-600">Related movement ID</span>
              <input
                name="relatedMovementId"
                type="number"
                value={form.relatedMovementId}
                onChange={handleChange}
                className="mt-2 w-full rounded-lg border border-slate-200 px-3 py-2 text-sm"
              />
            </label>
          </div>

          <button
            type="submit"
            className="mt-6 w-full rounded-lg bg-slate-900 px-4 py-2.5 text-sm font-semibold text-white hover:bg-slate-800"
          >
            Add entry
          </button>

          {(error || success) && (
            <p className={`mt-4 text-sm ${error ? "text-rose-600" : "text-emerald-600"}`}>
              {error || success}
            </p>
          )}
        </form>

        <div className="rounded-2xl bg-white p-6 shadow-sm">
          <h3 className="text-lg font-semibold">Recent entries</h3>
          <div className="mt-4 overflow-x-auto">
            <table className="min-w-full text-left text-sm">
              <thead className="border-b border-slate-200 text-xs uppercase text-slate-400">
                <tr>
                  <th className="py-2">TX</th>
                  <th className="py-2">Customer</th>
                  <th className="py-2">Type</th>
                  <th className="py-2">Amount</th>
                  <th className="py-2">Ref</th>
                  <th className="py-2">Description</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100">
                {entries.map((entry) => (
                  <tr key={entry.id}>
                    <td className="py-3 text-slate-500">{entry.transactionId || "-"}</td>
                    <td className="py-3 font-medium text-slate-700">{entry.customerName}</td>
                    <td className="py-3">
                      <span className="rounded-full bg-slate-100 px-3 py-1 text-xs font-semibold text-slate-700">
                        {entry.type}
                      </span>
                    </td>
                    <td className="py-3">${Number(entry.amount).toLocaleString()}</td>
                    <td className="py-3 text-slate-500">{entry.referenceType || "-"} {entry.referenceId || ""}</td>
                    <td className="py-3 text-slate-500">{entry.description || "-"}</td>
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
