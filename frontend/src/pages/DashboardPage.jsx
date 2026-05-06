import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import API from '../services/api';
import {
  BarChart, Bar, XAxis, YAxis, CartesianGrid,
  Tooltip, ResponsiveContainer, Cell
} from 'recharts';

export default function DashboardPage() {
  const navigate = useNavigate();
  const [stats, setStats] = useState(null);
  const [chartData, setChartData] = useState([]);
  const [riskData, setRiskData] = useState([]);

  useEffect(() => {
    API.get('/controls/stats')
      .then(res => {
        const d = res.data;
        setStats(d);

        if (d.byStatus) {
          setChartData(
            Object.entries(d.byStatus).map(([name, value]) => ({ name, value }))
          );
        }
        if (d.byRiskLevel) {
          setRiskData(
            Object.entries(d.byRiskLevel).map(([name, value]) => ({ name, value }))
          );
        }
      })
      .catch(() => {});
  }, []);

  const COLORS = { PENDING: '#ca8a04', IN_PROGRESS: '#2563eb', COMPLETED: '#16a34a', REVIEWED: '#7c3aed', ARCHIVED: '#6b7280' };
  const RISK_COLORS = { LOW: '#16a34a', MEDIUM: '#ca8a04', HIGH: '#ea580c', CRITICAL: '#dc2626' };

  return (
    <div className="p-6">
      <h2 className="text-2xl font-bold mb-6">Dashboard</h2>

      <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-4 gap-4 mb-8">
        <div className="bg-blue-800 text-white p-6 rounded shadow text-center">
          <p className="text-4xl font-bold">{stats?.totalControls ?? 0}</p>
          <p className="mt-2">Total Controls</p>
        </div>
        <div className="bg-yellow-500 text-white p-6 rounded shadow text-center">
          <p className="text-4xl font-bold">{stats?.pendingControls ?? 0}</p>
          <p className="mt-2">Pending</p>
        </div>
        <div className="bg-green-600 text-white p-6 rounded shadow text-center">
          <p className="text-4xl font-bold">{stats?.completedControls ?? 0}</p>
          <p className="mt-2">Completed</p>
        </div>
        <div className="bg-purple-600 text-white p-6 rounded shadow text-center">
          <p className="text-4xl font-bold">{stats?.averageScore?.toFixed(1) ?? 0}</p>
          <p className="mt-2">Avg Score</p>
        </div>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-6 mb-8">
        {chartData.length > 0 && (
          <div className="bg-white p-6 rounded shadow">
            <h3 className="text-lg font-bold mb-4">Controls by Status</h3>
            <ResponsiveContainer width="100%" height={250}>
              <BarChart data={chartData}>
                <CartesianGrid strokeDasharray="3 3" />
                <XAxis dataKey="name" tick={{ fontSize: 12 }} />
                <YAxis allowDecimals={false} />
                <Tooltip />
                <Bar dataKey="value" name="Count">
                  {chartData.map((entry, i) => (
                    <Cell key={i} fill={COLORS[entry.name] || '#1d4ed8'} />
                  ))}
                </Bar>
              </BarChart>
            </ResponsiveContainer>
          </div>
        )}

        {riskData.length > 0 && (
          <div className="bg-white p-6 rounded shadow">
            <h3 className="text-lg font-bold mb-4">Controls by Risk Level</h3>
            <ResponsiveContainer width="100%" height={250}>
              <BarChart data={riskData}>
                <CartesianGrid strokeDasharray="3 3" />
                <XAxis dataKey="name" tick={{ fontSize: 12 }} />
                <YAxis allowDecimals={false} />
                <Tooltip />
                <Bar dataKey="value" name="Count">
                  {riskData.map((entry, i) => (
                    <Cell key={i} fill={RISK_COLORS[entry.name] || '#1d4ed8'} />
                  ))}
                </Bar>
              </BarChart>
            </ResponsiveContainer>
          </div>
        )}
      </div>

      <div className="flex gap-4">
        <button
          onClick={() => navigate('/list')}
          className="bg-blue-800 text-white px-6 py-3 rounded hover:bg-blue-900"
        >
          View All Controls
        </button>
        <button
          onClick={() => navigate('/create')}
          className="bg-green-600 text-white px-6 py-3 rounded hover:bg-green-700"
        >
          + New Control
        </button>
        <button
          onClick={() => navigate('/analytics')}
          className="bg-purple-600 text-white px-6 py-3 rounded hover:bg-purple-700"
        >
          Analytics
        </button>
      </div>
    </div>
  );
}
