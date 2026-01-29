import { createContext, useContext, useEffect, useMemo, useState } from "react";
import { apiRequest } from "../lib/api";

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [token, setToken] = useState(localStorage.getItem("token"));
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);

  const refreshUser = async () => {
    if (!token) {
      setUser(null);
      return;
    }

    const me = await apiRequest("/api/auth/me");
    setUser(me);
  };

  useEffect(() => {
    const bootstrap = async () => {
      if (!token) {
        setLoading(false);
        return;
      }

      try {
        await refreshUser();
      } catch (error) {
        localStorage.removeItem("token");
        setToken(null);
        setUser(null);
      } finally {
        setLoading(false);
      }
    };

    bootstrap();
  }, [token]);

  const applyAuth = (authResponse) => {
    localStorage.setItem("token", authResponse.token);
    setToken(authResponse.token);
    setUser(authResponse.user);
  };

  const login = async (email, password) => {
    const authResponse = await apiRequest("/api/auth/login", {
      method: "POST",
      body: JSON.stringify({ email, password }),
    });
    applyAuth(authResponse);
    return authResponse;
  };

  const register = async (payload) => {
    const authResponse = await apiRequest("/api/auth/register", {
      method: "POST",
      body: JSON.stringify(payload),
    });
    applyAuth(authResponse);
    return authResponse;
  };

  const logout = () => {
    localStorage.removeItem("token");
    setToken(null);
    setUser(null);
  };

  const value = useMemo(
    () => ({ user, token, loading, login, register, logout, refreshUser }),
    [user, token, loading]
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error("useAuth must be used within AuthProvider");
  }
  return context;
}
