import { Link, NavLink, useNavigate } from 'react-router-dom'
import { authClient, useSession } from '../lib/auth-client'

export default function Navbar() {
  const { data: session, refresh } = useSession()
  const navigate = useNavigate()

  async function handleSignOut() {
    await authClient.signOut()
    refresh()
    navigate('/')
  }

  return (
    <header className="navbar">
      <div className="navbar-inner">
        <Link to="/" className="brand">
          <img src="/favicon.svg" alt="" className="brand-mark" />
          <span className="brand-word">
            Al Noor
            <small>AIRWAYS</small>
          </span>
        </Link>

        <nav className="nav-links">
          <NavLink to="/" end className={({ isActive }) => `nav-link${isActive ? ' active' : ''}`}>
            Book a Flight
          </NavLink>
          <NavLink to="/bookings" className={({ isActive }) => `nav-link${isActive ? ' active' : ''}`}>
            My Trips
          </NavLink>
        </nav>

        <div className="nav-user">
          {session ? (
            <>
              <span className="nav-user-name">{session.user.name}</span>
              <button type="button" className="btn btn-outline-light btn-sm" onClick={handleSignOut}>
                Sign out
              </button>
            </>
          ) : (
            <>
              <Link to="/sign-in" className="btn btn-outline-light btn-sm">Sign in</Link>
              <Link to="/sign-up" className="btn btn-gold btn-sm">Sign up</Link>
            </>
          )}
        </div>
      </div>
    </header>
  )
}
