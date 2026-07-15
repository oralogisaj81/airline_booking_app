import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { getMyBookings } from '../lib/api'
import { CABIN_LABELS, formatCurrency, formatDate } from '../lib/format'

export default function MyBookingsPage() {
  const [bookings, setBookings] = useState(null)
  const [error, setError] = useState('')

  useEffect(() => {
    getMyBookings().then(setBookings).catch((err) => setError(err.message))
  }, [])

  return (
    <div className="page">
      <div className="page-header">
        <span className="eyebrow">Your trips</span>
        <h2>My bookings</h2>
      </div>

      {error && <div className="error-banner">{error}</div>}

      {bookings && bookings.length === 0 && (
        <div className="empty-state">
          <p>You haven't booked any flights yet.</p>
          <Link to="/" className="btn btn-primary" style={{ marginTop: 16, display: 'inline-flex' }}>
            Search flights
          </Link>
        </div>
      )}

      {bookings && bookings.length > 0 && (
        <div className="stack">
          {bookings.map((booking) => (
            <Link to={`/bookings/${booking.id}`} key={booking.id} className="card my-booking-card">
              <div>
                <div className="route-line">
                  {booking.outbound.originCode} → {booking.outbound.destinationCode}
                  {booking.returnLeg && ` → ${booking.outbound.originCode}`}
                </div>
                <div style={{ color: 'var(--charcoal)', fontSize: '0.85rem', marginTop: 4 }}>
                  {formatDate(booking.outbound.departureTime)} &middot; {CABIN_LABELS[booking.cabinClass]} &middot; PNR {booking.pnr}
                </div>
              </div>
              <div style={{ display: 'flex', alignItems: 'center', gap: 16 }}>
                <span style={{ fontWeight: 700 }}>{formatCurrency(booking.totalPrice)}</span>
                <span className={`badge ${booking.status === 'CONFIRMED' ? 'badge-confirmed' : 'badge-cancelled'}`}>
                  {booking.status}
                </span>
              </div>
            </Link>
          ))}
        </div>
      )}
    </div>
  )
}
