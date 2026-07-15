import { useContext } from 'react'
import { SessionContext } from './session-store'

const BASE = '/api/auth'

async function request(path, options) {
  const res = await fetch(`${BASE}${path}`, {
    headers: { 'Content-Type': 'application/json' },
    ...options,
  })
  if (!res.ok) {
    const body = await res.json().catch(() => ({}))
    throw new Error(body.error || `Request failed with status ${res.status}`)
  }
  const text = await res.text()
  return text ? JSON.parse(text) : null
}

export function getSession() {
  return request('/me')
}

export function useSession() {
  const ctx = useContext(SessionContext)
  return { data: ctx.session, isPending: ctx.isPending, refresh: ctx.refresh }
}

export const authClient = {
  signUp(input) {
    return request('/register', { method: 'POST', body: JSON.stringify(input) })
  },
  signIn(input) {
    return request('/login', { method: 'POST', body: JSON.stringify(input) })
  },
  signOut() {
    return request('/logout', { method: 'POST' })
  },
}
