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
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const navigate = useNavigate();

  const fetchControls = useCallback(() => {
    setLoading(true);

    if (search) {
      API.get('/controls/search', { params: { q: search, page, size: 10 } })
        .then(res => {
          setControls(res.data.content || []);
          setTotalPages(res.data.totalPages || 0);
          setLoading(false);
        })
        .catch(() => setLoading(false));
    } else if (status || fromDate || toDate) {
      const params = { page, size: 10 };
      if (status) params.status = status;
      if (fromDate) params.startDate = fromDate;
      if (toDate) params.endDate = toDate;
      API.get('/controls/filter', { params })
        .then(res => {
          setControls(res.data.content || []);
          setTotalPages(res.data.totalPages || 0);
          setLoading(false);
        })
        .catch(() => setLoading(false));
    } else {
      API.get('/controls/all', { params: { page, size: 10 } })
        .then(res => {
          setControls(res.data.content || []);
          setTotalPages(res.data.totalPages || 0);
          setLoading(false);
        })
        .catch(() => setLoading(false));
    }
  }, [search, status, fromDate, toDate, page]);

  useEffect(() => {
    setPage(0);
  }, [search, status, fromDate, toDate]);

  useEffect(() => {
    const timer = setTimeout(() => fetchControls(), 400);
    return () => clearTimeout(timer);
  }, [fetchControls]);

  return (
    <div className="p-4 md:p-6">
      <h2 className="text-xl md:text-2xl font-bold mb-4">
        Control Effectiveness List
      </h2>

      <div className="flex justify-between items-center mb-4">
        <button
          onClick={() => navigate('/create')}
          className="bg-blue-800 text-white px-4 py-2 rounded hover:bg-blue-900 text-sm"
        >
          + New Control
        </button>
        <button
          onClick={() => {
            const token = localStorage.getItem('token');
            fetch('http://localhost:8080/api/controls/export', {
              headers: { Authorization: `Bearer ${token}` }
            })
              .then(r => r.blob())
              .then(blob => {
                const url = window.URL.createObjectURL(blob);
                const a = document.createElement('a');
                a.href = url;
                a.download = 'controls_export.csv';
                a.click();
              });
          }}
          className="bg-green-600 text-white px-4 py-2 rounded hover:bg-green-700 text-sm"
        >
          Export CSV
        </button>
      </div>

      <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-4 gap-3 mb-6">
        <input
          className="border p-2 rounded w-full"
          placeholder="Search controls..."
          value={search}
          onChange={e => setSearch(e.target.value)}
        />
        <select
          className="border p-2 rounded w-full"
          value={status}
          onChange={e => setStatus(e.target.value)}
        >
          <option value="">All Statuses</option>
          <option value="PENDING">PENDING</option>
          <option value="IN_PROGRESS">IN_PROGRESS</option>
          <option value="COMPLETED">COMPLETED</option>
          <option value="REVIEWED">REVIEWED</option>
          <option value="ARCHIVED">ARCHIVED</option>
        </select>
        <input
          className="border p-2 rounded w-full"
          type="date"
          value={fromDate}
          onChange={e => setFromDate(e.target.value)}
        />
        <input
          className="border p-2 rounded w-full"
          type="date"
          value={toDate}
          onChange={e => setToDate(e.target.value)}
        />
      </div>

      {loading ? (
        <div className="flex justify-center items-center h-64">
          <div className="animate-spin rounded-full h-10 w-10 border-b-2 border-blue-800"></div>
        </div>
      ) : controls.length === 0 ? (
        <div className="flex flex-col justify-center items-center h-64 text-gray-500">
          <p className="text-lg">No controls found.</p>
          <button
            onClick={() => navigate('/create')}
            className="mt-4 bg-blue-800 text-white px-4 py-2 rounded hover:bg-blue-900"
          >
            Create your first control
          </button>
        </div>
      ) : (
        <>
          <div className="hidden md:block overflow-x-auto">
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
                  <tr key={control.id} className="border-b hover:bg-gray-50">
                    <td className="p-3">{control.id}</td>
                    <td className="p-3 font-medium">{control.controlName}</td>
                    <td className="p-3">{control.category}</td>
                    <td className="p-3">
                      <span className={`px-2 py-1 rounded text-xs font-medium ${
                        control.status === 'COMPLETED' ? 'bg-green-100 text-green-800' :
                        control.status === 'PENDING' ? 'bg-yellow-100 text-yellow-800' :
                        control.status === 'IN_PROGRESS' ? 'bg-blue-100 text-blue-800' :
                        'bg-gray-100 text-gray-800'
                      }`}>
                        {control.status}
                      </span>
                    </td>
                    <td className="p-3">
                      <span className={`font-bold ${
                        control.effectivenessScore >= 75 ? 'text-green-600' :
                        control.effectivenessScore >= 50 ? 'text-yellow-600' :
                        'text-red-600'
                      }`}>
                        {control.effectivenessScore ?? '-'}
                      </span>
                    </td>
                    <td className="p-3">
                      <span className={`px-2 py-1 rounded text-xs font-medium ${
                        control.riskLevel === 'CRITICAL' ? 'bg-red-100 text-red-800' :
                        control.riskLevel === 'HIGH' ? 'bg-orange-100 text-orange-800' :
                        control.riskLevel === 'MEDIUM' ? 'bg-yellow-100 text-yellow-800' :
                        'bg-green-100 text-green-800'
                      }`}>
                        {control.riskLevel}
                      </span>
                    </td>
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
          </div>

          <div className="md:hidden space-y-4">
            {controls.map(control => (
              <div key={control.id} className="bg-white border rounded shadow p-4">
                <div className="flex justify-between items-center mb-2">
                  <h3 className="font-bold text-blue-800">{control.controlName}</h3>
                  <span className="text-xs bg-blue-100 text-blue-800 px-2 py-1 rounded">
                    {control.status}
                  </span>
                </div>
                <p className="text-sm text-gray-500">Category: {control.category}</p>
                <p className="text-sm text-gray-500">Score: {control.effectivenessScore ?? '-'}</p>
                <p className="text-sm text-gray-500">Risk: {control.riskLevel}</p>
                <button
                  onClick={() => navigate(`/detail/${control.id}`)}
                  className="mt-3 w-full bg-blue-800 text-white py-2 rounded text-sm hover:bg-blue-900"
                >
                  View Details
                </button>
              </div>
            ))}
          </div>

          {totalPages > 1 && (
            <div className="flex justify-center space-x-2 mt-6">
              <button
                onClick={() => setPage(p => Math.max(0, p - 1))}
                disabled={page === 0}
                className="px-4 py-2 border rounded disabled:opacity-50 hover:bg-gray-100"
              >
                Previous
              </button>
              <span className="px-4 py-2 text-gray-600">
                Page {page + 1} of {totalPages}
              </span>
              <button
                onClick={() => setPage(p => Math.min(totalPages - 1, p + 1))}
                disabled={page >= totalPages - 1}
                className="px-4 py-2 border rounded disabled:opacity-50 hover:bg-gray-100"
              >
                Next
              </button>
            </div>
          )}
        </>
      )}
    </div>
  );
}
