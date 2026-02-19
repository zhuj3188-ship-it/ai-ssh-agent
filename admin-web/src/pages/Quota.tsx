import { useEffect, useState } from "react";
import { get } from "../api";
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

function StatCard({ label, value, icon, color, colors, delay = 0 }: any) {
  const [vis, setVis] = useState(false);
  const [hov, setHov] = useState(false);
  useEffect(() => { const t = setTimeout(() => setVis(true), delay); return () => clearTimeout(t); }, []);
  return (
    <div onMouseEnter={() => setHov(true)} onMouseLeave={() => setHov(false)}
      style={{ background: colors.card, borderRadius: 16, border: "1px solid " + (hov ? color + "40" : colors.cardBorder),
        padding: 22, flex: "1 1 200px", opacity: vis ? 1 : 0,
        transform: "translateY(" + (vis ? "0" : "20px") + ") scale(" + (hov ? "1.03" : "1") + ")",
        transition: "all 0.4s cubic-bezier(0.4,0,0.2,1)",
        boxShadow: hov ? "0 8px 25px " + color + "20" : "0 2px 8px rgba(0,0,0,0.08)" }}>
      <div style={{ display: "flex", alignItems: "center", gap: 12, marginBottom: 12 }}>
        <div style={{ width: 40, height: 40, borderRadius: 12, background: color + "15",
          display: "flex", alignItems: "center", justifyContent: "center", fontSize: 18,
          transition: "transform 0.3s", transform: hov ? "rotate(10deg) scale(1.1)" : "none" }}>{icon}</div>
        <span style={{ fontSize: 12, color: colors.textSec, fontWeight: 500 }}>{label}</span>
      </div>
      <div style={{ fontSize: 26, fontWeight: 800, color: colors.text }}>{value}</div>
    </div>
  );
}

export default function Quota() {
  const { colors, dark: isDark } = useTheme();
  const { t } = useI18n();
  const [data, setData] = useState<any>(null);
  const [err, setErr] = useState("");

  useEffect(() => {
    get("/api/admin/quota/summary")
      .then(setData)
      .catch(() => setErr(t("quota.loadFail")));
  }, []);

  const provColors = ["#6C5CE7", "#00B894", "#E17055", "#0984E3", "#FDCB6E", "#E84393"];

  return (
    <div style={{ padding: 24 }}>
      <h1 style={{ fontSize: 24, fontWeight: 700, color: colors.text, marginBottom: 20 }}>{t("quota.title")}</h1>
      {err && <div style={{ padding: 12, background: "#ff4d4f20", borderRadius: 10, color: "#ff4d4f", marginBottom: 16, fontSize: 13 }}>{err}</div>}

      <div style={{ display: "flex", gap: 16, flexWrap: "wrap", marginBottom: 24 }}>
        <StatCard label={t("quota.totalTokens")} value={data ? (data.total_tokens || 0).toLocaleString() : "-"}
          icon={<svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="#6C5CE7" strokeWidth="2"><circle cx="12" cy="12" r="10"/><path d="M12 6v6l4 2"/></svg>}
          color="#6C5CE7" colors={colors} delay={0} />
        <StatCard label={t("quota.totalCost")} value={data ? "$" + (data.total_cost || 0).toFixed(2) : "-"}
          icon={<svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="#00B894" strokeWidth="2"><path d="M12 1v22M17 5H9.5a3.5 3.5 0 000 7h5a3.5 3.5 0 010 7H6"/></svg>}
          color="#00B894" colors={colors} delay={100} />
        <StatCard label={t("quota.providers")} value={data ? (data.providers || []).length : "-"}
          icon={<svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="#0984E3" strokeWidth="2"><rect x="2" y="3" width="20" height="14" rx="2"/><path d="M8 21h8M12 17v4"/></svg>}
          color="#0984E3" colors={colors} delay={200} />
      </div>

      <h2 style={{ fontSize: 16, fontWeight: 600, color: colors.text, marginBottom: 14 }}>{t("quota.byProvider")}</h2>
      <div style={{ display: "grid", gridTemplateColumns: "repeat(auto-fill, minmax(300px, 1fr))", gap: 16 }}>
        {(data?.providers || []).map((p: any, i: number) => {
          const c = provColors[i % provColors.length];
          return (
            <Card key={p.provider || i} colors={colors} delay={100 + i * 80}>
              <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", marginBottom: 16 }}>
                <div style={{ display: "flex", alignItems: "center", gap: 10 }}>
                  <div style={{ width: 10, height: 10, borderRadius: "50%", background: c }} />
                  <span style={{ fontWeight: 700, color: colors.text, fontSize: 15 }}>{p.provider}</span>
                </div>
                <span style={{ fontSize: 11, padding: "3px 10px", borderRadius: 20, background: c + "15", color: c, fontWeight: 600 }}>{p.model || "-"}</span>
              </div>
              <div style={{ display: "flex", gap: 20 }}>
                <div>
                  <div style={{ fontSize: 11, color: colors.textSec, marginBottom: 4 }}>{t("quota.tokens")}</div>
                  <div style={{ fontSize: 18, fontWeight: 700, color: colors.text }}>{(p.tokens || 0).toLocaleString()}</div>
                </div>
                <div>
                  <div style={{ fontSize: 11, color: colors.textSec, marginBottom: 4 }}>{t("quota.cost")}</div>
                  <div style={{ fontSize: 18, fontWeight: 700, color: colors.text }}>${(p.cost || 0).toFixed(4)}</div>
                </div>
                <div>
                  <div style={{ fontSize: 11, color: colors.textSec, marginBottom: 4 }}>{t("quota.requests")}</div>
                  <div style={{ fontSize: 18, fontWeight: 700, color: colors.text }}>{p.requests || 0}</div>
                </div>
              </div>
            </Card>
          );
        })}
      </div>
      {(!data?.providers || data.providers.length === 0) && !err && (
        <div style={{ textAlign: "center", padding: 40, color: colors.textSec }}>{t("quota.empty")}</div>
      )}
    </div>
  );
}
