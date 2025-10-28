import { Routes, Route, Navigate } from 'react-router-dom'
import Login from './pages/Login'
import Register from './pages/Register'
import RegisterCompany from './pages/RegisterCompany'
import RegisterCustomer from './pages/RegisterCustomer'
import RegisterStep3 from './pages/RegisterStep3'
import CustomerDashboard from './pages/CustomerDashboard'
import CompanyDashboard from './pages/CompanyDashboard'
import CompanyBuses from './pages/CompanyBuses'
import CompanyTrips from './pages/CompanyTrips'
import CompanyStats from './pages/CompanyStats'
import CompanyRoutes from './pages/CompanyRoutes'
import CompanySales from './pages/CompanySales'
import AdminDashboard from './pages/AdminDashboard'
import AdminUsers from './pages/AdminUsers'
import AdminBranches from './pages/AdminBranches'
import AdminStats from './pages/AdminStats'
import MyTickets from './pages/MyTickets'
import BuyTickets from './pages/BuyTickets'
import MyTransactions from './pages/MyTransactions'
import MyCards from './pages/MyCards'
import MyAccount from './pages/MyAccount'
import Cart from './pages/Cart'
import PaymentSelect from './pages/PaymentSelect'
import PaymentCards from './pages/PaymentCards'

export default function App() {
  return (
    <Routes>
      <Route path="/login" element={<Login />} />
      <Route path="/register" element={<Register />} />
      <Route path="/register/empresa" element={<RegisterCompany />} />
      <Route path="/register/cliente" element={<RegisterCustomer />} />
      <Route path="/register/paso-3" element={<RegisterStep3 />} />
      <Route path="/dashboard/cliente" element={<CustomerDashboard />} />
      <Route path="/dashboard/empresa" element={<CompanyDashboard />} />
      <Route path="/dashboard/empresa/buses" element={<CompanyBuses />} />
      <Route path="/dashboard/empresa/viajes" element={<CompanyTrips />} />
      <Route path="/dashboard/empresa/rutas" element={<CompanyRoutes />} />
      <Route path="/dashboard/empresa/ventas" element={<CompanySales />} />
      <Route path="/dashboard/empresa/estadisticas" element={<CompanyStats />} />
      <Route path="/dashboard/admin" element={<AdminDashboard />} />
      <Route path="/dashboard/admin/usuarios" element={<AdminUsers />} />
      <Route path="/dashboard/admin/sucursales" element={<AdminBranches />} />
      <Route path="/dashboard/admin/estadisticas" element={<AdminStats />} />
      <Route path="/dashboard/cliente/pasajes" element={<MyTickets />} />
      <Route path="/dashboard/cliente/comprar" element={<BuyTickets />} />
      <Route path="/dashboard/cliente/carrito" element={<Cart />} />
      <Route path="/dashboard/cliente/pago" element={<PaymentSelect />} />
      <Route path="/dashboard/cliente/pago/tarjetas/:brand" element={<PaymentCards />} />
      <Route path="/dashboard/cliente/movimientos" element={<MyTransactions />} />
      <Route path="/dashboard/cliente/tarjetas" element={<MyCards />} />
      <Route path="/micuenta" element={<MyAccount />} />
      <Route path="/" element={<Navigate to="/login" replace />} />
      <Route path="*" element={<Navigate to="/login" replace />} />
    </Routes>
  )
}
