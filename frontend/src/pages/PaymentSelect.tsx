import { Link, useNavigate } from 'react-router-dom'
import { useEffect, useState } from 'react'

type PM = {
  idMetodoPago: number
  nombre: string
  tipo?: string
  descripcion?: string
  estado?: string
  comision?: number
}

export default function PaymentSelect() {
  const navigate = useNavigate()
  const [methods, setMethods] = useState<PM[]>([])
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)
  const [open, setOpen] = useState(false)
  const [emailLabel, setEmailLabel] = useState('')
  const [selectedMethodId, setSelectedMethodId] = useState<number | null>(null)
  const [cards, setCards] = useState<{ idTarjeta: number; numeroMasked: string; fechaCaducidad?: string; marca?: string }[]>([])

  const API_BASE = (import.meta as unknown as { env: Record<string, string> }).env?.VITE_API_BASE_URL || 'http://localhost:8080'

  useEffect(() => {
    const load = async () => {
      setLoading(true)
      setError(null)
      try {
        const em = localStorage.getItem('userEmail') || ''
        setEmailLabel(em)
        const [res, profRes] = await Promise.all([
          fetch(`${API_BASE}/metodos-pago`),
          fetch(`${API_BASE}/auth/profile?userId=${localStorage.getItem('userId') || ''}`)
        ])
        if (!res.ok) { setError('No se pudieron cargar los métodos de pago'); return }
        const items = await res.json()
        const mapped: PM[] = (items || []).map((m: any) => ({
          idMetodoPago: m.idMetodoPago,
          nombre: m.nombre,
          tipo: m.tipo,
          descripcion: m.descripcion,
          estado: m.estado,
          comision: m.comision != null ? Number(m.comision) : undefined,
        }))
        setMethods(mapped)
        if (profRes.ok) {
          const p = await profRes.json()
          if (p?.tipo === 'cliente' && p?.idCliente) {
            const cardsRes = await fetch(`${API_BASE}/tarjetas/cliente/${p.idCliente}`)
            if (cardsRes.ok) {
              const cs = await cardsRes.json()
              setCards(cs || [])
            }
          }
        }
      } catch {
        setError('Error de red')
      } finally {
        setLoading(false)
      }
    }
    load()
  }, [])

  const filteredCards = (() => [])()

  return (
    <div className="min-h-screen bg-background-light text-text-primary">
      <header className="p-4 border-b border-border-soft bg-background-secondary">
        <div className="max-w-5xl mx-auto flex items-center justify-between gap-6">
          <div className="flex items-center gap-3">
            <Link to="/dashboard/cliente" aria-label="Inicio">
              <img src="/assets/logo.png" alt="Logo" className="h-16 md:h-20 w-auto" />
            </Link>
          </div>
          <nav className="flex items-center gap-4 text-sm">
            <Link to="/dashboard/cliente/pasajes" className="text-text-secondary hover:text-primary inline-flex items-center gap-1">
              <span className="material-symbols-outlined text-base">confirmation_number</span>
              Mis pasajes
            </Link>
            <Link to="/dashboard/cliente/comprar" className="text-text-secondary hover:text-primary inline-flex items-center gap-1">
              <span className="material-symbols-outlined text-base">directions_bus</span>
              Catalogo
            </Link>
            <Link to="/dashboard/cliente/movimientos" className="text-text-secondary hover:text-primary inline-flex items-center gap-1">
              <span className="material-symbols-outlined text-base">receipt_long</span>
              Movimientos
            </Link>
            <Link to="/dashboard/cliente/tarjetas" className="text-text-secondary hover:text-primary inline-flex items-center gap-1">
              <span className="material-symbols-outlined text-base">credit_card</span>
              Tarjetas
            </Link>
          </nav>
          <div className="flex items-center gap-3">
            <Link to="/dashboard/cliente/carrito" className="h-10 w-10 rounded-full border border-border-soft bg-white/50 flex items-center justify-center hover:border-primary hover:shadow-md" aria-label="Carrito">
              <span className="material-symbols-outlined">shopping_cart</span>
            </Link>
            <div className="relative">
              <button type="button" onClick={() => setOpen(v => !v)} aria-haspopup="menu" aria-expanded={open} className="h-10 w-10 rounded-full border border-border-soft bg-white/50 flex items-center justify-center hover:border-primary hover:shadow-md">
                <span className="material-symbols-outlined">person</span>
              </button>
              {open && (
                <div className="absolute right-0 mt-2 w-56 rounded-lg border border-border-soft bg-white shadow-xl p-3 z-20">
                  <div className="text-sm text-text-secondary mb-2 truncate" title={emailLabel}>{emailLabel || 'Usuario'}</div>
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

      <div className="p-6">
        <h1 className="font-display text-2xl text-primary mb-4 text-center">Selecciona un método de pago</h1>
        {loading && <div className="text-text-secondary">Cargando...</div>}
        {error && (
          <div className="flex justify-center my-6">
            <div className="inline-flex items-center gap-2 text-red-700 text-xl md:text-2xl">
              <span className="material-symbols-outlined">wifi_off</span>
              <span>{error}</span>
            </div>
          </div>
        )}
        {!loading && !error && (
          methods.length === 0 ? (
            <div className="text-text-secondary">No hay métodos de pago disponibles.</div>
          ) : (
            <>
              <div className="grid md:grid-cols-2 gap-4 max-w-5xl mx-auto">
                {methods.map(m => (
                  <div key={m.idMetodoPago} className="rounded-xl border border-border-soft bg-background-secondary p-4">
                    <div className="flex items-center justify-between">
                      <div className="font-bold text-lg">{m.nombre}</div>
                      <div className="text-xs px-2 py-0.5 rounded-md bg-white/70 text-text-secondary">{m.tipo}</div>
                    </div>
                    {m.descripcion && <div className="text-sm text-text-secondary mt-2">{m.descripcion}</div>}
                    <div className="mt-3 text-sm text-text-secondary flex items-center gap-2">
                      <span className="material-symbols-outlined text-base">payments</span>
                      <span>Comisión: {m.comision != null ? `${m.comision}%` : '0%'}</span>
                    </div>
                    <div className="mt-4 flex justify-end">
                      <button onClick={() => {
                        const brand = (m.nombre || '').toLowerCase()
                        navigate(`/dashboard/cliente/pago/tarjetas/${brand}`)
                      }} className="px-4 py-2 rounded-lg bg-primary text-white hover:opacity-90">Usar este método</button>
                    </div>
                  </div>
                ))}
              </div>
              
            </>
          )
        )}
      </div>
    </div>
  )
}
