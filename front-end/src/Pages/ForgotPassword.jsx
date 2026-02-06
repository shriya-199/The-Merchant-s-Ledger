import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

export default function ForgotPassword() {
  const { sendForgotPasswordOtp, resetForgottenPassword } = useAuth();
  const navigate = useNavigate();
  const [email, setEmail] = useState("");
  const [otpMeta, setOtpMeta] = useState(null);
  const [emailOtp, setEmailOtp] = useState("");
  const [newPassword, setNewPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");
  const [loading, setLoading] = useState(false);

  const handleSendOtp = async () => {
    setError("");
    setSuccess("");
    if (!email) {
      setError("Enter email first");
      return;
    }
    setLoading(true);
    try {
      const response = await sendForgotPasswordOtp({ email });
      setOtpMeta(response);
      setSuccess("OTP sent to your email.");
    } catch (err) {
      setError(err.message || "Failed to send OTP");
    } finally {
      setLoading(false);
    }
  };

  const handleReset = async (event) => {
    event.preventDefault();
    setError("");
    setSuccess("");
    if (!otpMeta) {
      setError("Please send OTP first");
      return;
    }
    if (newPassword !== confirmPassword) {
      setError("Passwords do not match");
      return;
    }
    setLoading(true);
    try {
      await resetForgottenPassword({
        email,
        emailOtpChallengeId: otpMeta.emailChallengeId,
        emailOtpCode: emailOtp,
        newPassword,
      });
      setSuccess("Password reset successful. Redirecting to login...");
      window.setTimeout(() => navigate("/login"), 900);
    } catch (err) {
      setError(err.message || "Failed to reset password");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen w-full grid grid-cols-1 md:grid-cols-2 bg-slate-100">
      <div className="hidden md:flex flex-col justify-center px-16 text-white bg-slate-700">
        <h1 className="text-4xl font-semibold mb-4">Reset Password</h1>
        <p className="text-slate-100 max-w-md">Use email OTP to securely set a new password.</p>
      </div>

      <div className="flex items-center justify-center px-6 bg-slate-50">
        <div className="w-full max-w-md rounded-2xl p-8 shadow-sm bg-white text-slate-900">
          <h2 className="text-2xl font-semibold mb-2">Forgot Password</h2>
          <p className="text-sm mb-8 text-slate-500">Enter email, verify OTP, and set a new password.</p>

          <form onSubmit={handleReset} className="space-y-5">
            <Field label="Email" type="email" value={email} onChange={setEmail} required />
            <button
              type="button"
              onClick={handleSendOtp}
              disabled={loading}
              className="w-full rounded-md border border-blue-200 bg-blue-50 py-2.5 text-sm font-medium text-blue-700 hover:bg-blue-100"
            >
              Send Email OTP
            </button>

            {otpMeta && (
              <>
                <Field label="Email OTP" value={emailOtp} onChange={setEmailOtp} required />
                {otpMeta.emailDevCode && <p className="text-xs text-slate-500">Dev OTP: {otpMeta.emailDevCode}</p>}
                <Field label="New Password" type="password" value={newPassword} onChange={setNewPassword} required />
                <Field
                  label="Confirm Password"
                  type="password"
                  value={confirmPassword}
                  onChange={setConfirmPassword}
                  required
                />
              </>
            )}

            <button
              type="submit"
              disabled={loading}
              className="w-full bg-blue-600 text-white font-medium py-2.5 rounded-md hover:bg-blue-700 transition"
            >
              {loading ? "Processing..." : "Reset Password"}
            </button>
          </form>

          {error && <p className="mt-4 text-sm text-rose-600">{error}</p>}
          {success && <p className="mt-4 text-sm text-emerald-600">{success}</p>}

          <p className="mt-6 text-sm text-slate-600">
            Back to <Link to="/login" className="text-blue-600 hover:underline">Sign in</Link>
          </p>
        </div>
      </div>
    </div>
  );
}

function Field({ label, type = "text", value, onChange, required = false }) {
  return (
    <div>
      <label className="block text-sm font-medium mb-1 text-slate-700">{label}</label>
      <input
        type={type}
        value={value}
        onChange={(e) => onChange(e.target.value)}
        required={required}
        className="w-full px-3 py-2.5 rounded-md border border-slate-300 bg-white text-slate-900 focus:outline-none focus:ring-2 focus:ring-blue-500"
      />
    </div>
  );
}
