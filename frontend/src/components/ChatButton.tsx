import { MouseEvent, useEffect, useRef, useState } from 'react'

const API_BASE = (import.meta as unknown as { env: Record<string, string> }).env?.VITE_API_BASE_URL || 'http://localhost:8080'

type Msg = { role: 'user' | 'assistant'; text: string }

function Linkify({ text }: { text: string }) {
  const urlRegex = /(https?:\/\/[^\s)]+)|(www\.[^\s)]+)/g
  const parts = text.split(urlRegex)
  return (
    <>
      {parts.map((part, i) => {
        if (!part) return null
        const isUrl = urlRegex.test(part)
        // reset lastIndex due to test on global regex
        urlRegex.lastIndex = 0
        if (isUrl) {
          const href = part.startsWith('http') ? part : `http://${part}`
          return (
            <a key={i} href={href} target="_blank" rel="noopener noreferrer" className="text-primary underline hover:opacity-80 break-words">
              {part}
            </a>
          )
        }
        return <span key={i}>{part}</span>
      })}
    </>
  )
}

function InlineBold({ text }: { text: string }) {
  const parts = text.split(/\*\*(.+?)\*\*/g)
  return (
    <>
      {parts.map((part, idx) => idx % 2 === 1
        ? <strong key={idx}><Linkify text={part} /></strong>
        : <Linkify key={idx} text={part} />)}
    </>
  )
}

function AssistantContent({ text }: { text: string }) {
  const paragraphs = text.split(/\n\s*\n/)
  return (
    <div className="space-y-2 leading-relaxed">
      {paragraphs.map((para, i) => {
        const normalized = para.replace(/\u00A0/g, ' ')
        const lines = normalized.split('\n')
        const isHeading = lines.length === 1 && /^\s*###\s+/.test(lines[0])
        const isDashList = lines.length > 1 && lines.every(l => l.trim().startsWith('- '))
        const isNumberedList = lines.length > 1 && lines.every(l => /^\s*\d+\.\s+/.test(l))
        const isStarList = lines.length > 1 && lines.every(l => l.trim().startsWith('* '))
        const isInlineStarList = lines.length === 1 && /\*\s+/.test(normalized)
        if (isHeading) {
          const headingText = lines[0].replace(/^\s*###\s+/, '')
          return (
            <h3 key={i} className="text-base font-semibold text-text-primary mt-2">
              <InlineBold text={headingText} />
            </h3>
          )
        }
        if (isDashList) {
          return (
            <ul key={i} className="list-disc pl-5 space-y-1">
              {lines.map((l, j) => (
                <li key={j}><InlineBold text={l.trim().slice(2)} /></li>
              ))}
            </ul>
          )
        }
        if (isNumberedList) {
          return (
            <ol key={i} className="list-decimal pl-5 space-y-1">
              {lines.map((l, j) => {
                const item = l.replace(/^\s*\d+\.\s+/, '')
                return <li key={j}><InlineBold text={item} /></li>
              })}
            </ol>
          )
        }
        if (isStarList) {
          return (
            <ul key={i} className="list-disc pl-5 space-y-1">
              {lines.map((l, j) => (
                <li key={j}><InlineBold text={l.trim().slice(2)} /></li>
              ))}
            </ul>
          )
        }
        if (isInlineStarList) {
          const firstIdx = normalized.indexOf('* ')
          const preface = normalized.slice(0, firstIdx).trim()
          const items = normalized.slice(firstIdx).split(/\s*\*\s+/).filter(Boolean)
          return (
            <div key={i} className="space-y-1">
              {preface && (
                <p className="m-0"><InlineBold text={preface} /></p>
              )}
              <ul className="list-disc pl-5 space-y-1">
                {items.map((it, j) => (
                  <li key={j}><InlineBold text={it.trim()} /></li>
                ))}
              </ul>
            </div>
          )
        }
        return (
          <p key={i} className="m-0">
            <InlineBold text={normalized} />
          </p>
        )
      })}
    </div>
  )
}

export default function ChatButton() {
  const [open, setOpen] = useState(false)
  const [messages, setMessages] = useState<Msg[]>([])
  const [input, setInput] = useState('')
  const [sending, setSending] = useState(false)
  const [role, setRole] = useState<'cliente'|'empresa'|'admin'|'none'>('none')
  const endRef = useRef<HTMLDivElement | null>(null)

  function toggle(e: MouseEvent) {
    e.stopPropagation()
    setOpen(v => !v)
  }

  useEffect(() => { endRef.current?.scrollIntoView({ behavior: 'smooth' }) }, [messages, open])

  useEffect(() => {
    // cargar el rol/tipo de usuario para enviar al backend
    const uid = localStorage.getItem('userId')
    if (!uid) { setRole('none'); return }
    ;(async () => {
      try {
        const res = await fetch(`${API_BASE}/auth/profile?userId=${uid}`)
        if (res.ok) {
          const p = await res.json()
          if (p?.tipo === 'cliente' || p?.tipo === 'empresa' || p?.tipo === 'admin') setRole(p.tipo)
          else setRole('none')
        } else {
          setRole('none')
        }
      } catch {
        setRole('none')
      }
    })()
  }, [])

  async function send() {
    const text = input.trim()
    if (!text || sending) return
    setInput('')
    setMessages(prev => [...prev, { role: 'user', text }])
    setSending(true)
    try {
      const res = await fetch(`${API_BASE}/ai/chat`, { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify({ message: text, role }) })
      if (res.ok) {
        const data = await res.json()
        setMessages(prev => [...prev, { role: 'assistant', text: data.reply || '' }])
      } else {
        setMessages(prev => [...prev, { role: 'assistant', text: 'No pude responder en este momento.' }])
      }
    } catch {
      setMessages(prev => [...prev, { role: 'assistant', text: 'Error de red.' }])
    } finally {
      setSending(false)
    }
  }

  return (
    <>
      <button
        type="button"
        onClick={toggle}
        aria-label="Abrir chat"
        className="fixed bottom-6 right-6 z-50 h-14 w-14 rounded-full bg-primary text-white shadow-xl hover:opacity-90 focus:outline-none focus:ring-4 focus:ring-primary/30 flex items-center justify-center"
      >
        <span className="material-symbols-outlined" style={{ fontSize: 28 }}>chat</span>
      </button>

      {open && (
        <div className="fixed bottom-24 right-6 z-50 w-[min(420px,92vw)] h-[60vh] rounded-2xl border-2 border-border-soft bg-white shadow-2xl overflow-hidden flex flex-col">
          <div className="px-4 py-3 border-b border-border-soft bg-background-secondary flex items-center justify-between">
            <div className="font-semibold">Asistente Qapac</div>
            <button onClick={() => setOpen(false)} className="text-text-secondary hover:text-primary" aria-label="Cerrar"><span className="material-symbols-outlined">close</span></button>
          </div>
          <div className="flex-1 overflow-y-auto p-3 space-y-2 text-sm">
            {messages.length === 0 && (
              <div className="text-text-secondary">Hola, soy tu asistente de Qapac. Pregúntame sobre las funciones de la app.</div>
            )}
            {messages.map((m, i) => (
              <div key={i} className={`flex ${m.role === 'user' ? 'justify-end' : 'justify-start'}`}>
                <div className={`px-3 py-2 rounded-xl border ${m.role === 'user' ? 'bg-primary text-white border-transparent' : 'bg-white/70 text-text-primary border-border-soft'}`}>
                  {m.role === 'assistant' ? <AssistantContent text={m.text} /> : m.text}
                </div>
              </div>
            ))}
            {sending && messages[messages.length - 1]?.role === 'user' && (
              <div className="flex justify-start">
                <div
                  className="px-3 py-2 rounded-xl border bg-white/70 text-text-secondary border-border-soft inline-flex items-center gap-3"
                  aria-live="polite"
                  aria-label="El asistente está escribiendo"
                >
                  <div className="flex items-end gap-1">
                    <span className="w-2 h-2 rounded-full bg-gray-400 animate-bounce" style={{ animationDelay: '0ms' }} />
                    <span className="w-2 h-2 rounded-full bg-gray-400 animate-bounce" style={{ animationDelay: '150ms' }} />
                    <span className="w-2 h-2 rounded-full bg-gray-400 animate-bounce" style={{ animationDelay: '300ms' }} />
                  </div>
                  <span className="sr-only">El asistente está escribiendo</span>
                </div>
              </div>
            )}
            <div ref={endRef} />
          </div>
          <div className="p-3 border-t border-border-soft flex items-center gap-2">
            <input
              value={input}
              onChange={e => setInput(e.target.value)}
              onKeyDown={e => { if (e.key === 'Enter') send() }}
              placeholder="Escribe tu pregunta sobre Qapac..."
              className="flex-1 rounded-lg border border-border-soft bg-white/70 px-3 py-2 outline-none focus:border-primary"
            />
            <button onClick={send} disabled={sending} className="px-4 py-2 rounded-lg bg-primary text-white font-bold hover:opacity-90 disabled:opacity-60">Enviar</button>
          </div>
        </div>
      )}
    </>
  )
}
