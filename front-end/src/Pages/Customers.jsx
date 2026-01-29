import { useEffect, useState } from "react";
import { apiRequest } from "../lib/api";

export default function Customers() {
  const [customers, setCustomers] = useState([]);
  const [form, setForm] = useState({ name: "", email: "", phone: "", address: "" });
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  const loadCustomers = async () => {
    try {
      const data = await apiRequest("/api/customers");
      setCustomers(data);
    } catch (err) {
      setError(err.message || "Failed to load customers");
    }
  };

  useEffect(() => {
    loadCustomers();
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
      await apiRequest("/api/customers", {
        method: "POST",
        body: JSON.stringify(form),
      });
      setForm({ name: "", email: "", phone: "", address: "" });
      setSuccess("Customer created successfully");
      loadCustomers();
    } catch (err) {
      setError(err.message || "Failed to create customer");
    }
  };

  return (
    <div className="space-y-8">
      <header>
        <h2 className="text-2xl font-semibold">Customers</h2>
        <p className="text-slate-600">Manage your customer records and balances.</p>
      </header>

      <section className="grid gap-6 lg:grid-cols-[1fr_1.2fr]">
        <form onSubmit={handleSubmit} className="rounded-2xl bg-white p-6 shadow-sm">
          <h3 className="text-lg font-semibold">Add new customer</h3>
          <p className="text-sm text-slate-500">Capture contact details for ledger tracking.</p>

          <div className="mt-6 space-y-4">
            <Field label="Name" name="name" value={form.name} onChange={handleChange} required />
            <Field label="Email" name="email" value={form.email} onChange={handleChange} />
            <Field label="Phone" name="phone" value={form.phone} onChange={handleChange} />
            <Field label="Address" name="address" value={form.address} onChange={handleChange} />
          </div>

          <button
            type="submit"
            className="mt-6 w-full rounded-lg bg-slate-900 px-4 py-2.5 text-sm font-semibold text-white hover:bg-slate-800"
          >
            Save customer
          </button>

          {(error || success) && (
            <p className={`mt-4 text-sm ${error ? "text-rose-600" : "text-emerald-600"}`}>
              {error || success}
            </p>
          )}
        </form>

        <div className="rounded-2xl bg-white p-6 shadow-sm">
          <h3 className="text-lg font-semibold">Customer directory</h3>
          <div className="mt-4 overflow-x-auto">
            <table className="min-w-full text-left text-sm">
              <thead className="border-b border-slate-200 text-xs uppercase text-slate-400">
                <tr>
                  <th className="py-2">Name</th>
                  <th className="py-2">Email</th>
                  <th className="py-2">Phone</th>
                  <th className="py-2">Balance</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100">
                {customers.map((customer) => (
                  <tr key={customer.id}>
                    <td className="py-3 font-medium text-slate-700">{customer.name}</td>
                    <td className="py-3 text-slate-500">{customer.email || "-"}</td>
                    <td className="py-3 text-slate-500">{customer.phone || "-"}</td>
                    <td className="py-3 text-slate-700">
                      ${Number(customer.balance).toLocaleString()}
                    </td>
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

function Field({ label, name, value, onChange, required = false }) {
  return (
    <label className="block">
      <span className="text-sm font-medium text-slate-600">{label}</span>
      <input
        name={name}
        value={value}
        onChange={onChange}
        required={required}
        className="mt-2 w-full rounded-lg border border-slate-200 px-3 py-2 text-sm focus:border-slate-400 focus:outline-none"
      />
    </label>
  );
}
