export const ROLE_GROUPS = {
  PLATFORM_ADMIN: ["SYSTEM_ADMIN", "ADMIN"],
  MERCHANT_ADMIN: ["MERCHANT_ADMIN", "ADMIN"],
  MERCHANT_OPERATIONS: ["MERCHANT_OPERATIONS", "MANAGER", "STAFF"],
  FINANCE: ["MERCHANT_FINANCE"],
  WAREHOUSE_MANAGER: ["WAREHOUSE_MANAGER", "MANAGER"],
  INVENTORY_AUDITOR: ["INVENTORY_AUDITOR"],
  PICKER: ["PICKER_PACKER", "STAFF"],
  RECEIVER: ["RECEIVER_GRN_OPERATOR", "STAFF"],
  VIEW_ONLY: ["MERCHANT_VIEWER", "USER"],
};

export function hasAnyRole(userRoles = [], requiredRoles = []) {
  return requiredRoles.some((role) => userRoles.includes(role));
}

export function canManageUsers(userRoles = []) {
  return hasAnyRole(userRoles, [...ROLE_GROUPS.PLATFORM_ADMIN, "MERCHANT_ADMIN"]);
}

export function canManageInventory(userRoles = []) {
  return hasAnyRole(userRoles, [
    ...ROLE_GROUPS.PLATFORM_ADMIN,
    "MERCHANT_ADMIN",
    "MERCHANT_OPERATIONS",
    "WAREHOUSE_MANAGER",
    "INVENTORY_AUDITOR",
    "PICKER_PACKER",
    "RECEIVER_GRN_OPERATOR",
    "MANAGER",
    "STAFF",
  ]);
}

export function canViewFinance(userRoles = []) {
  return hasAnyRole(userRoles, ["MERCHANT_FINANCE", "MERCHANT_ADMIN", "SYSTEM_ADMIN", "ADMIN"]);
}

export function canRunReconciliation(userRoles = []) {
  return hasAnyRole(userRoles, [
    "SYSTEM_ADMIN",
    "MERCHANT_ADMIN",
    "WAREHOUSE_MANAGER",
    "INVENTORY_AUDITOR",
    "ADMIN",
    "MANAGER",
  ]);
}

const DASHBOARD_ROUTE_PRIORITY = [
  { role: "SYSTEM_ADMIN", path: "/dashboard/system-admin" },
  { role: "SUPPORT_AGENT", path: "/dashboard/support-agent" },
  { role: "AUTOMATION_BOT", path: "/dashboard/automation-bot" },
  { role: "MERCHANT_ADMIN", path: "/dashboard/merchant-admin" },
  { role: "MERCHANT_FINANCE", path: "/dashboard/merchant-finance" },
  { role: "MERCHANT_OPERATIONS", path: "/dashboard/merchant-operations" },
  { role: "MERCHANT_VIEWER", path: "/dashboard/merchant-viewer" },
  { role: "WAREHOUSE_MANAGER", path: "/dashboard/warehouse-manager" },
  { role: "INVENTORY_AUDITOR", path: "/dashboard/inventory-auditor" },
  { role: "PICKER_PACKER", path: "/dashboard/picker-packer" },
  { role: "RECEIVER_GRN_OPERATOR", path: "/dashboard/receiver" },
  { role: "ADMIN", path: "/dashboard/admin" },
  { role: "MANAGER", path: "/dashboard/manager" },
  { role: "STAFF", path: "/dashboard/staff" },
  { role: "USER", path: "/dashboard/user" },
];

export function getDashboardPath(userRoles = []) {
  const match = DASHBOARD_ROUTE_PRIORITY.find((entry) => userRoles.includes(entry.role));
  return match?.path || "/dashboard/merchant-operations";
}
