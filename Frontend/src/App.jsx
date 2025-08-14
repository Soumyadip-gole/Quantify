import React, { useEffect, useState } from 'react';
import { Search, Menu, X, ArrowRight } from 'react-feather';
import logo from './assets/logo.png';

const NavLinks = ({ className }) => (
  <nav className={className}>
    <a href="#features" className="hover:text-brand-gold transition-colors">Features</a>
    <a href="#solution" className="hover:text-brand-gold transition-colors">Solution</a>
    <a href="#pricing" className="hover:text-brand-gold transition-colors">Pricing</a>
    <a href="#about" className="hover:text-brand-gold transition-colors">About</a>
  </nav>
);

function App() {
  const [isMenuOpen, setIsMenuOpen] = useState(false);

  useEffect(() => {
    const link = document.querySelector("link[rel='icon']") || document.createElement('link');
    link.rel = 'icon';
    link.type = 'image/png';
    link.href = logo;
    if (!link.parentNode) document.head.appendChild(link);
  }, []);

  return (
    <div className="bg-brand-dark text-brand-light font-sans">
      <div className="relative min-h-screen overflow-hidden">
        {/* Animated Background Elements */}
        <div className="pointer-events-none absolute inset-0">
          {/* Primary glow */}
          <div
            className="absolute top-1/4 right-1/4 w-96 h-96 rounded-full animate-pulse"
            style={{
              background: 'radial-gradient(circle at center, rgba(212, 175, 55, 0.15), transparent 70%)',
              animation: 'float 6s ease-in-out infinite',
            }}
          />
          {/* Secondary glow */}
          <div
            className="absolute bottom-1/4 left-1/3 w-64 h-64 rounded-full"
            style={{
              background: 'radial-gradient(circle at center, rgba(212, 175, 55, 0.08), transparent 60%)',
              animation: 'float 8s ease-in-out infinite reverse',
            }}
          />
          {/* Grid pattern */}
          <div
            className="absolute inset-0 opacity-[0.02]"
            style={{
              backgroundImage: `
                linear-gradient(rgba(212, 175, 55, 0.3) 1px, transparent 1px),
                linear-gradient(90deg, rgba(212, 175, 55, 0.3) 1px, transparent 1px)
              `,
              backgroundSize: '60px 60px',
            }}
          />
        </div>

        {/* Header */}
        <header className="relative z-20 backdrop-blur-sm bg-brand-dark/80">
          <div className="container mx-auto px-6 py-5 flex justify-between items-center">
            <div className="flex items-center gap-3">
              <img src={logo} alt="Quantify logo" className="h-8 w-8 object-contain" />
              <span className="text-xl font-bold tracking-wider">QUANTIFY</span>
            </div>
            <NavLinks className="hidden lg:flex items-center gap-8 text-sm text-brand-gray" />
            <div className="hidden lg:flex items-center gap-4">
              <button className="text-sm text-brand-gray hover:text-brand-light transition-colors">Sign In</button>
              <button className="rounded-md bg-brand-gold px-4 py-2 text-sm font-bold text-brand-dark shadow-sm hover:brightness-110 transition">Get Started</button>
            </div>
            <button className="lg:hidden" onClick={() => setIsMenuOpen(!isMenuOpen)}>
              {isMenuOpen ? <X className="text-brand-gray" /> : <Menu className="text-brand-gray" />}
            </button>
          </div>
        </header>

        {/* Mobile Menu */}
        {isMenuOpen && (
          <div className="lg:hidden absolute top-20 left-0 right-0 bg-brand-dark/95 backdrop-blur-sm z-10">
            <div className="container mx-auto px-6 py-8 flex flex-col items-center gap-6">
              <NavLinks className="flex flex-col items-center gap-6 text-brand-gray" />
              <div className="flex flex-col items-center gap-4 w-full pt-6 border-t border-brand-gray/20">
                <button className="text-sm text-brand-gray hover:text-brand-light transition-colors w-full">Sign In</button>
                <button className="rounded-md bg-brand-gold px-4 py-2 text-sm font-bold text-brand-dark shadow-sm hover:brightness-110 transition w-full">Get Started</button>
              </div>
            </div>
          </div>
        )}

        {/* Hero Section */}
        <main className="relative z-10 min-h-screen flex items-center">
          <div className="container mx-auto px-6 grid lg:grid-cols-2 gap-12 items-center">
            {/* Left Content */}
            <div className="space-y-8">
              <div>
                <h1 className="text-5xl lg:text-6xl xl:text-7xl font-black tracking-tight leading-[0.9]">
                  Welcome<span className="text-brand-gold">.</span>
                </h1>
                <p className="mt-6 text-xl text-brand-gray max-w-2xl leading-relaxed">
                  Transform market data into actionable insights. Monitor trends, analyze patterns, and make informed decisions with real-time market intelligence.
                </p>
              </div>

              {/* CTA Section */}
              <div className="space-y-6">
                <div className="flex flex-col sm:flex-row gap-4">
                  <button className="relative inline-flex items-center justify-center gap-3 rounded-xl border-2 border-brand-gold bg-brand-gold/20 px-10 py-5 text-brand-gold font-bold text-xl shadow-lg shadow-brand-gold/50 hover:shadow-xl hover:shadow-brand-gold/70 hover:bg-brand-gold/30 hover:border-brand-gold hover:scale-105 hover:text-yellow-100 transition-all duration-300 group backdrop-blur-sm overflow-hidden">
                    <span className="relative z-10 drop-shadow-lg">Explore Now</span>

                    {/* Bright golden shine effect */}
                    <div className="absolute inset-0 bg-gradient-to-r from-transparent via-yellow-300/8 to-transparent -translate-x-full group-hover:translate-x-full transition-transform duration-700 ease-out transform -skew-x-12"></div>

                    {/* Golden sparkle background */}
                    <div className="absolute inset-0 bg-gradient-to-br from-brand-gold/30 via-yellow-400/20 to-brand-gold/30 opacity-60 group-hover:opacity-90 transition-opacity duration-300 rounded-xl"></div>

                    {/* Golden border highlight */}
                    <div className="absolute inset-0 rounded-xl border-2 border-white-400/40 "></div>
                  </button>
                </div>
              </div>
            </div>

            {/* Right Content - Clean Logo Display */}
            <div className="relative lg:h-[600px] flex items-center justify-center">
              <div className="relative">
                <img
                  src={logo}
                  alt="Quantify"
                  className="w-96 h-96 object-contain opacity-25"
                  style={{ animation: 'spin 30s linear infinite' }}
                />
              </div>
            </div>
          </div>
        </main>
      </div>

      {/* CSS Animations */}
      <style jsx>{`
        @keyframes float {
          0%, 100% { transform: translateY(0px); }
          50% { transform: translateY(-20px); }
        }
      `}</style>
    </div>
  );
}

export default App;
