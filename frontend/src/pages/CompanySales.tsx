import { Link } from 'react-router-dom'
import { useEffect, useState } from 'react'
import CompanyHeader from '../components/CompanyHeader'
import { useTheme } from '../hooks/useTheme'

export default function CompanySales() {
  useTheme()
  const [open, setOpen] = useState(false)
  const [email, setEmail] = useState<string>('')
  const [empresaId, setEmpresaId] = useState<number | null>(null)
  const [empresaInfo, setEmpresaInfo] = useState<{ nombre?: string; ruc?: string; razonSocial?: string } | null>(null)
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)
  const [items, setItems] = useState<Array<SaleItem>>([])
  const [q, setQ] = useState('')
  const [fromDate, setFromDate] = useState<string>(() => {
    const end = new Date(); const start = new Date(); start.setDate(end.getDate()-29)
    return start.toISOString().slice(0,10)
  })
  const [toDate, setToDate] = useState<string>(() => new Date().toISOString().slice(0,10))
  const [estado, setEstado] = useState<string>('')
  const [metodo, setMetodo] = useState<string>('')
  const [detailOpen, setDetailOpen] = useState(false)
  const [detail, setDetail] = useState<SaleDetail|null>(null)
  const [detailLoading, setDetailLoading] = useState(false)
  const [detailError, setDetailError] = useState<string|null>(null)
  const API_BASE = (import.meta as unknown as { env: Record<string, string> }).env?.VITE_API_BASE_URL || 'http://localhost:8080'

  type SaleItem = { idVenta:number; idDetalleVenta:number; fecha:string|null; hora:string|null; ruta:string; busMatricula:string|null; asiento:string|null; precio:number; cliente:string; metodoPago:string; estadoCarrito?: string|null }
  type SaleDetail = { idVenta:number; fecha:string|null; hora:string|null; ruta:string; busMatricula:string|null; metodoPago:string; cliente:string; total:number; items:Array<{ asiento:string|null; precio:number }>; cancelada?: boolean; refund?: number; empresaGana?: number }

  useEffect(() => { setEmail(localStorage.getItem('userEmail') || '') }, [])
  useEffect(() => { (async()=>{
    const uid = localStorage.getItem('userId'); if (!uid) return
    try { const res = await fetch(`${API_BASE}/auth/profile?userId=${uid}`); if (res.ok){ const p=await res.json(); if (p?.idEmpresa) setEmpresaId(Number(p.idEmpresa)) } } catch {}
  })() }, [])

  useEffect(() => { if (empresaId){ setError(null); void Promise.all([loadEmpresa(), loadSales()]) } }, [empresaId, fromDate, toDate, estado, metodo])

  async function loadEmpresa(){
    try {
      const res = await fetch(`${API_BASE}/empresas/${empresaId}`)
      if (res.ok){ const e = await res.json(); setEmpresaInfo({ nombre: e.nombre, ruc: e.ruc, razonSocial: e.razonSocial }) }
    } catch {}
  }

  function computeFromTo(): { from: string; to: string } {
    // Validar fechas
    const from = fromDate
    const to = toDate
    if (!from || !to) return { from: from || to || new Date().toISOString().slice(0,10), to: to || from || new Date().toISOString().slice(0,10) }
    // Asegurar from <= to
    if (new Date(from) > new Date(to)) {
      // Intercambiar si el usuario se equivoca
      return { from: to, to: from }
    }
    return { from, to }
  }

  async function loadSales(){
    setLoading(true); setError(null)
    try {
      const { from, to } = computeFromTo()
      const params = new URLSearchParams({ empresaId: String(empresaId), from, to })
      if (estado) params.set('estado', estado)
      if (metodo) params.set('metodo', metodo)
      const res = await fetch(`${API_BASE}/company-sales/list?${params.toString()}`)
      if (!res.ok) throw new Error('No se pudo cargar ventas')
      const data = await res.json()
      setItems((data.items||[]).map((x:any)=>({ ...x, precio: Number(x.precio??0) })))
    } catch(e:any){ setError(e.message||'Error al cargar ventas') } finally { setLoading(false) }
  }

  function filtered(){
    const qq = q.trim().toLowerCase(); if (!qq) return items
    return items.filter(it => (it.ruta||'').toLowerCase().includes(qq) || (it.busMatricula||'').toLowerCase().includes(qq) || (it.cliente||'').toLowerCase().includes(qq))
  }

  async function openDetail(idVenta:number){
    setDetailOpen(true); setDetailLoading(true); setDetailError(null); setDetail(null)
    try {
      const r = await fetch(`${API_BASE}/company-sales/detail/${idVenta}`)
      if (!r.ok) throw new Error('No se pudo cargar el detalle')
      const d = await r.json()
      setDetail({
        ...d,
        total: Number(d.total??0),
        refund: d.refund != null ? Number(d.refund) : undefined,
        empresaGana: d.empresaGana != null ? Number(d.empresaGana) : undefined,
        items: (d.items||[]).map((it:any)=>({ asiento: it.asiento, precio: Number(it.precio??0) }))
      })
    } catch(e:any){ setDetailError(e.message||'Error al cargar detalle') } finally { setDetailLoading(false) }
  }

  function printReport(){
    const rows = filtered()
    const totalTickets = rows.length
    const totalIngresado = rows.reduce((s,r)=> s + (r.precio||0), 0)
    const ventasUnicas = Array.from(new Set(rows.map(r=>r.idVenta))).length
    const html = `<!doctype html><html><head><meta charset="utf-8"><meta name="viewport" content="width=device-width, initial-scale=1" /><title>Reporte de Ventas</title>
    <style>
      body{font-family: ui-sans-serif,system-ui,Segoe UI,Roboto,Ubuntu,Helvetica,Arial; color:#0f172a;}
      .header{display:flex;align-items:center;gap:12px;margin-bottom:8px}
      .sub{display:flex;justify-content:space-between;gap:16px;margin-bottom:16px}
      .col{font-size:12px;line-height:1.2}
      .logo{height:56px}
      h1{font-size:20px;margin:0;color:#0ea5e9}
      table{width:100%;border-collapse:collapse;font-size:12px}
      th,td{border:1px solid #e5e7eb;padding:8px;text-align:left}
      th{background:#f3f4f6}
      .muted{color:#64748b}
      .summary{margin-top:12px;display:flex;gap:24px;justify-content:flex-end;font-size:12px}
      .chip{background:#f3f4f6;border:1px solid #e5e7eb;border-radius:8px;padding:8px 12px}
      @media print { body { -webkit-print-color-adjust: exact; print-color-adjust: exact; } th{background:#f3f4f6 !important} }
    </style></head><body>
    <div class="header"><img class="logo" src="${location.origin}/assets/logo.png"><div><h1>Reporte de Ventas</h1><div class="muted">Generado: ${new Date().toLocaleString()}</div></div></div>
    <div class="sub">
      <div class="col">
        <div><strong>Empresa:</strong> ${empresaInfo?.nombre ?? ''}</div>
        <div><strong>Razón Social:</strong> ${empresaInfo?.razonSocial ?? ''}</div>
      </div>
      <div class="col" style="text-align:right">
        <div><strong>RUC:</strong> ${empresaInfo?.ruc ?? ''}</div>
        <div><strong>Correo:</strong> ${email ?? ''}</div>
      </div>
    </div>
    <table><thead><tr><th>Fecha</th><th>Hora</th><th>Ruta</th><th>Bus</th><th>Asiento</th><th>Cliente</th><th>Método</th><th>Precio</th></tr></thead><tbody>
    ${rows.map(r=>`<tr><td>${r.fecha??''}</td><td>${(r.hora||'').slice(0,5)}</td><td>${r.ruta??''}</td><td>${r.busMatricula??''}</td><td>${r.asiento??''}</td><td>${r.cliente??''}</td><td>${r.metodoPago??''}</td><td>S/ ${(r.precio||0).toFixed(2)}</td></tr>`).join('')}
    </tbody></table>
    <div class="summary">
      <div class="chip"><strong>Ventas:</strong> ${ventasUnicas}</div>
      <div class="chip"><strong>Tickets vendidos:</strong> ${totalTickets}</div>
      <div class="chip"><strong>Total ingresado:</strong> S/ ${totalIngresado.toFixed(2)}</div>
    </div>
    </body></html>`
    // Render in a hidden iframe to avoid popup blockers and ensure onload before printing
    const iframe = document.createElement('iframe')
    iframe.style.position = 'fixed'
    iframe.style.right = '0'
    iframe.style.bottom = '0'
    iframe.style.width = '0'
    iframe.style.height = '0'
    iframe.style.border = '0'
    document.body.appendChild(iframe)
    const doc = iframe.contentDocument || (iframe.contentWindow && iframe.contentWindow.document)
    if (!doc) { document.body.removeChild(iframe); return }
    doc.open()
    doc.write(html)
    doc.close()
    iframe.onload = () => {
      try {
        iframe.contentWindow?.focus()
        iframe.contentWindow?.print()
      } finally {
        // Give the browser time to spawn the print dialog before cleanup
        setTimeout(() => {
          if (iframe.parentNode) iframe.parentNode.removeChild(iframe)
        }, 1000)
      }
    }
  }

  return (
    <div className="min-h-screen bg-background-light text-text-primary">
      <CompanyHeader />
      <main className="flex-1 flex flex-col items-center py-10 px-4">
        <div className="w-full max-w-5xl mb-6 flex items-center justify-between gap-4">
          <h1 className="font-display text-3xl text-primary">Ventas</h1>
          <div className="flex items-center gap-2">
            <input type="date" value={fromDate} onChange={e=>setFromDate(e.target.value)} className="rounded-lg border border-border-soft bg-white/70 px-3 py-2 outline-none focus:border-primary" />
            <span className="text-text-secondary">a</span>
            <input type="date" value={toDate} onChange={e=>setToDate(e.target.value)} className="rounded-lg border border-border-soft bg-white/70 px-3 py-2 outline-none focus:border-primary" />
            <button onClick={printReport} className="px-4 py-2 rounded-lg bg-primary text-background-light font-bold hover:bg-accent inline-flex items-center gap-2">
              <span className="material-symbols-outlined">print</span>
              Imprimir reporte
            </button>
          </div>
        </div>

        <div className="w-full max-w-5xl mb-4 grid grid-cols-1 md:grid-cols-4 gap-3">
          <input value={q} onChange={e=>setQ(e.target.value)} placeholder="Buscar por ruta, bus o cliente" className="w-full rounded-lg border border-border-soft bg-white/70 px-4 py-2 outline-none focus:border-primary" />
          <select value={estado} onChange={e=>setEstado(e.target.value)} className="w-full rounded-lg border border-border-soft bg-white/70 px-4 py-2 outline-none focus:border-primary">
            <option value="">Estado (todos)</option>
            <option value="pagado">Pagado</option>
            <option value="completado">Completado</option>
            <option value="cancelado">Cancelado</option>
          </select>
          <input value={metodo} onChange={e=>setMetodo(e.target.value)} placeholder="Método de pago (texto)" className="w-full rounded-lg border border-border-soft bg-white/70 px-4 py-2 outline-none focus:border-primary" />
          <button onClick={loadSales} className="px-4 py-2 rounded-lg border border-border-soft hover:border-primary">Refrescar</button>
        </div>

        {error && <div className="w-full max-w-5xl text-sm text-red-700 mb-3">{error}</div>}
        {loading ? (
          <div className="w-full max-w-5xl text-text-secondary">Cargando...</div>
        ) : (
          <div className="w-full max-w-5xl overflow-x-auto rounded-xl border border-border-soft bg-white/50">
            <table className="min-w-full text-sm">
              <thead className="bg-background-secondary/60">
                <tr>
                  <th className="text-left p-3">Fecha</th>
                  <th className="text-left p-3">Hora</th>
                  <th className="text-left p-3">Ruta</th>
                  <th className="text-left p-3">Bus</th>
                  <th className="text-left p-3">Asiento</th>
                  <th className="text-left p-3">Cliente</th>
                  <th className="text-left p-3">Método</th>
                  <th className="text-left p-3">Estado</th>
                  <th className="text-left p-3">Precio</th>
                  <th className="text-left p-3">Acciones</th>
                </tr>
              </thead>
              <tbody>
                {filtered().map(r => (
                  <tr key={`${r.idVenta}-${r.idDetalleVenta}`} className="border-top border-border-soft">
                    <td className="p-3">{r.fecha || '-'}</td>
                    <td className="p-3">{(r.hora||'').slice(0,5) || '-'}</td>
                    <td className="p-3">{r.ruta || '-'}</td>
                    <td className="p-3">{r.busMatricula || '-'}</td>
                    <td className="p-3">{r.asiento || '-'}</td>
                    <td className="p-3">{r.cliente || '-'}</td>
                    <td className="p-3">{r.metodoPago || '-'}</td>
                    <td className="p-3">
                      {r.estadoCarrito ? (
                        <span className={`px-2 py-1 rounded text-xs border ${r.estadoCarrito.toLowerCase()==='cancelado' ? 'border-red-300 text-red-700 bg-red-50' : r.estadoCarrito.toLowerCase()==='completado' ? 'border-green-300 text-green-700 bg-green-50' : 'border-amber-300 text-amber-700 bg-amber-50'}`}>
                          {r.estadoCarrito}
                        </span>
                      ) : '-'}
                    </td>
                    <td className="p-3">S/ {(r.precio||0).toFixed(2)}</td>
                    <td className="p-3">
                      <button onClick={()=>openDetail(r.idVenta)} className="px-3 py-1 rounded-md border border-border-soft hover:border-primary text-xs inline-flex items-center gap-1">
                        <span className="material-symbols-outlined text-base">visibility</span>
                        Ver detalle
                      </button>
                    </td>
                  </tr>
                ))}
                {filtered().length===0 && (
                  <tr><td className="p-4 text-text-secondary" colSpan={10}>No hay ventas en el rango seleccionado</td></tr>
                )}
              </tbody>
            </table>
          </div>
        )}

        {detailOpen && (
          <div className="fixed inset-0 bg-black/40 flex items-center justify-center z-50">
            <div className="w-full max-w-2xl rounded-xl border border-border-soft bg-white p-6 shadow-2xl">
              <div className="flex items-center justify-between mb-3">
                <h2 className="font-display text-xl">Detalle de venta</h2>
                <button onClick={()=>setDetailOpen(false)} className="text-text-secondary hover:text-primary"><span className="material-symbols-outlined">close</span></button>
              </div>
              {detailLoading && <div className="text-text-secondary">Cargando detalle...</div>}
              {detailError && <div className="text-red-700 text-sm mb-2">{detailError}</div>}
              {!detailLoading && detail && (
                <div className="grid gap-4">
                  <div className="grid grid-cols-1 md:grid-cols-2 gap-3 text-sm">
                    <div><span className="text-text-secondary">Fecha:</span> {detail.fecha || '-'}</div>
                    <div><span className="text-text-secondary">Hora:</span> {(detail.hora||'').slice(0,5)}</div>
                    <div><span className="text-text-secondary">Ruta:</span> {detail.ruta}</div>
                    <div><span className="text-text-secondary">Bus:</span> {detail.busMatricula || '-'}</div>
                    <div><span className="text-text-secondary">Método de pago:</span> {detail.metodoPago}</div>
                    <div><span className="text-text-secondary">Cliente:</span> {detail.cliente}</div>
                  </div>
                  <div className="rounded-lg border border-border-soft bg-white/60 p-3 text-sm">
                    <div className="flex items-center gap-4 flex-wrap">
                      <div>
                        <span className="text-text-secondary">Estado:</span>{' '}
                        {detail.cancelada ? <span className="px-2 py-1 rounded text-xs border border-red-300 text-red-700 bg-red-50">Cancelada</span> : <span className="px-2 py-1 rounded text-xs border border-green-300 text-green-700 bg-green-50">No cancelada</span>}
                      </div>
                      <div><span className="text-text-secondary">Reembolso (1/3):</span> S/ {(detail.refund ?? 0).toFixed(2)}</div>
                      <div><span className="text-text-secondary">Gana empresa:</span> S/ {(detail.empresaGana ?? detail.total).toFixed(2)}</div>
                    </div>
                  </div>
                  <div className="rounded-lg border border-border-soft bg-white/60 p-3">
                    <h3 className="font-display text-lg mb-2">Asientos</h3>
                    <table className="w-full text-sm">
                      <thead><tr><th className="text-left p-2">Asiento</th><th className="text-left p-2">Precio</th></tr></thead>
                      <tbody>
                        {detail.items.map((it,i)=> (
                          <tr key={i}><td className="p-2">{it.asiento||'-'}</td><td className="p-2">S/ {(it.precio||0).toFixed(2)}</td></tr>
                        ))}
                        <tr><td className="p-2 font-bold">Total</td><td className="p-2 font-bold">S/ {detail.total.toFixed(2)}</td></tr>
                      </tbody>
                    </table>
                  </div>
                </div>
              )}
            </div>
          </div>
        )}
      </main>
    </div>
  )
}
