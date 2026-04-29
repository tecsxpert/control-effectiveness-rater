import { useEffect, useState } from 'react';
import AiPanel from '../components/AiPanel';
import { useParams, useNavigate } from 'react-router-dom';
import API from '../services/api';

export default function DetailPage() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [control, setControl] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    API.get(`/controls/${id}`)
      .then(res => {
        setControl(res.data);
        setLoading(false);
      })
      .catch(() => setLoading(false));
  }, [id]);

  const handleDelete = () => {
    if (window.confirm('Are you sure you want to delete this control?')) {
      API.delete(`/controls/${id}`)
        .then(() => navigate('/list'))
        .catch(() => alert('Error deleting control'));
    }
  };

  const getScoreBadge = (score) => {
    if (score >= 75) return 'bg-green-500';
    if (score >= 50) return 'bg-yellow-500';
    return 'bg-red-500';
  };

  if (loading) {
    return (
      <div className="flex justify-center items-center h-64">
        <p className="text-gray-500">Loading...</p>
      </div>
    );
  }

  if (!control) {
    return (
      <div className="flex justify-center items-center h-64">
        <p className="text-gray-500">Control not found.</p>
      </div>
    );
  }

  return (
    <div className="p-6 max-w-2xl mx-auto">
      <h2 className="text-2xl font-bold mb-6">Control Details</h2>

      <div className="bg-white rounded shadow p-6 space-y-4">
        <div className="flex justify-between items-center">
          <h3 className="text-xl font-bold">{control.controlName}</h3>
          {/* Score Badge */}
          <span className={`${getScoreBadge(control.effectivenessScore)} text-white px-3 py-1 rounded-full text-sm font-bold`}>
            Score: {control.effectivenessScore}
          </span>
        </div>

        <div className="grid grid-cols-2 gap-4">
          <div>
            <p className="text-gray-500 text-sm">Category</p>
            <p className="font-medium">{control.category}</p>
          </div>
          <div>
            <p className="text-gray-500 text-sm">Status</p>
            <p className="font-medium">{control.status}</p>
          </div>
          <div>
            <p className="text-gray-500 text-sm">Risk Level</p>
            <p className="font-medium">{control.riskLevel}</p>
          </div>
          <div>
            <p className="text-gray-500 text-sm">Owner</p>
            <p className="font-medium">{control.owner}</p>
          </div>
        </div>

        <div>
          <p className="text-gray-500 text-sm">Description</p>
          <p className="font-medium">{control.controlDescription}</p>
        </div>

        {/* Edit and Delete Buttons */}
        <div className="flex space-x-4 pt-4">
          <button
            onClick={() => navigate(`/edit/${id}`)}
            className="bg-blue-800 text-white px-6 py-2 rounded hover:bg-blue-900"
          >
            Edit
          </button>
          <button
            onClick={handleDelete}
            className="bg-red-600 text-white px-6 py-2 rounded hover:bg-red-700"
          >
            Delete
          </button>
        </div>
      </div>
    </div>
  );
}