import { Link } from 'react-router-dom'
import { useEffect, useState } from 'react'
import CompanyHeader from '../components/CompanyHeader'
import { useTheme } from '../hooks/useTheme'

export default function CompanyBuses() {
  useTheme()
  const [open, setOpen] = useState(false)
  const [email, setEmail] = useState<string>('')
  const [empresaId, setEmpresaId] = useState<number | null>(null)
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)
  const [items, setItems] = useState<Array<{ idBus:number; matricula:string; capacidad:number; estado:string }>>([])
  const [q, setQ] = useState('')
  const [estadoFilter, setEstadoFilter] = useState<string>('')
  const [showForm, setShowForm] = useState(false)
  const [editing, setEditing] = useState<null | { idBus:number; matricula:string; capacidad:number; estado:string }>(null)
  const [matricula, setMatricula] = useState('')
  const [capacidad, setCapacidad] = useState<number | ''>('')
  const [estado, setEstado] = useState('disponible')
  const [saving, setSaving] = useState(false)
  const API_BASE = (import.meta as unknown as { env: Record<string, string> }).env?.VITE_API_BASE_URL || 'http://localhost:8080'

  useEffect(() => {
    const e = localStorage.getItem('userEmail') || ''
    setEmail(e)
    ;(async () => {
      // obtener empresa del usuario actual
      const uid = localStorage.getItem('userId')
      if (!uid) return
      try {
        const profRes = await fetch(`${API_BASE}/auth/profile?userId=${uid}`)
        if (profRes.ok) {
          const prof = await profRes.json()
          if (prof?.idEmpresa) {
            setEmpresaId(Number(prof.idEmpresa))
          }
        }
      } catch {}
    })()
  }, [])

  useEffect(() => {
    if (!empresaId) return
    void loadBuses()
  // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [empresaId])

  async function loadBuses() {
    if (!empresaId) return
    setLoading(true)
    setError(null)
    try {
      const res = await fetch(`${API_BASE}/buses?empresaId=${empresaId}`)
      if (!res.ok) throw new Error('No se pudo cargar buses')
      const data = await res.json()
      setItems(data as any)
    } catch (e:any) {
      setError(e.message || 'Error de red')
    } finally {
      setLoading(false)
    }
  }

  function filteredItems() {
    return items.filter(it => {
      const okQ = q ? it.matricula.toLowerCase().includes(q.toLowerCase()) : true
      const okE = estadoFilter ? it.estado === estadoFilter : true
      return okQ && okE
    })
  }

  function onNew() {
    setEditing(null)
    setMatricula('')
    setCapacidad('')
    setEstado('disponible')
    setShowForm(true)
  }

  function onEdit(it: { idBus:number; matricula:string; capacidad:number; estado:string }) {
    setEditing(it)
    setMatricula(it.matricula)
    setCapacidad(it.capacidad)
    setEstado(it.estado)
    setShowForm(true)
  }

  async function onDelete(it: { idBus:number; matricula:string }) {
    if (!confirm(`¿Eliminar bus ${it.matricula}?`)) return
    try {
      const res = await fetch(`${API_BASE}/buses/${it.idBus}`, { method: 'DELETE' })
      if (!res.ok) throw new Error('No se pudo eliminar')
      await loadBuses()
    } catch (e:any) {
      alert(e.message || 'Error al eliminar')
    }
  }

  async function onSubmit(e: React.FormEvent) {
    e.preventDefault()
    if (!empresaId) return
    if (!matricula || !capacidad || capacidad <= 0) { alert('Complete matrícula y capacidad válida'); return }
    setSaving(true)
    try {
      const body = editing
        ? { matricula, capacidad: Number(capacidad), estado }
        : { matricula, capacidad: Number(capacidad), estado, idEmpresa: empresaId }
      const method = editing ? 'PUT' : 'POST'
      const url = editing ? `${API_BASE}/buses/${editing.idBus}` : `${API_BASE}/buses`
      const res = await fetch(url, { method, headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(body) })
      if (!res.ok) throw new Error('No se pudo guardar')
      setShowForm(false)
      await loadBuses()
    } catch (e:any) {
      alert(e.message || 'Error al guardar')
    } finally {
      setSaving(false)
    }
  }
 
  return (
    <div className="min-h-screen bg-background-light text-text-primary">
      <CompanyHeader />
      <main className="flex-1 flex flex-col items-center py-10 px-4">
        <div className="w-full max-w-5xl mb-6 flex items-center justify-between gap-4">
          <h1 className="font-display text-3xl text-primary">Buses</h1>
          <button onClick={onNew} className="px-4 py-2 rounded-lg bg-primary text-background-light font-bold hover:bg-accent">Añadir bus</button>
        </div>
        <div className="w-full max-w-5xl mb-4 grid grid-cols-1 md:grid-cols-3 gap-3">
          <input value={q} onChange={e=>setQ(e.target.value)} placeholder="Buscar por matrícula" className="w-full rounded-lg border border-border-soft bg-white/70 px-4 py-2 outline-none focus:border-primary" />
          <select value={estadoFilter} onChange={e=>setEstadoFilter(e.target.value)} className="w-full rounded-lg border border-border-soft bg-white/70 px-4 py-2 outline-none focus:border-primary">
            <option value="">Todos los estados</option>
            <option value="disponible">Disponible</option>
            <option value="en_ruta">En ruta</option>
            <option value="mantenimiento">Mantenimiento</option>
            <option value="inactivo">Inactivo</option>
          </select>
          <button onClick={loadBuses} className="px-4 py-2 rounded-lg border border-border-soft hover:border-primary">Refrescar</button>
        </div>

        {error && <div className="w-full max-w-5xl text-sm text-red-700 mb-3">{error}</div>}
        {loading ? (
          <div className="w-full max-w-5xl text-text-secondary">Cargando...</div>
        ) : (
          <div className="w-full max-w-5xl overflow-x-auto rounded-xl border border-border-soft bg-white/50">
            <table className="min-w-full text-sm">
              <thead className="bg-background-secondary/60">
                <tr>
                  <th className="text-left p-3">Matrícula</th>
                  <th className="text-left p-3">Capacidad</th>
                  <th className="text-left p-3">Estado</th>
                  <th className="text-left p-3">Acciones</th>
                </tr>
              </thead>
              <tbody>
                {filteredItems().map(it => (
                  <tr key={it.idBus} className="border-t border-border-soft">
                    <td className="p-3">{it.matricula}</td>
                    <td className="p-3">{it.capacidad}</td>
                    <td className="p-3 capitalize">{it.estado.replace('_',' ')}</td>
                    <td className="p-3 flex gap-2">
                      <button onClick={()=>onEdit(it)} className="px-3 py-1 rounded-md border border-border-soft hover:border-primary text-xs inline-flex items-center gap-1">
                        <span className="material-symbols-outlined text-base">edit</span>
                        Editar
                      </button>
                      <button onClick={()=>onDelete(it)} className="px-3 py-1 rounded-md border border-border-soft hover:border-primary text-xs inline-flex items-center gap-1 text-red-600">
                        <span className="material-symbols-outlined text-base">delete</span>
                        Eliminar
                      </button>
                    </td>
                  </tr>
                ))}
                {filteredItems().length === 0 && (
                  <tr>
                    <td className="p-4 text-text-secondary" colSpan={4}>No hay buses para mostrar</td>
                  </tr>
                )}
              </tbody>
            </table>
          </div>
        )}

        {showForm && (
          <div className="fixed inset-0 bg-black/40 flex items-center justify-center z-50">
            <div className="w-full max-w-md rounded-xl border border-border-soft bg-white p-6 shadow-2xl">
              <div className="flex items-center justify-between mb-4">
                <h2 className="font-display text-xl">{editing ? 'Editar bus' : 'Nuevo bus'}</h2>
                <button onClick={()=>setShowForm(false)} className="text-text-secondary hover:text-primary">
                  <span className="material-symbols-outlined">close</span>
                </button>
              </div>
              <form onSubmit={onSubmit} className="grid gap-4">
                <div>
                  <label className="sr-only" htmlFor="mat">Matrícula</label>
                  <input id="mat" value={matricula} onChange={e=>setMatricula(e.target.value)} placeholder="Matrícula" className="w-full rounded-lg border border-border-soft bg-white/70 px-4 py-2 outline-none focus:border-primary" />
                </div>
                <div>
                  <label className="sr-only" htmlFor="cap">Capacidad</label>
                  <input id="cap" type="number" min={1} value={capacidad} onChange={e=>setCapacidad(e.target.value === '' ? '' : Number(e.target.value))} placeholder="Capacidad" className="w-full rounded-lg border border-border-soft bg-white/70 px-4 py-2 outline-none focus:border-primary" />
                </div>
                <div>
                  <label className="sr-only" htmlFor="est">Estado</label>
                  <select id="est" value={estado} onChange={e=>setEstado(e.target.value)} className="w-full rounded-lg border border-border-soft bg-white/70 px-4 py-2 outline-none focus:border-primary">
                    <option value="disponible">Disponible</option>
                    <option value="en_ruta">En ruta</option>
                    <option value="mantenimiento">Mantenimiento</option>
                    <option value="inactivo">Inactivo</option>
                  </select>
                </div>
                <div className="flex items-center justify-end gap-2 mt-2">
                  <button type="button" onClick={()=>setShowForm(false)} className="px-4 py-2 rounded-lg border border-border-soft hover:border-primary">Cancelar</button>
                  <button type="submit" disabled={saving} className="px-4 py-2 rounded-lg bg-primary text-background-light font-bold hover:bg-accent disabled:opacity-60">{saving ? 'Guardando...' : 'Guardar'}</button>
                </div>
              </form>
            </div>
          </div>
        )}
      </main>
    </div>
  )
}
