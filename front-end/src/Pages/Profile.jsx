import { useEffect, useState } from "react";
import { apiRequest } from "../lib/api";
import { useAuth } from "../context/AuthContext";

export default function Profile() {
  const { user, logout, refreshUser } = useAuth();
  const [form, setForm] = useState({
    fullName: "",
    phone: "",
    address: "",
    companyName: "",
    roleTitle: "",
  });
  const [status, setStatus] = useState({ type: "", message: "" });

  useEffect(() => {
    if (user) {
      setForm({
        fullName: user.fullName || "",
        phone: user.phone || "",
        address: user.address || "",
        companyName: user.companyName || "",
        roleTitle: user.roleTitle || "",
      });
    }
  }, [user]);

  const handleChange = (event) => {
    const { name, value } = event.target;
    setForm((prev) => ({ ...prev, [name]: value }));
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    setStatus({ type: "", message: "" });

    try {
      await apiRequest("/api/users/me", {
        method: "PUT",
        body: JSON.stringify(form),
      });
      await refreshUser();
      setStatus({ type: "success", message: "Profile updated" });
    } catch (error) {
      setStatus({ type: "error", message: error.message || "Update failed" });
    }
  };

  return (
    <div className="space-y-6">
      <header>
        <h2 className="text-2xl font-semibold">Profile</h2>
        <p className="text-slate-600">Manage your account details and preferences.</p>
      </header>

      <form onSubmit={handleSubmit} className="rounded-2xl bg-white p-6 shadow-sm">
        <div className="grid gap-4 md:grid-cols-2">
          <Field label="Full name" name="fullName" value={form.fullName} onChange={handleChange} />
          <Field label="Phone" name="phone" value={form.phone} onChange={handleChange} />
          <Field label="Company" name="companyName" value={form.companyName} onChange={handleChange} />
          <Field label="Role title" name="roleTitle" value={form.roleTitle} onChange={handleChange} />
          <Field label="Address" name="address" value={form.address} onChange={handleChange} />
        </div>

        <div className="mt-6 flex items-center gap-3">
          <button
            type="submit"
            className="rounded-lg bg-slate-900 px-4 py-2 text-sm font-semibold text-white hover:bg-slate-800"
          >
            Save changes
          </button>
          <button
            type="button"
            onClick={logout}
            className="rounded-lg border border-slate-200 px-4 py-2 text-sm"
          >
            Sign out
          </button>
        </div>

        {status.message && (
          <p className={`mt-4 text-sm ${status.type === "success" ? "text-emerald-600" : "text-rose-600"}`}>
            {status.message}
          </p>
        )}
      </form>
    </div>
  );
}

function Field({ label, name, value, onChange }) {
  return (
    <label className="block">
      <span className="text-sm font-medium text-slate-600">{label}</span>
      <input
        name={name}
        value={value}
        onChange={onChange}
        className="mt-2 w-full rounded-lg border border-slate-200 px-3 py-2 text-sm"
      />
    </label>
  );
}
