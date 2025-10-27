import { Link, useNavigate, useParams } from 'react-router-dom'
import { useEffect, useMemo, useState } from 'react'

type CardItem = {
  idTarjeta: number
  numeroMasked: string
  fechaCaducidad?: string
  marca?: string
  metodoPago?: string
}

export default function PaymentCards() {
  const { brand } = useParams()
  const navigate = useNavigate()
  const [cards, setCards] = useState<CardItem[]>([])
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)
  const [open, setOpen] = useState(false)
  const [emailLabel, setEmailLabel] = useState('')
  const [methods, setMethods] = useState<{ idMetodoPago: number; nombre: string; comision?: number }[]>([])
  const [cartRows, setCartRows] = useState<{ precio?: number }[]>([])
  const [confirmData, setConfirmData] = useState<{ cardId: number; bruto: number; comision: number; neto: number; metodoId: number } | null>(null)
  const [toast, setToast] = useState<{ type: 'success' | 'error'; message: string } | null>(null)

  const API_BASE = (import.meta as unknown as { env: Record<string, string> }).env?.VITE_API_BASE_URL || 'http://localhost:8080'

  useEffect(() => {
    const load = async () => {
      setLoading(true)
      setError(null)
      try {
        const em = localStorage.getItem('userEmail') || ''
        setEmailLabel(em)
        const userId = localStorage.getItem('userId')
        if (!userId) { setError('No hay sesión'); return }
        const prof = await fetch(`${API_BASE}/auth/profile?userId=${userId}`)
        if (!prof.ok) { setError('No se pudo obtener el perfil'); return }
        const p = await prof.json()
        if (p?.tipo !== 'cliente' || !p?.idCliente) { setCards([]); return }
        const [cardsRes, methRes, cartsRes] = await Promise.all([
          fetch(`${API_BASE}/tarjetas/cliente/${p.idCliente}`),
          fetch(`${API_BASE}/metodos-pago`),
          fetch(`${API_BASE}/carritos/cliente/${p.idCliente}`),
        ])
        if (!cardsRes.ok) { setError('No se pudieron cargar tus tarjetas'); return }
        const items = await cardsRes.json()
        setCards(items || [])
        if (methRes.ok) {
          const m = await methRes.json()
          setMethods((m || []).map((x: any) => ({ idMetodoPago: x.idMetodoPago, nombre: x.nombre, comision: x.comision != null ? Number(x.comision) : 0 })))
        }
        if (cartsRes.ok) {
          const c = await cartsRes.json()
          setCartRows(c || [])
        }
      } catch {
        setError('Error de red')
      } finally {
        setLoading(false)
      }
    }
    load()
  }, [])

  useEffect(() => {
    if (!toast) return
    const t = setTimeout(() => setToast(null), 3000)
    return () => clearTimeout(t)
  }, [toast])

  const brandName = useMemo(() => {
    const b = (brand || '').toString().toLowerCase()
    if (b === 'visa') return 'Visa'
    if (b === 'mastercard') return 'Mastercard'
    return brand || ''
  }, [brand])

  const filtered = useMemo(() => {
    const b = (brand || '').toString().toLowerCase()
    return cards.filter(c => (c.metodoPago || '').toLowerCase().includes(b))
  }, [cards, brand])

  function openConfirm(cardId: number) {
    const b = (brand || '').toString().toLowerCase()
    const method = methods.find(m => (m.nombre || '').toLowerCase().includes(b))
    const bruto = (cartRows || []).reduce((sum, r) => sum + (r.precio != null ? Number(r.precio) : 0), 0)
    const comision = method?.comision != null ? Number(method.comision) : 0
    const neto = bruto * (1 + comision / 100)
    if (method) setConfirmData({ cardId, bruto, comision, neto, metodoId: method.idMetodoPago })
  }

  async function confirmPayment() {
    try {
      if (!confirmData) return
      const userId = localStorage.getItem('userId')
      if (!userId) { setToast({ type: 'error', message: 'No hay sesión' }); return }
      const prof = await fetch(`${API_BASE}/auth/profile?userId=${userId}`)
      if (!prof.ok) { setToast({ type: 'error', message: 'No se pudo obtener perfil' }); return }
      const p = await prof.json()
      if (p?.tipo !== 'cliente' || !p?.idCliente) { setToast({ type: 'error', message: 'No es cliente' }); return }
      const res = await fetch(`${API_BASE}/metodos-pago/confirm`, {
        method: 'POST', headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ idCliente: p.idCliente, idMetodoPago: confirmData.metodoId, idTarjeta: confirmData.cardId })
      })
      if (res.ok) {
        setToast({ type: 'success', message: 'Compra realizada con éxito' })
        setConfirmData(null)
        setTimeout(() => navigate('/dashboard/cliente'), 800)
      } else {
        const msg = await res.text(); setToast({ type: 'error', message: msg || 'No se pudo completar el pago' })
      }
    } catch {
      setToast({ type: 'error', message: 'Error de red al confirmar pago' })
    }
  }

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
        <h1 className="font-display text-2xl text-primary mb-6 text-center">Tarjetas {brandName}</h1>
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
          filtered.length === 0 ? (
            <div className="flex items-center justify-between max-w-6xl mx-auto">
              <div className="text-text-secondary">No tienes tarjetas {brandName} registradas.</div>
              <button onClick={() => navigate('/dashboard/cliente/tarjetas')} className="px-4 py-2 rounded-lg border border-border-soft hover:border-primary inline-flex items-center gap-2">
                <span className="material-symbols-outlined">add_card</span>
                Ir a Tarjetas
              </button>
            </div>
          ) : (
            <div className="grid sm:grid-cols-2 lg:grid-cols-3 gap-6 max-w-6xl mx-auto">
              {filtered.map(c => (
                <div key={c.idTarjeta} className="rounded-xl border border-border-soft bg-background-secondary p-5 shadow-sm hover:shadow-md transition-shadow">
                  <div className="flex items-center justify-between">
                    <div className="flex items-center gap-3">
                      <span className="material-symbols-outlined text-3xl text-primary">credit_card</span>
                      <div className="font-bold">{c.metodoPago || brandName}</div>
                    </div>
                    {c.metodoPago && (
                      <span className="text-xs px-2 py-0.5 rounded-md bg-white/70 text-text-secondary">{c.metodoPago}</span>
                    )}
                  </div>
                  <div className="mt-4 text-lg tracking-widest">{c.numeroMasked}</div>
                  <div className="mt-2 text-sm text-text-secondary">Vence: {c.fechaCaducidad || '-'}</div>
                  <div className="mt-4 flex justify-end">
                    <button onClick={() => openConfirm(c.idTarjeta)} className="px-4 py-2 rounded-lg bg-primary text-white hover:opacity-90">Usar esta tarjeta</button>
                  </div>
                </div>
              ))}
            </div>
          )
        )}
      </div>
      {confirmData && (
        <div className="fixed inset-0 bg-black/30 backdrop-blur-sm flex items-center justify-center p-4 z-50" role="dialog" aria-modal="true">
          <div className="w-full max-w-md rounded-xl bg-background-secondary border-2 border-border-soft shadow-2xl">
            <div className="flex items-center justify-between p-4 border-b border-border-soft">
              <h2 className="font-display text-xl">Confirmar pago</h2>
              <button onClick={() => setConfirmData(null)} className="text-text-secondary hover:text-primary" aria-label="Cerrar">
                <span className="material-symbols-outlined">close</span>
              </button>
            </div>
            <div className="p-4 text-sm">
              <div className="rounded-lg border border-border-soft bg-white/50 p-3">
                <div className="flex justify-between"><span>Total bruto:</span><span>S/ {confirmData.bruto.toFixed(2)}</span></div>
                <div className="flex justify-between"><span>Comisión ({confirmData.comision}%):</span><span>S/ {(confirmData.bruto * (confirmData.comision/100)).toFixed(2)}</span></div>
                <div className="flex justify-between font-bold mt-2"><span>Total a pagar:</span><span>S/ {confirmData.neto.toFixed(2)}</span></div>
              </div>
            </div>
            <div className="p-4 border-t border-border-soft flex justify-end gap-2">
              <button onClick={() => setConfirmData(null)} className="px-4 py-2 rounded-lg border border-border-soft hover:border-primary">Cancelar</button>
              <button onClick={confirmPayment} className="px-4 py-2 rounded-lg bg-primary text-white hover:opacity-90">Confirmar pago</button>
            </div>
          </div>
        </div>
      )}
      {toast && (
        <div className="fixed bottom-4 right-4 z-[60]">
          <div className={`min-w-[260px] max-w-sm rounded-xl border-2 shadow-2xl p-3 flex items-start gap-2 ${toast.type === 'success' ? 'bg-background-secondary border-border-soft' : 'bg-white border-red-200'}`}>
            <span className={`material-symbols-outlined ${toast.type === 'success' ? 'text-green-600' : 'text-red-600'}`}>
              {toast.type === 'success' ? 'check_circle' : 'error' }
            </span>
            <div className="flex-1 text-sm text-text-primary">{toast.message}</div>
            <button onClick={() => setToast(null)} className="text-text-secondary hover:text-primary" aria-label="Cerrar">
              <span className="material-symbols-outlined text-base">close</span>
            </button>
          </div>
        </div>
      )}
    </div>
  )
}
