import { useEffect, useState } from "react";
import { get, post } from "../api";
import { useTheme } from "../ThemeContext";
import { useI18n } from "../I18nContext";

function Card({ children, colors, delay = 0 }: any) {
  const [vis, setVis] = useState(false);
  const [hov, setHov] = useState(false);
  useEffect(() => { const t = setTimeout(() => setVis(true), delay); return () => clearTimeout(t); }, []);
  return (
    <div onMouseEnter={() => setHov(true)} onMouseLeave={() => setHov(false)}
      style={{ background: colors.card, borderRadius: 16, border: "1px solid " + (hov ? colors.accent + "40" : colors.cardBorder),
        padding: 24, opacity: vis ? 1 : 0, transform: "translateY(" + (vis ? "0" : "20px") + ") scale(" + (hov ? "1.01" : "1") + ")",
        transition: "all 0.4s cubic-bezier(0.4,0,0.2,1)", boxShadow: hov ? "0 8px 30px " + colors.accent + "15" : "0 2px 8px rgba(0,0,0,0.1)" }}>
      {children}
    </div>
  );
}

function StatBar({ label, value, max, unit, color, colors }: any) {
  const [w, setW] = useState(0);
  const pct = max > 0 ? Math.min(100, (value / max) * 100) : 0;
  useEffect(() => { setTimeout(() => setW(pct), 200); }, [pct]);
  return (
    <div style={{ marginBottom: 16 }}>
      <div style={{ display: "flex", justifyContent: "space-between", marginBottom: 6 }}>
        <span style={{ fontSize: 13, color: colors.textSec }}>{label}</span>
        <span style={{ fontSize: 13, fontWeight: 600, color: colors.text }}>{value}{unit} / {max}{unit}</span>
      </div>
      <div style={{ height: 8, borderRadius: 4, background: colors.cardBorder, overflow: "hidden" }}>
        <div style={{ height: "100%", width: w + "%", borderRadius: 4, background: "linear-gradient(90deg, " + color + ", " + color + "aa)",
          transition: "width 1s cubic-bezier(0.4,0,0.2,1)" }} />
      </div>
    </div>
  );
}

export default function Settings() {
  const { colors, dark: isDark } = useTheme();
  const { t } = useI18n();
  const [health, setHealth] = useState<any>(null);
  const [providers, setProviders] = useState<string[]>([]);
  const [defaultProv, setDefaultProv] = useState("");
  const [cleaning, setCleaning] = useState(false);
  const [cleanMsg, setCleanMsg] = useState("");

  useEffect(() => {
    get("/api/admin/system/health").then(setHealth).catch(() => {});
    get("/api/admin/system/providers").then(r => { setProviders(r.providers || []); setDefaultProv(r.default || ""); }).catch(() => {});
  }, []);

  const cleanup = () => {
    setCleaning(true); setCleanMsg("");
    post("/api/admin/system/cleanup-keys")
      .then(() => setCleanMsg(t("settings.cleanSuccess")))
      .catch(() => setCleanMsg(t("settings.cleanFail")))
      .finally(() => setCleaning(false));
  };

  return (
    <div style={{ padding: 24 }}>
      <h1 style={{ fontSize: 24, fontWeight: 700, color: colors.text, marginBottom: 20 }}>{t("settings.title")}</h1>

      <div style={{ display: "grid", gridTemplateColumns: "repeat(auto-fit, minmax(400px, 1fr))", gap: 20 }}>
        <Card colors={colors} delay={0}>
          <h2 style={{ fontSize: 16, fontWeight: 700, color: colors.text, marginBottom: 20, display: "flex", alignItems: "center", gap: 8 }}>
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke={colors.accent} strokeWidth="2"><path d="M22 12h-4l-3 9L9 3l-3 9H2"/></svg>
            {t("settings.health")}
          </h2>
          {health ? (
            <>
              <StatBar label={t("settings.disk")} value={health.disk_used_gb || 0} max={health.disk_total_gb || 100} unit="GB" color="#6C5CE7" colors={colors} />
              <StatBar label={t("settings.memory")} value={health.mem_used_gb || 0} max={health.mem_total_gb || 8} unit="GB" color="#00B894" colors={colors} />
              <StatBar label="CPU" value={health.cpu_percent || 0} max={100} unit="%" color="#E17055" colors={colors} />
              <div style={{ display: "flex", gap: 16, marginTop: 12 }}>
                <div style={{ padding: "8px 16px", borderRadius: 10, background: isDark ? "rgba(255,255,255,0.04)" : "#f5f6f8", fontSize: 12 }}>
                  <span style={{ color: colors.textSec }}>{t("settings.uptime")}: </span>
                  <span style={{ color: colors.text, fontWeight: 600 }}>{health.uptime || "-"}</span>
                </div>
                <div style={{ padding: "8px 16px", borderRadius: 10, background: isDark ? "rgba(255,255,255,0.04)" : "#f5f6f8", fontSize: 12 }}>
                  <span style={{ color: colors.textSec }}>{t("settings.dbConn")}: </span>
                  <span style={{ color: health.db_ok ? "#52c41a" : "#ff4d4f", fontWeight: 600 }}>{health.db_ok ? "OK" : "Err"}</span>
                </div>
              </div>
            </>
          ) : (
            <div style={{ textAlign: "center", padding: 20, color: colors.textSec }}>{t("settings.loading")}</div>
          )}
        </Card>

        <Card colors={colors} delay={150}>
          <h2 style={{ fontSize: 16, fontWeight: 700, color: colors.text, marginBottom: 20, display: "flex", alignItems: "center", gap: 8 }}>
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke={colors.accent} strokeWidth="2"><rect x="2" y="3" width="20" height="14" rx="2"/><path d="M8 21h8M12 17v4"/></svg>
            {t("settings.providers")}
          </h2>
          {providers.length > 0 ? (
            <div style={{ display: "flex", flexDirection: "column", gap: 10 }}>
              {providers.map((p, i) => (
                <ProvItem key={p} name={p} isDefault={p === defaultProv} colors={colors} isDark={isDark} delay={i * 60} t={t} />
              ))}
            </div>
          ) : (
            <div style={{ textAlign: "center", padding: 20, color: colors.textSec }}>{t("settings.noProviders")}</div>
          )}
        </Card>

        <Card colors={colors} delay={300}>
          <h2 style={{ fontSize: 16, fontWeight: 700, color: colors.text, marginBottom: 20, display: "flex", alignItems: "center", gap: 8 }}>
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke={colors.accent} strokeWidth="2"><path d="M14.7 6.3a1 1 0 000 1.4l1.6 1.6a1 1 0 001.4 0l3.77-3.77a6 6 0 01-7.94 7.94l-6.91 6.91a2.12 2.12 0 01-3-3l6.91-6.91a6 6 0 017.94-7.94l-3.76 3.76z"/></svg>
            {t("settings.maintenance")}
          </h2>
          <p style={{ fontSize: 13, color: colors.textSec, marginBottom: 16, lineHeight: 1.6 }}>{t("settings.cleanDesc")}</p>
          <button onClick={cleanup} disabled={cleaning}
            style={{ padding: "12px 24px", borderRadius: 12, fontSize: 14, fontWeight: 600,
              background: cleaning ? colors.cardBorder : colors.accent, color: "#fff",
              border: "none", cursor: cleaning ? "not-allowed" : "pointer",
              transition: "all 0.3s", opacity: cleaning ? 0.6 : 1 }}>
            {cleaning ? t("settings.cleaning") : t("settings.cleanBtn")}
          </button>
          {cleanMsg && <p style={{ marginTop: 12, fontSize: 13, color: cleanMsg.includes("success") || cleanMsg.includes("成功") || cleanMsg.includes("successful") ? "#52c41a" : "#ff4d4f" }}>{cleanMsg}</p>}
        </Card>
      </div>
    </div>
  );
}

function ProvItem({ name, isDefault, colors, isDark, delay, t }: any) {
  const [vis, setVis] = useState(false);
  const [hov, setHov] = useState(false);
  useEffect(() => { const tm = setTimeout(() => setVis(true), delay); return () => clearTimeout(tm); }, []);
  return (
    <div onMouseEnter={() => setHov(true)} onMouseLeave={() => setHov(false)}
      style={{ display: "flex", alignItems: "center", justifyContent: "space-between",
        padding: "12px 16px", borderRadius: 12,
        background: hov ? (isDark ? "rgba(255,255,255,0.06)" : "rgba(0,0,0,0.03)") : (isDark ? "rgba(255,255,255,0.03)" : "#f8f9fb"),
        border: "1px solid " + (hov ? colors.accent + "30" : "transparent"),
        opacity: vis ? 1 : 0, transform: "translateX(" + (vis ? "0" : "-10px") + ")",
        transition: "all 0.35s ease", cursor: "default" }}>
      <span style={{ fontWeight: 600, color: colors.text, fontSize: 14 }}>{name}</span>
      {isDefault && (
        <span style={{ fontSize: 11, padding: "3px 10px", borderRadius: 20, background: colors.accent + "15", color: colors.accent, fontWeight: 600 }}>
          {t("settings.default")}
        </span>
      )}
    </div>
  );
}
