const DATE_FMT = new Intl.DateTimeFormat('en-US', { weekday: 'short', day: 'numeric', month: 'short', year: 'numeric' })
const TIME_FMT = new Intl.DateTimeFormat('en-US', { hour: 'numeric', minute: '2-digit', hour12: true })
const CURRENCY_FMT = new Intl.NumberFormat('en-US', { style: 'currency', currency: 'USD', maximumFractionDigits: 0 })

// Flight departure/arrival times are naive local-to-the-airport values
// (no timezone offset), so parse them as plain wall-clock dates rather
// than routing through Date's UTC-assuming ISO parsing.
function parseNaive(dateTimeString) {
  return new Date(dateTimeString.replace(' ', 'T'))
}

export function formatDate(dateTimeString) {
  return DATE_FMT.format(parseNaive(dateTimeString))
}

export function formatTime(dateTimeString) {
  return TIME_FMT.format(parseNaive(dateTimeString))
}

export function formatDuration(totalMinutes) {
  const hours = Math.floor(totalMinutes / 60)
  const minutes = totalMinutes % 60
  return `${hours}h ${minutes}m`
}

export function formatCurrency(amount) {
  return CURRENCY_FMT.format(Number(amount))
}

export function todayIso() {
  const now = new Date()
  return `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, '0')}-${String(now.getDate()).padStart(2, '0')}`
}

export const CABIN_LABELS = {
  ECONOMY: 'Economy',
  BUSINESS: 'Business',
  FIRST: 'First Class',
}

export const PASSENGER_TYPE_LABELS = {
  ADULT: 'Adult',
  CHILD: 'Child (2-11)',
  INFANT: 'Infant (under 2)',
}

export const MEAL_LABELS = {
  NONE: 'No preference',
  VEGETARIAN: 'Vegetarian',
  VEGAN: 'Vegan',
  HALAL: 'Halal',
  KOSHER: 'Kosher',
  GLUTEN_FREE: 'Gluten-free',
  DIABETIC: 'Diabetic',
}
