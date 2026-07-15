import { useCallback, useEffect, useState } from 'react'
import { getSession } from './auth-client'
import { SessionContext } from './session-store'

export function SessionProvider({ children }) {
  const [session, setSession] = useState(null)
  const [isPending, setIsPending] = useState(true)

  const refresh = useCallback(() => {
    setIsPending(true)
    getSession()
      .then((user) => setSession({ user }))
      .catch(() => setSession(null))
      .finally(() => setIsPending(false))
  }, [])

  useEffect(() => {
    refresh()
  }, [refresh])

  return <SessionContext.Provider value={{ session, isPending, refresh }}>{children}</SessionContext.Provider>
}
