import { BrowserRouter as Router, Route, Routes } from "react-router-dom";
import Home from "./Pages/Home.jsx";
import Login from "./Pages/Login.jsx";
import Signup from "./Pages/SignUp.jsx";
import Dashboard from "./Pages/Dashboard.jsx";
import Customers from "./Pages/Customers.jsx";
import Ledger from "./Pages/Ledger.jsx";
import Profile from "./Pages/Profile.jsx";
import InventoryDashboard from "./Pages/InventoryDashboard.jsx";
import Warehouses from "./Pages/Warehouses.jsx";
import Products from "./Pages/Products.jsx";
import Movements from "./Pages/Movements.jsx";
import ManagerDashboard from "./Pages/ManagerDashboard.jsx";
import StaffDashboard from "./Pages/StaffDashboard.jsx";
import Reports from "./Pages/Reports.jsx";
import Alerts from "./Pages/Alerts.jsx";
import AdminUsers from "./Pages/AdminUsers.jsx";
import AuditLogs from "./Pages/AuditLogs.jsx";
import Reconciliation from "./Pages/Reconciliation.jsx";
import RoleDashboard from "./Pages/RoleDashboard.jsx";
import CompleteProfile from "./Pages/CompleteProfile.jsx";
import ProtectedRoute from "./components/ProtectedRoute.jsx";
import AppLayout from "./components/AppLayout.jsx";
import RoleRedirect from "./components/RoleRedirect.jsx";
import ThemeToggle from "./components/ThemeToggle.jsx";

function App() {
  return (
    <Router>
      <ThemeToggle />
      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/login" element={<Login />} />
        <Route path="/signup" element={<Signup />} />
        <Route
          path="/complete-profile"
          element={
            <ProtectedRoute>
              <CompleteProfile />
            </ProtectedRoute>
          }
        />

        <Route
          element={
            <ProtectedRoute>
              <AppLayout />
            </ProtectedRoute>
          }
        >
          <Route path="/inventory" element={<InventoryDashboard />} />
          <Route path="/manager" element={<ManagerDashboard />} />
          <Route path="/staff" element={<StaffDashboard />} />
          <Route path="/dashboard/system-admin" element={<RoleDashboard roleKey="system-admin" />} />
          <Route path="/dashboard/support-agent" element={<RoleDashboard roleKey="support-agent" />} />
          <Route path="/dashboard/automation-bot" element={<RoleDashboard roleKey="automation-bot" />} />
          <Route path="/dashboard/merchant-admin" element={<RoleDashboard roleKey="merchant-admin" />} />
          <Route path="/dashboard/merchant-finance" element={<RoleDashboard roleKey="merchant-finance" />} />
          <Route path="/dashboard/merchant-operations" element={<RoleDashboard roleKey="merchant-operations" />} />
          <Route path="/dashboard/merchant-viewer" element={<RoleDashboard roleKey="merchant-viewer" />} />
          <Route path="/dashboard/warehouse-manager" element={<RoleDashboard roleKey="warehouse-manager" />} />
          <Route path="/dashboard/inventory-auditor" element={<RoleDashboard roleKey="inventory-auditor" />} />
          <Route path="/dashboard/picker-packer" element={<RoleDashboard roleKey="picker-packer" />} />
          <Route path="/dashboard/receiver" element={<RoleDashboard roleKey="receiver" />} />
          <Route path="/dashboard/admin" element={<RoleDashboard roleKey="admin" />} />
          <Route path="/dashboard/manager" element={<RoleDashboard roleKey="manager" />} />
          <Route path="/dashboard/staff" element={<RoleDashboard roleKey="staff" />} />
          <Route path="/dashboard/user" element={<RoleDashboard roleKey="user" />} />
          <Route path="/warehouses" element={<Warehouses />} />
          <Route path="/products" element={<Products />} />
          <Route path="/movements" element={<Movements />} />
          <Route path="/reports" element={<Reports />} />
          <Route path="/alerts" element={<Alerts />} />
          <Route path="/admin/users" element={<AdminUsers />} />
          <Route path="/audit" element={<AuditLogs />} />
          <Route path="/reconciliation" element={<Reconciliation />} />
          <Route path="/profile" element={<Profile />} />
          <Route path="/dashboard" element={<RoleRedirect />} />
          <Route path="/customers" element={<Customers />} />
          <Route path="/ledger" element={<Ledger />} />
        </Route>
      </Routes>
    </Router>
  );
}

export default App;
