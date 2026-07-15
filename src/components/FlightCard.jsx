import { formatCurrency, formatDuration, formatTime } from '../lib/format'

const CABIN_PRICE_KEY = {
  ECONOMY: 'economyPrice',
  BUSINESS: 'businessPrice',
  FIRST: 'firstPrice',
}

const CABIN_SEATS_KEY = {
  ECONOMY: 'economySeatsAvailable',
  BUSINESS: 'businessSeatsAvailable',
  FIRST: 'firstSeatsAvailable',
}

export default function FlightCard({ flight, cabinClass, passengerCount, selected, onSelect }) {
  const price = flight[CABIN_PRICE_KEY[cabinClass]]
  const seatsAvailable = flight[CABIN_SEATS_KEY[cabinClass]]
  const canBook = seatsAvailable >= passengerCount

  return (
    <div className={`flight-card${selected ? ' selected' : ''}`}>
      <div className="flight-route">
        <div className="flight-endpoint">
          <div className="time">{formatTime(flight.departureTime)}</div>
          <div className="code">{flight.originCode}</div>
        </div>
        <div className="flight-path">
          <div className="duration">{formatDuration(flight.durationMinutes)} &middot; {flight.flightNumber}</div>
          <div className="flight-path-line" />
          <div className="flight-meta">{flight.aircraftType}</div>
        </div>
        <div className="flight-endpoint">
          <div className="time">{formatTime(flight.arrivalTime)}</div>
          <div className="code">{flight.destinationCode}</div>
        </div>
      </div>

      <div className="flight-fare">
        <div className="price">{formatCurrency(price)}</div>
        <div className="per-person">per passenger</div>
        <span className={`seats-left ${seatsAvailable <= 5 ? 'low' : 'ok'}`}>
          {seatsAvailable > 0 ? `${seatsAvailable} seats left` : 'Sold out'}
        </span>
      </div>

      <button
        type="button"
        className={`btn ${selected ? 'btn-primary' : 'btn-outline'}`}
        disabled={!canBook}
        onClick={() => onSelect(flight)}
      >
        {selected ? 'Selected' : canBook ? 'Select' : 'Unavailable'}
      </button>
    </div>
  )
}
