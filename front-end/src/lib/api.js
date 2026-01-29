const API_BASE = import.meta.env.VITE_API_BASE_URL || "http://localhost:8080";

export async function apiRequest(path, options = {}) {
  const token = localStorage.getItem("token");
  const headers = {
    "Content-Type": "application/json",
    ...options.headers,
  };

  if (token) {
    headers.Authorization = `Bearer ${token}`;
  }

  const response = await fetch(`${API_BASE}${path}`, {
    ...options,
    headers,
  });

  if (!response.ok) {
    const errorBody = await response.json().catch(() => ({ message: "Request failed" }));
    throw new Error(errorBody.message || "Request failed");
  }

  if (response.status === 204) {
    return null;
  }

  return response.json();
}

export { API_BASE };
