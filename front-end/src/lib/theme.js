const STORAGE_KEY = "auth_theme";
const EVENT_NAME = "app-theme-change";

function applyTheme(theme) {
  const resolved = theme === "dark" ? "dark" : "light";
  document.documentElement.setAttribute("data-theme", resolved);
  document.body.setAttribute("data-theme", resolved);
}

export function getTheme() {
  if (typeof window === "undefined") {
    return "dark";
  }
  const saved = localStorage.getItem(STORAGE_KEY);
  return saved === "light" ? "light" : "dark";
}

export function setTheme(theme) {
  const resolved = theme === "light" ? "light" : "dark";
  localStorage.setItem(STORAGE_KEY, resolved);
  applyTheme(resolved);
  window.dispatchEvent(new CustomEvent(EVENT_NAME, { detail: resolved }));
  return resolved;
}

export function toggleTheme() {
  const next = getTheme() === "dark" ? "light" : "dark";
  return setTheme(next);
}

export function initTheme() {
  applyTheme(getTheme());
}

export function subscribeTheme(listener) {
  const handler = (event) => listener(event.detail);
  window.addEventListener(EVENT_NAME, handler);
  return () => window.removeEventListener(EVENT_NAME, handler);
}
