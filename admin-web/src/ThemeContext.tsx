import { createContext, useContext, useState, useEffect, ReactNode } from "react";

interface ThemeCtx {
  dark: boolean;
  toggle: () => void;
  colors: {
    bg: string; sidebar: string; sidebarBorder: string;
    card: string; cardBorder: string; cardHover: string;
    text: string; textSec: string; textTri: string;
    accent: string; accentLight: string; accentGlow: string;
    danger: string; success: string; warning: string;
    inputBg: string; inputBorder: string; inputFocus: string;
    divider: string; tagBg: string;
  };
}

const dark = {
  bg: "#0a0a0f", sidebar: "linear-gradient(180deg, rgba(12,12,20,0.98) 0%, rgba(6,6,12,1) 100%)",
  sidebarBorder: "rgba(255,255,255,0.08)", card: "rgba(255,255,255,0.03)",
  cardBorder: "rgba(255,255,255,0.06)", cardHover: "rgba(255,255,255,0.06)",
  text: "#ffffff", textSec: "#aaaaaa", textTri: "#666666",
  accent: "#00b4d8", accentLight: "rgba(0,180,255,0.12)", accentGlow: "rgba(0,180,255,0.3)",
  danger: "#ff6b6b", success: "#10b981", warning: "#f59e0b",
  inputBg: "rgba(255,255,255,0.04)", inputBorder: "rgba(255,255,255,0.1)",
  inputFocus: "rgba(0,180,255,0.4)", divider: "rgba(255,255,255,0.06)",
  tagBg: "rgba(255,255,255,0.06)",
};

const light = {
  bg: "#f0f2f5", sidebar: "linear-gradient(180deg, #ffffff 0%, #f8f9fb 100%)",
  sidebarBorder: "#e2e5ea", card: "#ffffff",
  cardBorder: "#e2e5ea", cardHover: "#f5f7fa",
  text: "#1a1a2e", textSec: "#555555", textTri: "#999999",
  accent: "#0077b6", accentLight: "rgba(0,119,182,0.08)", accentGlow: "rgba(0,119,182,0.15)",
  danger: "#e63946", success: "#2a9d8f", warning: "#e9c46a",
  inputBg: "#f5f7fa", inputBorder: "#dce0e6",
  inputFocus: "rgba(0,119,182,0.4)", divider: "#e8eaed",
  tagBg: "#eef1f5",
};

const ThemeContext = createContext<ThemeCtx>({
  dark: true, toggle: () => {}, colors: dark,
});

export function ThemeProvider({ children }: { children: ReactNode }) {
  const [isDark, setIsDark] = useState(() => {
    const saved = localStorage.getItem("theme");
    return saved ? saved === "dark" : true;
  });

  useEffect(() => {
    localStorage.setItem("theme", isDark ? "dark" : "light");
    document.body.style.background = isDark ? dark.bg : light.bg;
  }, [isDark]);

  return (
    <ThemeContext.Provider value={{ dark: isDark, toggle: () => setIsDark(!isDark), colors: isDark ? dark : light }}>
      {children}
    </ThemeContext.Provider>
  );
}

export const useTheme = () => useContext(ThemeContext);
