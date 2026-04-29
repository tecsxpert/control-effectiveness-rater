import { useState } from 'react';
import API from '../services/api';

export default function LoginPage({ onLogin }) {
  const [form, setForm] = useState({ username: '', password: '' });
  const [error, setError] = useState('');

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const handleLogin = () => {
    API.post('/auth/login', form)
      .then(res => {
        localStorage.setItem('token', res.data.token);
        onLogin();
      })
      .catch(() => {
        setError('Invalid username or password');
      });
  };

  return (
    <div className="flex justify-center items-center h-screen bg-gray-100">
      <div className="bg-white p-8 rounded shadow-md w-96">
        <h2 className="text-2xl font-bold mb-6 text-center text-blue-800">
          Control Effectiveness Rater
        </h2>
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
          />
          <input
            className="w-full border p-2 rounded"
            placeholder="Password"
            name="password"
            type="password"
            value={form.password}
            onChange={handleChange}
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