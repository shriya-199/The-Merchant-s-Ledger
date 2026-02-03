import { useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import { getDashboardPath } from "../lib/roles";

export default function RoleRedirect() {
  const { user, loading } = useAuth();
  const navigate = useNavigate();

  useEffect(() => {
    if (loading) {
      return;
    }

    const roles = user?.roles || [];
    navigate(getDashboardPath(roles), { replace: true });
  }, [user, loading, navigate]);

  return null;
}
