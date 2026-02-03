import { useEffect, useRef, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import { loadGoogleScript, renderGoogleButton } from "../lib/googleIdentity";

export default function Login() {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [emailOtp, setEmailOtp] = useState("");
  const [phoneOtp, setPhoneOtp] = useState("");
  const [otpMeta, setOtpMeta] = useState(null);
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);
  const googleButtonRef = useRef(null);
  const navigate = useNavigate();
  const { login, loginWithGoogle, requiresProfileCompletion, sendLoginOtp } = useAuth();
  const googleClientId = import.meta.env.VITE_GOOGLE_CLIENT_ID;

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");
    if (!otpMeta) {
      setError("Please send OTP first");
      return;
    }
    setLoading(true);
    try {
      await login({
        email,
        password,
        emailOtpChallengeId: otpMeta.emailChallengeId,
        emailOtpCode: emailOtp,
        phoneOtpChallengeId: otpMeta.phoneChallengeId,
        phoneOtpCode: phoneOtp,
      });
      navigate("/dashboard");
    } catch (err) {
      setError(err.message || "Login failed");
    } finally {
      setLoading(false);
    }
  };

  const handleSendOtp = async () => {
    setError("");
    if (!email || !password) {
      setError("Enter email and password first");
      return;
    }
    setLoading(true);
    try {
      const response = await sendLoginOtp({ email, password });
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
              setError(err.message || "Google login failed");
            } finally {
              setLoading(false);
            }
          },
        })
      )
      .catch((err) => setError(err.message || "Google Sign-In unavailable"));
  }, [googleClientId, loginWithGoogle, navigate, requiresProfileCompletion]);

  return (
    <div className="min-h-screen w-full grid grid-cols-1 md:grid-cols-2">
      {/* Left Branding Section */}
      <div className="hidden md:flex flex-col justify-center px-16 bg-slate-900 text-white">
        <h1 className="text-4xl font-semibold mb-4">Welcome Back</h1>
        <p className="text-slate-300 max-w-md">
          Real-time inventory control across warehouses, orders, and suppliers.
        </p>
      </div>

      {/* Right Login Section */}
      <div className="flex items-center justify-center bg-slate-50 px-6">
        <div className="w-full max-w-md">
          <h2 className="text-2xl font-semibold text-slate-800 mb-2">Sign in</h2>
          <p className="text-sm text-slate-500 mb-8">
            Use your credentials to access the dashboard
          </p>

          <form onSubmit={handleSubmit} className="space-y-5">
            <div>
              <label className="block text-sm font-medium text-slate-700 mb-1">Email</label>
              <input
                type="email"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                placeholder="you@example.com"
                className="w-full px-3 py-2.5 rounded-md border border-slate-300
                           focus:outline-none focus:ring-2 focus:ring-blue-500"
                required
              />
            </div>

            <div>
              <label className="block text-sm font-medium text-slate-700 mb-1">Password</label>
              <input
                type="password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                placeholder="********"
                className="w-full px-3 py-2.5 rounded-md border border-slate-300
                           focus:outline-none focus:ring-2 focus:ring-blue-500"
                required
              />
            </div>

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
                <div>
                  <label className="block text-sm font-medium text-slate-700 mb-1">Email OTP</label>
                  <input
                    type="text"
                    value={emailOtp}
                    onChange={(e) => setEmailOtp(e.target.value)}
                    placeholder="Enter email OTP"
                    className="w-full px-3 py-2.5 rounded-md border border-slate-300 focus:outline-none focus:ring-2 focus:ring-blue-500"
                    required
                  />
                </div>
                <div>
                  <label className="block text-sm font-medium text-slate-700 mb-1">Mobile OTP</label>
                  <input
                    type="text"
                    value={phoneOtp}
                    onChange={(e) => setPhoneOtp(e.target.value)}
                    placeholder="Enter mobile OTP"
                    className="w-full px-3 py-2.5 rounded-md border border-slate-300 focus:outline-none focus:ring-2 focus:ring-blue-500"
                    required
                  />
                </div>
                {(otpMeta.emailDevCode || otpMeta.phoneDevCode) && (
                  <p className="text-xs text-slate-500">
                    Dev OTPs - Email: {otpMeta.emailDevCode || "-"}, Mobile: {otpMeta.phoneDevCode || "-"}
                  </p>
                )}
              </>
            )}

            <button
              type="submit"
              className="w-full bg-blue-600 text-white font-medium py-2.5 rounded-md
                         hover:bg-blue-700 transition"
              disabled={loading}
            >
              {loading ? "Signing in..." : "Sign In"}
            </button>

            <div className="flex items-center justify-between">
              <a href="#" className="text-sm text-blue-600 hover:underline">
                Forgot Password?
              </a>

              <p className="text-sm text-slate-600">
                New user?{" "}
                <Link to="/signup" className="text-blue-600 hover:underline">
                  Sign up
                </Link>
              </p>
            </div>

            {error && <p className="text-sm text-rose-600">{error}</p>}
          </form>

          <div className="mt-4 space-y-2">
            {!googleClientId && (
              <p className="text-xs text-amber-600">Set `VITE_GOOGLE_CLIENT_ID` to enable Google login.</p>
            )}
            <div ref={googleButtonRef} className="flex justify-center" />
          </div>

          <p className="text-xs text-slate-500 mt-8 text-center">
            (c) 2026 Enterprise Platform. All rights reserved.
          </p>
        </div>
      </div>
    </div>
  );
}
