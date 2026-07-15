import { useEffect, useState } from 'react'
import { useNavigate, useSearchParams } from 'react-router-dom'
import FlightCard from '../components/FlightCard'
import { searchFlights } from '../lib/api'
import { CABIN_LABELS, formatDate, formatTime } from '../lib/format'

export default function SearchResultsPage() {
  const [searchParams] = useSearchParams()
  const navigate = useNavigate()

  const tripType = searchParams.get('tripType') || 'ONE_WAY'
  const origin = searchParams.get('origin')
  const destination = searchParams.get('destination')
  const date = searchParams.get('date')
  const returnDate = searchParams.get('returnDate')
  const passengers = Number(searchParams.get('passengers') || 1)
  const cabinClass = searchParams.get('cabinClass') || 'ECONOMY'
  const roundTrip = tripType === 'ROUND_TRIP'

  const [outboundFlights, setOutboundFlights] = useState(null)
  const [returnFlights, setReturnFlights] = useState(null)
  const [selectedOutbound, setSelectedOutbound] = useState(null)
  const [selectedReturn, setSelectedReturn] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  useEffect(() => {
    setSelectedOutbound(null)
    setSelectedReturn(null)
    setReturnFlights(null)
    setLoading(true)
    setError('')
    searchFlights({ origin, destination, date })
      .then(setOutboundFlights)
      .catch((err) => setError(err.message))
      .finally(() => setLoading(false))
  }, [origin, destination, date])

  function handleSelectOutbound(flight) {
    setSelectedOutbound(flight)
    if (roundTrip) {
      setLoading(true)
      searchFlights({ origin: destination, destination: origin, date: returnDate })
        .then(setReturnFlights)
        .catch((err) => setError(err.message))
        .finally(() => setLoading(false))
    }
  }

  function handleSelectReturn(flight) {
    setSelectedReturn(flight)
  }

  const ready = selectedOutbound && (!roundTrip || selectedReturn)

  function handleContinue() {
    navigate('/checkout', {
      state: {
        outboundFlightId: selectedOutbound.id,
        returnFlightId: selectedReturn ? selectedReturn.id : null,
        cabinClass,
        passengerCount: passengers,
      },
    })
  }

  const showingReturn = roundTrip && selectedOutbound && !selectedReturn

  return (
    <div className="page">
      <div className="page-header">
        <span className="eyebrow">{CABIN_LABELS[cabinClass]} &middot; {passengers} {passengers === 1 ? 'passenger' : 'passengers'}</span>
        <h2>{origin} &rarr; {destination}{roundTrip ? ` → ${origin}` : ''}</h2>
        <p>{formatDate(`${date}T00:00`)}{roundTrip ? ` · Returning ${formatDate(`${returnDate}T00:00`)}` : ''}</p>
      </div>

      {error && <div className="error-banner">{error}</div>}

      {loading && <div className="empty-state">Searching flights&hellip;</div>}

      {!loading && !showingReturn && outboundFlights && outboundFlights.length === 0 && (
        <div className="empty-state">No flights found for this route and date. Try a different date.</div>
      )}

      {!loading && !showingReturn && outboundFlights && outboundFlights.length > 0 && (
        <div>
          <h3>Outbound &middot; {origin} to {destination}</h3>
          {outboundFlights.map((flight) => (
            <FlightCard
              key={flight.id}
              flight={flight}
              cabinClass={cabinClass}
              passengerCount={passengers}
              selected={selectedOutbound?.id === flight.id}
              onSelect={handleSelectOutbound}
            />
          ))}
        </div>
      )}

      {!loading && showingReturn && returnFlights && returnFlights.length === 0 && (
        <div className="empty-state">No return flights found for this date.</div>
      )}

      {!loading && showingReturn && returnFlights && returnFlights.length > 0 && (
        <div>
          <h3>Return &middot; {destination} to {origin}</h3>
          {returnFlights.map((flight) => (
            <FlightCard
              key={flight.id}
              flight={flight}
              cabinClass={cabinClass}
              passengerCount={passengers}
              selected={selectedReturn?.id === flight.id}
              onSelect={handleSelectReturn}
            />
          ))}
        </div>
      )}

      {ready && (
        <div className="booking-summary-bar">
          <div className="legs">
            <span>Outbound: <strong>{selectedOutbound.flightNumber}</strong> {formatTime(selectedOutbound.departureTime)}</span>
            {selectedReturn && (
              <span>Return: <strong>{selectedReturn.flightNumber}</strong> {formatTime(selectedReturn.departureTime)}</span>
            )}
          </div>
          <button type="button" className="btn btn-primary" onClick={handleContinue}>
            Continue to passenger details
          </button>
        </div>
      )}
    </div>
  )
}
