import FileUpload from '../components/FileUpload';
import { useState } from 'react';
import API from '../services/api';

export default function FormPage() {
  const [form, setForm] = useState({
    controlName: '',
    category: '',
    status: 'PENDING',
    effectivenessScore: '',
    riskLevel: '',
    owner: '',
    controlDescription: ''
  });
  const [message, setMessage] = useState('');

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const handleSubmit = () => {
    API.post('/controls', form)
      .then(() => {
        setMessage('Control created successfully!');
        setForm({
          controlName: '',
          category: '',
          status: 'PENDING',
          effectivenessScore: '',
          riskLevel: '',
          owner: '',
          controlDescription: ''
        });
      })
      .catch(() => {
        setMessage('Error creating control. Please try again.');
      });
  };

  return (
    <div className="p-6 max-w-2xl mx-auto">
      <h2 className="text-2xl font-bold mb-6">Create New Control</h2>
      {message && (
        <div className="mb-4 p-3 bg-green-100 text-green-800 rounded">
          {message}
        </div>
      )}
      <div className="space-y-4">
        <input
          className="w-full border p-2 rounded"
          placeholder="Control Name *"
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
        <button
          onClick={handleSubmit}
          className="w-full bg-blue-800 text-white p-3 rounded hover:bg-blue-900"
        >
          Create Control
        </button>
      </div>
    <FileUpload />
    </div>
  );
}