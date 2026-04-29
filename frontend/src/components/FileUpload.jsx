import { useState } from 'react';
import API from '../services/api';

export default function FileUpload() {
  const [file, setFile] = useState(null);
  const [message, setMessage] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const allowedTypes = ['application/pdf', 'image/jpeg', 'image/png', 'text/csv'];
  const maxSize = 5 * 1024 * 1024; // 5MB

  const handleFileChange = (e) => {
    const selected = e.target.files[0];
    setError('');
    setMessage('');

    if (!selected) return;

    // Type validation
    if (!allowedTypes.includes(selected.type)) {
      setError('Invalid file type. Only PDF, JPG, PNG, CSV allowed.');
      return;
    }

    // Size validation
    if (selected.size > maxSize) {
      setError('File too large. Maximum size is 5MB.');
      return;
    }

    setFile(selected);
  };

  const handleUpload = () => {
    if (!file) {
      setError('Please select a file first.');
      return;
    }

    setLoading(true);
    const formData = new FormData();
    formData.append('file', file);

    API.post('/controls/upload', formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })
      .then(() => {
        setMessage('File uploaded successfully!');
        setFile(null);
        setLoading(false);
      })
      .catch(() => {
        setError('Upload failed. Please try again.');
        setLoading(false);
      });
  };

  return (
    <div className="bg-gray-50 border rounded p-4 mt-4">
      <h3 className="text-lg font-bold mb-3 text-blue-800">File Upload</h3>

      <input
        type="file"
        onChange={handleFileChange}
        className="mb-3"
        accept=".pdf,.jpg,.jpeg,.png,.csv"
      />

      {error && (
        <div className="mb-3 p-3 bg-red-100 text-red-800 rounded">
          {error}
        </div>
      )}

      {message && (
        <div className="mb-3 p-3 bg-green-100 text-green-800 rounded">
          {message}
        </div>
      )}

      {file && (
        <div className="mb-3 p-2 bg-blue-50 rounded text-sm">
          <p>File: {file.name}</p>
          <p>Size: {(file.size / 1024).toFixed(2)} KB</p>
          <p>Type: {file.type}</p>
        </div>
      )}

      <button
        onClick={handleUpload}
        disabled={loading || !file}
        className="bg-blue-800 text-white px-4 py-2 rounded hover:bg-blue-900 disabled:opacity-50"
      >
        {loading ? 'Uploading...' : 'Upload File'}
      </button>
    </div>
  );
}