import { BrowserRouter, Routes, Route } from 'react-router-dom';
import { AuthProvider } from './services/AuthContext';
import Navbar from './components/Navbar';
import ListPage from './pages/ListPage';
import FormPage from './pages/FormPage';
import LoginPage from './pages/LoginPage';
import ProtectedRoute from './components/ProtectedRoute';

function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <Navbar />
        <Routes>
          <Route path="/login" element={<LoginPage />} />
          <Route path="/" element={
            <ProtectedRoute>
              <ListPage />
            </ProtectedRoute>
          } />
          <Route path="/create" element={
            <ProtectedRoute>
              <FormPage />
            </ProtectedRoute>
          } />
        </Routes>
      </BrowserRouter>
    </AuthProvider>
  );
}

export default App;