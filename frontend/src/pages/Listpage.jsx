import { useEffect, useState } from 'react';
import API from '../services/api';

export default function ListPage() {
  const [controls, setControls] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    API.get('/controls')
      .then(res => {
        setControls(res.data);
        setLoading(false);
      })
      .catch(() => {
        setLoading(false);
      });
  }, []);

  if (loading) {
    return (
      <div className="flex justify-center items-center h-64">
        <p className="text-gray-500 text-lg">Loading...</p>
      </div>
    );
  }

  if (controls.length === 0) {
    return (
      <div className="flex justify-center items-center h-64">
        <p className="text-gray-500 text-lg">No controls found.</p>
      </div>
    );
  }

  return (
    <div className="p-6">
      <h2 className="text-2xl font-bold mb-4">Control Effectiveness List</h2>
      <table className="w-full border-collapse border border-gray-300">
        <thead className="bg-blue-800 text-white">
          <tr>
            <th className="p-3 text-left">ID</th>
            <th className="p-3 text-left">Control Name</th>
            <th className="p-3 text-left">Category</th>
            <th className="p-3 text-left">Status</th>
            <th className="p-3 text-left">Score</th>
            <th className="p-3 text-left">Risk Level</th>
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
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}