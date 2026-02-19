import { useEffect, useState } from "react";
import { get, patch, del } from "../api";
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

export default function Users() {
  const { colors, dark: isDark } = useTheme();
  const { t } = useI18n();
  const [users, setUsers] = useState<any[]>([]);
  const [search, setSearch] = useState("");
  const [err, setErr] = useState("");

  const load = () => {
    get("/api/admin/users/?search=" + search)
      .then(r => setUsers(r.users || r || []))
      .catch(() => { setUsers([]); setErr(t("users.loadFail")); });
  };
  useEffect(load, []);

  const toggleBan = (u: any) => {
    patch("/api/admin/users/" + u.id, { is_banned: !u.is_banned }).then(load);
  };
  const toggleRole = (u: any) => {
    patch("/api/admin/users/" + u.id, { role: u.role === "admin" ? "user" : "admin" }).then(load);
  };
  const remove = (u: any) => {
    if (confirm(t("users.confirmDelete") + " " + u.username + "?")) del("/api/admin/users/" + u.id).then(load);
  };

  return (
    <div style={{ padding: 24 }}>
      <h1 style={{ fontSize: 24, fontWeight: 700, color: colors.text, marginBottom: 20 }}>{t("users.title")}</h1>
      {err && <div style={{ padding: 12, background: "#ff4d4f20", borderRadius: 10, color: "#ff4d4f", marginBottom: 16, fontSize: 13 }}>{err}</div>}
      <Card colors={colors} delay={0}>
        <div style={{ display: "flex", gap: 10, marginBottom: 20 }}>
          <input value={search} onChange={e => setSearch(e.target.value)} placeholder={t("users.search")}
            onKeyDown={e => e.key === "Enter" && load()}
            style={{ flex: 1, padding: "10px 16px", borderRadius: 10, border: "1px solid " + colors.cardBorder,
              background: isDark ? "rgba(255,255,255,0.05)" : "#f5f6f8", color: colors.text, fontSize: 14, outline: "none",
              transition: "border 0.3s" }} />
          <button onClick={load}
            style={{ padding: "10px 20px", borderRadius: 10, background: colors.accent, color: "#fff",
              border: "none", cursor: "pointer", fontWeight: 600, fontSize: 14, transition: "all 0.3s" }}>
            {t("users.searchBtn")}
          </button>
        </div>
        <div style={{ overflowX: "auto" }}>
          <table style={{ width: "100%", borderCollapse: "collapse" }}>
            <thead>
              <tr style={{ borderBottom: "2px solid " + colors.cardBorder }}>
                {["ID", t("users.username"), t("users.email"), t("users.role"), t("users.status"), t("users.lastLogin"), t("users.actions")].map((h, i) => (
                  <th key={i} style={{ padding: "12px 10px", textAlign: "left", color: colors.textSec, fontSize: 12, fontWeight: 600, textTransform: "uppercase", letterSpacing: 0.5 }}>{h}</th>
                ))}
              </tr>
            </thead>
            <tbody>
              {users.map((u, i) => (
                <Tr key={u.id} u={u} i={i} colors={colors} isDark={isDark} t={t} toggleBan={toggleBan} toggleRole={toggleRole} remove={remove} />
              ))}
              {users.length === 0 && (
                <tr><td colSpan={7} style={{ padding: 40, textAlign: "center", color: colors.textSec }}>{t("users.empty")}</td></tr>
              )}
            </tbody>
          </table>
        </div>
      </Card>
    </div>
  );
}

function Tr({ u, i, colors, isDark, t, toggleBan, toggleRole, remove }: any) {
  const [hov, setHov] = useState(false);
  return (
    <tr onMouseEnter={() => setHov(true)} onMouseLeave={() => setHov(false)}
      style={{ borderBottom: "1px solid " + colors.cardBorder,
        background: hov ? (isDark ? "rgba(255,255,255,0.03)" : "rgba(0,0,0,0.02)") : "transparent",
        transition: "background 0.2s" }}>
      <td style={{ padding: "12px 10px", color: colors.textSec, fontSize: 13 }}>{u.id}</td>
      <td style={{ padding: "12px 10px", color: colors.text, fontWeight: 600, fontSize: 14 }}>{u.username}</td>
      <td style={{ padding: "12px 10px", color: colors.textSec, fontSize: 13 }}>{u.email || "-"}</td>
      <td style={{ padding: "12px 10px" }}>
        <span style={{ padding: "3px 10px", borderRadius: 20, fontSize: 11, fontWeight: 600,
          background: u.role === "admin" ? colors.accent + "20" : (isDark ? "rgba(255,255,255,0.06)" : "#f0f0f0"),
          color: u.role === "admin" ? colors.accent : colors.textSec }}>{u.role}</span>
      </td>
      <td style={{ padding: "12px 10px" }}>
        <span style={{ padding: "3px 10px", borderRadius: 20, fontSize: 11, fontWeight: 600,
          background: u.is_banned ? "#ff4d4f20" : "#52c41a20",
          color: u.is_banned ? "#ff4d4f" : "#52c41a" }}>{u.is_banned ? t("users.banned") : t("users.active")}</span>
      </td>
      <td style={{ padding: "12px 10px", color: colors.textSec, fontSize: 12 }}>{u.last_login ? new Date(u.last_login).toLocaleString() : "-"}</td>
      <td style={{ padding: "12px 10px" }}>
        <div style={{ display: "flex", gap: 6 }}>
          <Btn text={u.is_banned ? t("users.unban") : t("users.ban")} color={u.is_banned ? "#52c41a" : "#ff4d4f"} onClick={() => toggleBan(u)} />
          <Btn text={u.role === "admin" ? t("users.demote") : t("users.promote")} color={colors.accent} onClick={() => toggleRole(u)} />
          <Btn text={t("users.delete")} color="#ff4d4f" onClick={() => remove(u)} />
        </div>
      </td>
    </tr>
  );
}

function Btn({ text, color, onClick }: any) {
  const [h, setH] = useState(false);
  return (
    <button onClick={onClick} onMouseEnter={() => setH(true)} onMouseLeave={() => setH(false)}
      style={{ padding: "5px 12px", borderRadius: 8, fontSize: 11, fontWeight: 600,
        background: h ? color + "20" : "transparent", border: "1px solid " + color + "40",
        color, cursor: "pointer", transition: "all 0.25s", transform: h ? "scale(1.05)" : "scale(1)" }}>
      {text}
    </button>
  );
}
