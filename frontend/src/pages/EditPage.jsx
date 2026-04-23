import { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import API from '../services/api';

export default function EditPage() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [form, setForm] = useState({
    controlName: '',
    category: '',
    status: 'PENDING',
    effectivenessScore: '',
    riskLevel: '',
    owner: '',
    controlDescription: ''
  });

  useEffect(() => {
    API.get(`/controls/${id}`)
      .then(res => setForm(res.data))
      .catch(() => {});
  }, [id]);

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const handleUpdate = () => {
    API.put(`/controls/${id}`, form)
      .then(() => navigate(`/detail/${id}`))
      .catch(() => alert('Error updating control'));
  };

  return (
    <div className="p-6 max-w-2xl mx-auto">
      <h2 className="text-2xl font-bold mb-6">Edit Control</h2>
      <div className="space-y-4">
        <input
          className="w-full border p-2 rounded"
          placeholder="Control Name"
          name="controlName"
          value={form.controlName}
          onChange={handleChange}
        />
        <input
          className="w-full border p-2 rounded"
          placeholder="Category"
          name="category"
          value={form.category}
          onChange={handleChange}
        />
        <select
          className="w-full border p-2 rounded"
          name="status"
          value={form.status}
          onChange={handleChange}
        >
          <option value="PENDING">PENDING</option>
          <option value="ACTIVE">ACTIVE</option>
          <option value="INACTIVE">INACTIVE</option>
          <option value="REVIEW">REVIEW</option>
        </select>
        <input
          className="w-full border p-2 rounded"
          placeholder="Effectiveness Score (0-100)"
          name="effectivenessScore"
          type="number"
          min="0"
          max="100"
          value={form.effectivenessScore}
          onChange={handleChange}
        />
        <select
          className="w-full border p-2 rounded"
          name="riskLevel"
          value={form.riskLevel}
          onChange={handleChange}
        >
          <option value="">Select Risk Level</option>
          <option value="LOW">LOW</option>
          <option value="MEDIUM">MEDIUM</option>
          <option value="HIGH">HIGH</option>
          <option value="CRITICAL">CRITICAL</option>
        </select>
        <input
          className="w-full border p-2 rounded"
          placeholder="Owner"
          name="owner"
          value={form.owner}
          onChange={handleChange}
        />
        <textarea
          className="w-full border p-2 rounded"
          placeholder="Description"
          name="controlDescription"
          rows="4"
          value={form.controlDescription}
          onChange={handleChange}
        />
        <div className="flex space-x-4">
          <button
            onClick={handleUpdate}
            className="bg-blue-800 text-white px-6 py-2 rounded hover:bg-blue-900"
          >
            Update
          </button>
          <button
            onClick={() => navigate(`/detail/${id}`)}
            className="bg-gray-500 text-white px-6 py-2 rounded hover:bg-gray-600"
          >
            Cancel
          </button>
        </div>
      </div>
    </div>
  );
}