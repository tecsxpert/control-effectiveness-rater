import FileUpload from '../components/FileUpload';
import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import API from '../services/api';

export default function FormPage() {
  const navigate = useNavigate();
  const [form, setForm] = useState({
    controlName: '',
    category: '',
    status: 'PENDING',
    effectivenessScore: '',
    riskLevel: 'MEDIUM',
    assessor: '',
    department: '',
    controlDescription: ''
  });
  const [message, setMessage] = useState('');
  const [error, setError] = useState('');

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const handleSubmit = () => {
    setMessage('');
    setError('');
    const payload = {
      ...form,
      effectivenessScore: form.effectivenessScore ? parseInt(form.effectivenessScore) : null
    };
    API.post('/controls/create', payload)
      .then(res => {
        setMessage('Control created successfully! AI analysis is running in the background.');
        setTimeout(() => navigate(`/detail/${res.data.id}`), 1500);
      })
      .catch(err => {
        const msg = err.response?.data?.message || 'Error creating control. Please check all fields.';
        setError(msg);
      });
  };

  return (
    <div className="p-6 max-w-2xl mx-auto">
      <div className="flex items-center mb-6">
        <button onClick={() => navigate(-1)} className="mr-4 text-gray-500 hover:text-gray-700">
          ← Back
        </button>
        <h2 className="text-2xl font-bold">Create New Control</h2>
      </div>

      {message && (
        <div className="mb-4 p-3 bg-green-100 text-green-800 rounded">{message}</div>
      )}
      {error && (
        <div className="mb-4 p-3 bg-red-100 text-red-800 rounded">{error}</div>
      )}

      <div className="bg-white rounded shadow p-6 space-y-4">
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">Control Name *</label>
          <input
            className="w-full border p-2 rounded"
            placeholder="e.g. Firewall Rule Review"
            name="controlName"
            value={form.controlName}
            onChange={handleChange}
          />
        </div>
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">Category</label>
          <input
            className="w-full border p-2 rounded"
            placeholder="e.g. Network Security"
            name="category"
            value={form.category}
            onChange={handleChange}
          />
        </div>
        <div className="grid grid-cols-2 gap-4">
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Status</label>
            <select
              className="w-full border p-2 rounded"
              name="status"
              value={form.status}
              onChange={handleChange}
            >
              <option value="PENDING">PENDING</option>
              <option value="IN_PROGRESS">IN_PROGRESS</option>
              <option value="COMPLETED">COMPLETED</option>
              <option value="REVIEWED">REVIEWED</option>
              <option value="ARCHIVED">ARCHIVED</option>
            </select>
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Risk Level</label>
            <select
              className="w-full border p-2 rounded"
              name="riskLevel"
              value={form.riskLevel}
              onChange={handleChange}
            >
              <option value="LOW">LOW</option>
              <option value="MEDIUM">MEDIUM</option>
              <option value="HIGH">HIGH</option>
              <option value="CRITICAL">CRITICAL</option>
            </select>
          </div>
        </div>
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">Effectiveness Score (0–100)</label>
          <input
            className="w-full border p-2 rounded"
            placeholder="e.g. 75"
            name="effectivenessScore"
            type="number"
            min="0"
            max="100"
            value={form.effectivenessScore}
            onChange={handleChange}
          />
        </div>
        <div className="grid grid-cols-2 gap-4">
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Assessor</label>
            <input
              className="w-full border p-2 rounded"
              placeholder="e.g. John Smith"
              name="assessor"
              value={form.assessor}
              onChange={handleChange}
            />
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Department</label>
            <input
              className="w-full border p-2 rounded"
              placeholder="e.g. IT Security"
              name="department"
              value={form.department}
              onChange={handleChange}
            />
          </div>
        </div>
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">Description</label>
          <textarea
            className="w-full border p-2 rounded"
            placeholder="Describe the control..."
            name="controlDescription"
            rows="4"
            value={form.controlDescription}
            onChange={handleChange}
          />
        </div>
        <button
          onClick={handleSubmit}
          className="w-full bg-blue-800 text-white p-3 rounded hover:bg-blue-900"
        >
          Create Control
        </button>
      </div>

      <div className="mt-6">
        <FileUpload />
      </div>
    </div>
  );
}
