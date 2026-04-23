import { Link } from 'react-router-dom';
import { useAuth } from '../services/AuthContext';

export default function Navbar() {
  const { token, logout } = useAuth();

  return (
    <nav className="bg-blue-800 text-white p-4 flex justify-between items-center">
      <h1 className="text-xl font-bold">Control Effectiveness Rater</h1>
      {token && (
        <div className="space-x-4">
          <Link to="/" className="hover:underline">Dashboard</Link>
          <Link to="/list" className="hover:underline">List</Link>
          <Link to="/create" className="hover:underline">Create New</Link>
          <button onClick={logout} className="hover:underline">Logout</button>
        </div>
      )}
    </nav>
  );
}