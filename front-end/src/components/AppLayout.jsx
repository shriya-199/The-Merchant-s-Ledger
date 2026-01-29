import { NavLink, Outlet } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

export default function AppLayout() {
  const { user, logout } = useAuth();
  const roles = user?.roles || [];
  const hasRole = (role) => roles.includes(role);

  return (
    <div className="min-h-screen bg-slate-50 text-slate-900">
      <header className="border-b border-slate-200 bg-white">
        <div className="mx-auto flex w-full max-w-6xl items-center justify-between px-6 py-4">
          <div>
            <p className="text-xs uppercase tracking-[0.2em] text-slate-500">Merchant's Ledger</p>
            <h1 className="text-xl font-semibold">Operations Console</h1>
          </div>
          <div className="flex items-center gap-4 text-sm">
            <span className="text-slate-600">{user?.fullName}</span>
            <button
              onClick={logout}
              className="rounded-md border border-slate-200 px-3 py-1.5 text-sm hover:bg-slate-100"
            >
              Sign out
            </button>
          </div>
        </div>
      </header>

      <div className="mx-auto w-full max-w-6xl px-6">
        <nav className="flex flex-wrap gap-3 py-6 text-sm text-slate-600">
          <NavLink
            to={roles.includes("ADMIN") ? "/inventory" : roles.includes("MANAGER") ? "/manager" : "/staff"}
            className={({ isActive }) =>
              isActive
                ? "rounded-full bg-slate-900 px-4 py-2 text-white"
                : "rounded-full border border-slate-200 px-4 py-2 hover:bg-white"
            }
          >
            Dashboard
          </NavLink>
          <NavLink
            to="/inventory"
            className={({ isActive }) =>
              isActive
                ? "rounded-full bg-slate-900 px-4 py-2 text-white"
                : "rounded-full border border-slate-200 px-4 py-2 hover:bg-white"
            }
          >
            Inventory
          </NavLink>
          {(hasRole("ADMIN") || hasRole("MANAGER")) && (
            <NavLink
              to="/movements"
              className={({ isActive }) =>
                isActive
                  ? "rounded-full bg-slate-900 px-4 py-2 text-white"
                  : "rounded-full border border-slate-200 px-4 py-2 hover:bg-white"
              }
            >
              Movements
            </NavLink>
          )}
          {(hasRole("ADMIN") || hasRole("MANAGER")) && (
            <NavLink
              to="/reports"
              className={({ isActive }) =>
                isActive
                  ? "rounded-full bg-slate-900 px-4 py-2 text-white"
                  : "rounded-full border border-slate-200 px-4 py-2 hover:bg-white"
              }
            >
              Reports
            </NavLink>
          )}
          <NavLink
            to="/alerts"
            className={({ isActive }) =>
              isActive
                ? "rounded-full bg-slate-900 px-4 py-2 text-white"
                : "rounded-full border border-slate-200 px-4 py-2 hover:bg-white"
            }
          >
            Alerts
          </NavLink>
          {(hasRole("ADMIN") || hasRole("MANAGER")) && (
            <NavLink
              to="/products"
              className={({ isActive }) =>
                isActive
                  ? "rounded-full bg-slate-900 px-4 py-2 text-white"
                  : "rounded-full border border-slate-200 px-4 py-2 hover:bg-white"
              }
            >
              Products
            </NavLink>
          )}
          {hasRole("ADMIN") && (
            <NavLink
              to="/warehouses"
              className={({ isActive }) =>
                isActive
                  ? "rounded-full bg-slate-900 px-4 py-2 text-white"
                  : "rounded-full border border-slate-200 px-4 py-2 hover:bg-white"
              }
            >
              Warehouses
            </NavLink>
          )}
          {hasRole("ADMIN") && (
            <NavLink
              to="/admin/users"
              className={({ isActive }) =>
                isActive
                  ? "rounded-full bg-slate-900 px-4 py-2 text-white"
                  : "rounded-full border border-slate-200 px-4 py-2 hover:bg-white"
              }
            >
              Users
            </NavLink>
          )}
          {(hasRole("ADMIN") || hasRole("MANAGER")) && (
            <NavLink
              to="/audit"
              className={({ isActive }) =>
                isActive
                  ? "rounded-full bg-slate-900 px-4 py-2 text-white"
                  : "rounded-full border border-slate-200 px-4 py-2 hover:bg-white"
              }
            >
              Audit
            </NavLink>
          )}
          <NavLink
            to="/profile"
            className={({ isActive }) =>
              isActive
                ? "rounded-full bg-slate-900 px-4 py-2 text-white"
                : "rounded-full border border-slate-200 px-4 py-2 hover:bg-white"
            }
          >
            Profile
          </NavLink>
        </nav>
      </div>

      <main className="mx-auto w-full max-w-6xl px-6 pb-16">
        <Outlet />
      </main>
    </div>
  );
}
