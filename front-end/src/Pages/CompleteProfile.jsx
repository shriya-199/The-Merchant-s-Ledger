import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

const roleOptions = [
  { value: "MERCHANT_OPERATIONS", label: "Merchant Operations" },
  { value: "MERCHANT_FINANCE", label: "Merchant Finance" },
  { value: "MERCHANT_VIEWER", label: "Merchant Viewer" },
  { value: "PICKER_PACKER", label: "Picker/Packer" },
  { value: "RECEIVER_GRN_OPERATOR", label: "Receiver / GRN Operator" },
  { value: "STAFF", label: "Legacy Staff" },
  { value: "MANAGER", label: "Legacy Manager" },
  { value: "USER", label: "Legacy User" },
];

export default function CompleteProfile() {
  const { user, completeProfile } = useAuth();
  const navigate = useNavigate();
  const [form, setForm] = useState({
    fullName: "",
    email: "",
    phone: "",
    companyName: "",
    address: "",
    roleTitle: "",
    roleName: "MERCHANT_OPERATIONS",
  });
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if (!user) {
      return;
    }
    setForm((prev) => ({
      ...prev,
      fullName: user.fullName || "",
      email: user.email || "",
      phone: user.phone || "",
      companyName: user.companyName && user.companyName !== "Google Account" ? user.companyName : "",
      address: user.address || "",
      roleTitle: user.roleTitle && user.roleTitle !== "Merchant Operations" ? user.roleTitle : "",
      roleName: user.roles?.[0] || "MERCHANT_OPERATIONS",
    }));
  }, [user]);

  const handleChange = (event) => {
    const { name, value } = event.target;
    setForm((prev) => ({ ...prev, [name]: value }));
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    setError("");
    setLoading(true);
    try {
      await completeProfile({
        fullName: form.fullName,
        phone: form.phone,
        companyName: form.companyName,
        address: form.address,
        roleTitle: form.roleTitle,
        roleName: form.roleName,
      });
      navigate("/dashboard");
    } catch (err) {
      setError(err.message || "Failed to complete profile");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-slate-50 flex items-center justify-center px-6 py-10">
      <div className="w-full max-w-lg rounded-2xl bg-white p-6 shadow-sm">
        <h2 className="text-2xl font-semibold text-slate-900">Complete your profile</h2>
        <p className="mt-2 text-sm text-slate-500">
          Google se name aur email prefill ho gaye hain. Baaki details add karke continue karein.
        </p>

        <form onSubmit={handleSubmit} className="mt-6 space-y-4">
          <Field label="Full name" name="fullName" value={form.fullName} onChange={handleChange} required />
          <Field label="Email" name="email" value={form.email} onChange={handleChange} disabled />
          <Field label="Phone" name="phone" value={form.phone} onChange={handleChange} required />
          <Field label="Company" name="companyName" value={form.companyName} onChange={handleChange} required />
          <Field label="Address" name="address" value={form.address} onChange={handleChange} />
          <Field label="Role title" name="roleTitle" value={form.roleTitle} onChange={handleChange} required />

          <label className="block">
            <span className="text-sm font-medium text-slate-700">Role</span>
            <select
              name="roleName"
              value={form.roleName}
              onChange={handleChange}
              className="mt-2 w-full rounded-lg border border-slate-200 bg-white px-3 py-2 text-sm"
              required
            >
              {roleOptions.map((role) => (
                <option key={role.value} value={role.value}>
                  {role.label}
                </option>
              ))}
            </select>
          </label>

          {error && <p className="text-sm text-rose-600">{error}</p>}

          <button
            type="submit"
            disabled={loading}
            className="w-full rounded-lg bg-slate-900 px-4 py-2.5 text-sm font-semibold text-white hover:bg-slate-800"
          >
            {loading ? "Saving..." : "Save and continue"}
          </button>
        </form>
      </div>
    </div>
  );
}

function Field({ label, name, value, onChange, required = false, disabled = false }) {
  return (
    <label className="block">
      <span className="text-sm font-medium text-slate-700">{label}</span>
      <input
        name={name}
        value={value}
        onChange={onChange}
        required={required}
        disabled={disabled}
        className="mt-2 w-full rounded-lg border border-slate-200 px-3 py-2 text-sm disabled:bg-slate-100 disabled:text-slate-500"
      />
    </label>
  );
}
