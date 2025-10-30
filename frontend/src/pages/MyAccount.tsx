import { useEffect, useRef, useState } from 'react'
import { Link } from 'react-router-dom'
import UserAvatar from '../components/UserAvatar'
import CompanyHeader from '../components/CompanyHeader'
import { useTheme } from '../hooks/useTheme'

export default function MyAccount() {
  useTheme()
  const [avatarUrl, setAvatarUrl] = useState<string | null>(null)
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [confirm, setConfirm] = useState('')
  const [themeDark, setThemeDark] = useState(() => {
    const saved = localStorage.getItem('theme')
    return saved === 'dark'
  })
  const [msg, setMsg] = useState<string | null>(null)
  const [ok, setOk] = useState<boolean | null>(null)
  const fileRef = useRef<HTMLInputElement | null>(null)
  const [open, setOpen] = useState(false)
  const [emailLabel, setEmailLabel] = useState('')

  const [tipo, setTipo] = useState<'cliente' | 'empresa' | 'admin' | 'none'>('none')
  const [idCliente, setIdCliente] = useState<number | null>(null)
  const [idEmpresa, setIdEmpresa] = useState<number | null>(null)
  const [loadingProfile, setLoadingProfile] = useState(false)

  // campos cliente
  const [cliNombres, setCliNombres] = useState('')
  const [cliApellidos, setCliApellidos] = useState('')
  const [cliDni, setCliDni] = useState('')
  const [cliDomicilio, setCliDomicilio] = useState('')
  const [cliTelefono, setCliTelefono] = useState('')
  const [editCliNombres, setEditCliNombres] = useState(false)
  const [editCliApellidos, setEditCliApellidos] = useState(false)
  const [editCliDni, setEditCliDni] = useState(false)
  const [editCliDomicilio, setEditCliDomicilio] = useState(false)
  const [editCliTelefono, setEditCliTelefono] = useState(false)

  // campos empresa
  const [empNombre, setEmpNombre] = useState('')
  const [empRuc, setEmpRuc] = useState('')
  const [empRazonSocial, setEmpRazonSocial] = useState('')

  const API_BASE = (import.meta as unknown as { env: Record<string, string> }).env?.VITE_API_BASE_URL || 'http://localhost:8080'
  const [loadError, setLoadError] = useState<string | null>(null)
  const [userId, setUserId] = useState<string | null>(null)

  useEffect(() => {
    const e = localStorage.getItem('userEmail') || ''
    setEmailLabel(e)
    setEmail(e)
    const uid = localStorage.getItem('userId')
    setUserId(uid)
    if (!uid) return
    setLoadingProfile(true)
    ;(async () => {
      try {
        const profRes = await fetch(`${API_BASE}/auth/profile?userId=${uid}`)
        if (profRes.ok) {
          const p = await profRes.json()
          setTipo(p?.tipo || 'none')
          if (p?.tipo === 'cliente' && p?.idCliente) {
            setIdCliente(p.idCliente)
            const det = await fetch(`${API_BASE}/clientes/${p.idCliente}`)
            if (det.ok) {
              const c = await det.json()
              setCliNombres(c.nombres || '')
              setCliApellidos(c.apellidos || '')
              setCliDni(c.dni || '')
              setCliDomicilio(c.domicilio || '')
              setCliTelefono(c.telefono || '')
              setLoadError(null)
            } else {
              setLoadError('No se pudo cargar los datos del cliente')
            }
          } else if (p?.tipo === 'empresa' && p?.idEmpresa) {
            setIdEmpresa(p.idEmpresa)
            const det = await fetch(`${API_BASE}/empresas/${p.idEmpresa}`)
            if (det.ok) {
              const e = await det.json()
              setEmpNombre(e.nombre || '')
              setEmpRuc(e.ruc || '')
              setEmpRazonSocial(e.razonSocial || '')
              setLoadError(null)
            } else {
              setLoadError('No se pudo cargar los datos de la empresa')
            }
          } else if (p?.tipo === 'admin') {
            setTipo('admin')
          }
        } else {
          setLoadError('No se pudo cargar el perfil del usuario')
        }
      } catch (err) {
        setLoadError('Error de red al cargar el perfil')
      } finally {
        setLoadingProfile(false)
      }
    })()

    // Cargar logo de usuario si existe
    ;(async () => {
      if (!uid) return
      try {
        const res = await fetch(`${API_BASE}/usuarios/${uid}/logo`)
        if (res.ok) {
          // usar la URL del endpoint para mostrar la imagen
          setAvatarUrl(`${API_BASE}/usuarios/${uid}/logo?ts=${Date.now()}`)
        }
      } catch {}
    })()
  }, [])

  function onPickAvatar() {
    fileRef.current?.click()
  }

  async function onAvatarChange(e: React.ChangeEvent<HTMLInputElement>) {
    const f = e.target.files?.[0]
    if (!f) return
    if (!userId) { setOk(false); setMsg('No hay sesión'); return }
    const form = new FormData()
    form.append('file', f)
    try {
      const res = await fetch(`${API_BASE}/usuarios/${userId}/logo`, { method: 'PUT', body: form })
      if (res.ok) {
        setAvatarUrl(`${API_BASE}/usuarios/${userId}/logo?ts=${Date.now()}`)
        setOk(true); setMsg('Logo actualizado')
      } else {
        setOk(false); setMsg('No se pudo actualizar el logo')
      }
    } catch {
      setOk(false); setMsg('Error de red al subir el logo')
    }
  }

  function toggleTheme() {
    const next = !themeDark
    setThemeDark(next)
    localStorage.setItem('theme', next ? 'dark' : 'light')
    if (next) {
      document.documentElement.classList.add('dark')
    } else {
      document.documentElement.classList.remove('dark')
    }
  }

  function onSaveEmail(e: React.FormEvent) {
    e.preventDefault()
    if (!email) { setOk(false); setMsg('El correo es obligatorio'); return }
    const re = /[^@\s]+@[^@\s]+\.[^@\s]+/
    if (!re.test(email)) { setOk(false); setMsg('Formato de correo inválido'); return }
    setOk(true); setMsg('Correo actualizado (demo)')
  }

  function onSavePassword(e: React.FormEvent) {
    e.preventDefault()
    if (!password) { setOk(false); setMsg('La contraseña es obligatoria'); return }
    if (password.length < 6) { setOk(false); setMsg('La contraseña debe tener al menos 6 caracteres'); return }
    if (password !== confirm) { setOk(false); setMsg('Las contraseñas no coinciden'); return }
    setOk(true); setMsg('Contraseña actualizada (demo)')
  }

  async function onSaveCliente(e: React.FormEvent) {
    e.preventDefault()
    if (!idCliente) return
    if (!cliNombres || !cliApellidos || !cliDni) { setOk(false); setMsg('Nombres, apellidos y DNI son obligatorios'); return }
    if (!/^\d{8}$/.test(cliDni)) { setOk(false); setMsg('DNI debe tener 8 dígitos'); return }
    const body = { nombres: cliNombres, apellidos: cliApellidos, domicilio: cliDomicilio, dni: cliDni, telefono: cliTelefono }
    const res = await fetch(`${API_BASE}/clientes/${idCliente}`, { method: 'PUT', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(body) })
    if (res.ok) { setOk(true); setMsg('Perfil de cliente actualizado') } else { setOk(false); setMsg('No se pudo actualizar cliente') }
  }

  async function onSaveEmpresa(e: React.FormEvent) {
    e.preventDefault()
    if (!idEmpresa) return
    if (!empNombre || !empRuc || !empRazonSocial) { setOk(false); setMsg('Nombre, RUC y Razón Social son obligatorios'); return }
    if (!/^\d{11}$/.test(empRuc)) { setOk(false); setMsg('RUC debe tener 11 dígitos'); return }
    const body = { nombre: empNombre, ruc: empRuc, razonSocial: empRazonSocial }
    const res = await fetch(`${API_BASE}/empresas/${idEmpresa}`, { method: 'PUT', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(body) })
    if (res.ok) { setOk(true); setMsg('Perfil de empresa actualizado') } else { setOk(false); setMsg('No se pudo actualizar empresa') }
  }

  return (
    <div className="min-h-screen bg-background-light text-text-primary">
      {tipo === 'empresa' ? (
        <CompanyHeader />
      ) : tipo === 'admin' ? (
        <header className="p-4 border-b border-border-soft bg-background-secondary">
          <div className="max-w-5xl mx-auto flex items-center justify-between gap-6">
            <div className="flex items-center gap-3">
              <Link to="/dashboard/admin" aria-label="Inicio">
                <img src="/assets/logo.png" alt="Logo" className="h-16 md:h-20 w-auto" />
              </Link>
            </div>
            <nav className="flex items-center gap-4 text-sm">
              <Link to="/dashboard/admin/usuarios" className="text-text-secondary hover:text-primary inline-flex items-center gap-1">
                <span className="material-symbols-outlined text-base">group</span> Usuarios
              </Link>
              <Link to="/dashboard/admin/empleados" className="text-text-secondary hover:text-primary inline-flex items-center gap-1">
                <span className="material-symbols-outlined text-base">badge</span> Empleados
              </Link>
              <Link to="/dashboard/admin/sucursales" className="text-text-secondary hover:text-primary inline-flex items-center gap-1">
                <span className="material-symbols-outlined text-base">apartment</span> Sucursales
              </Link>
              <Link to="/dashboard/admin/estadisticas" className="text-text-secondary hover:text-primary inline-flex items-center gap-1">
                <span className="material-symbols-outlined text-base">monitoring</span> Estadísticas
              </Link>
            </nav>
            <div className="flex items-center gap-3">
              <div className="relative">
                <button type="button" onClick={() => setOpen(v => !v)} aria-haspopup="menu" aria-expanded={open} className="h-10 w-10 rounded-full border border-border-soft bg-white/50 flex items-center justify-center hover:border-primary hover:shadow-md">
                  <UserAvatar size={40} />
                </button>
                {open && (
                  <div className="absolute right-0 mt-2 w-56 rounded-lg border border-border-soft bg-white shadow-xl p-3 z-20">
                    <div className="text-sm text-text-secondary mb-2 truncate" title={emailLabel}>{emailLabel || 'Administrador'}</div>
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
      ) : (
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
                <button type="button" onClick={() => setOpen(v => !v)} aria-haspopup="menu" aria-expanded={open} className="rounded-full hover:border-primary hover:shadow-md">
                  <UserAvatar size={40} />
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
      )}
      <div className="max-w-3xl mx-auto px-4 py-10">
        <h1 className="font-display text-3xl text-primary text-center mb-8">Mi cuenta</h1>

        <div className="flex flex-col items-center mb-10">
          <button onClick={onPickAvatar} className="relative rounded-full hover:border-primary hover:shadow-xl transition" style={{ width: 160, height: 160 }} aria-label="Cambiar foto de perfil">
            <UserAvatar size={160} />
          </button>
          <input ref={fileRef} type="file" accept="image/*" className="hidden" onChange={onAvatarChange} />
          <p className="mt-3 text-text-secondary text-sm">Haz click para cambiar tu foto de perfil</p>
        </div>

        {msg && (
          <div className={`mb-6 text-sm ${ok ? 'text-green-700' : 'text-red-700'}`}>{msg}</div>
        )}

        {loadingProfile && (
          <div className="text-center text-text-secondary mb-6">Cargando perfil...</div>
        )}
        {loadError && (
          <div className="text-center text-red-700 mb-6 text-sm">{loadError}</div>
        )}

        {tipo === 'cliente' && (
          <section className="rounded-xl border-2 border-border-soft bg-background-secondary p-6 mb-6">
            <h2 className="font-display text-xl mb-4">Datos de cliente</h2>
            <form onSubmit={onSaveCliente} className="grid gap-4 md:grid-cols-2">
              <div className="md:col-span-1">
                <div className="flex items-center justify-between mb-1">
                  <label className="text-sm text-text-secondary">Nombres</label>
                  <button type="button" onClick={()=>setEditCliNombres(v=>!v)} className="text-text-secondary hover:text-primary"><span className="material-symbols-outlined text-base">edit</span></button>
                </div>
                {editCliNombres ? (
                  <input id="cliN" value={cliNombres} onChange={e=>setCliNombres(e.target.value)} placeholder="Nombres" className="w-full rounded-lg border border-border-soft bg-white/70 px-4 py-2 outline-none focus:border-primary" />
                ) : (
                  <div className="w-full rounded-lg border border-border-soft bg-white/50 px-4 py-2 text-text-primary">{cliNombres || '-'}</div>
                )}
              </div>

              <div className="md:col-span-1">
                <div className="flex items-center justify-between mb-1">
                  <label className="text-sm text-text-secondary">Apellidos</label>
                  <button type="button" onClick={()=>setEditCliApellidos(v=>!v)} className="text-text-secondary hover:text-primary"><span className="material-symbols-outlined text-base">edit</span></button>
                </div>
                {editCliApellidos ? (
                  <input id="cliA" value={cliApellidos} onChange={e=>setCliApellidos(e.target.value)} placeholder="Apellidos" className="w-full rounded-lg border border-border-soft bg-white/70 px-4 py-2 outline-none focus:border-primary" />
                ) : (
                  <div className="w-full rounded-lg border border-border-soft bg-white/50 px-4 py-2 text-text-primary">{cliApellidos || '-'}</div>
                )}
              </div>

              <div className="md:col-span-1">
                <div className="flex items-center justify-between mb-1">
                  <label className="text-sm text-text-secondary">DNI</label>
                  <button type="button" onClick={()=>setEditCliDni(v=>!v)} className="text-text-secondary hover:text-primary"><span className="material-symbols-outlined text-base">edit</span></button>
                </div>
                {editCliDni ? (
                  <input id="cliDni" value={cliDni} onChange={e=>setCliDni(e.target.value)} placeholder="DNI" className="w-full rounded-lg border border-border-soft bg-white/70 px-4 py-2 outline-none focus:border-primary" />
                ) : (
                  <div className="w-full rounded-lg border border-border-soft bg-white/50 px-4 py-2 text-text-primary">{cliDni || '-'}</div>
                )}
              </div>

              <div className="md:col-span-1">
                <div className="flex items-center justify-between mb-1">
                  <label className="text-sm text-text-secondary">Teléfono</label>
                  <button type="button" onClick={()=>setEditCliTelefono(v=>!v)} className="text-text-secondary hover:text-primary"><span className="material-symbols-outlined text-base">edit</span></button>
                </div>
                {editCliTelefono ? (
                  <input id="cliTel" value={cliTelefono} onChange={e=>setCliTelefono(e.target.value)} placeholder="Teléfono" className="w-full rounded-lg border border-border-soft bg-white/70 px-4 py-2 outline-none focus:border-primary" />
                ) : (
                  <div className="w-full rounded-lg border border-border-soft bg-white/50 px-4 py-2 text-text-primary">{cliTelefono || '-'}</div>
                )}
              </div>

              <div className="md:col-span-2">
                <div className="flex items-center justify-between mb-1">
                  <label className="text-sm text-text-secondary">Domicilio</label>
                  <button type="button" onClick={()=>setEditCliDomicilio(v=>!v)} className="text-text-secondary hover:text-primary"><span className="material-symbols-outlined text-base">edit</span></button>
                </div>
                {editCliDomicilio ? (
                  <input id="cliDom" value={cliDomicilio} onChange={e=>setCliDomicilio(e.target.value)} placeholder="Domicilio" className="w-full rounded-lg border border-border-soft bg-white/70 px-4 py-2 outline-none focus:border-primary" />
                ) : (
                  <div className="w-full rounded-lg border border-border-soft bg-white/50 px-4 py-2 text-text-primary">{cliDomicilio || '-'}</div>
                )}
              </div>

              <div className="md:col-span-2">
                <button type="submit" className="px-4 py-2 rounded-lg bg-primary text-background-light font-bold hover:bg-accent">Guardar</button>
              </div>
            </form>
          </section>
        )}

        {tipo === 'empresa' && (
          <section className="rounded-xl border-2 border-border-soft bg-background-secondary p-6 mb-6">
            <h2 className="font-display text-xl mb-4">Datos de empresa</h2>
            <form onSubmit={onSaveEmpresa} className="grid gap-4 md:grid-cols-2">
              <div className="md:col-span-1">
                <label htmlFor="empNom" className="sr-only">Nombre</label>
                <input id="empNom" value={empNombre} onChange={e=>setEmpNombre(e.target.value)} placeholder="Nombre" className="w-full rounded-lg border border-border-soft bg-white/70 px-4 py-2 outline-none focus:border-primary" />
              </div>
              <div className="md:col-span-1">
                <label htmlFor="empRuc" className="sr-only">RUC</label>
                <input id="empRuc" value={empRuc} onChange={e=>setEmpRuc(e.target.value)} placeholder="RUC" className="w-full rounded-lg border border-border-soft bg-white/70 px-4 py-2 outline-none focus:border-primary" />
              </div>
              <div className="md:col-span-2">
                <label htmlFor="empRaz" className="sr-only">Razón Social</label>
                <input id="empRaz" value={empRazonSocial} onChange={e=>setEmpRazonSocial(e.target.value)} placeholder="Razón Social" className="w-full rounded-lg border border-border-soft bg-white/70 px-4 py-2 outline-none focus:border-primary" />
              </div>
              <div className="md:col-span-2">
                <button type="submit" className="px-4 py-2 rounded-lg bg-primary text-background-light font-bold hover:bg-accent">Guardar</button>
              </div>
            </form>
          </section>
        )}

        <div className="grid gap-6 md:grid-cols-2">
          <section className="rounded-xl border-2 border-border-soft bg-background-secondary p-6">
            <h2 className="font-display text-xl mb-4">Cambiar correo</h2>
            <form onSubmit={onSaveEmail} className="space-y-4">
              <div>
                <label htmlFor="email" className="sr-only">Correo</label>
                <input id="email" type="email" value={email} onChange={e => setEmail(e.target.value)} placeholder={emailLabel || 'Correo'} className="w-full rounded-lg border border-border-soft bg-white/70 px-4 py-2 outline-none focus:border-primary" />
              </div>
              <button type="submit" className="px-4 py-2 rounded-lg bg-primary text-background-light font-bold hover:bg-accent">Guardar</button>
            </form>
          </section>

          <section className="rounded-xl border-2 border-border-soft bg-background-secondary p-6">
            <h2 className="font-display text-xl mb-4">Cambiar contraseña</h2>
            <form onSubmit={onSavePassword} className="space-y-4">
              <div>
                <label htmlFor="pwd" className="sr-only">Nueva contraseña</label>
                <input id="pwd" type="password" value={password} onChange={e => setPassword(e.target.value)} placeholder="Nueva contraseña" className="w-full rounded-lg border border-border-soft bg-white/70 px-4 py-2 outline-none focus:border-primary" />
              </div>
              <div>
                <label htmlFor="confirm" className="sr-only">Confirmar contraseña</label>
                <input id="confirm" type="password" value={confirm} onChange={e => setConfirm(e.target.value)} placeholder="Confirmar contraseña" className="w-full rounded-lg border border-border-soft bg-white/70 px-4 py-2 outline-none focus:border-primary" />
              </div>
              <button type="submit" className="px-4 py-2 rounded-lg bg-primary text-background-light font-bold hover:bg-accent">Guardar</button>
            </form>
          </section>
        </div>

        <section className="mt-6 rounded-xl border-2 border-border-soft bg-background-secondary p-6">
          <h2 className="font-display text-xl mb-4">Apariencia</h2>
          <div className="flex items-center justify-between">
            <div className="flex items-center gap-2">
              <span className="material-symbols-outlined">dark_mode</span>
              <span>Modo oscuro</span>
            </div>
            <button onClick={toggleTheme} className={`w-14 h-8 rounded-full border-2 transition relative ${themeDark ? 'bg-primary border-primary' : 'bg-white border-border-soft'}`} aria-pressed={themeDark}>
              <span className={`absolute top-1/2 -translate-y-1/2 transition ${themeDark ? 'right-1' : 'left-1' } w-6 h-6 rounded-full bg-background-light`}></span>
            </button>
          </div>
        </section>
      </div>
    </div>
  )
}
