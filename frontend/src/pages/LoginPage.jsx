import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import API from '../services/api';
import { useAuth } from '../services/AuthContext';

export default function LoginPage() {
  const [form, setForm] = useState({ username: '', password: '' });
  const [error, setError] = useState('');
  const { login } = useAuth();
  const navigate = useNavigate();

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const handleLogin = () => {
    setError('');
    API.post('/auth/login', form)
      .then(res => {
        login(res.data.accessToken);
        navigate('/');
      })
      .catch(() => {
        setError('Invalid username or password');
      });
  };

  const handleKeyDown = (e) => {
    if (e.key === 'Enter') handleLogin();
  };

  return (
    <div className="flex justify-center items-center h-screen bg-gray-100">
      <div className="bg-white p-8 rounded shadow-md w-96">
        <h2 className="text-2xl font-bold mb-2 text-center text-blue-800">
          Control Effectiveness Rater
        </h2>
        <p className="text-center text-gray-500 text-sm mb-6">
          Default: admin / admin123
        </p>
        {error && (
          <div className="mb-4 p-3 bg-red-100 text-red-800 rounded">
            {error}
          </div>
        )}
        <div className="space-y-4">
          <input
            className="w-full border p-2 rounded"
            placeholder="Username"
            name="username"
            value={form.username}
            onChange={handleChange}
            onKeyDown={handleKeyDown}
          />
          <input
            className="w-full border p-2 rounded"
            placeholder="Password"
            name="password"
            type="password"
            value={form.password}
            onChange={handleChange}
            onKeyDown={handleKeyDown}
          />
          <button
            onClick={handleLogin}
            className="w-full bg-blue-800 text-white p-3 rounded hover:bg-blue-900"
          >
            Login
          </button>
        </div>
      </div>
    </div>
  );
}
