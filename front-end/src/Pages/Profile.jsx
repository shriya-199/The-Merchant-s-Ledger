import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { apiRequest } from "../lib/api";
import { useAuth } from "../context/AuthContext";

export default function Profile() {
  const { user, logout, refreshUser } = useAuth();
  const navigate = useNavigate();
  const [form, setForm] = useState({
    fullName: "",
    phone: "",
    address: "",
    companyName: "",
    roleTitle: "",
  });
  const [status, setStatus] = useState({ type: "", message: "" });
  const [deleteOtpMeta, setDeleteOtpMeta] = useState(null);
  const [deleteOtpCode, setDeleteOtpCode] = useState("");
  const [deleteLoading, setDeleteLoading] = useState(false);

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

  const sendDeleteOtp = async () => {
    setStatus({ type: "", message: "" });
    setDeleteLoading(true);
    try {
      const response = await apiRequest("/api/users/me/delete/send-otp", {
        method: "POST",
      });
      setDeleteOtpMeta(response);
      setStatus({ type: "success", message: "Delete OTP sent to your mobile number." });
    } catch (error) {
      setStatus({ type: "error", message: error.message || "Failed to send delete OTP" });
    } finally {
      setDeleteLoading(false);
    }
  };

  const confirmDeleteAccount = async () => {
    setStatus({ type: "", message: "" });
    if (!deleteOtpMeta) {
      setStatus({ type: "error", message: "Please send OTP first." });
      return;
    }
    if (!deleteOtpCode) {
      setStatus({ type: "error", message: "Enter mobile OTP to delete account." });
      return;
    }
    setDeleteLoading(true);
    try {
      await apiRequest("/api/users/me/delete", {
        method: "POST",
        body: JSON.stringify({
          phoneOtpChallengeId: deleteOtpMeta.phoneChallengeId,
          phoneOtpCode: deleteOtpCode,
        }),
      });
      logout();
      navigate("/login");
    } catch (error) {
      setStatus({ type: "error", message: error.message || "Failed to delete account" });
    } finally {
      setDeleteLoading(false);
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

      <section className="rounded-2xl border border-rose-200 bg-rose-50 p-6">
        <h3 className="text-lg font-semibold text-rose-800">Delete account</h3>
        <p className="mt-1 text-sm text-rose-700">
          This action is permanent. Mobile OTP verification is required before deletion.
        </p>
        <div className="mt-4 space-y-3">
          <button
            type="button"
            onClick={sendDeleteOtp}
            disabled={deleteLoading}
            className="rounded-lg border border-rose-300 bg-white px-4 py-2 text-sm font-semibold text-rose-700"
          >
            Send Mobile OTP
          </button>

          {deleteOtpMeta && (
            <>
              <label className="block">
                <span className="text-sm font-medium text-rose-700">Mobile OTP</span>
                <input
                  value={deleteOtpCode}
                  onChange={(event) => setDeleteOtpCode(event.target.value)}
                  className="mt-2 w-full rounded-lg border border-rose-200 px-3 py-2 text-sm"
                  placeholder="Enter OTP"
                />
              </label>
              {deleteOtpMeta.phoneDevCode && (
                <p className="text-xs text-rose-600">Dev OTP: {deleteOtpMeta.phoneDevCode}</p>
              )}
              <button
                type="button"
                onClick={confirmDeleteAccount}
                disabled={deleteLoading}
                className="rounded-lg bg-rose-600 px-4 py-2 text-sm font-semibold text-white hover:bg-rose-700"
              >
                Confirm Delete Account
              </button>
            </>
          )}
        </div>
      </section>
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
