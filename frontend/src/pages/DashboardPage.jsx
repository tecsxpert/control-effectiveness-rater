import { useEffect, useState } from 'react';
import API from '../services/api';
import {
  BarChart, Bar, XAxis, YAxis, CartesianGrid,
  Tooltip, ResponsiveContainer
} from 'recharts';

export default function DashboardPage() {
  const [stats, setStats] = useState({
    total: 0,
    active: 0,
    pending: 0,
    highRisk: 0
  });

  const [chartData, setChartData] = useState([
    { name: 'Active', value: 0 },
    { name: 'Pending', value: 0 },
    { name: 'Inactive', value: 0 },
    { name: 'Review', value: 0 },
  ]);

  useEffect(() => {
    API.get('/controls/stats')
      .then(res => {
        setStats(res.data);
        setChartData([
          { name: 'Active', value: res.data.active || 0 },
          { name: 'Pending', value: res.data.pending || 0 },
          { name: 'Inactive', value: res.data.inactive || 0 },
          { name: 'Review', value: res.data.review || 0 },
        ]);
      })
      .catch(() => {});
  }, []);

  return (
    <div className="p-6">
      <h2 className="text-2xl font-bold mb-6">Dashboard</h2>

      {/* KPI Cards */}
      <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-4 gap-4 mb-8">
        <div className="bg-blue-800 text-white p-6 rounded shadow text-center">
          <p className="text-4xl font-bold">{stats.total}</p>
          <p className="mt-2">Total Controls</p>
        </div>
        <div className="bg-green-600 text-white p-6 rounded shadow text-center">
          <p className="mt-2">Active Controls</p>
        </div>
        <div className="bg-yellow-500 text-white p-6 rounded shadow text-center">
          <p className="text-4xl font-bold">{stats.pending}</p>
          <p className="mt-2">Pending</p>
        </div>
        <div className="bg-red-600 text-white p-6 rounded shadow text-center">
          <p className="text-4xl font-bold">{stats.inactive}</p>
          <p className="mt-2">Inactive</p>
        </div>
      </div>
    </div>
  );
}