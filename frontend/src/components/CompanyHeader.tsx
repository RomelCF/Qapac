import { Link } from 'react-router-dom'
import { useEffect, useState } from 'react'
import UserAvatar from './UserAvatar'

export default function CompanyHeader() {
  const [open, setOpen] = useState(false)
  const [email, setEmail] = useState<string>('')
  useEffect(() => {
    const e = localStorage.getItem('userEmail') || ''
    setEmail(e)
  }, [])
  return (
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
            <button type="button" onClick={() => setOpen(v => !v)} aria-haspopup="menu" aria-expanded={open} className="rounded-full hover:border-primary hover:shadow-md">
              <UserAvatar size={40} />
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
  )
}
