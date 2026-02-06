const STORAGE_KEY = "auth_theme";
const EVENT_NAME = "app-theme-change";

function applyTheme(theme) {
  const resolved = "light";
  document.documentElement.setAttribute("data-theme", resolved);
  document.body.setAttribute("data-theme", resolved);
}

export function getTheme() {
  return "light";
}

export function setTheme(theme) {
  const resolved = "light";
  localStorage.setItem(STORAGE_KEY, resolved);
  applyTheme(resolved);
  window.dispatchEvent(new CustomEvent(EVENT_NAME, { detail: resolved }));
  return resolved;
}

export function toggleTheme() {
  return setTheme("light");
}

export function initTheme() {
  applyTheme(getTheme());
}

export function subscribeTheme(listener) {
  const handler = (event) => listener(event.detail);
  window.addEventListener(EVENT_NAME, handler);
  return () => window.removeEventListener(EVENT_NAME, handler);
}
