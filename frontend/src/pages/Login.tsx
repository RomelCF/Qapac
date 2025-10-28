import { useState } from 'react'
import type { FormEvent } from 'react'
import { Link, useNavigate } from 'react-router-dom'

export default function Login() {
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [showPassword, setShowPassword] = useState(false)
  const [loading, setLoading] = useState(false)
  const [message, setMessage] = useState<string | null>(null)
  const [ok, setOk] = useState<boolean | null>(null)
  const [emailError, setEmailError] = useState<string | null>(null)
  const [passwordError, setPasswordError] = useState<string | null>(null)

  const API_BASE = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'
  const navigate = useNavigate()

  function validateEmail(value: string) {
    if (!value) return 'El correo es obligatorio'
    const re = /[^@\s]+@[^@\s]+\.[^@\s]+/
    if (!re.test(value)) return 'Formato de correo inválido'
    return null
  }

  function validatePassword(value: string) {
    if (!value) return 'La contraseña es obligatoria'
    if (value.length < 6) return 'Mínimo 6 caracteres'
    return null
  }

  async function onSubmit(e: FormEvent<HTMLFormElement>) {
    e.preventDefault()
    setLoading(true)
    setMessage(null)
    setOk(null)
    // validar antes de enviar
    const eErr = validateEmail(email)
    const pErr = validatePassword(password)
    setEmailError(eErr)
    setPasswordError(pErr)
    if (eErr || pErr) { setLoading(false); return }
    try {
      const res = await fetch(`${API_BASE}/auth/login`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email, password })
      })
      const data = await res.json()
      setOk(!!data.success)
      setMessage(data.message ?? (res.ok ? 'Autenticación exitosa' : 'Credenciales inválidas'))
      if (res.ok && data.success && data.userId) {
        // guardar userId y determinar tipo de cuenta
        localStorage.setItem('userId', String(data.userId))
        localStorage.setItem('userEmail', email)
        try {
          const profRes = await fetch(`${API_BASE}/auth/profile?userId=${data.userId}`)
          if (profRes.ok) {
            const prof = await profRes.json()
            if (prof?.tipo === 'cliente') {
              navigate('/dashboard/cliente')
            } else if (prof?.tipo === 'empresa') {
              navigate('/dashboard/empresa')
            } else if (prof?.tipo === 'admin') {
              navigate('/dashboard/admin')
            }
          }
        } catch {}
      }
    } catch (err) {
      setOk(false)
      setMessage('Error de red')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="relative flex min-h-screen w-full flex-col group/design-root overflow-x-hidden bg-background-light">
      <div className="flex-grow flex items-center justify-center p-4">
        <div className="w-full max-w-md p-8 space-y-6 bg-background-secondary rounded-xl shadow-2xl border-2 border-border-soft">
          <div className="text-center">
            <img
              alt="Logo"
              className="mx-auto h-24 md:h-28 w-auto"
              src="/assets/logo.png"
            />
            <div className="mt-5">
              <h1 className="text-primary text-4xl font-bold font-display tracking-wider">BIENVENIDO</h1>
            </div>
          </div>

          <form className="mt-8 space-y-6" onSubmit={onSubmit}>
            <div className="rounded-md shadow-sm -space-y-px">
              <div>
                <label className="sr-only" htmlFor="email-address">Correo electrónico</label>
                <div className="relative">
                  <span className="material-symbols-outlined absolute inset-y-0 left-0 pl-3 flex items-center text-text-secondary">mail</span>
                  <input
                    id="email-address"
                    name="email"
                    type="email"
                    autoComplete="email"
                    required
                    placeholder="Correo electrónico"
                    value={email}
                    onChange={(e) => {
                      setEmail(e.target.value)
                      if (emailError) setEmailError(null)
                    }}
                    onBlur={() => setEmailError(validateEmail(email))}
                    className="form-input appearance-none rounded-lg relative block w-full px-3 py-4 pl-10 border border-border-soft placeholder-text-secondary/70 text-text-primary bg-background-light focus:outline-none focus:ring-primary focus:border-primary focus:z-10 sm:text-sm font-body"
                  />
                </div>
                {emailError && <p className="mt-2 text-sm text-red-700">{emailError}</p>}
              </div>

              <div className="pt-4">
                <label className="sr-only" htmlFor="password">Contraseña</label>
                <div className="relative">
                  <span className="material-symbols-outlined absolute inset-y-0 left-0 pl-3 flex items-center text-text-secondary">lock</span>
                  <input
                    id="password"
                    name="password"
                    type={showPassword ? 'text' : 'password'}
                    autoComplete="current-password"
                    required
                    placeholder="Contraseña"
                    value={password}
                    onChange={(e) => {
                      setPassword(e.target.value)
                      if (passwordError) setPasswordError(null)
                    }}
                    onBlur={() => setPasswordError(validatePassword(password))}
                    className="form-input appearance-none rounded-lg relative block w-full px-3 py-4 pl-10 border border-border-soft placeholder-text-secondary/70 text-text-primary bg-background-light focus:outline-none focus:ring-primary focus:border-primary focus:z-10 sm:text-sm font-body"
                  />
                  <button type="button" onClick={() => setShowPassword(v => !v)} className="absolute inset-y-0 right-0 pr-3 flex items-center text-sm leading-5">
                    <span className="material-symbols-outlined text-text-secondary/70">{showPassword ? 'visibility_off' : 'visibility'}</span>
                  </button>
                </div>
                {passwordError && <p className="mt-2 text-sm text-red-700">{passwordError}</p>}
              </div>
            </div>

            {message && (
              <div className={ok ? 'text-green-700' : 'text-red-700'}>
                {message}
              </div>
            )}

            <div>
              <button
                type="submit"
                disabled={loading}
                className="group relative w-full flex justify-center py-3 px-4 border border-transparent text-sm font-bold rounded-lg text-background-light bg-primary hover:bg-accent disabled:opacity-60 disabled:cursor-not-allowed focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-primary font-display tracking-widest transition duration-150 ease-in-out"
              >
                {loading ? 'INGRESANDO...' : 'INICIAR SESIÓN'}
              </button>
            </div>
          </form>

          <div className="text-center">
            <p className="text-sm text-text-secondary font-body">
              ¿No tienes una cuenta?
              <Link to="/register" className="font-medium text-accent hover:text-primary"> Regístrate aquí </Link>
            </p>
          </div>
        </div>
      </div>
    </div>
  )
}
