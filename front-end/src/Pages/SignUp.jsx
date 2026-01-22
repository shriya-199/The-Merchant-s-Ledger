import { useState } from "react";

export default function Signup() {
  const [name, setName] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");

  const handleSubmit = (e) => {
    e.preventDefault();
    console.log({ name, email, password, confirmPassword });
  };

  return (
    <div className="min-h-screen w-full grid grid-cols-1 md:grid-cols-2">

      {/* Left Branding Section */}
      <div className="hidden md:flex flex-col justify-center px-16 bg-slate-900 text-white">
        <h1 className="text-4xl font-semibold mb-4">
          Create Account
        </h1>
        <p className="text-slate-300 max-w-md">
          Register for seamless enterprise inventory management.
        </p>
      </div>

      {/* Right Signup Section */}
      <div className="flex items-center justify-center bg-slate-50 px-6">
        <div className="w-full max-w-md">

          <h2 className="text-2xl font-semibold text-slate-800 mb-2">
            Sign up
          </h2>
          <p className="text-sm text-slate-500 mb-8">
            Create your account to get started
          </p>

          <form onSubmit={handleSubmit} className="space-y-5">

            {/* Full Name */}
            <div>
              <label className="block text-sm font-medium text-slate-700 mb-1">
                Full Name
              </label>
              <input
                type="text"
                value={name}
                onChange={(e) => setName(e.target.value)}
                placeholder="Enter your full name"
                className="w-full px-3 py-2.5 rounded-md border border-slate-300
                           focus:outline-none focus:ring-2 focus:ring-blue-500"
                required
              />
            </div>

            {/* Email */}
            <div>
              <label className="block text-sm font-medium text-slate-700 mb-1">
                Email address
              </label>
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

            {/* Confirm Password */}
            <div>
              <label className="block text-sm font-medium text-slate-700 mb-1">
                Confirm Password
              </label>
              <input
                type="password"
                value={confirmPassword}
                onChange={(e) => setConfirmPassword(e.target.value)}
                placeholder="••••••••"
                className="w-full px-3 py-2.5 rounded-md border border-slate-300
                           focus:outline-none focus:ring-2 focus:ring-blue-500"
                required
              />
            </div>

            {/* Button */}
            <button
              type="submit"
              className="w-full bg-blue-600 text-white font-medium py-2.5 rounded-md
                         hover:bg-blue-700 transition"
            >
              Create Account
            </button>
          </form>

          {/* Footer link */}
          <p className="text-sm text-slate-600 mt-6 text-center">
            Already have an account?{" "}
            <a href="#" className="text-blue-600 hover:underline font-medium">
              Sign in
            </a>
          </p>

          <p className="text-xs text-slate-500 mt-6 text-center">
            © 2026 Enterprise Platform. All rights reserved.
          </p>
        </div>
      </div>
    </div>
  );
}
