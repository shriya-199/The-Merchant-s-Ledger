import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";

export default function Login() {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const navigate = useNavigate();

  const handleSubmit = (e) => {
    e.preventDefault();
    console.log({ username, password });
    // Example: redirect to home after login
    navigate("/home");
  };

  return (
    <div className="min-h-screen w-full grid grid-cols-1 md:grid-cols-2">

      {/* Left Branding Section */}
      <div className="hidden md:flex flex-col justify-center px-16 bg-slate-900 text-white">
        <h1 className="text-4xl font-semibold mb-4">
          Welcome Back
        </h1>
        <p className="text-slate-300 max-w-md">
          Real-time inventory control across warehouses, orders, and suppliers.
        </p>
      </div>

      {/* Right Login Section */}
      <div className="flex items-center justify-center bg-slate-50 px-6">
        <div className="w-full max-w-md">

          <h2 className="text-2xl font-semibold text-slate-800 mb-2">
            Sign in
          </h2>
          <p className="text-sm text-slate-500 mb-8">
            Use your credentials to access the dashboard
          </p>

          <form onSubmit={handleSubmit} className="space-y-5">

            {/* Username */}
            <div>
              <label className="block text-sm font-medium text-slate-700 mb-1">
                Username
              </label>
              <input
                type="text"
                value={username}
                onChange={(e) => setUsername(e.target.value)}
                placeholder="Enter your username"
                className="w-full px-3 py-2.5 rounded-md border border-slate-300
                           focus:outline-none focus:ring-2 focus:ring-blue-500"
                required
              />
            </div>

            {/* Password */}
            <div>
              <label className="block text-sm font-medium text-slate-700 mb-1">
                Password
              </label>
              <input
                type="password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                placeholder="••••••••"
                className="w-full px-3 py-2.5 rounded-md border border-slate-300
                           focus:outline-none focus:ring-2 focus:ring-blue-500"
                required
              />
            </div>

            {/* Sign In Button */}
            <button
              type="submit"
              className="w-full bg-blue-600 text-white font-medium py-2.5 rounded-md
                         hover:bg-blue-700 transition"
            >
              Sign In
            </button>

            {/* Forgot Password & Sign Up Links */}
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
          </form>

          {/* Sign In with Google */}
          <div className="mt-4">
            <button
              type="button"
              className="w-full bg-blue-600 text-white font-medium py-2.5 rounded-md
                         hover:bg-blue-700 transition"
            >
              Sign-in with Google
            </button>
          </div>

          <p className="text-xs text-slate-500 mt-8 text-center">
            © 2026 Enterprise Platform. All rights reserved.
          </p>
        </div>
      </div>
    </div>
  );
}
