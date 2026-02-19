import { useEffect, useState } from "react";
import { get } from "../api";
import { useTheme } from "../ThemeContext";
import { useI18n } from "../I18nContext";

interface DashData {
  total_users: number; active_today: number; online_devices: number;
  commands_today: number; blocked_today: number; cost_this_week_usd: number;
  daily_trend: { date: string; commands: number }[];
}

function AnimCard({ children, delay = 0 }: { children: React.ReactNode; delay?: number }) {
  const { colors } = useTheme();
  const { t } = useI18n();
  const [visible, setVisible] = useState(false);
  const [hovered, setHovered] = useState(false);
  const [pressed, setPressed] = useState(false);
  useEffect(() => { const t = setTimeout(() => setVisible(true), delay); return () => clearTimeout(t); }, [delay]);

  return (
    <div onMouseEnter={() => setHovered(true)} onMouseLeave={() => { setHovered(false); setPressed(false); }}
      onMouseDown={() => setPressed(true)} onMouseUp={() => setPressed(false)}
      style={{
        background: hovered ? colors.cardHover : colors.card,
        border: "1px solid " + (hovered ? colors.accent + "30" : colors.cardBorder),
        borderRadius: 14, padding: "20px 18px", cursor: "default",
        transform: "translateY(" + (visible ? "0" : "20") + "px) scale(" + (pressed ? "0.97" : hovered ? "1.02" : "1") + ")",
        opacity: visible ? 1 : 0,
        transition: "all 0.4s cubic-bezier(0.4, 0, 0.2, 1)",
        boxShadow: hovered ? "0 8px 25px " + colors.accentGlow : "none",
      }}>{children}</div>
  );
}

function BarItem({ commands, maxC, date, index }: { commands: number; maxC: number; date: string; index: number }) {
  const { colors } = useTheme();
  const { t } = useI18n();
  const [hovered, setHovered] = useState(false);
  const [grown, setGrown] = useState(false);
  useEffect(() => { const t = setTimeout(() => setGrown(true), 100 + index * 100); return () => clearTimeout(t); }, [index]);
  const barH = Math.max((commands / maxC) * 130, 6);

  return (
    <div onMouseEnter={() => setHovered(true)} onMouseLeave={() => setHovered(false)}
      style={{ flex: 1, display: "flex", flexDirection: "column", alignItems: "center",
        opacity: grown ? 1 : 0, transition: "all 0.5s ease" }}>
      <span style={{ color: hovered ? colors.text : colors.textTri, fontSize: 11, marginBottom: 6, transition: "0.2s" }}>{commands}</span>
      <div style={{
        width: "100%", maxWidth: 44, borderRadius: "8px 8px 0 0",
        background: hovered ? "linear-gradient(to top, #00d4ff, #6366f1)" : "linear-gradient(to top, #00b4d8, #4361ee)",
        height: grown ? barH : 0, transition: "all 0.8s cubic-bezier(0.4,0,0.2,1)",
        boxShadow: hovered ? "0 0 20px " + colors.accentGlow : "none",
        transform: hovered ? "scaleX(1.1)" : "scaleX(1)", cursor: "pointer",
      }} />
      <span style={{ color: colors.textTri, fontSize: 10, marginTop: 8 }}>{date}</span>
    </div>
  );
}

export default function Dashboard() {
  const { colors } = useTheme();
  const { t } = useI18n();
  const [d, setD] = useState<DashData | null>(null);
  const [err, setErr] = useState("");

  useEffect(() => {
    get("/api/admin/dashboard").then(setD).catch(() => {
      setD({ total_users: 1, active_today: 1, online_devices: 0, commands_today: 0, blocked_today: 0, cost_this_week_usd: 0,
        daily_trend: [{ date: "Mon", commands: 0 },{ date: "Tue", commands: 0 },{ date: "Wed", commands: 0 },
          { date: "Thu", commands: 0 },{ date: "Fri", commands: 0 },{ date: "Sat", commands: 0 },{ date: "Sun", commands: 0 }] });
      setErr(t("dash.loadFail"));
    });
  }, []);

  if (!d) return (
    <div style={{ display: "flex", alignItems: "center", justifyContent: "center", height: "60vh" }}>
      <div style={{ width: 40, height: 40, borderRadius: "50%", border: "3px solid " + colors.accent + "30",
        borderTopColor: colors.accent, animation: "spin 1s linear infinite" }} />
      <style>{"@keyframes spin { to { transform: rotate(360deg); } }"}</style>
    </div>
  );

  const cards = [
    { label: t("dash.totalUsers"), value: d.total_users, color: "#00b4d8", icon: "M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" },
    { label: t("dash.activeToday"), value: d.active_today, color: "#10b981", icon: "M13 10V3L4 14h7v7l9-11h-7z" },
    { label: t("dash.onlineDevices"), value: d.online_devices, color: "#6366f1", icon: "M9.75 17L9 20l-1 1h8l-1-1-.75-3M3 13h18M5 17h14a2 2 0 002-2V5a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z" },
    { label: t("dash.commandsToday"), value: d.commands_today, color: "#f59e0b", icon: "M8 9l3 3-3 3m5 0h3M5 20h14a2 2 0 002-2V6a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" },
    { label: t("dash.blockedToday"), value: d.blocked_today, color: "#ef4444", icon: "M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z" },
    { label: t("dash.weeklyCost"), value: "$" + (d.cost_this_week_usd || 0).toFixed(2), color: "#a855f7", icon: "M12 8c-1.657 0-3 .895-3 2s1.343 2 3 2 3 .895 3 2-1.343 2-3 2m0-8c1.11 0 2.08.402 2.599 1M12 8V7m0 1v8m0 0v1m0-1c-1.11 0-2.08-.402-2.599-1M21 12a9 9 0 11-18 0 9 9 0 0118 0z" },
  ];
  const maxC = Math.max(...d.daily_trend.map(x => x.commands), 1);

  return (
    <div>
      {err && <div style={{ background: colors.warning + "18", border: "1px solid " + colors.warning + "30",
        borderRadius: 10, padding: "10px 16px", marginBottom: 20, color: colors.warning, fontSize: 13 }}>{err}</div>}

      <div style={{ display: "grid", gridTemplateColumns: "repeat(auto-fit, minmax(170px, 1fr))", gap: 16, marginBottom: 24 }}>
        {cards.map((c, i) => (
          <AnimCard key={c.label} delay={i * 100}>
            <div style={{ display: "flex", alignItems: "center", justifyContent: "space-between", marginBottom: 12 }}>
              <span style={{ color: colors.textTri, fontSize: 12 }}>{c.label}</span>
              <div style={{ width: 36, height: 36, borderRadius: 10, background: c.color + "15",
                display: "flex", alignItems: "center", justifyContent: "center" }}>
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke={c.color}
                  strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round"><path d={c.icon} /></svg>
              </div>
            </div>
            <div style={{ color: colors.text, fontSize: 28, fontWeight: 700 }}>{c.value}</div>
          </AnimCard>
        ))}
      </div>

      <AnimCard delay={600}>
        <h3 style={{ color: colors.text, fontSize: 15, fontWeight: 600, marginBottom: 20 }}>{t("dash.trend")}</h3>
        <div style={{ display: "flex", alignItems: "flex-end", gap: 10, height: 180 }}>
          {d.daily_trend.map((t, i) => (
            <BarItem key={t.date} commands={t.commands} maxC={maxC} date={t.date} index={i} />
          ))}
        </div>
      </AnimCard>
    </div>
  );
}
