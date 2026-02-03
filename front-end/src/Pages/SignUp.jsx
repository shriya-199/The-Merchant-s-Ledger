import { useEffect, useRef, useState } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import { loadGoogleScript, renderGoogleButton } from "../lib/googleIdentity";

export default function Signup() {
  const roleOptions = [
    { value: "MERCHANT_OPERATIONS", label: "Merchant Operations" },
    { value: "MERCHANT_FINANCE", label: "Merchant Finance" },
    { value: "MERCHANT_VIEWER", label: "Merchant Viewer" },
    { value: "PICKER_PACKER", label: "Picker/Packer" },
    { value: "RECEIVER_GRN_OPERATOR", label: "Receiver / GRN Operator" },
    { value: "STAFF", label: "Legacy Staff" },
    { value: "MANAGER", label: "Legacy Manager" },
  ];

  const [form, setForm] = useState({
    fullName: "",
    email: "",
    password: "",
    confirmPassword: "",
    phone: "",
    address: "",
    companyName: "",
    roleTitle: "",
    roleName: "MERCHANT_OPERATIONS",
  });
  const [emailOtp, setEmailOtp] = useState("");
  const [phoneOtp, setPhoneOtp] = useState("");
  const [otpMeta, setOtpMeta] = useState(null);
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);
  const googleButtonRef = useRef(null);
  const navigate = useNavigate();
  const { register, loginWithGoogle, requiresProfileCompletion, sendSignupOtp } = useAuth();
  const googleClientId = import.meta.env.VITE_GOOGLE_CLIENT_ID;

  const handleChange = (event) => {
    const { name, value } = event.target;
    setForm((prev) => ({ ...prev, [name]: value }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");

    if (form.password !== form.confirmPassword) {
      setError("Passwords do not match");
      return;
    }

    setLoading(true);
    try {
      if (!otpMeta) {
        throw new Error("Please send OTP first");
      }
      await register({
        fullName: form.fullName,
        email: form.email,
        password: form.password,
        phone: form.phone,
        address: form.address,
        companyName: form.companyName,
        roleTitle: form.roleTitle,
        roleName: form.roleName,
        emailOtpChallengeId: otpMeta.emailChallengeId,
        emailOtpCode: emailOtp,
        phoneOtpChallengeId: otpMeta.phoneChallengeId,
        phoneOtpCode: phoneOtp,
      });
      navigate("/dashboard");
    } catch (err) {
      setError(err.message || "Registration failed");
    } finally {
      setLoading(false);
    }
  };

  const handleSendOtp = async () => {
    setError("");
    if (!form.email || !form.phone) {
      setError("Enter email and phone first");
      return;
    }
    setLoading(true);
    try {
      const response = await sendSignupOtp({ email: form.email, phone: form.phone });
      setOtpMeta(response);
    } catch (err) {
      setError(err.message || "Failed to send OTP");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (!googleClientId || !googleButtonRef.current) {
      return;
    }

    loadGoogleScript()
      .then(() =>
        renderGoogleButton({
          clientId: googleClientId,
          container: googleButtonRef.current,
          onCredential: async (credential) => {
            setError("");
            setLoading(true);
            try {
              const result = await loginWithGoogle(credential);
              navigate(requiresProfileCompletion(result.user) ? "/complete-profile" : "/dashboard");
            } catch (err) {
              setError(err.message || "Google signup failed");
            } finally {
              setLoading(false);
            }
          },
        })
      )
      .catch((err) => setError(err.message || "Google Sign-Up unavailable"));
  }, [googleClientId, loginWithGoogle, navigate, requiresProfileCompletion]);

  return (
    <div className="min-h-screen w-full grid grid-cols-1 md:grid-cols-2">
      <div className="hidden md:flex flex-col justify-center px-16 bg-slate-900 text-white">
        <h1 className="text-4xl font-semibold mb-4">Create Account</h1>
        <p className="text-slate-300 max-w-md">
          Register for seamless enterprise inventory management.
        </p>
      </div>

      <div className="flex items-center justify-center bg-slate-50 px-6">
        <div className="w-full max-w-md">
          <h2 className="text-2xl font-semibold text-slate-800 mb-2">Sign up</h2>
          <p className="text-sm text-slate-500 mb-8">Create your account to get started</p>

          <form onSubmit={handleSubmit} className="space-y-5">
            <Field label="Full name" name="fullName" value={form.fullName} onChange={handleChange} required />
            <Field label="Email address" type="email" name="email" value={form.email} onChange={handleChange} required />
            <Field label="Password" type="password" name="password" value={form.password} onChange={handleChange} required />
            <Field
              label="Confirm password"
              type="password"
              name="confirmPassword"
              value={form.confirmPassword}
              onChange={handleChange}
              required
            />
            <Field label="Phone" name="phone" value={form.phone} onChange={handleChange} />
            <Field label="Address" name="address" value={form.address} onChange={handleChange} />
            <Field label="Company" name="companyName" value={form.companyName} onChange={handleChange} />
            <Field label="Role title" name="roleTitle" value={form.roleTitle} onChange={handleChange} />
            <button
              type="button"
              onClick={handleSendOtp}
              className="w-full rounded-md border border-blue-200 bg-blue-50 py-2.5 text-sm font-medium text-blue-700 hover:bg-blue-100 transition"
              disabled={loading}
            >
              Send OTP (Email + Mobile)
            </button>

            {otpMeta && (
              <>
                <Field
                  label="Email OTP"
                  name="emailOtp"
                  value={emailOtp}
                  onChange={(e) => setEmailOtp(e.target.value)}
                  required
                />
                <Field
                  label="Mobile OTP"
                  name="phoneOtp"
                  value={phoneOtp}
                  onChange={(e) => setPhoneOtp(e.target.value)}
                  required
                />
                {(otpMeta.emailDevCode || otpMeta.phoneDevCode) && (
                  <p className="text-xs text-slate-500">
                    Dev OTPs - Email: {otpMeta.emailDevCode || "-"}, Mobile: {otpMeta.phoneDevCode || "-"}
                  </p>
                )}
              </>
            )}

            <div>
              <label className="block text-sm font-medium text-slate-700 mb-1">Role</label>
              <select
                name="roleName"
                value={form.roleName}
                onChange={handleChange}
                className="w-full px-3 py-2.5 rounded-md border border-slate-300
                           focus:outline-none focus:ring-2 focus:ring-blue-500"
              >
                {roleOptions.map((role) => (
                  <option key={role.value} value={role.value}>
                    {role.label}
                  </option>
                ))}
              </select>
              <p className="mt-1 text-xs text-slate-500">
                Admin accounts can only be created by existing admins.
              </p>
            </div>

            <button
              type="submit"
              className="w-full bg-blue-600 text-white font-medium py-2.5 rounded-md
                         hover:bg-blue-700 transition"
              disabled={loading}
            >
              {loading ? "Creating..." : "Create Account"}
            </button>

            {error && <p className="text-sm text-rose-600">{error}</p>}
          </form>

          <div className="mt-5 space-y-2">
            {!googleClientId && (
              <p className="text-xs text-amber-600">Set `VITE_GOOGLE_CLIENT_ID` to enable Google signup.</p>
            )}
            <div ref={googleButtonRef} className="flex justify-center" />
          </div>

          <p className="text-sm text-slate-600 mt-6 text-center">
            Already have an account?{" "}
            <button
              onClick={() => navigate("/login")}
              className="text-blue-600 hover:underline font-medium"
            >
              Sign in
            </button>
          </p>

          <p className="text-xs text-slate-500 mt-6 text-center">
            (c) 2026 Enterprise Platform. All rights reserved.
          </p>
        </div>
      </div>
    </div>
  );
}

function Field({ label, name, value, onChange, required = false, type = "text" }) {
  return (
    <div>
      <label className="block text-sm font-medium text-slate-700 mb-1">{label}</label>
      <input
        type={type}
        name={name}
        value={value}
        onChange={onChange}
        className="w-full px-3 py-2.5 rounded-md border border-slate-300
                   focus:outline-none focus:ring-2 focus:ring-blue-500"
        required={required}
      />
    </div>
  );
}
