import { createContext, useContext, useState, ReactNode } from "react";

const zhCN: Record<string, string> = {
  "nav.dashboard":"仪表盘","nav.users":"用户管理","nav.logs":"审计日志","nav.quota":"配额管理","nav.settings":"系统设置",
  "nav.title":"管理控制台","nav.navigation":"导航菜单","nav.lightMode":"日间模式","nav.darkMode":"夜间模式",
  "nav.online":"在线","nav.signOut":"退出登录",
  "dash.totalUsers":"总用户数","dash.activeToday":"今日活跃","dash.onlineDevices":"在线设备",
  "dash.commandsToday":"今日命令","dash.blockedToday":"今日拦截","dash.weeklyCost":"本周成本",
  "dash.trend":"7 日命令趋势","dash.cmds":"命令","dash.loadFail":"后端数据加载失败，显示默认值",
  "dash.status":"系统状态","dash.statusOk":"所有服务运行正常","dash.recentActivity":"最近活动",
  "dash.noActivity":"暂无活动记录",
  "login.subtitle":"管理控制台登录","login.username":"用户名","login.password":"密码",
  "login.submit":"登 录","login.loading":"登录中...","login.fail":"用户名或密码错误","login.required":"请输入用户名和密码",
  "users.title":"用户管理","users.search":"搜索用户名或邮箱...","users.searchBtn":"搜索",
  "users.username":"用户名","users.email":"邮箱","users.role":"角色","users.status":"状态",
  "users.lastLogin":"最后登录","users.actions":"操作","users.active":"正常","users.banned":"封禁",
  "users.ban":"封禁","users.unban":"解封","users.promote":"设为管理员","users.demote":"取消管理员",
  "users.delete":"删除","users.confirmDelete":"确认删除用户","users.empty":"暂无用户","users.loadFail":"加载失败",
  "logs.title":"审计日志","logs.time":"时间","logs.user":"用户","logs.action":"操作",
  "logs.target":"目标","logs.result":"结果","logs.detail":"详情","logs.empty":"暂无日志",
  "logs.total":"总计","logs.prev":"上一页","logs.next":"下一页","logs.loadFail":"加载失败",
  "quota.title":"配额管理","quota.totalTokens":"总 Token 数","quota.totalCost":"总费用",
  "quota.providers":"服务商数","quota.byProvider":"按服务商统计","quota.tokens":"Token",
  "quota.cost":"费用","quota.requests":"请求数","quota.empty":"暂无配额数据","quota.loadFail":"加载失败",
  "settings.title":"系统设置","settings.health":"系统健康","settings.disk":"磁盘","settings.memory":"内存",
  "settings.uptime":"运行时间","settings.dbConn":"数据库连接","settings.providers":"AI 服务商",
  "settings.default":"默认","settings.noProviders":"未配置服务商","settings.maintenance":"运维工具",
  "settings.cleanDesc":"清理过期的 API 密钥和无效会话，释放系统资源。",
  "settings.cleanBtn":"清理过期密钥","settings.cleaning":"清理中...","settings.cleanSuccess":"清理成功",
  "settings.cleanFail":"清理失败","settings.loading":"加载中..."
};

const enUS: Record<string, string> = {
  "nav.dashboard":"Dashboard","nav.users":"Users","nav.logs":"Audit Logs","nav.quota":"Quota","nav.settings":"Settings",
  "nav.title":"Admin Console","nav.navigation":"NAVIGATION","nav.lightMode":"Light Mode","nav.darkMode":"Dark Mode",
  "nav.online":"Online","nav.signOut":"Sign Out",
  "dash.totalUsers":"Total Users","dash.activeToday":"Active Today","dash.onlineDevices":"Online Devices",
  "dash.commandsToday":"Commands Today","dash.blockedToday":"Blocked Today","dash.weeklyCost":"Weekly Cost",
  "dash.trend":"7-Day Command Trend","dash.cmds":"Commands","dash.loadFail":"Failed to load, showing defaults",
  "dash.status":"System Status","dash.statusOk":"All services running normally","dash.recentActivity":"Recent Activity",
  "dash.noActivity":"No activity records",
  "login.subtitle":"Admin Console Login","login.username":"Username","login.password":"Password",
  "login.submit":"Sign In","login.loading":"Signing in...","login.fail":"Invalid credentials","login.required":"Please enter username and password",
  "users.title":"User Management","users.search":"Search username or email...","users.searchBtn":"Search",
  "users.username":"Username","users.email":"Email","users.role":"Role","users.status":"Status",
  "users.lastLogin":"Last Login","users.actions":"Actions","users.active":"Active","users.banned":"Banned",
  "users.ban":"Ban","users.unban":"Unban","users.promote":"Promote","users.demote":"Demote",
  "users.delete":"Delete","users.confirmDelete":"Confirm delete user","users.empty":"No users","users.loadFail":"Load failed",
  "logs.title":"Audit Logs","logs.time":"Time","logs.user":"User","logs.action":"Action",
  "logs.target":"Target","logs.result":"Result","logs.detail":"Detail","logs.empty":"No logs",
  "logs.total":"Total","logs.prev":"Previous","logs.next":"Next","logs.loadFail":"Load failed",
  "quota.title":"Quota Management","quota.totalTokens":"Total Tokens","quota.totalCost":"Total Cost",
  "quota.providers":"Providers","quota.byProvider":"By Provider","quota.tokens":"Tokens",
  "quota.cost":"Cost","quota.requests":"Requests","quota.empty":"No quota data","quota.loadFail":"Load failed",
  "settings.title":"System Settings","settings.health":"System Health","settings.disk":"Disk","settings.memory":"Memory",
  "settings.uptime":"Uptime","settings.dbConn":"DB Connection","settings.providers":"AI Providers",
  "settings.default":"Default","settings.noProviders":"No providers configured","settings.maintenance":"Maintenance",
  "settings.cleanDesc":"Clean up expired API keys and invalid sessions to free system resources.",
  "settings.cleanBtn":"Clean Expired Keys","settings.cleaning":"Cleaning...","settings.cleanSuccess":"Cleanup successful",
  "settings.cleanFail":"Cleanup failed","settings.loading":"Loading..."
};

interface I18n { lang: string; setLang: (l: string) => void; t: (k: string) => string; }

const I18nContext = createContext<I18n>({ lang: "zh", setLang: () => {}, t: (k) => k });

export function I18nProvider({ children }: { children: ReactNode }) {
  const [lang, setLangState] = useState(() => localStorage.getItem("lang") || "zh");
  const dict = lang === "zh" ? zhCN : enUS;
  const setLang = (l: string) => { setLangState(l); localStorage.setItem("lang", l); };
  const t = (k: string) => dict[k] || k;
  return <I18nContext.Provider value={{ lang, setLang, t }}>{children}</I18nContext.Provider>;
}

export const useI18n = () => useContext(I18nContext);
