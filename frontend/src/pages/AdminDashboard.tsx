import { Link } from 'react-router-dom'
import { useEffect, useState } from 'react'
import { useTheme } from '../hooks/useTheme'
import UserAvatar from '../components/UserAvatar'

export default function AdminDashboard() {
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
            <Link to="/dashboard/admin" aria-label="Inicio">
              <img src="/assets/logo.png" alt="Logo" className="h-16 md:h-20 w-auto" />
            </Link>
          </div>
          <nav className="flex items-center gap-4 text-sm">
            <Link to="/dashboard/admin/usuarios" className="text-text-secondary hover:text-primary inline-flex items-center gap-1">
              <span className="material-symbols-outlined text-base">group</span>
              Usuarios
            </Link>
            <Link to="/dashboard/admin/empleados" className="text-text-secondary hover:text-primary inline-flex items-center gap-1">
              <span className="material-symbols-outlined text-base">badge</span>
              Empleados
            </Link>
            <Link to="/dashboard/admin/sucursales" className="text-text-secondary hover:text-primary inline-flex items-center gap-1">
              <span className="material-symbols-outlined text-base">apartment</span>
              Sucursales
            </Link>
            <Link to="/dashboard/admin/estadisticas" className="text-text-secondary hover:text-primary inline-flex items-center gap-1">
              <span className="material-symbols-outlined text-base">monitoring</span>
              Estadísticas
            </Link>
          </nav>
          <div className="flex items-center gap-3">
            <div className="relative">
              <button type="button" onClick={() => setOpen(v => !v)} aria-haspopup="menu" aria-expanded={open} className="h-10 w-10 rounded-full border border-border-soft bg-white/50 flex items-center justify-center hover:border-primary hover:shadow-md">
                <UserAvatar size={40} />
              </button>
              {open && (
                <div className="absolute right-0 mt-2 w-56 rounded-lg border border-border-soft bg-white shadow-xl p-3 z-20">
                  <div className="text-sm text-text-secondary mb-2 truncate" title={email}>{email || 'Administrador'}</div>
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
          <h1 className="font-display text-3xl text-primary text-center">Panel de Administración</h1>
        </div>
        <div className="w-full max-w-5xl">
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
            <Link to="/dashboard/admin/usuarios" className="flex flex-col items-center justify-center gap-4 rounded-xl border border-border-soft bg-white/50 p-8 text-center transition-all duration-300 hover:shadow-xl hover:border-primary/50">
              <div className="text-primary">
                <span className="material-symbols-outlined" style={{ fontSize: 64 }} aria-hidden>
                  group
                </span>
              </div>
              <div className="flex flex-col gap-1">
                <h2 className="text-xl font-bold leading-tight">Usuarios</h2>
                <p className="text-text-secondary text-sm">Gestiona cuentas y roles.</p>
              </div>
            </Link>

            <Link to="/dashboard/admin/empleados" className="flex flex-col items-center justify-center gap-4 rounded-xl border border-border-soft bg-white/50 p-8 text-center transition-all duration-300 hover:shadow-xl hover:border-primary/50">
              <div className="text-primary">
                <span className="material-symbols-outlined" style={{ fontSize: 64 }} aria-hidden>
                  badge
                </span>
              </div>
              <div className="flex flex-col gap-1">
                <h2 className="text-xl font-bold leading-tight">Empleados</h2>
                <p className="text-text-secondary text-sm">Administra empleados del sistema.</p>
              </div>
            </Link>

            <Link to="/dashboard/admin/sucursales" className="flex flex-col items-center justify-center gap-4 rounded-xl border border-border-soft bg-white/50 p-8 text-center transition-all duration-300 hover:shadow-xl hover:border-primary/50">
              <div className="text-primary">
                <span className="material-symbols-outlined" style={{ fontSize: 64 }} aria-hidden>
                  apartment
                </span>
              </div>
              <div className="flex flex-col gap-1">
                <h2 className="text-xl font-bold leading-tight">Sucursales</h2>
                <p className="text-text-secondary text-sm">Administra sucursales y ubicaciones.</p>
              </div>
            </Link>

            <Link to="/dashboard/admin/estadisticas" className="flex flex-col items-center justify-center gap-4 rounded-xl border border-border-soft bg-white/50 p-8 text-center transition-all duration-300 hover:shadow-xl hover:border-primary/50">
              <div className="text-primary">
                <span className="material-symbols-outlined" style={{ fontSize: 64 }} aria-hidden>
                  monitoring
                </span>
              </div>
              <div className="flex flex-col gap-1">
                <h2 className="text-xl font-bold leading-tight">Estadísticas</h2>
                <p className="text-text-secondary text-sm">Visión general del sistema.</p>
              </div>
            </Link>
          </div>
        </div>
      </main>
    </div>
  )
}
