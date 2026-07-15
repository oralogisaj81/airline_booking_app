import { useEffect, useState } from 'react'
import { useLocation, useNavigate } from 'react-router-dom'
import { useSession } from '../lib/auth-client'
import { createBooking, getFlight } from '../lib/api'
import { CABIN_LABELS, PASSENGER_TYPE_LABELS, formatCurrency, formatDate, formatTime } from '../lib/format'

const EMPTY_PASSENGER = { fullName: '', passportNumber: '', dateOfBirth: '', passengerType: 'ADULT' }

export default function CheckoutPage() {
  const location = useLocation()
  const navigate = useNavigate()
  const { data: session } = useSession()
  const draft = location.state

  const [outboundFlight, setOutboundFlight] = useState(null)
  const [returnFlight, setReturnFlight] = useState(null)
  const [loading, setLoading] = useState(true)
  const [loadError, setLoadError] = useState('')
  const [submitError, setSubmitError] = useState('')
  const [submitting, setSubmitting] = useState(false)
  const [passengers, setPassengers] = useState([])
  const [contactEmail, setContactEmail] = useState(session?.user?.email ?? '')
  const [contactPhone, setContactPhone] = useState('')

  useEffect(() => {
    if (!draft) return
    setPassengers(Array.from({ length: draft.passengerCount }, () => ({ ...EMPTY_PASSENGER })))
  }, [draft])

  useEffect(() => {
    if (!draft) return
    setLoading(true)
    Promise.all([
      getFlight(draft.outboundFlightId),
      draft.returnFlightId ? getFlight(draft.returnFlightId) : Promise.resolve(null),
    ])
      .then(([out, ret]) => {
        setOutboundFlight(out)
        setReturnFlight(ret)
      })
      .catch((err) => setLoadError(err.message))
      .finally(() => setLoading(false))
  }, [draft])

  if (!draft) {
    return (
      <div className="page empty-state">
        <p>Start a new search to book a flight.</p>
        <button type="button" className="btn btn-primary" style={{ marginTop: 16 }} onClick={() => navigate('/')}>
          Search flights
        </button>
      </div>
    )
  }

  function updatePassenger(index, field, value) {
    setPassengers((prev) => prev.map((p, i) => (i === index ? { ...p, [field]: value } : p)))
  }

  const seatKey = { ECONOMY: 'economySeatsAvailable', BUSINESS: 'businessSeatsAvailable', FIRST: 'firstSeatsAvailable' }[draft.cabinClass]
  const priceKey = { ECONOMY: 'economyPrice', BUSINESS: 'businessPrice', FIRST: 'firstPrice' }[draft.cabinClass]
  const legPrice = (outboundFlight ? Number(outboundFlight[priceKey]) : 0) + (returnFlight ? Number(returnFlight[priceKey]) : 0)
  const total = legPrice * draft.passengerCount
  const stillAvailable = outboundFlight
    && outboundFlight[seatKey] >= draft.passengerCount
    && (!returnFlight || returnFlight[seatKey] >= draft.passengerCount)

  async function handleSubmit(e) {
    e.preventDefault()
    setSubmitError('')
    setSubmitting(true)
    try {
      const booking = await createBooking({
        outboundFlightId: draft.outboundFlightId,
        returnFlightId: draft.returnFlightId,
        cabinClass: draft.cabinClass,
        contactEmail,
        contactPhone,
        passengers,
      })
      navigate(`/bookings/${booking.id}`, { state: { justBooked: true } })
    } catch (err) {
      setSubmitError(err.message)
      // Re-fetch live fare/availability -- someone may have just taken the
      // last seats while this passenger was filling out the form.
      Promise.all([
        getFlight(draft.outboundFlightId),
        draft.returnFlightId ? getFlight(draft.returnFlightId) : Promise.resolve(null),
      ]).then(([out, ret]) => {
        setOutboundFlight(out)
        setReturnFlight(ret)
      })
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <div className="page">
      <div className="page-header">
        <span className="eyebrow">Passenger details</span>
        <h2>Complete your booking</h2>
      </div>

      {loading && <div className="empty-state">Loading flight details&hellip;</div>}
      {loadError && <div className="error-banner">{loadError}</div>}

      {!loading && outboundFlight && (
        <div className="checkout-layout">
          <div>
            {submitError && <div className="error-banner">{submitError}</div>}
            {!stillAvailable && (
              <div className="error-banner">
                Seat availability changed while you were booking. Please go back and choose another flight.
              </div>
            )}

            <form onSubmit={handleSubmit} className="stack">
              {passengers.map((passenger, index) => (
                <div className="passenger-card" key={index}>
                  <h4>
                    Passenger {index + 1}
                    <span className="passenger-badge">{CABIN_LABELS[draft.cabinClass]}</span>
                  </h4>
                  <div className="form-grid">
                    <div className="field">
                      <label>Full name (as on passport)</label>
                      <input
                        required
                        value={passenger.fullName}
                        onChange={(e) => updatePassenger(index, 'fullName', e.target.value)}
                      />
                    </div>
                    <div className="field">
                      <label>Passport number</label>
                      <input
                        required
                        value={passenger.passportNumber}
                        onChange={(e) => updatePassenger(index, 'passportNumber', e.target.value)}
                      />
                    </div>
                    <div className="field">
                      <label>Date of birth</label>
                      <input
                        type="date"
                        required
                        max={new Date().toISOString().slice(0, 10)}
                        value={passenger.dateOfBirth}
                        onChange={(e) => updatePassenger(index, 'dateOfBirth', e.target.value)}
                      />
                    </div>
                    <div className="field">
                      <label>Passenger type</label>
                      <select
                        value={passenger.passengerType}
                        onChange={(e) => updatePassenger(index, 'passengerType', e.target.value)}
                      >
                        {Object.entries(PASSENGER_TYPE_LABELS).map(([value, label]) => (
                          <option key={value} value={value}>{label}</option>
                        ))}
                      </select>
                    </div>
                  </div>
                </div>
              ))}

              <div className="card">
                <h4 style={{ marginBottom: 16 }}>Contact details</h4>
                <div className="form-grid">
                  <div className="field">
                    <label>Email</label>
                    <input type="email" required value={contactEmail} onChange={(e) => setContactEmail(e.target.value)} />
                  </div>
                  <div className="field">
                    <label>Phone</label>
                    <input type="tel" required value={contactPhone} onChange={(e) => setContactPhone(e.target.value)} />
                  </div>
                </div>
              </div>

              <button type="submit" className="btn btn-primary btn-block" disabled={submitting || !stillAvailable}>
                {submitting ? 'Booking…' : `Confirm booking · ${formatCurrency(total)}`}
              </button>
            </form>
          </div>

          <aside className="summary-panel card">
            <h4>Trip summary</h4>
            <LegSummary label="Outbound" flight={outboundFlight} />
            {returnFlight && <LegSummary label="Return" flight={returnFlight} />}
            <div className="summary-row">
              <span>Cabin</span>
              <span>{CABIN_LABELS[draft.cabinClass]}</span>
            </div>
            <div className="summary-row">
              <span>Passengers</span>
              <span>{draft.passengerCount}</span>
            </div>
            <div className="summary-row">
              <span>Fare per passenger</span>
              <span>{formatCurrency(legPrice)}</span>
            </div>
            <div className="summary-row total">
              <span>Total</span>
              <span className="amount">{formatCurrency(total)}</span>
            </div>
          </aside>
        </div>
      )}
    </div>
  )
}

function LegSummary({ label, flight }) {
  return (
    <div className="summary-row" style={{ flexDirection: 'column', alignItems: 'flex-start', gap: 4 }}>
      <span className="eyebrow" style={{ fontSize: '0.68rem' }}>{label}</span>
      <span style={{ color: 'var(--ink)', fontWeight: 600 }}>
        {flight.originCode} → {flight.destinationCode} &middot; {flight.flightNumber}
      </span>
      <span>{formatDate(flight.departureTime)} &middot; {formatTime(flight.departureTime)}</span>
    </div>
  )
}
