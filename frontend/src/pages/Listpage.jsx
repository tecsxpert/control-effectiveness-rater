import { useEffect, useState, useCallback } from 'react';
import { useNavigate } from 'react-router-dom';
import API from '../services/api';

export default function ListPage() {
  const [controls, setControls] = useState([]);
  const [loading, setLoading] = useState(true);
  const [search, setSearch] = useState('');
  const [status, setStatus] = useState('');
  const [fromDate, setFromDate] = useState('');
  const [toDate, setToDate] = useState('');
  const navigate = useNavigate();

  const fetchControls = useCallback(() => {
    setLoading(true);
    const params = {};
    if (search) params.q = search;
    if (status) params.status = status;
    if (fromDate) params.from = fromDate;
    if (toDate) params.to = toDate;

    API.get('/controls', { params })
      .then(res => {
        setControls(res.data);
        setLoading(false);
      })
      .catch(() => setLoading(false));
  }, [search, status, fromDate, toDate]);

  // Debounced search - waits 500ms after user stops typing
  useEffect(() => {
    const timer = setTimeout(() => {
      fetchControls();
    }, 500);
    return () => clearTimeout(timer);
  }, [fetchControls]);

  return (
    <div className="p-6">
      <h2 className="text-2xl font-bold mb-4">Control Effectiveness List</h2>

      {/* Export CSV Button */}
<div className="flex justify-end mb-4">
  <button
    onClick={() => {
      window.open('http://localhost:8080/api/controls/export', '_blank');
    }}
    className="bg-green-600 text-white px-4 py-2 rounded hover:bg-green-700"
  >
    Export CSV
  </button>
</div>{/* Search and Filter Bar */}
      <div className="grid grid-cols-1 md:grid-cols-4 gap-4 mb-6">
        <input
          className="border p-2 rounded"
          placeholder="Search controls..."
          value={search}
          onChange={e => setSearch(e.target.value)}
        />
        <select
          className="border p-2 rounded"
          value={status}
          onChange={e => setStatus(e.target.value)}
        >
          <option value="">All Statuses</option>
          <option value="ACTIVE">ACTIVE</option>
          <option value="PENDING">PENDING</option>
          <option value="INACTIVE">INACTIVE</option>
          <option value="REVIEW">REVIEW</option>
        </select>
        <input
          className="border p-2 rounded"
          type="date"
          placeholder="From Date"
          value={fromDate}
          onChange={e => setFromDate(e.target.value)}
        />
        <input
          className="border p-2 rounded"
          type="date"
          placeholder="To Date"
          value={toDate}
          onChange={e => setToDate(e.target.value)}
        />
      </div>

      {loading ? (
        <div className="flex justify-center items-center h-64">
          <p className="text-gray-500 text-lg">Loading...</p>
        </div>
      ) : controls.length === 0 ? (
        <div className="flex justify-center items-center h-64">
          <p className="text-gray-500 text-lg">No controls found.</p>
        </div>
      ) : (
        <table className="w-full border-collapse border border-gray-300">
          <thead className="bg-blue-800 text-white">
            <tr>
              <th className="p-3 text-left">ID</th>
              <th className="p-3 text-left">Control Name</th>
              <th className="p-3 text-left">Category</th>
              <th className="p-3 text-left">Status</th>
              <th className="p-3 text-left">Score</th>
              <th className="p-3 text-left">Risk Level</th>
              <th className="p-3 text-left">Actions</th>
            </tr>
          </thead>
          <tbody>
            {controls.map(control => (
              <tr key={control.id} className="border-b hover:bg-gray-100">
                <td className="p-3">{control.id}</td>
                <td className="p-3">{control.controlName}</td>
                <td className="p-3">{control.category}</td>
                <td className="p-3">{control.status}</td>
                <td className="p-3">{control.effectivenessScore}</td>
                <td className="p-3">{control.riskLevel}</td>
                <td className="p-3">
                  <button
                    onClick={() => navigate(`/detail/${control.id}`)}
                    className="bg-blue-800 text-white px-3 py-1 rounded text-sm hover:bg-blue-900"
                  >
                    View
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
}