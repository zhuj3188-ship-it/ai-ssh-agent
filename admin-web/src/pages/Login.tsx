import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { post } from "../api";
import { useI18n } from "../I18nContext";

export default function Login() {
  const nav = useNavigate();
  const { t, lang, setLang } = useI18n();
  const [u, setU] = useState("");
  const [p, setP] = useState("");
  const [err, setErr] = useState("");
  const [loading, setLoading] = useState(false);
  const [showPw, setShowPw] = useState(false);

  const submit = async () => {
    if (!u || !p) return setErr(t("login.required"));
    setLoading(true); setErr("");
    try {
      const res = await post("/api/auth/login", { username: u, password: p });
      localStorage.setItem("token", res.token);
      nav("/");
    } catch { setErr(t("login.fail")); }
    finally { setLoading(false); }
  };

  return (
    <div style={{ minHeight: "100vh", display: "flex", alignItems: "center", justifyContent: "center",
      background: "linear-gradient(135deg, #0a0a1a 0%, #1a1a3e 50%, #0a0a1a 100%)" }}>
      <div style={{ position: "absolute", top: 20, right: 20 }}>
        <button onClick={() => setLang(lang === "zh" ? "en" : "zh")}
          style={{ padding: "6px 14px", borderRadius: 8, background: "rgba(255,255,255,0.1)",
            border: "1px solid rgba(255,255,255,0.15)", color: "#fff", cursor: "pointer", fontSize: 12, fontWeight: 600 }}>
          {lang === "zh" ? "EN" : "\u4e2d"}
        </button>
      </div>
      <div style={{ width: 380, padding: 40, borderRadius: 20,
        background: "rgba(255,255,255,0.05)", backdropFilter: "blur(20px)",
        border: "1px solid rgba(255,255,255,0.1)", boxShadow: "0 20px 60px rgba(0,0,0,0.5)" }}>
        <div style={{ textAlign: "center", marginBottom: 32 }}>
          <div style={{ width: 56, height: 56, margin: "0 auto 16px", borderRadius: 16,
            background: "linear-gradient(135deg, #6C5CE7, #a29bfe)",
            display: "flex", alignItems: "center", justifyContent: "center" }}>
            <svg width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="#fff" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
              <path d="M13 2L3 14h9l-1 8 10-12h-9l1-8z"/>
            </svg>
          </div>
          <h1 style={{ fontSize: 22, fontWeight: 700, color: "#fff", marginBottom: 6 }}>AI SSH Agent</h1>
          <p style={{ fontSize: 13, color: "rgba(255,255,255,0.5)" }}>{t("login.subtitle")}</p>
        </div>
        {err && <div style={{ padding: 10, borderRadius: 10, background: "#ff4d4f15", color: "#ff6b6b",
          fontSize: 13, textAlign: "center", marginBottom: 16 }}>{err}</div>}
        <div style={{ marginBottom: 16 }}>
          <label style={{ display: "block", fontSize: 12, color: "rgba(255,255,255,0.5)", marginBottom: 6, fontWeight: 500 }}>{t("login.username")}</label>
          <input value={u} onChange={e => setU(e.target.value)} onKeyDown={e => e.key === "Enter" && submit()}
            style={{ width: "100%", padding: "12px 16px", borderRadius: 12,
              background: "rgba(255,255,255,0.06)", border: "1px solid rgba(255,255,255,0.1)",
              color: "#fff", fontSize: 14, outline: "none", boxSizing: "border-box",
              transition: "border 0.3s" }} />
        </div>
        <div style={{ marginBottom: 24 }}>
          <label style={{ display: "block", fontSize: 12, color: "rgba(255,255,255,0.5)", marginBottom: 6, fontWeight: 500 }}>{t("login.password")}</label>
          <div style={{ position: "relative" }}>
            <input type={showPw ? "text" : "password"} value={p} onChange={e => setP(e.target.value)} onKeyDown={e => e.key === "Enter" && submit()}
              style={{ width: "100%", padding: "12px 44px 12px 16px", borderRadius: 12,
                background: "rgba(255,255,255,0.06)", border: "1px solid rgba(255,255,255,0.1)",
                color: "#fff", fontSize: 14, outline: "none", boxSizing: "border-box",
                transition: "border 0.3s" }} />
            <button onClick={() => setShowPw(!showPw)} type="button"
              style={{ position: "absolute", right: 12, top: "50%", transform: "translateY(-50%)",
                background: "none", border: "none", cursor: "pointer", color: "rgba(255,255,255,0.4)", padding: 4 }}>
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round">
                {showPw ? (
                  <><path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/><circle cx="12" cy="12" r="3"/></>
                ) : (
                  <><path d="M17.94 17.94A10.07 10.07 0 0112 20c-7 0-11-8-11-8a18.45 18.45 0 015.06-5.94"/><path d="M9.9 4.24A9.12 9.12 0 0112 4c7 0 11 8 11 8a18.5 18.5 0 01-2.16 3.19"/><line x1="1" y1="1" x2="23" y2="23"/></>
                )}
              </svg>
            </button>
          </div>
        </div>
        <button onClick={submit} disabled={loading}
          style={{ width: "100%", padding: "13px 0", borderRadius: 12,
            background: loading ? "#555" : "linear-gradient(135deg, #6C5CE7, #a29bfe)",
            color: "#fff", fontWeight: 700, fontSize: 15, border: "none",
            cursor: loading ? "not-allowed" : "pointer", transition: "all 0.3s",
            boxShadow: "0 4px 15px rgba(108,92,231,0.3)" }}>
          {loading ? t("login.loading") : t("login.submit")}
        </button>
      </div>
    </div>
  );
}
