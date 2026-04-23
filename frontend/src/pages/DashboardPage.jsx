import { useEffect, useState } from 'react';
import API from '../services/api';

export default function DashboardPage() {
  const [stats, setStats] = useState({
    total: 0,
    active: 0,
    pending: 0,
    highRisk: 0
  });

  useEffect(() => {
    API.get('/controls/stats')
      .then(res => setStats(res.data))
      .catch(() => {});
  }, []);

  return (
    <div className="p-6">
      <h2 className="text-2xl font-bold mb-6">Dashboard</h2>
      <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
        <div className="bg-blue-800 text-white p-6 rounded shadow text-center">
          <p className="text-4xl font-bold">{stats.total}</p>
          <p className="mt-2">Total Controls</p>
        </div>
        <div className="bg-green-600 text-white p-6 rounded shadow text-center">
          <p className="text-4xl font-bold">{stats.active}</p>
          <p className="mt-2">Active</p>
        </div>
        <div className="bg-yellow-500 text-white p-6 rounded shadow text-center">
          <p className="text-4xl font-bold">{stats.pending}</p>
          <p className="mt-2">Pending</p>
        </div>
        <div className="bg-red-600 text-white p-6 rounded shadow text-center">
          <p className="text-4xl font-bold">{stats.highRisk}</p>
          <p className="mt-2">High Risk</p>
        </div>
      </div>
    </div>
  );
}s