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
        .catch(() => alert('Error deleting control. Admin role required.'));
    }
  };

  const getScoreBadge = (score) => {
    if (score == null) return 'bg-gray-400';
    if (score >= 75) return 'bg-green-500';
    if (score >= 50) return 'bg-yellow-500';
    return 'bg-red-500';
  };

  if (loading) {
    return (
      <div className="flex justify-center items-center h-64">
        <div className="animate-spin rounded-full h-10 w-10 border-b-2 border-blue-800"></div>
      </div>
    );
  }

  if (!control) {
    return (
      <div className="flex flex-col justify-center items-center h-64">
        <p className="text-gray-500">Control not found.</p>
        <button onClick={() => navigate('/list')} className="mt-4 text-blue-800 underline">
          Back to list
        </button>
      </div>
    );
  }

  return (
    <div className="p-6 max-w-2xl mx-auto">
      <div className="flex items-center mb-6">
        <button onClick={() => navigate('/list')} className="mr-4 text-gray-500 hover:text-gray-700">
          ← Back
        </button>
        <h2 className="text-2xl font-bold">Control Details</h2>
      </div>

      <div className="bg-white rounded shadow p-6 space-y-4">
        <div className="flex justify-between items-start">
          <h3 className="text-xl font-bold">{control.controlName}</h3>
          <span className={`${getScoreBadge(control.effectivenessScore)} text-white px-3 py-1 rounded-full text-sm font-bold`}>
            Score: {control.effectivenessScore ?? 'N/A'}
          </span>
        </div>

        <div className="grid grid-cols-2 gap-4">
          <div>
            <p className="text-gray-500 text-sm">Category</p>
            <p className="font-medium">{control.category || '—'}</p>
          </div>
          <div>
            <p className="text-gray-500 text-sm">Status</p>
            <span className={`px-2 py-1 rounded text-xs font-medium ${
              control.status === 'COMPLETED' ? 'bg-green-100 text-green-800' :
              control.status === 'PENDING' ? 'bg-yellow-100 text-yellow-800' :
              control.status === 'IN_PROGRESS' ? 'bg-blue-100 text-blue-800' :
              'bg-gray-100 text-gray-800'
            }`}>
              {control.status}
            </span>
          </div>
          <div>
            <p className="text-gray-500 text-sm">Risk Level</p>
            <span className={`px-2 py-1 rounded text-xs font-medium ${
              control.riskLevel === 'CRITICAL' ? 'bg-red-100 text-red-800' :
              control.riskLevel === 'HIGH' ? 'bg-orange-100 text-orange-800' :
              control.riskLevel === 'MEDIUM' ? 'bg-yellow-100 text-yellow-800' :
              'bg-green-100 text-green-800'
            }`}>
              {control.riskLevel}
            </span>
          </div>
          <div>
            <p className="text-gray-500 text-sm">Assessor</p>
            <p className="font-medium">{control.assessor || '—'}</p>
          </div>
          <div>
            <p className="text-gray-500 text-sm">Department</p>
            <p className="font-medium">{control.department || '—'}</p>
          </div>
          <div>
            <p className="text-gray-500 text-sm">Review Date</p>
            <p className="font-medium">{control.reviewDate || '—'}</p>
          </div>
        </div>

        {control.controlDescription && (
          <div>
            <p className="text-gray-500 text-sm">Description</p>
            <p className="text-gray-700">{control.controlDescription}</p>
          </div>
        )}

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

      <AiPanel
        controlId={control.id}
        aiDescription={control.aiDescription}
        aiRecommendations={control.aiRecommendations}
        aiReport={control.aiReport}
      />
    </div>
  );
}
