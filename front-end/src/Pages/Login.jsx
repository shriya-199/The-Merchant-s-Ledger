import { useEffect, useRef, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import { loadGoogleScript, renderGoogleButton } from "../lib/googleIdentity";
import { getTheme, subscribeTheme } from "../lib/theme";

export default function Login() {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [theme, setTheme] = useState(getTheme);
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);
  const googleButtonRef = useRef(null);
  const navigate = useNavigate();
  const { login, loginWithGoogle, requiresProfileCompletion } = useAuth();
  const googleClientId = import.meta.env.VITE_GOOGLE_CLIENT_ID;
  const isDark = theme === "dark";

  useEffect(() => {
    const unsubscribe = subscribeTheme(setTheme);
    return unsubscribe;
  }, []);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");
    setLoading(true);
    try {
      await login(email, password);
      navigate("/dashboard");
    } catch (err) {
      setError(err.message || "Login failed");
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
    <div className={`min-h-screen w-full grid grid-cols-1 md:grid-cols-2 transition-colors duration-500 ${isDark ? "bg-slate-950" : "bg-slate-100"}`}>
      {/* Left Branding Section */}
      <div className="relative hidden md:flex flex-col justify-center px-16 text-white overflow-hidden">
        <div className={`absolute inset-0 transition-colors duration-700 ${isDark ? "bg-slate-900" : "bg-slate-700"}`} />
        <div className="relative z-10">
          <h1 className="text-4xl font-semibold mb-4">Welcome Back</h1>
          <p className="text-slate-100 max-w-md">
            Real-time inventory control across warehouses, orders, and suppliers.
          </p>
        </div>
      </div>

      {/* Right Login Section */}
      <div className={`relative flex items-center justify-center px-6 transition-colors duration-500 ${isDark ? "bg-slate-900" : "bg-slate-50"}`}>
        <div className={`w-full max-w-md rounded-2xl p-8 shadow-sm transition-colors duration-500 ${isDark ? "bg-slate-800 text-slate-100" : "bg-white text-slate-900"}`}>
          <h2 className={`text-2xl font-semibold mb-2 ${isDark ? "text-slate-100" : "text-slate-800"}`}>Sign in</h2>
          <p className={`text-sm mb-8 transition-colors duration-500 ${isDark ? "text-slate-300" : "text-slate-500"}`}>
            Use your credentials to access the dashboard
          </p>

          <form onSubmit={handleSubmit} className="space-y-5">
            <div>
              <label className={`block text-sm font-medium mb-1 ${isDark ? "text-slate-100" : "text-slate-700"}`}>Email</label>
              <input
                type="email"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                placeholder="you@example.com"
                className={`w-full px-3 py-2.5 rounded-md border transition-colors duration-500 ${
                  isDark ? "border-slate-600 bg-slate-900 text-slate-100" : "border-slate-300 bg-white text-slate-900"
                }
                           focus:outline-none focus:ring-2 focus:ring-blue-500`}
                required
              />
            </div>

            <div>
              <label className={`block text-sm font-medium mb-1 ${isDark ? "text-slate-100" : "text-slate-700"}`}>Password</label>
              <input
                type="password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                placeholder="********"
                className={`w-full px-3 py-2.5 rounded-md border transition-colors duration-500 ${
                  isDark ? "border-slate-600 bg-slate-900 text-slate-100" : "border-slate-300 bg-white text-slate-900"
                }
                           focus:outline-none focus:ring-2 focus:ring-blue-500`}
                required
              />
            </div>

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

              <p className={`text-sm ${isDark ? "text-slate-300" : "text-slate-600"}`}>
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

          <p className={`text-xs mt-8 text-center ${isDark ? "text-slate-400" : "text-slate-500"}`}>
            (c) 2026 Enterprise Platform. All rights reserved.
          </p>
        </div>
      </div>
    </div>
  );
}
