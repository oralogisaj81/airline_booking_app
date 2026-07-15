import { Route, Routes } from 'react-router-dom'
import Navbar from './components/Navbar'
import RequireAuth from './components/RequireAuth'
import HomePage from './pages/HomePage'
import SearchResultsPage from './pages/SearchResultsPage'
import CheckoutPage from './pages/CheckoutPage'
import BookingReceiptPage from './pages/BookingReceiptPage'
import MyBookingsPage from './pages/MyBookingsPage'
import SignInPage from './pages/SignInPage'
import SignUpPage from './pages/SignUpPage'

function App() {
  return (
    <>
      <Navbar />
      <main className="app-main">
        <Routes>
          <Route path="/" element={<HomePage />} />
          <Route path="/flights" element={<SearchResultsPage />} />
          <Route path="/sign-in" element={<SignInPage />} />
          <Route path="/sign-up" element={<SignUpPage />} />
          <Route
            path="/checkout"
            element={(
              <RequireAuth>
                <CheckoutPage />
              </RequireAuth>
            )}
          />
          <Route
            path="/bookings"
            element={(
              <RequireAuth>
                <MyBookingsPage />
              </RequireAuth>
            )}
          />
          <Route
            path="/bookings/:id"
            element={(
              <RequireAuth>
                <BookingReceiptPage />
              </RequireAuth>
            )}
          />
        </Routes>
      </main>
      <footer className="footer">
        Al Noor Airways &middot; A fictional airline built for demonstration purposes.
      </footer>
    </>
  )
}

export default App
