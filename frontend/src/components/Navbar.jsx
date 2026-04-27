import { useState } from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../services/AuthContext';

export default function Navbar() {
  const { token, logout } = useAuth();
  const [menuOpen, setMenuOpen] = useState(false);

  return (
    <nav className="bg-blue-800 text-white p-4">
      <div className="flex justify-between items-center">
        <h1 className="text-xl font-bold">Control Effectiveness Rater</h1>

        {/* Hamburger Menu for Mobile */}
        {token && (
          <button
            className="md:hidden"
            onClick={() => setMenuOpen(!menuOpen)}
          >
            ☰
          </button>
        )}

        {/* Desktop Menu */}
        {token && (
          <div className="hidden md:flex space-x-4">
            <Link to="/" className="hover:underline">Dashboard</Link>
            <Link to="/list" className="hover:underline">List</Link>
            <Link to="/analytics" className="hover:underline">Analytics</Link>
            <Link to="/create" className="hover:underline">Create New</Link>
            <button onClick={logout} className="hover:underline">Logout</button>
          </div>
        )}
      </div>

      {/* Mobile Menu */}
      {token && menuOpen && (
        <div className="md:hidden flex flex-col space-y-2 mt-3">
          <Link to="/" className="hover:underline" onClick={() => setMenuOpen(false)}>Dashboard</Link>
          <Link to="/list" className="hover:underline" onClick={() => setMenuOpen(false)}>List</Link>
          <Link to="/analytics" className="hover:underline" onClick={() => setMenuOpen(false)}>Analytics</Link>
          <Link to="/create" className="hover:underline" onClick={() => setMenuOpen(false)}>Create New</Link>
          <button onClick={() => { logout(); setMenuOpen(false); }} className="hover:underline text-left">Logout</button>
        </div>
      )}
    </nav>
  );
}