const BASE = ""

function headers(): Record<string, string> {
  const token = localStorage.getItem("token")
  return {
    "Content-Type": "application/json",
    ...(token ? { Authorization: "Bearer " + token } : {}),
  }
}

export async function api<T = any>(path: string, opts?: RequestInit): Promise<T> {
  const res = await fetch(BASE + path, { headers: headers(), ...opts })
  if (res.status === 401) {
    localStorage.removeItem("token")
    window.location.href = "/login"
    throw new Error("Unauthorized")
  }
  if (!res.ok) throw new Error(await res.text())
  return res.json()
}

export const get = <T = any>(path: string) => api<T>(path)
export const post = <T = any>(path: string, body?: any) =>
  api<T>(path, { method: "POST", body: body ? JSON.stringify(body) : undefined })
export const patch = <T = any>(path: string, body: any) =>
  api<T>(path, { method: "PATCH", body: JSON.stringify(body) })
export const del = <T = any>(path: string) =>
  api<T>(path, { method: "DELETE" })
