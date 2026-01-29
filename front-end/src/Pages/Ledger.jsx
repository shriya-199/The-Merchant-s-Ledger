import { useEffect, useState } from "react";
import { apiRequest } from "../lib/api";

const emptyForm = {
  customerId: "",
  type: "CREDIT",
  amount: "",
  description: "",
};

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
      await apiRequest("/api/ledger", {
        method: "POST",
        body: JSON.stringify({
          ...form,
          customerId: Number(form.customerId),
          amount: Number(form.amount),
        }),
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
        <h2 className="text-2xl font-semibold">Ledger</h2>
        <p className="text-slate-600">Record credits and debits across customer accounts.</p>
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
                <option value="CREDIT">Credit</option>
                <option value="DEBIT">Debit</option>
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
                  <th className="py-2">Customer</th>
                  <th className="py-2">Type</th>
                  <th className="py-2">Amount</th>
                  <th className="py-2">Description</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100">
                {entries.map((entry) => (
                  <tr key={entry.id}>
                    <td className="py-3 font-medium text-slate-700">{entry.customerName}</td>
                    <td className="py-3">
                      <span
                        className={`rounded-full px-3 py-1 text-xs font-semibold ${
                          entry.type === "CREDIT"
                            ? "bg-emerald-100 text-emerald-700"
                            : "bg-amber-100 text-amber-700"
                        }`}
                      >
                        {entry.type}
                      </span>
                    </td>
                    <td className="py-3">${Number(entry.amount).toLocaleString()}</td>
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
