import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { getAirports } from '../lib/api'
import { todayIso } from '../lib/format'

export default function HomePage() {
  const navigate = useNavigate()
  const [airports, setAirports] = useState([])
  const [tripType, setTripType] = useState('ROUND_TRIP')
  const [origin, setOrigin] = useState('')
  const [destination, setDestination] = useState('')
  const [date, setDate] = useState('')
  const [returnDate, setReturnDate] = useState('')
  const [passengers, setPassengers] = useState(1)
  const [cabinClass, setCabinClass] = useState('ECONOMY')
  const [error, setError] = useState('')

  useEffect(() => {
    getAirports()
      .then((data) => {
        setAirports(data)
        if (data.length >= 2) {
          setOrigin(data.find((a) => a.code === 'DXB')?.code ?? data[0].code)
          setDestination(data.find((a) => a.code !== 'DXB')?.code ?? data[1].code)
        }
      })
      .catch(() => setAirports([]))
  }, [])

  function swapAirports() {
    setOrigin(destination)
    setDestination(origin)
  }

  function handleSubmit(e) {
    e.preventDefault()
    if (origin === destination) {
      setError('Origin and destination cannot be the same.')
      return
    }
    if (!date) {
      setError('Please choose a departure date.')
      return
    }
    if (tripType === 'ROUND_TRIP' && !returnDate) {
      setError('Please choose a return date.')
      return
    }
    setError('')

    const params = new URLSearchParams({
      tripType,
      origin,
      destination,
      date,
      passengers: String(passengers),
      cabinClass,
    })
    if (tripType === 'ROUND_TRIP') {
      params.set('returnDate', returnDate)
    }
    navigate(`/flights?${params.toString()}`)
  }

  const today = todayIso()

  return (
    <>
      <section className="hero">
        <div className="hero-inner">
          <svg className="hero-plane" viewBox="0 0 64 64" fill="none">
            <path d="M32 8 L37 27 L58 34 L58 39 L37 35 L34 50 L41 55 L41 58 L32 56 L23 58 L23 55 L30 50 L27 35 L6 39 L6 34 L27 27 Z" fill="#C9A961" />
          </svg>
          <span className="eyebrow">Fly the Al Noor way</span>
          <h1>Where the world meets Dubai</h1>
          <p className="hero-subtitle">Search, book, and manage your flights across our network of premium global routes.</p>
        </div>
      </section>

      <div className="page" style={{ paddingTop: 0 }}>
        <form className="search-panel" onSubmit={handleSubmit}>
          <div className="trip-toggle">
            <button type="button" className={tripType === 'ROUND_TRIP' ? 'active' : ''} onClick={() => setTripType('ROUND_TRIP')}>
              Round trip
            </button>
            <button type="button" className={tripType === 'ONE_WAY' ? 'active' : ''} onClick={() => setTripType('ONE_WAY')}>
              One way
            </button>
          </div>

          {error && <div className="error-banner">{error}</div>}

          <div className="search-grid">
            <div className="field">
              <label htmlFor="origin">From</label>
              <select id="origin" value={origin} onChange={(e) => setOrigin(e.target.value)}>
                {airports.map((a) => (
                  <option key={a.code} value={a.code}>{a.city} ({a.code})</option>
                ))}
              </select>
            </div>

            <div className="field">
              <label htmlFor="destination">To</label>
              <select id="destination" value={destination} onChange={(e) => setDestination(e.target.value)}>
                {airports.map((a) => (
                  <option key={a.code} value={a.code}>{a.city} ({a.code})</option>
                ))}
              </select>
            </div>

            <div className="field">
              <label htmlFor="date">Depart</label>
              <input id="date" type="date" min={today} value={date} onChange={(e) => setDate(e.target.value)} required />
            </div>

            <div className="field">
              <label htmlFor="returnDate">Return</label>
              <input
                id="returnDate"
                type="date"
                min={date || today}
                value={returnDate}
                disabled={tripType !== 'ROUND_TRIP'}
                onChange={(e) => setReturnDate(e.target.value)}
                required={tripType === 'ROUND_TRIP'}
              />
            </div>

            <div className="field">
              <label htmlFor="passengers">Passengers</label>
              <select id="passengers" value={passengers} onChange={(e) => setPassengers(Number(e.target.value))}>
                {Array.from({ length: 9 }, (_, i) => i + 1).map((n) => (
                  <option key={n} value={n}>{n} {n === 1 ? 'passenger' : 'passengers'}</option>
                ))}
              </select>
            </div>

            <div className="field">
              <label htmlFor="cabinClass">Cabin</label>
              <select id="cabinClass" value={cabinClass} onChange={(e) => setCabinClass(e.target.value)}>
                <option value="ECONOMY">Economy</option>
                <option value="BUSINESS">Business</option>
                <option value="FIRST">First Class</option>
              </select>
            </div>

            <div className="search-actions">
              <button type="button" className="btn btn-outline" onClick={swapAirports} style={{ marginRight: 'auto' }}>
                Swap
              </button>
              <button type="submit" className="btn btn-primary">Search flights</button>
            </div>
          </div>
        </form>
      </div>
    </>
  )
}
