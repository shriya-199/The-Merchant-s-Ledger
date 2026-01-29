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
import ProtectedRoute from "./components/ProtectedRoute.jsx";
import AppLayout from "./components/AppLayout.jsx";
import RoleRedirect from "./components/RoleRedirect.jsx";

function App() {
  return (
    <Router>
      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/login" element={<Login />} />
        <Route path="/signup" element={<Signup />} />

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
          <Route path="/warehouses" element={<Warehouses />} />
          <Route path="/products" element={<Products />} />
          <Route path="/movements" element={<Movements />} />
          <Route path="/reports" element={<Reports />} />
          <Route path="/alerts" element={<Alerts />} />
          <Route path="/admin/users" element={<AdminUsers />} />
          <Route path="/audit" element={<AuditLogs />} />
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
