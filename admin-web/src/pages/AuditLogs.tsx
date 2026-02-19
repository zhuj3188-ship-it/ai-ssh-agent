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

export default function AuditLogs() {
  const { colors, dark: isDark } = useTheme();
  const { t } = useI18n();
  const [logs, setLogs] = useState<any[]>([]);
  const [page, setPage] = useState(1);
  const [total, setTotal] = useState(0);
  const [err, setErr] = useState("");
  const pageSize = 20;

  const load = () => {
    get("/api/admin/logs/?page=" + page + "&size=" + pageSize)
      .then(r => { setLogs(r.logs || r || []); setTotal(r.total || 0); })
      .catch(() => { setLogs([]); setErr(t("logs.loadFail")); });
  };
  useEffect(load, [page]);

  const totalPages = Math.max(1, Math.ceil(total / pageSize));

  const resultColor = (r: string) => {
    if (r === "blocked") return "#ff4d4f";
    if (r === "success") return "#52c41a";
    if (r === "error") return "#faad14";
    return colors.textSec;
  };

  return (
    <div style={{ padding: 24 }}>
      <h1 style={{ fontSize: 24, fontWeight: 700, color: colors.text, marginBottom: 20 }}>{t("logs.title")}</h1>
      {err && <div style={{ padding: 12, background: "#ff4d4f20", borderRadius: 10, color: "#ff4d4f", marginBottom: 16, fontSize: 13 }}>{err}</div>}
      <Card colors={colors}>
        <div style={{ overflowX: "auto" }}>
          <table style={{ width: "100%", borderCollapse: "collapse" }}>
            <thead>
              <tr style={{ borderBottom: "2px solid " + colors.cardBorder }}>
                {[t("logs.time"), t("logs.user"), t("logs.action"), t("logs.target"), t("logs.result"), t("logs.detail")].map((h, i) => (
                  <th key={i} style={{ padding: "12px 10px", textAlign: "left", color: colors.textSec, fontSize: 12, fontWeight: 600, textTransform: "uppercase", letterSpacing: 0.5 }}>{h}</th>
                ))}
              </tr>
            </thead>
            <tbody>
              {logs.map((log, i) => (
                <LogRow key={log.id || i} log={log} colors={colors} isDark={isDark} resultColor={resultColor} />
              ))}
              {logs.length === 0 && (
                <tr><td colSpan={6} style={{ padding: 40, textAlign: "center", color: colors.textSec }}>{t("logs.empty")}</td></tr>
              )}
            </tbody>
          </table>
        </div>
        <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", marginTop: 20 }}>
          <span style={{ fontSize: 13, color: colors.textSec }}>{t("logs.total")}: {total}</span>
          <div style={{ display: "flex", gap: 8 }}>
            <PageBtn text={t("logs.prev")} disabled={page <= 1} onClick={() => setPage(p => p - 1)} colors={colors} />
            <span style={{ padding: "6px 14px", fontSize: 13, color: colors.text }}>{page} / {totalPages}</span>
            <PageBtn text={t("logs.next")} disabled={page >= totalPages} onClick={() => setPage(p => p + 1)} colors={colors} />
          </div>
        </div>
      </Card>
    </div>
  );
}

function LogRow({ log, colors, isDark, resultColor }: any) {
  const [hov, setHov] = useState(false);
  return (
    <tr onMouseEnter={() => setHov(true)} onMouseLeave={() => setHov(false)}
      style={{ borderBottom: "1px solid " + colors.cardBorder,
        background: hov ? (isDark ? "rgba(255,255,255,0.03)" : "rgba(0,0,0,0.02)") : "transparent",
        transition: "background 0.2s" }}>
      <td style={{ padding: "12px 10px", color: colors.textSec, fontSize: 12, whiteSpace: "nowrap" }}>{log.created_at ? new Date(log.created_at).toLocaleString() : "-"}</td>
      <td style={{ padding: "12px 10px", color: colors.text, fontWeight: 500, fontSize: 13 }}>{log.username || log.user_id || "-"}</td>
      <td style={{ padding: "12px 10px" }}>
        <span style={{ padding: "3px 10px", borderRadius: 20, fontSize: 11, fontWeight: 600,
          background: colors.accent + "15", color: colors.accent }}>{log.action || "-"}</span>
      </td>
      <td style={{ padding: "12px 10px", color: colors.textSec, fontSize: 13, maxWidth: 200, overflow: "hidden", textOverflow: "ellipsis" }}>{log.target || "-"}</td>
      <td style={{ padding: "12px 10px" }}>
        <span style={{ padding: "3px 10px", borderRadius: 20, fontSize: 11, fontWeight: 600,
          background: resultColor(log.result) + "15", color: resultColor(log.result) }}>{log.result || "-"}</span>
      </td>
      <td style={{ padding: "12px 10px", color: colors.textSec, fontSize: 12, maxWidth: 250, overflow: "hidden", textOverflow: "ellipsis" }}>{log.detail || "-"}</td>
    </tr>
  );
}

function PageBtn({ text, disabled, onClick, colors }: any) {
  const [h, setH] = useState(false);
  return (
    <button disabled={disabled} onClick={onClick}
      onMouseEnter={() => setH(true)} onMouseLeave={() => setH(false)}
      style={{ padding: "6px 16px", borderRadius: 8, fontSize: 13, fontWeight: 600,
        background: disabled ? "transparent" : (h ? colors.accent : colors.accent + "15"),
        color: disabled ? colors.textSec : (h ? "#fff" : colors.accent),
        border: "1px solid " + (disabled ? colors.cardBorder : colors.accent + "40"),
        cursor: disabled ? "not-allowed" : "pointer", opacity: disabled ? 0.4 : 1,
        transition: "all 0.25s", transform: h && !disabled ? "scale(1.05)" : "scale(1)" }}>
      {text}
    </button>
  );
}
