import { useState, useEffect } from "react";
import { NavLink, Outlet, useNavigate, useLocation } from "react-router-dom";
import { useTheme } from "../ThemeContext";
import { useI18n } from "../I18nContext";

const menu = [
  { path: "/", label: "nav.dashboard", d: "M3 12l2-2m0 0l7-7 7 7M5 10v10a1 1 0 001 1h3m10-11l2 2m-2-2v10a1 1 0 01-1 1h-3m-4 0h4" },
  { path: "/users", label: "nav.users", d: "M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0z" },
  { path: "/logs", label: "nav.logs", d: "M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2m-3 7h3m-3 4h3m-6-4h.01M9 16h.01" },
  { path: "/quota", label: "nav.quota", d: "M16 8v8m-4-5v5m-4-2v2m-2 4h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z" },
  { path: "/settings", label: "nav.settings", d: "M10.325 4.317c.426-1.756 2.924-1.756 3.35 0a1.724 1.724 0 002.573 1.066c1.543-.94 3.31.826 2.37 2.37a1.724 1.724 0 001.066 2.573c1.756.426 1.756 2.924 0 3.35a1.724 1.724 0 00-1.066 2.573c.94 1.543-.826 3.31-2.37 2.37a1.724 1.724 0 00-2.573 1.066c-.426 1.756-2.924 1.756-3.35 0a1.724 1.724 0 00-2.573-1.066c-1.543.94-3.31-.826-2.37-2.37a1.724 1.724 0 00-1.066-2.573c-1.756-.426-1.756-2.924 0-3.35a1.724 1.724 0 001.066-2.573c-.94-1.543.826-3.31 2.37-2.37.996.608 2.296.07 2.572-1.065z M15 12a3 3 0 11-6 0 3 3 0 016 0z" },
];

const iconColors = ["#00b4d8", "#6366f1", "#f59e0b", "#10b981", "#a855f7"];

function NavItem({ item, index }: { item: typeof menu[0]; index: number }) {
  const { colors, dark: isDark } = useTheme();
  const { t } = useI18n();
  const [hovered, setHovered] = useState(false);
  const [pressed, setPressed] = useState(false);
  const [visible, setVisible] = useState(false);
  const location = useLocation();
  const isActive = item.path === "/" ? location.pathname === "/" : location.pathname.startsWith(item.path);
  const iconColor = iconColors[index % iconColors.length];

  useEffect(() => {
    const tm = setTimeout(() => setVisible(true), 80 + index * 60);
    return () => clearTimeout(tm);
  }, [index]);

  return (
    <NavLink to={item.path} end={item.path === "/"}
      onMouseEnter={() => setHovered(true)}
      onMouseLeave={() => { setHovered(false); setPressed(false); }}
      onMouseDown={() => setPressed(true)}
      onMouseUp={() => setPressed(false)}
      style={{
        display: "flex", alignItems: "center", gap: 12,
        padding: "11px 14px", borderRadius: 12, marginBottom: 4,
        textDecoration: "none", fontSize: 13, position: "relative",
        background: isActive
          ? (isDark ? "rgba(255,255,255,0.06)" : "rgba(0,0,0,0.04)")
          : hovered ? (isDark ? "rgba(255,255,255,0.03)" : "rgba(0,0,0,0.02)") : "transparent",
        color: isActive ? colors.text : hovered ? colors.textSec : colors.textTri,
        transform: "translateX(" + (visible ? "0" : "-20") + "px) scale(" + (pressed ? "0.95" : hovered ? "1.01" : "1") + ")",
        opacity: visible ? 1 : 0,
        transition: "all 0.3s cubic-bezier(0.4, 0, 0.2, 1)",
        boxShadow: isActive ? (isDark ? "0 2px 12px rgba(0,0,0,0.3), inset 0 0 0 1px rgba(255,255,255,0.06)" : "0 2px 8px rgba(0,0,0,0.06), inset 0 0 0 1px rgba(0,0,0,0.04)") : "none",
      }}>
      <div style={{
        width: 34, height: 34, borderRadius: 10,
        display: "flex", alignItems: "center", justifyContent: "center",
        background: isActive ? iconColor + "20" : hovered ? (isDark ? "rgba(255,255,255,0.05)" : "rgba(0,0,0,0.03)") : "transparent",
        boxShadow: isActive ? "0 0 12px " + iconColor + "25" : "none",
        transition: "all 0.3s ease",
        transform: hovered ? "scale(1.08)" : "scale(1)",
      }}>
        <svg width="18" height="18" viewBox="0 0 24 24" fill="none"
          stroke={isActive ? iconColor : hovered ? colors.textSec : colors.textTri}
          strokeWidth="1.8" strokeLinecap="round" strokeLinejoin="round"
          style={{ transition: "all 0.3s", filter: isActive ? "drop-shadow(0 0 4px " + iconColor + "60)" : "none" }}>
          <path d={item.d} />
        </svg>
      </div>
      <span style={{ fontWeight: isActive ? 600 : 400, letterSpacing: 0.2, transition: "all 0.2s" }}>{t(item.label)}</span>
      {isActive && <div style={{
        position: "absolute", left: 0, top: "20%", bottom: "20%", width: 3, borderRadius: "0 4px 4px 0",
        background: "linear-gradient(180deg, " + iconColor + ", " + iconColor + "80)",
        boxShadow: "0 0 10px " + iconColor + "60",
      }} />}
    </NavLink>
  );
}

export default function Layout() {
  const { dark: isDark, toggle, colors } = useTheme();
  const { lang, setLang, t } = useI18n();
  const nav = useNavigate();
  const user = JSON.parse(localStorage.getItem("user") || "{}");
  const [logoVis, setLogoVis] = useState(false);
  const [userVis, setUserVis] = useState(false);
  const [logoutH, setLogoutH] = useState(false);
  const [logoutP, setLogoutP] = useState(false);
  const [themeH, setThemeH] = useState(false);
  const [themeP, setThemeP] = useState(false);

  useEffect(() => { setTimeout(() => setLogoVis(true), 50); setTimeout(() => setUserVis(true), 500); }, []);
  const logout = () => { localStorage.removeItem("token"); localStorage.removeItem("user"); nav("/login"); };

  return (
    <div style={{ display: "flex", height: "100vh", overflow: "hidden", background: colors.bg, position: "relative" }}>
      <style>{"@keyframes pulse{0%,100%{opacity:1;transform:scale(1)}50%{opacity:.4;transform:scale(1.4)}}@keyframes glowLine{0%,100%{opacity:.15}50%{opacity:.5}}@keyframes ambientFloat{0%,100%{transform:translate(0,0)}50%{transform:translate(10px,-10px)}}"}</style>

      {isDark && <>
        <div style={{ position: "fixed", top: "10%", right: "15%", width: 350, height: 350,
          background: "radial-gradient(circle, rgba(0,180,255,0.04) 0%, transparent 70%)",
          borderRadius: "50%", pointerEvents: "none", animation: "ambientFloat 15s ease-in-out infinite", zIndex: 0 }} />
        <div style={{ position: "fixed", bottom: "15%", left: "40%", width: 300, height: 300,
          background: "radial-gradient(circle, rgba(99,102,241,0.03) 0%, transparent 70%)",
          borderRadius: "50%", pointerEvents: "none", animation: "ambientFloat 20s ease-in-out infinite reverse", zIndex: 0 }} />
      </>}

      <aside style={{
        width: 240, display: "flex", flexDirection: "column", padding: "20px 0",
        background: isDark ? "rgba(10,10,18,0.95)" : "rgba(255,255,255,0.97)",
        borderRight: "1px solid " + colors.sidebarBorder,
        backdropFilter: "blur(20px)", position: "relative", zIndex: 1,
        boxShadow: isDark ? "4px 0 20px rgba(0,0,0,0.3)" : "4px 0 15px rgba(0,0,0,0.04)",
      }}>
        {isDark && <div style={{ position: "absolute", top: 0, right: -1, bottom: 0, width: 1,
          background: "linear-gradient(180deg, transparent 10%, rgba(0,180,255,0.25) 50%, transparent 90%)",
          animation: "glowLine 4s ease-in-out infinite" }} />}

        <div style={{ padding: "0 20px", marginBottom: 28, display: "flex", alignItems: "center", gap: 12,
          opacity: logoVis ? 1 : 0, transform: "translateY(" + (logoVis ? "0" : "-15") + "px)", transition: "all 0.6s ease" }}>
          <div style={{ width: 42, height: 42, borderRadius: 14,
            background: "linear-gradient(135deg, #00b4d8, #4361ee)",
            display: "flex", alignItems: "center", justifyContent: "center",
            boxShadow: "0 4px 20px rgba(0,180,255,0.3), 0 0 40px rgba(0,180,255,0.1)",
            cursor: "pointer", transition: "all 0.3s" }}
            onMouseEnter={e => { (e.currentTarget as HTMLElement).style.transform = "rotate(8deg) scale(1.1)"; (e.currentTarget as HTMLElement).style.boxShadow = "0 4px 30px rgba(0,180,255,0.5), 0 0 60px rgba(0,180,255,0.15)"; }}
            onMouseLeave={e => { (e.currentTarget as HTMLElement).style.transform = "rotate(0) scale(1)"; (e.currentTarget as HTMLElement).style.boxShadow = "0 4px 20px rgba(0,180,255,0.3), 0 0 40px rgba(0,180,255,0.1)"; }}>
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="#fff" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
              <path d="M13 2L3 14h9l-1 8 10-12h-9l1-8z"/>
            </svg>
          </div>
          <div>
            <div style={{ color: colors.text, fontSize: 15, fontWeight: 700 }}>AI SSH Agent</div>
            <div style={{ color: colors.textTri, fontSize: 10, letterSpacing: 1 }}>ADMIN CONSOLE</div>
          </div>
        </div>

        <div style={{ margin: "0 20px 14px", height: 1, background: "linear-gradient(90deg, transparent, " + colors.divider + ", transparent)" }} />

        <div style={{ padding: "0 16px", marginBottom: 8 }}>
          <span style={{ fontSize: 10, color: colors.textTri, fontWeight: 600, letterSpacing: 2 }}>NAVIGATION</span>
        </div>

        <nav style={{ flex: 1, padding: "0 12px", overflowY: "auto" }}>
          {menu.map((m, i) => <NavItem key={m.path} item={m} index={i} />)}
        </nav>

        <div style={{ margin: "10px 20px", height: 1, background: "linear-gradient(90deg, transparent, " + colors.divider + ", transparent)" }} />

        <div style={{ padding: "0 14px", marginBottom: 6, display: "flex", gap: 6 }}>
          <button onClick={toggle}
            onMouseEnter={() => setThemeH(true)} onMouseLeave={() => { setThemeH(false); setThemeP(false); }}
            onMouseDown={() => setThemeP(true)} onMouseUp={() => setThemeP(false)}
            style={{ width: "100%", display: "flex", alignItems: "center", justifyContent: "center", gap: 8,
              padding: "10px 0", borderRadius: 12, fontSize: 12, fontWeight: 500,
              background: themeH ? colors.accentLight : (isDark ? "rgba(255,255,255,0.03)" : "rgba(0,0,0,0.02)"),
              border: "1px solid " + (themeH ? colors.accent + "30" : colors.cardBorder),
              color: themeH ? colors.accent : colors.textSec, cursor: "pointer",
              transform: "scale(" + (themeP ? "0.96" : "1") + ")",
              transition: "all 0.25s ease",
              boxShadow: themeH ? "0 2px 10px " + colors.accent + "15" : "none" }}>
            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round">
              {isDark
                ? <><circle cx="12" cy="12" r="5"/><path d="M12 1v2M12 21v2M4.22 4.22l1.42 1.42M18.36 18.36l1.42 1.42M1 12h2M21 12h2M4.22 19.78l1.42-1.42M18.36 5.64l1.42-1.42"/></>
                : <path d="M21 12.79A9 9 0 1111.21 3 7 7 0 0021 12.79z"/>}
            </svg>
            {isDark ? t("nav.lightMode") : t("nav.darkMode")}
          </button>
          <button onClick={() => setLang(lang === "zh" ? "en" : "zh")}
            style={{ width: 44, display: "flex", alignItems: "center", justifyContent: "center",
              padding: "9px 0", borderRadius: 10, fontSize: 12, fontWeight: 700,
              background: isDark ? "rgba(255,255,255,0.03)" : "rgba(0,0,0,0.02)",
              border: "1px solid " + colors.cardBorder,
              color: colors.accent, cursor: "pointer", transition: "all 0.25s ease" }}>
            {lang === "zh" ? "EN" : "\u4e2d"}
          </button>
        </div>

        <div style={{ padding: "0 12px", opacity: userVis ? 1 : 0, transform: "translateY(" + (userVis ? "0" : "10") + "px)", transition: "all 0.5s ease" }}>
          <div style={{ padding: 14, borderRadius: 14,
            background: isDark ? "rgba(255,255,255,0.03)" : "#f8f9fb",
            border: "1px solid " + colors.cardBorder,
            boxShadow: isDark ? "inset 0 1px 0 rgba(255,255,255,0.03)" : "inset 0 1px 0 rgba(255,255,255,0.5)" }}>
            <div style={{ display: "flex", alignItems: "center", gap: 10, marginBottom: 10 }}>
              <div style={{ width: 36, height: 36, borderRadius: "50%",
                background: "linear-gradient(135deg, #00b4d8, #4361ee)",
                display: "flex", alignItems: "center", justifyContent: "center",
                color: "#fff", fontSize: 14, fontWeight: 600,
                boxShadow: "0 2px 12px rgba(0,180,255,0.3)" }}>
                {(user.username || "A")[0].toUpperCase()}
              </div>
              <div>
                <div style={{ color: colors.text, fontSize: 13, fontWeight: 600 }}>{user.username || "Admin"}</div>
                <div style={{ color: colors.textTri, fontSize: 10, display: "flex", alignItems: "center", gap: 4 }}>
                  <div style={{ width: 6, height: 6, borderRadius: "50%", background: "#10b981",
                    boxShadow: "0 0 8px rgba(16,185,129,0.6)", animation: "pulse 2s ease-in-out infinite" }} />
                  Online
                </div>
              </div>
            </div>
            <button onClick={logout}
              onMouseEnter={() => setLogoutH(true)} onMouseLeave={() => { setLogoutH(false); setLogoutP(false); }}
              onMouseDown={() => setLogoutP(true)} onMouseUp={() => setLogoutP(false)}
              style={{ width: "100%", padding: "8px 0", fontSize: 12, fontWeight: 500,
                background: logoutH ? "rgba(239,68,68,0.12)" : "rgba(239,68,68,0.05)",
                border: "1px solid " + (logoutH ? "rgba(239,68,68,0.3)" : "rgba(239,68,68,0.1)"),
                borderRadius: 10, color: "#ef4444", cursor: "pointer",
                transform: "scale(" + (logoutP ? "0.96" : "1") + ")", transition: "all 0.2s",
                boxShadow: logoutH ? "0 2px 10px rgba(239,68,68,0.1)" : "none" }}>
              Sign Out
            </button>
          </div>
        </div>
      </aside>

      <main style={{ flex: 1, overflow: "auto", padding: 28, background: colors.bg, position: "relative", zIndex: 1 }}>
        <Outlet />
      </main>
    </div>
  );
}
