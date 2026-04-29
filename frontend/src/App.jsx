import { BrowserRouter, Routes, Route } from 'react-router-dom';
import { AuthProvider } from './services/AuthContext';
import Navbar from './components/Navbar';
import ListPage from './pages/ListPage';
import FormPage from './pages/FormPage';
import LoginPage from './pages/LoginPage';
import DashboardPage from './pages/DashboardPage';
import DetailPage from './pages/DetailPage';
import EditPage from './pages/EditPage';
import AnalyticsPage from './pages/AnalyticsPage';
import ProtectedRoute from './components/ProtectedRoute';

function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <Navbar />
        <Routes>
          <Route path="/login" element={<LoginPage />} />
          <Route path="/" element={
            <ProtectedRoute><DashboardPage /></ProtectedRoute>
          } />
          <Route path="/list" element={
            <ProtectedRoute><ListPage /></ProtectedRoute>
          } />
          <Route path="/create" element={
            <ProtectedRoute><FormPage /></ProtectedRoute>
          } />
          <Route path="/detail/:id" element={
            <ProtectedRoute><DetailPage /></ProtectedRoute>
          } />
          <Route path="/edit/:id" element={
            <ProtectedRoute><EditPage /></ProtectedRoute>
          } />
          <Route path="/analytics" element={
            <ProtectedRoute><AnalyticsPage /></ProtectedRoute>
          } />
        </Routes>
      </BrowserRouter>
    </AuthProvider>
  );
}

export default App;