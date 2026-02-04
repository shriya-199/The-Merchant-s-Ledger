import { useEffect, useState } from "react";
import { getTheme, initTheme, subscribeTheme, toggleTheme } from "../lib/theme";

export default function ThemeToggle() {
  const [theme, setTheme] = useState(getTheme);
  const [fading, setFading] = useState(false);
  const isDark = theme === "dark";

  useEffect(() => {
    initTheme();
    const unsubscribe = subscribeTheme(setTheme);
    return unsubscribe;
  }, []);

  const handleToggle = () => {
    setFading(true);
    toggleTheme();
    window.setTimeout(() => setFading(false), 260);
  };

  return (
    <>
      <div
        className={`pointer-events-none fixed inset-0 z-40 transition-opacity duration-300 ${
          fading ? "opacity-100" : "opacity-0"
        } ${isDark ? "bg-slate-950/25" : "bg-slate-200/30"}`}
      />
      <button
        type="button"
        onClick={handleToggle}
        className={`fixed right-5 top-5 z-50 rounded-full px-4 py-2 text-xs font-semibold shadow-sm transition-colors duration-300 ${
          isDark ? "bg-slate-800 text-slate-100 hover:bg-slate-700" : "bg-slate-900 text-white hover:bg-slate-700"
        }`}
      >
        {isDark ? "Light Theme" : "Dark Theme"}
      </button>
    </>
  );
}
