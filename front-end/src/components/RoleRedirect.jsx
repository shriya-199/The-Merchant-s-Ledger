import { useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

export default function RoleRedirect() {
  const { user, loading } = useAuth();
  const navigate = useNavigate();

  useEffect(() => {
    if (loading) {
      return;
    }

    const roles = user?.roles || [];
    if (roles.includes("ADMIN")) {
      navigate("/inventory", { replace: true });
      return;
    }
    if (roles.includes("MANAGER")) {
      navigate("/manager", { replace: true });
      return;
    }
    if (roles.includes("STAFF")) {
      navigate("/staff", { replace: true });
      return;
    }

    navigate("/inventory", { replace: true });
  }, [user, loading, navigate]);

  return null;
}
