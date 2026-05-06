import { useState } from 'react';
import API from '../services/api';

export default function AiPanel({ controlId, aiDescription, aiRecommendations, aiReport }) {
  const [loading, setLoading] = useState({ recommend: false, report: false });
  const [recommendations, setRecommendations] = useState(aiRecommendations || null);
  const [report, setReport] = useState(aiReport || null);
  const [error, setError] = useState('');

  const parseJson = (str) => {
    if (!str) return null;
    try { return JSON.parse(str); } catch { return { raw: str }; }
  };

  const handleRecommend = () => {
    setLoading(l => ({ ...l, recommend: true }));
    setError('');
    API.post(`/controls/${controlId}/ai/recommend`)
      .then(res => {
        setRecommendations(res.data.aiRecommendations);
        setLoading(l => ({ ...l, recommend: false }));
      })
      .catch(() => {
        setError('AI service unavailable. Please try again.');
        setLoading(l => ({ ...l, recommend: false }));
      });
  };

  const handleReport = () => {
    setLoading(l => ({ ...l, report: true }));
    setError('');
    API.post(`/controls/${controlId}/ai/report`)
      .then(res => {
        setReport(res.data.aiReport);
        setLoading(l => ({ ...l, report: false }));
      })
      .catch(() => {
        setError('AI service unavailable. Please try again.');
        setLoading(l => ({ ...l, report: false }));
      });
  };

  const descData = parseJson(aiDescription);
  const recData = parseJson(recommendations);
  const repData = parseJson(report);

  return (
    <div className="bg-gray-50 border rounded p-4 mt-6 space-y-4">
      <h3 className="text-lg font-bold text-blue-800">AI Analysis</h3>

      {error && (
        <div className="p-3 bg-red-100 text-red-800 rounded text-sm">{error}</div>
      )}

      {/* AI Description (auto-generated on create) */}
      {descData && (
        <div className="bg-white border border-blue-200 rounded p-4">
          <h4 className="font-semibold text-blue-800 mb-2">AI Description</h4>
          {descData.is_fallback && (
            <span className="text-xs bg-yellow-100 text-yellow-700 px-2 py-1 rounded mb-2 inline-block">Fallback response</span>
          )}
          <p className="text-gray-700 text-sm">{descData.description || descData.raw}</p>
          {descData.key_strengths?.length > 0 && (
            <div className="mt-2">
              <p className="text-xs font-semibold text-green-700 mb-1">Key Strengths:</p>
              <ul className="list-disc list-inside text-sm text-gray-600">
                {descData.key_strengths.map((s, i) => <li key={i}>{s}</li>)}
              </ul>
            </div>
          )}
          {descData.potential_gaps?.length > 0 && (
            <div className="mt-2">
              <p className="text-xs font-semibold text-red-700 mb-1">Potential Gaps:</p>
              <ul className="list-disc list-inside text-sm text-gray-600">
                {descData.potential_gaps.map((g, i) => <li key={i}>{g}</li>)}
              </ul>
            </div>
          )}
          {descData.overall_assessment && (
            <p className="mt-2 text-xs text-gray-500 italic">{descData.overall_assessment}</p>
          )}
        </div>
      )}

      {/* Recommend Button + Output */}
      <div>
        <button
          onClick={handleRecommend}
          disabled={loading.recommend}
          className="bg-blue-800 text-white px-4 py-2 rounded hover:bg-blue-900 disabled:opacity-50 text-sm"
        >
          {loading.recommend ? 'Getting recommendations...' : 'Get AI Recommendations'}
        </button>
        {loading.recommend && (
          <div className="flex items-center mt-2">
            <div className="animate-spin rounded-full h-5 w-5 border-b-2 border-blue-800 mr-2"></div>
            <p className="text-gray-500 text-sm">AI is analyzing...</p>
          </div>
        )}
        {recData && !loading.recommend && (
          <div className="mt-3 bg-white border border-blue-200 rounded p-4">
            <h4 className="font-semibold text-blue-800 mb-2">Recommendations</h4>
            {recData.is_fallback && (
              <span className="text-xs bg-yellow-100 text-yellow-700 px-2 py-1 rounded mb-2 inline-block">Fallback response</span>
            )}
            {Array.isArray(recData.recommendations) ? (
              <div className="space-y-3">
                {recData.recommendations.map((rec, i) => (
                  <div key={i} className="border-l-4 border-blue-400 pl-3">
                    <div className="flex items-center gap-2 mb-1">
                      <span className={`text-xs px-2 py-0.5 rounded font-medium ${
                        rec.priority === 'HIGH' ? 'bg-red-100 text-red-700' :
                        rec.priority === 'MEDIUM' ? 'bg-yellow-100 text-yellow-700' :
                        'bg-green-100 text-green-700'
                      }`}>{rec.priority}</span>
                      <span className="text-xs text-gray-500">{rec.action_type}</span>
                    </div>
                    <p className="text-sm text-gray-700">{rec.description}</p>
                    {rec.expected_impact && (
                      <p className="text-xs text-gray-500 mt-1">Impact: {rec.expected_impact}</p>
                    )}
                  </div>
                ))}
              </div>
            ) : (
              <p className="text-sm text-gray-700">{recData.raw || JSON.stringify(recData)}</p>
            )}
          </div>
        )}
      </div>

      {/* Report Button + Output */}
      <div>
        <button
          onClick={handleReport}
          disabled={loading.report}
          className="bg-green-700 text-white px-4 py-2 rounded hover:bg-green-800 disabled:opacity-50 text-sm"
        >
          {loading.report ? 'Generating report...' : 'Generate AI Report'}
        </button>
        {loading.report && (
          <div className="flex items-center mt-2">
            <div className="animate-spin rounded-full h-5 w-5 border-b-2 border-green-700 mr-2"></div>
            <p className="text-gray-500 text-sm">Generating report...</p>
          </div>
        )}
        {repData && !loading.report && (
          <div className="mt-3 bg-white border border-green-200 rounded p-4">
            <h4 className="font-semibold text-green-800 mb-2">
              {repData.title || 'AI Report'}
            </h4>
            {repData.is_fallback && (
              <span className="text-xs bg-yellow-100 text-yellow-700 px-2 py-1 rounded mb-2 inline-block">Fallback response</span>
            )}
            {repData.summary && <p className="text-sm text-gray-700 mb-2"><strong>Summary:</strong> {repData.summary}</p>}
            {repData.overview && <p className="text-sm text-gray-700 mb-2">{repData.overview}</p>}
            {repData.risk_assessment && <p className="text-sm text-gray-700 mb-2"><strong>Risk:</strong> {repData.risk_assessment}</p>}
            {Array.isArray(repData.key_findings) && repData.key_findings.length > 0 && (
              <div className="mt-2">
                <p className="text-xs font-semibold text-gray-700 mb-1">Key Findings:</p>
                <ul className="list-disc list-inside text-sm text-gray-600">
                  {repData.key_findings.map((f, i) => <li key={i}>{f}</li>)}
                </ul>
              </div>
            )}
            {repData.raw && <p className="text-sm text-gray-700">{repData.raw}</p>}
          </div>
        )}
      </div>
    </div>
  );
}
