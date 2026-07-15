import { useEffect, useState } from 'react'
import { Link, useLocation, useParams } from 'react-router-dom'
import { cancelBooking, getBooking } from '../lib/api'
import { CABIN_LABELS, PASSENGER_TYPE_LABELS, formatCurrency, formatDate, formatTime } from '../lib/format'

export default function BookingReceiptPage() {
  const { id } = useParams()
  const location = useLocation()
  const [booking, setBooking] = useState(null)
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(true)
  const [cancelling, setCancelling] = useState(false)
  const [confirmingCancel, setConfirmingCancel] = useState(false)

  useEffect(() => {
    setLoading(true)
    getBooking(id)
      .then(setBooking)
      .catch((err) => setError(err.message))
      .finally(() => setLoading(false))
  }, [id])

  async function handleCancel() {
    setCancelling(true)
    setError('')
    try {
      const updated = await cancelBooking(id)
      setBooking(updated)
      setConfirmingCancel(false)
    } catch (err) {
      setError(err.message)
    } finally {
      setCancelling(false)
    }
  }

  if (loading) return <div className="page empty-state">Loading booking&hellip;</div>
  if (error && !booking) return <div className="page"><div className="error-banner">{error}</div></div>
  if (!booking) return null

  return (
    <div className="page page-narrow">
      {location.state?.justBooked && (
        <div className="success-banner">Your booking is confirmed! A summary has been sent to {booking.contactEmail}.</div>
      )}
      {error && <div className="error-banner">{error}</div>}

      <div className="card">
        <div className="receipt-header">
          <div>
            <div className="pnr-label">Booking reference</div>
            <div className="pnr-code">{booking.pnr}</div>
          </div>
          <span className={`badge ${booking.status === 'CONFIRMED' ? 'badge-confirmed' : 'badge-cancelled'}`}>
            {booking.status}
          </span>
        </div>

        <Leg label="Outbound" leg={booking.outbound} />
        {booking.returnLeg && <Leg label="Return" leg={booking.returnLeg} />}

        <div className="section">
          <h4>Passengers &middot; {CABIN_LABELS[booking.cabinClass]}</h4>
          <ul className="receipt-passenger-list">
            {booking.passengers.map((p) => (
              <li key={p.id}>
                <span>{p.fullName} <span style={{ color: 'var(--charcoal)' }}>({PASSENGER_TYPE_LABELS[p.passengerType]})</span></span>
                <span>Seat {p.seatNumber}</span>
              </li>
            ))}
          </ul>
        </div>

        <div className="summary-row total">
          <span>Total paid</span>
          <span className="amount">{formatCurrency(booking.totalPrice)}</span>
        </div>

        <div className="summary-row">
          <span>Contact</span>
          <span>{booking.contactEmail} &middot; {booking.contactPhone}</span>
        </div>
      </div>

      <div style={{ marginTop: 20, display: 'flex', gap: 12, justifyContent: 'space-between' }}>
        <Link to="/bookings" className="btn btn-outline">Back to my trips</Link>
        {booking.status === 'CONFIRMED' && (
          confirmingCancel ? (
            <div style={{ display: 'flex', gap: 8 }}>
              <span style={{ alignSelf: 'center', color: 'var(--charcoal)', fontSize: '0.85rem' }}>Cancel this booking?</span>
              <button type="button" className="btn btn-outline" onClick={() => setConfirmingCancel(false)}>No</button>
              <button type="button" className="btn btn-primary" disabled={cancelling} onClick={handleCancel}>
                {cancelling ? 'Cancelling…' : 'Yes, cancel'}
              </button>
            </div>
          ) : (
            <button type="button" className="btn btn-danger-text" onClick={() => setConfirmingCancel(true)}>
              Cancel booking
            </button>
          )
        )}
      </div>
    </div>
  )
}

function Leg({ label, leg }) {
  return (
    <div className="receipt-leg">
      <div className="receipt-leg-title">{label}</div>
      <div className="flight-route">
        <div className="flight-endpoint">
          <div className="time">{formatTime(leg.departureTime)}</div>
          <div className="code">{leg.originCode}</div>
        </div>
        <div className="flight-path">
          <div className="duration">{leg.flightNumber}</div>
          <div className="flight-path-line" />
          <div className="flight-meta">{leg.aircraftType}</div>
        </div>
        <div className="flight-endpoint">
          <div className="time">{formatTime(leg.arrivalTime)}</div>
          <div className="code">{leg.destinationCode}</div>
        </div>
      </div>
      <div className="flight-meta" style={{ marginTop: 8 }}>{formatDate(leg.departureTime)}</div>
    </div>
  )
}
