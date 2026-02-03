import { useEffect, useState } from "react";
import { apiRequest } from "../lib/api";

const roles = [
  "SYSTEM_ADMIN",
  "SUPPORT_AGENT",
  "AUTOMATION_BOT",
  "MERCHANT_ADMIN",
  "MERCHANT_FINANCE",
  "MERCHANT_OPERATIONS",
  "MERCHANT_VIEWER",
  "WAREHOUSE_MANAGER",
  "INVENTORY_AUDITOR",
  "PICKER_PACKER",
  "RECEIVER_GRN_OPERATOR",
  "ADMIN",
  "MANAGER",
  "STAFF",
  "USER",
];

export default function AdminUsers() {
  const [users, setUsers] = useState([]);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  const load = async () => {
    try {
      const data = await apiRequest("/api/admin/users");
      setUsers(data);
    } catch (err) {
      setError(err.message || "Failed to load users");
    }
  };

  useEffect(() => {
    load();
  }, []);

  const updateUser = async (id, payload) => {
    setError("");
    setSuccess("");
    try {
      await apiRequest(`/api/admin/users/${id}`, {
        method: "PUT",
        body: JSON.stringify(payload),
      });
      setSuccess("User updated");
      load();
    } catch (err) {
      setError(err.message || "Failed to update user");
    }
  };

  return (
    <div className="space-y-6">
      <header>
        <h2 className="text-2xl font-semibold">User Management</h2>
        <p className="text-slate-600">Manage roles and access for each team member.</p>
      </header>

      {(error || success) && (
        <div className={`rounded-lg border p-4 ${error ? "border-rose-200 bg-rose-50 text-rose-600" : "border-emerald-200 bg-emerald-50 text-emerald-600"}`}>
          {error || success}
        </div>
      )}

      <div className="rounded-2xl bg-white p-6 shadow-sm">
        <table className="min-w-full text-left text-sm">
          <thead className="border-b border-slate-200 text-xs uppercase text-slate-400">
            <tr>
              <th className="py-2">Name</th>
              <th className="py-2">Email</th>
              <th className="py-2">Role</th>
              <th className="py-2">Status</th>
              <th className="py-2">Actions</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-slate-100">
            {users.map((user) => (
              <tr key={user.id}>
                <td className="py-3 font-medium text-slate-700">{user.fullName}</td>
                <td className="py-3 text-slate-500">{user.email}</td>
                <td className="py-3">
                  <select
                    className="rounded-md border border-slate-200 px-2 py-1"
                    value={(user.roles && user.roles.length > 0 ? user.roles[0] : "MERCHANT_OPERATIONS")}
                    onChange={(event) => updateUser(user.id, { roleName: event.target.value })}
                  >
                    {roles.map((role) => (
                      <option key={role} value={role}>{role}</option>
                    ))}
                  </select>
                </td>
                <td className="py-3">
                  <span className={`rounded-full px-2 py-1 text-xs ${user.enabled ? "bg-emerald-100 text-emerald-700" : "bg-rose-100 text-rose-700"}`}>
                    {user.enabled ? "Active" : "Disabled"}
                  </span>
                </td>
                <td className="py-3">
                  <button
                    className="rounded-md border border-slate-200 px-3 py-1 text-xs"
                    onClick={() => updateUser(user.id, { enabled: !user.enabled })}
                  >
                    {user.enabled ? "Disable" : "Enable"}
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}
