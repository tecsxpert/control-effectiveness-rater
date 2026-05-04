import { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../services/AuthContext';

export default function LoginPage() {
  const navigate = useNavigate();
  const { login } = useAuth();

  useEffect(() => {
    login('demo-token-123');
    navigate('/list');
  }, []);

  return <div className="p-6">Redirecting...</div>;
}
