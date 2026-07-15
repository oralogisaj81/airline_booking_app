import { useState } from 'react'
import { Link, useLocation, useNavigate } from 'react-router-dom'
import { authClient, useSession } from '../lib/auth-client'

export default function SignInPage() {
  const navigate = useNavigate()
  const location = useLocation()
  const { refresh } = useSession()
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState('')
  const [submitting, setSubmitting] = useState(false)

  async function handleSubmit(e) {
    e.preventDefault()
    setSubmitting(true)
    setError('')
    try {
      await authClient.signIn({ email, password })
      refresh()
      navigate(location.state?.from || '/')
    } catch (err) {
      setError(err.message)
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <div className="page auth-card">
      <h1>Welcome back</h1>
      {error && <div className="error-banner">{error}</div>}
      <form className="stack" onSubmit={handleSubmit}>
        <div className="field">
          <label>Email</label>
          <input type="email" required value={email} onChange={(e) => setEmail(e.target.value)} />
        </div>
        <div className="field">
          <label>Password</label>
          <input type="password" required value={password} onChange={(e) => setPassword(e.target.value)} />
        </div>
        <button type="submit" className="btn btn-primary btn-block" disabled={submitting}>
          {submitting ? 'Signing in…' : 'Sign in'}
        </button>
      </form>
      <p className="auth-switch">
        New to Al Noor Airways? <Link to="/sign-up">Create an account</Link>
      </p>
    </div>
  )
}
