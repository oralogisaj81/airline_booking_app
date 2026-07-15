const BASE = '/api'

async function request(path, options) {
  const res = await fetch(`${BASE}${path}`, {
    headers: { 'Content-Type': 'application/json' },
    ...options,
  })
  if (!res.ok) {
    const body = await res.json().catch(() => ({}))
    throw new Error(body.error || `Request failed with status ${res.status}`)
  }
  const text = await res.text()
  return text ? JSON.parse(text) : null
}

export function getAirports() {
  return request('/airports')
}

export function searchFlights({ origin, destination, date }) {
  const params = new URLSearchParams({ origin, destination, date })
  return request(`/flights/search?${params.toString()}`)
}

export function getFlight(id) {
  return request(`/flights/${id}`)
}

export function createBooking(input) {
  return request('/bookings', { method: 'POST', body: JSON.stringify(input) })
}

export function getMyBookings() {
  return request('/bookings')
}

export function getBooking(id) {
  return request(`/bookings/${id}`)
}

export function cancelBooking(id) {
  return request(`/bookings/${id}/cancel`, { method: 'PATCH' })
}
