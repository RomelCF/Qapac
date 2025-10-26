import { Link } from 'react-router-dom'

export default function Register() {
  return (
    <div className="bg-background-light text-text-primary min-h-screen flex items-center justify-center p-4">
      <div className="w-full max-w-2xl text-center">
        <img
          alt="Logo"
          className="mx-auto h-24 md:h-28 w-auto mb-4"
          src="/assets/logo.png"
        />
        <h1 className="font-display text-4xl font-bold text-primary mb-2">ELIGE TU TIPO DE CUENTA</h1>
        <p className="text-text-secondary mb-10">Para continuar, por favor selecciona cómo te quieres registrar.</p>

        <div className="grid md:grid-cols-2 gap-8">
          <Link to="/register/empresa" className="group">
            <div className="bg-background-secondary rounded-xl border-2 border-border-soft p-8 flex flex-col items-center justify-center aspect-square hover:border-primary hover:shadow-2xl hover:shadow-primary/20 transition-all duration-300 transform hover:-translate-y-2">
              <span className="material-symbols-outlined text-6xl text-primary mb-4">apartment</span>
              <h2 className="font-display text-2xl font-bold text-text-primary mb-2">EMPRESA</h2>
              <p className="text-text-secondary text-center">Registra tu empresa de transportes y ofrece tus servicios en nuestra plataforma.</p>
            </div>
          </Link>

          <Link to="/register/cliente" className="group">
            <div className="bg-background-secondary rounded-xl border-2 border-border-soft p-8 flex flex-col items-center justify-center aspect-square hover:border-primary hover:shadow-2xl hover:shadow-primary/20 transition-all duration-300 transform hover:-translate-y-2">
              <span className="material-symbols-outlined text-6xl text-primary mb-4">person</span>
              <h2 className="font-display text-2xl font-bold text-text-primary mb-2">CLIENTE</h2>
              <p className="text-text-secondary text-center">Crea tu cuenta personal para comprar pasajes y gestionar tus viajes de forma fácil.</p>
            </div>
          </Link>
        </div>

        <p className="mt-10 text-text-secondary">
          ¿Ya tienes una cuenta?{' '}
          <Link to="/login" className="font-bold text-accent hover:text-primary">Inicia sesión aquí</Link>
        </p>
      </div>
    </div>
  )
}
