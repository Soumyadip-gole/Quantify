import React from 'react';
import { Outlet } from 'react-router-dom';
import Navbar from '../components/Navbar.jsx';

export default function MainLayout() {
  return (
    <div className="bg-brand-dark text-brand-light min-h-screen font-sans">
      <Navbar />
      <Outlet />
    </div>
  );
}

