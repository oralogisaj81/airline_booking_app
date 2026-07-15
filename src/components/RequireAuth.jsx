import { Navigate, useLocation } from 'react-router-dom'
import { useSession } from '../lib/auth-client'

export default function RequireAuth({ children }) {
  const { data: session, isPending } = useSession()
  const location = useLocation()

  if (isPending) {
    return (
      <div className="page">
        <div className="empty-state">Loading...</div>
      </div>
    )
  }

  if (!session) {
    return <Navigate to="/sign-in" state={{ from: location.pathname }} replace />
  }

  return children
}
