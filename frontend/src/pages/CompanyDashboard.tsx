import { Link } from 'react-router-dom'
import { useEffect, useState } from 'react'
import { useTheme } from '../hooks/useTheme'

export default function CompanyDashboard() {
  useTheme()
  const [open, setOpen] = useState(false)
  const [email, setEmail] = useState<string>('')
  useEffect(() => {
    const e = localStorage.getItem('userEmail') || ''
    setEmail(e)
  }, [])
  return (
    <div className="min-h-screen bg-background-light text-text-primary">
      <header className="p-4 border-b border-border-soft bg-background-secondary">
        <div className="max-w-5xl mx-auto flex items-center justify-between gap-6">
          <div className="flex items-center gap-3">
            <Link to="/dashboard/empresa" aria-label="Inicio">
              <img src="/assets/logo.png" alt="Logo" className="h-16 md:h-20 w-auto" />
            </Link>
          </div>
          <nav className="flex items-center gap-4 text-sm">
            <Link to="/dashboard/empresa/buses" className="text-text-secondary hover:text-primary inline-flex items-center gap-1">
              <span className="material-symbols-outlined text-base">directions_bus</span>
              Buses
            </Link>
            <Link to="/dashboard/empresa/viajes" className="text-text-secondary hover:text-primary inline-flex items-center gap-1">
              <span className="material-symbols-outlined text-base">route</span>
              Viajes
            </Link>
            <Link to="/dashboard/empresa/rutas" className="text-text-secondary hover:text-primary inline-flex items-center gap-1">
              <span className="material-symbols-outlined text-base">signpost</span>
              Rutas
            </Link>
            <Link to="/dashboard/empresa/ventas" className="text-text-secondary hover:text-primary inline-flex items-center gap-1">
              <span className="material-symbols-outlined text-base">point_of_sale</span>
              Ventas
            </Link>
            <Link to="/dashboard/empresa/estadisticas" className="text-text-secondary hover:text-primary inline-flex items-center gap-1">
              <span className="material-symbols-outlined text-base">monitoring</span>
              Estadísticas
            </Link>
          </nav>
          <div className="flex items-center gap-3">
            <div className="relative">
              <button type="button" onClick={() => setOpen(v => !v)} aria-haspopup="menu" aria-expanded={open} className="h-10 w-10 rounded-full border border-border-soft bg-white/50 flex items-center justify-center hover:border-primary hover:shadow-md">
                <span className="material-symbols-outlined">business</span>
              </button>
              {open && (
                <div className="absolute right-0 mt-2 w-56 rounded-lg border border-border-soft bg-white shadow-xl p-3 z-20">
                  <div className="text-sm text-text-secondary mb-2 truncate" title={email}>{email || 'Empresa'}</div>
                  <div className="flex flex-col gap-1">
                    <Link to="/micuenta" className="px-3 py-2 rounded-md hover:bg-background-light text-text-primary">Mi cuenta</Link>
                    <Link to="/login" className="px-3 py-2 rounded-md hover:bg-background-light text-red-600">Cerrar sesión</Link>
                  </div>
                </div>
              )}
            </div>
          </div>
        </div>
      </header>

      <main className="flex-1 flex flex-col items-center py-10 px-4">
        <div className="w-full max-w-5xl mb-6">
          <h1 className="font-display text-3xl text-primary text-center">Panel de Empresa</h1>
        </div>
        <div className="w-full max-w-5xl">
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-5 gap-6">
            <Link to="/dashboard/empresa/buses" className="flex flex-col items-center justify-center gap-4 rounded-xl border border-border-soft bg-white/50 p-8 text-center transition-all duration-300 hover:shadow-xl hover:border-primary/50">
              <div className="text-primary">
                <span className="material-symbols-outlined" style={{ fontSize: 64 }} aria-hidden>
                  directions_bus
                </span>
              </div>
              <div className="flex flex-col gap-1">
                <h2 className="text-xl font-bold leading-tight">Buses</h2>
                <p className="text-text-secondary text-sm">Administra tu flota y unidades.</p>
              </div>
            </Link>

            <Link to="/dashboard/empresa/viajes" className="flex flex-col items-center justify-center gap-4 rounded-xl border border-border-soft bg-white/50 p-8 text-center transition-all duration-300 hover:shadow-xl hover:border-primary/50">
              <div className="text-primary">
                <span className="material-symbols-outlined" style={{ fontSize: 64 }} aria-hidden>
                  route
                </span>
              </div>
              <div className="flex flex-col gap-1">
                <h2 className="text-xl font-bold leading-tight">Viajes</h2>
                <p className="text-text-secondary text-sm">Crea y gestiona rutas y horarios.</p>
              </div>
            </Link>

            <Link to="/dashboard/empresa/rutas" className="flex flex-col items-center justify-center gap-4 rounded-xl border border-border-soft bg-white/50 p-8 text-center transition-all duration-300 hover:shadow-xl hover:border-primary/50">
              <div className="text-primary">
                <span className="material-symbols-outlined" style={{ fontSize: 64 }} aria-hidden>
                  signpost
                </span>
              </div>
              <div className="flex flex-col gap-1">
                <h2 className="text-xl font-bold leading-tight">Rutas</h2>
                <p className="text-text-secondary text-sm">Gestiona rutas (origen/destino, precios).</p>
              </div>
            </Link>

            <Link to="/dashboard/empresa/ventas" className="flex flex-col items-center justify-center gap-4 rounded-xl border border-border-soft bg-white/50 p-8 text-center transition-all duration-300 hover:shadow-xl hover:border-primary/50">
              <div className="text-primary">
                <span className="material-symbols-outlined" style={{ fontSize: 64 }} aria-hidden>
                  point_of_sale
                </span>
              </div>
              <div className="flex flex-col gap-1">
                <h2 className="text-xl font-bold leading-tight">Ventas</h2>
                <p className="text-text-secondary text-sm">Consulta ventas y reportes.</p>
              </div>
            </Link>

            <Link to="/dashboard/empresa/estadisticas" className="flex flex-col items-center justify-center gap-4 rounded-xl border border-border-soft bg-white/50 p-8 text-center transition-all duration-300 hover:shadow-xl hover:border-primary/50">
              <div className="text-primary">
                <span className="material-symbols-outlined" style={{ fontSize: 64 }} aria-hidden>
                  monitoring
                </span>
              </div>
              <div className="flex flex-col gap-1">
                <h2 className="text-xl font-bold leading-tight">Estadísticas</h2>
                <p className="text-text-secondary text-sm">Analiza ventas y ocupación.</p>
              </div>
            </Link>
          </div>
        </div>
      </main>
    </div>
  )
}
