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

  const login = async (emailOrPayload, password) => {
    const payload =
      typeof emailOrPayload === "object" && emailOrPayload !== null
        ? emailOrPayload
        : { email: emailOrPayload, password };
    const authResponse = await apiRequest("/api/auth/login", {
      method: "POST",
      body: JSON.stringify(payload),
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

  const loginWithGoogle = async (credential) => {
    const authResponse = await apiRequest("/api/auth/google", {
      method: "POST",
      body: JSON.stringify({ credential }),
    });
    applyAuth(authResponse);
    return authResponse;
  };

  const completeProfile = async (payload) => {
    const updatedUser = await apiRequest("/api/users/me/complete-profile", {
      method: "PUT",
      body: JSON.stringify(payload),
    });
    setUser(updatedUser);
    return updatedUser;
  };

  const sendSignupOtp = async ({ email, phone }) => {
    return apiRequest("/api/auth/otp/send", {
      method: "POST",
      body: JSON.stringify({
        email,
        phone,
        purpose: "REGISTER",
      }),
    });
  };

  const sendLoginOtp = async ({ email, password }) => {
    return apiRequest("/api/auth/login/send-otp", {
      method: "POST",
      body: JSON.stringify({ email, password }),
    });
  };

  const requiresProfileCompletion = (candidateUser) => {
    if (!candidateUser) {
      return false;
    }
    return candidateUser.profileCompleted === false;
  };

  const logout = () => {
    localStorage.removeItem("token");
    setToken(null);
    setUser(null);
  };

  const value = useMemo(
    () => ({
      user,
      token,
      loading,
      login,
      register,
      loginWithGoogle,
      sendSignupOtp,
      sendLoginOtp,
      completeProfile,
      requiresProfileCompletion,
      logout,
      refreshUser,
    }),
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
