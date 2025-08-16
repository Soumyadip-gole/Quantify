import React from 'react';

// Minimal golden button: subtle, clean, accessible
export default function Button({ as: As = 'button', className = '', children, ...props }) {
  const base = 'inline-flex items-center justify-center gap-2 rounded-lg border-2 border-brand-gold text-brand-gold px-6 py-3 font-semibold focus:outline-none focus:ring-2 focus:ring-brand-gold/50 focus:ring-offset-0 hover:bg-brand-gold/10 active:bg-brand-gold/15 transition-colors duration-200';
  return (
    <As className={`${base} ${className}`} {...props}>
      {children}
    </As>
  );
}

