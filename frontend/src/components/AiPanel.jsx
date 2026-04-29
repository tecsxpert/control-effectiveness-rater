import { useState } from 'react';
import API from '../services/api';

export default function AiPanel({ controlId }) {
  const [loading, setLoading] = useState(false);
  const [aiResponse, setAiResponse] = useState(null);
  const [error, setError] = useState('');

  const handleDescribe = () => {
    setLoading(true);
    setError('');
    API.post(`/controls/${controlId}/describe`)
      .then(res => {
        setAiResponse(res.data);
        setLoading(false);
      })
      .catch(() => {
        setError('AI service unavailable. Please try again.');
        setLoading(false);
      });
  };

  return (
    <div className="bg-gray-50 border rounded p-4 mt-6">
      <h3 className="text-lg font-bold mb-3 text-blue-800">AI Analysis</h3>

      <button
        onClick={handleDescribe}
        disabled={loading}
        className="bg-blue-800 text-white px-4 py-2 rounded hover:bg-blue-900 disabled:opacity-50"
      >
        {loading ? 'Analyzing...' : 'Get AI Description'}
      </button>

      {/* Loading Spinner */}
      {loading && (
        <div className="flex justify-center items-center mt-4">
          <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-800"></div>
          <p className="ml-3 text-gray-500">AI is analyzing...</p>
        </div>
      )}

      {/* Error */}
      {error && (
        <div className="mt-4 p-3 bg-red-100 text-red-800 rounded">
          {error}
        </div>
      )}

      {/* AI Response Card */}
      {aiResponse && !loading && (
        <div className="mt-4 bg-white border border-blue-200 rounded p-4">
          <div className="flex justify-between items-center mb-2">
            <h4 className="font-bold text-blue-800">AI Response</h4>
            <span className="text-xs text-gray-400">
              {aiResponse.generated_at}
            </span>
          </div>
          <p className="text-gray-700 whitespace-pre-wrap">
            {aiResponse.description}
          </p>
        </div>
      )}
    {/* AI Panel */}
        <AiPanel controlId={id} />
    </div>
  );
}