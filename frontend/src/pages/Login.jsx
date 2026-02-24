import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { LogIn, Mail, Lock } from 'lucide-react';
import { useAuth } from '../hooks/useAuth';
import './Auth.css';

const Login = () => {
    const [mode, setMode] = useState('login');
    const [credentials, setCredentials] = useState({ email: '', password: '' });
    const [resetData, setResetData] = useState({ email: '', newPassword: '', confirmPassword: '' });
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');
    const navigate = useNavigate();
    const { login, resetPassword } = useAuth();

    const handleLoginSubmit = async (e) => {
        e.preventDefault();
        setLoading(true);
        setError('');
        setSuccess('');

        const result = await login(credentials);
        setLoading(false);

        if (result.success) {
            navigate('/');
            return;
        }

        setError(result.message);
    };

    const handleResetSubmit = async (e) => {
        e.preventDefault();
        setLoading(true);
        setError('');
        setSuccess('');

        if (resetData.newPassword !== resetData.confirmPassword) {
            setError('Sifreler eslesmiyor.');
            setLoading(false);
            return;
        }

        const result = await resetPassword({
            email: resetData.email,
            newPassword: resetData.newPassword
        });
        setLoading(false);

        if (result.success) {
            setSuccess('Sifre basariyla sifirlandi. Simdi giris yapabilirsiniz.');
            setMode('login');
            setCredentials((prev) => ({ ...prev, email: resetData.email }));
            setResetData({ email: '', newPassword: '', confirmPassword: '' });
            return;
        }

        setError(result.message);
    };

    const isLoginMode = mode === 'login';

    return (
        <div className="auth-page container flex-center animate-fade-in">
            <div className="auth-container glass-panel">
                <div className="auth-header text-center mb-4">
                    <div className="auth-icon-wrapper mx-auto flex-center mb-3">
                        <LogIn size={28} className="text-primary" />
                    </div>
                    <h2>{isLoginMode ? 'Hos Geldiniz' : 'Sifre Sifirlama'}</h2>
                    <p className="text-muted">
                        {isLoginMode
                            ? 'Hesabiniza giris yaparak devam edin.'
                            : 'E-posta adresinizi ve yeni sifrenizi girin.'}
                    </p>
                </div>

                {error && <div className="error-banner mb-4">{error}</div>}
                {success && <div className="success-banner mb-4">{success}</div>}

                {isLoginMode ? (
                    <form onSubmit={handleLoginSubmit} className="auth-form">
                        <div className="form-group">
                            <label>E-posta Adresi</label>
                            <div className="input-wrapper">
                                <Mail size={18} className="input-icon" />
                                <input
                                    type="email"
                                    name="email"
                                    placeholder="ornek@email.com"
                                    value={credentials.email}
                                    onChange={(e) => setCredentials({ ...credentials, email: e.target.value })}
                                    required
                                />
                            </div>
                        </div>

                        <div className="form-group">
                            <div className="flex-between">
                                <label>Sifre</label>
                                <button type="button" className="link-button text-xs text-primary" onClick={() => setMode('reset')}>
                                    Sifremi Unuttum
                                </button>
                            </div>
                            <div className="input-wrapper">
                                <Lock size={18} className="input-icon" />
                                <input
                                    type="password"
                                    name="password"
                                    placeholder="********"
                                    value={credentials.password}
                                    onChange={(e) => setCredentials({ ...credentials, password: e.target.value })}
                                    required
                                />
                            </div>
                        </div>

                        <button type="submit" className="btn btn-primary w-100 mt-2" disabled={loading}>
                            {loading ? 'Giris Yapiliyor...' : 'Giris Yap'}
                        </button>
                    </form>
                ) : (
                    <form onSubmit={handleResetSubmit} className="auth-form">
                        <div className="form-group">
                            <label>E-posta Adresi</label>
                            <div className="input-wrapper">
                                <Mail size={18} className="input-icon" />
                                <input
                                    type="email"
                                    placeholder="ornek@email.com"
                                    value={resetData.email}
                                    onChange={(e) => setResetData({ ...resetData, email: e.target.value })}
                                    required
                                />
                            </div>
                        </div>

                        <div className="form-group">
                            <label>Yeni Sifre</label>
                            <div className="input-wrapper">
                                <Lock size={18} className="input-icon" />
                                <input
                                    type="password"
                                    placeholder="********"
                                    value={resetData.newPassword}
                                    onChange={(e) => setResetData({ ...resetData, newPassword: e.target.value })}
                                    required
                                    minLength={6}
                                />
                            </div>
                        </div>

                        <div className="form-group">
                            <label>Yeni Sifre Tekrar</label>
                            <div className="input-wrapper">
                                <Lock size={18} className="input-icon" />
                                <input
                                    type="password"
                                    placeholder="********"
                                    value={resetData.confirmPassword}
                                    onChange={(e) => setResetData({ ...resetData, confirmPassword: e.target.value })}
                                    required
                                    minLength={6}
                                />
                            </div>
                        </div>

                        <button type="submit" className="btn btn-primary w-100 mt-2" disabled={loading}>
                            {loading ? 'SifirlanÄ±yor...' : 'Sifreyi Sifirla'}
                        </button>

                        <button type="button" className="btn btn-outline w-100 mt-2" onClick={() => setMode('login')}>
                            Giris Ekranina Don
                        </button>
                    </form>
                )}

                <div className="auth-footer text-center mt-4 pt-4">
                    <p className="text-muted text-sm">
                        Henuz hesabiniz yok mu?{' '}
                        <Link to="/register" className="text-primary font-bold">Kayit Ol</Link>
                    </p>
                </div>
            </div>
        </div>
    );
};

export default Login;

