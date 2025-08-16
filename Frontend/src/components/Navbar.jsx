import React, { useState } from 'react';
import { Menu, X } from 'lucide-react';

// A reusable, responsive Navbar component
export default function Navbar() {
    const [open, setOpen] = useState(false);

    // Navigation links data
    const navLinks = [
        { name: 'Home', href: '#' },
        { name: 'Watchlist', href: '#' },
        { name: 'User', href: '#' },
    ];

    return (
        <header className="sticky top-0 z-50 border-b border-zinc-800 bg-black/80 backdrop-blur-sm">
            <div className="container mx-auto px-6 h-16 flex items-center justify-between">
                {/* Logo / Brand Name */}
                <a href="#" className="flex items-center gap-3">
                    <span className="text-xl font-bold tracking-wider text-white">Quantify</span>
                </a>

                {/* Desktop Navigation */}
                <nav className="hidden md:flex items-center gap-8">
                    {navLinks.map(link => (
                        <a key={link.name} href={link.href} className="text-sm text-zinc-300 hover:text-white transition-colors">
                            {link.name}
                        </a>
                    ))}
                </nav>

                {/* Mobile Menu Button */}
                <button
                    aria-label="Toggle menu"
                    className="md:hidden text-zinc-300 hover:text-white"
                    onClick={() => setOpen(!open)}
                >
                    {open ? <X size={20} /> : <Menu size={20} />}
                </button>
            </div>

            {/* Mobile Navigation Menu */}
            {open && (
                <div className="md:hidden border-t border-zinc-800">
                    <div className="px-6 py-4 flex flex-col gap-4">
                        {navLinks.map(link => (
                            <a
                                key={link.name}
                                href={link.href}
                                className="text-sm text-zinc-300 hover:text-white"
                                onClick={() => setOpen(false)}
                            >
                                {link.name}
                            </a>
                        ))}
                    </div>
                </div>
            )}
        </header>
    );
}
