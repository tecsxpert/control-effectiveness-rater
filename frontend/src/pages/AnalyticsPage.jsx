import { useEffect, useState } from 'react';
import API from '../services/api';
import {
  LineChart, Line, BarChart, Bar,
  XAxis, YAxis, CartesianGrid,
  Tooltip, ResponsiveContainer, Legend
} from 'recharts';

export default function AnalyticsPage() {
  const [period, setPeriod] = useState('weekly');
  const [data, setData] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    setLoading(true);
    API.get(`/controls/analytics?period=${period}`)
      .then(res => {
        setData(res.data);
        setLoading(false);
      })
      .catch(() => {
        // Use dummy data if API not ready
        setData([
          { label: 'Week 1', active: 4, pending: 2, highRisk: 1 },
          { label: 'Week 2', active: 6, pending: 3, highRisk: 2 },
          { label: 'Week 3', active: 8, pending: 1, highRisk: 1 },
          { label: 'Week 4', active: 10, pending: 4, highRisk: 3 },
        ]);
        setLoading(false);
      });
  }, [period]);

  return (
    <div className="p-6">
      <h2 className="text-2xl font-bold mb-6">Analytics</h2>

      {/* Period Selector */}
      <div className="flex space-x-4 mb-6">
        <button
          onClick={() => setPeriod('weekly')}
          className={`px-4 py-2 rounded ${period === 'weekly'
            ? 'bg-blue-800 text-white'
            : 'bg-gray-200 text-gray-700'}`}
        >
          Weekly
        </button>
        <button
          onClick={() => setPeriod('monthly')}
          className={`px-4 py-2 rounded ${period === 'monthly'
            ? 'bg-blue-800 text-white'
            : 'bg-gray-200 text-gray-700'}`}
        >
          Monthly
        </button>
        <button
          onClick={() => setPeriod('yearly')}
          className={`px-4 py-2 rounded ${period === 'yearly'
            ? 'bg-blue-800 text-white'
            : 'bg-gray-200 text-gray-700'}`}
        >
          Yearly
        </button>
      </div>

      {loading ? (
        <div className="flex justify-center items-center h-64">
          <p className="text-gray-500">Loading analytics...</p>
        </div>
      ) : (
        <div className="space-y-8">

          {/* Bar Chart */}
          <div className="bg-white p-6 rounded shadow">
            <h3 className="text-xl font-bold mb-4">Controls by Status</h3>
            <ResponsiveContainer width="100%" height={300}>
              <BarChart data={data}>
                <CartesianGrid strokeDasharray="3 3" />
                <XAxis dataKey="label" />
                <YAxis />
                <Tooltip />
                <Legend />
                <Bar dataKey="active" fill="#16a34a" name="Active" />
                <Bar dataKey="pending" fill="#ca8a04" name="Pending" />
                <Bar dataKey="highRisk" fill="#dc2626" name="High Risk" />
              </BarChart>
            </ResponsiveContainer>
          </div>

          {/* Line Chart */}
          <div className="bg-white p-6 rounded shadow">
            <h3 className="text-xl font-bold mb-4">Trend Over Time</h3>
            <ResponsiveContainer width="100%" height={300}>
              <LineChart data={data}>
                <CartesianGrid strokeDasharray="3 3" />
                <XAxis dataKey="label" />
                <YAxis />
                <Tooltip />
                <Legend />
                <Line
                  type="monotone"
                  dataKey="active"
                  stroke="#16a34a"
                  name="Active"
                />
                <Line
                  type="monotone"
                  dataKey="pending"
                  stroke="#ca8a04"
                  name="Pending"
                />
                <Line
                  type="monotone"
                  dataKey="highRisk"
                  stroke="#dc2626"
                  name="High Risk"
                />
              </LineChart>
            </ResponsiveContainer>
          </div>

        </div>
      )}
    </div>
  );
}