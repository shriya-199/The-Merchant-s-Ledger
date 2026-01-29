import { useEffect, useState } from "react";
import { apiRequest } from "../lib/api";

export default function Warehouses() {
  const [warehouses, setWarehouses] = useState([]);
  const [form, setForm] = useState({ name: "", code: "", location: "" });
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  const load = async () => {
    try {
      const data = await apiRequest("/api/warehouses");
      setWarehouses(data);
    } catch (err) {
      setError(err.message || "Failed to load warehouses");
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
      await apiRequest("/api/warehouses", {
        method: "POST",
        body: JSON.stringify(form),
      });
      setForm({ name: "", code: "", location: "" });
      setSuccess("Warehouse created");
      load();
    } catch (err) {
      setError(err.message || "Failed to create warehouse");
    }
  };

  return (
    <div className="space-y-8">
      <header>
        <h2 className="text-2xl font-semibold">Warehouses</h2>
        <p className="text-slate-600">Manage storage locations and hubs.</p>
      </header>

      <section className="grid gap-6 lg:grid-cols-[1fr_1.3fr]">
        <form onSubmit={handleSubmit} className="rounded-2xl bg-white p-6 shadow-sm">
          <h3 className="text-lg font-semibold">Add warehouse</h3>
          <div className="mt-6 space-y-4">
            <Field label="Name" name="name" value={form.name} onChange={handleChange} required />
            <Field label="Code" name="code" value={form.code} onChange={handleChange} />
            <Field label="Location" name="location" value={form.location} onChange={handleChange} />
          </div>
          <button
            type="submit"
            className="mt-6 w-full rounded-lg bg-slate-900 px-4 py-2.5 text-sm font-semibold text-white"
          >
            Save warehouse
          </button>
          {(error || success) && (
            <p className={`mt-4 text-sm ${error ? "text-rose-600" : "text-emerald-600"}`}>
              {error || success}
            </p>
          )}
        </form>

        <div className="rounded-2xl bg-white p-6 shadow-sm">
          <h3 className="text-lg font-semibold">Locations</h3>
          <div className="mt-4 overflow-x-auto">
            <table className="min-w-full text-left text-sm">
              <thead className="border-b border-slate-200 text-xs uppercase text-slate-400">
                <tr>
                  <th className="py-2">Name</th>
                  <th className="py-2">Code</th>
                  <th className="py-2">Location</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100">
                {warehouses.map((warehouse) => (
                  <tr key={warehouse.id}>
                    <td className="py-3 font-medium text-slate-700">{warehouse.name}</td>
                    <td className="py-3 text-slate-500">{warehouse.code || "-"}</td>
                    <td className="py-3 text-slate-500">{warehouse.location || "-"}</td>
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
        className="mt-2 w-full rounded-lg border border-slate-200 px-3 py-2 text-sm"
      />
    </label>
  );
}
