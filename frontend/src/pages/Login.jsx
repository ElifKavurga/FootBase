import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { LogIn, Mail, Lock } from 'lucide-react';
import './Auth.css'; // We will create a shared Auth.css

const Login = () => {
    const [credentials, setCredentials] = useState({ email: '', password: '' });
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');
    const navigate = useNavigate();

    const handleChange = (e) => {
        setCredentials({ ...credentials, [e.target.name]: e.target.value });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setLoading(true);
        setError('');

        try {
            // Placeholder logic for future API connection
            // const response = await api.post('/auth/login', credentials);
            // localStorage.setItem('token', response.data.token);

            // Simulating API call
            setTimeout(() => {
                setLoading(false);
                // simulate successful login
                navigate('/');
            }, 1000);
        } catch (err) {
            setError('Giriş başarısız. Lütfen bilgilerinizi kontrol edin.');
            setLoading(false);
        }
    };

    return (
        <div className="auth-page container flex-center animate-fade-in">
            <div className="auth-container glass-panel">
                <div className="auth-header text-center mb-4">
                    <div className="auth-icon-wrapper mx-auto flex-center mb-3">
                        <LogIn size={28} className="text-primary" />
                    </div>
                    <h2>Hoş Geldiniz</h2>
                    <p className="text-muted">Hesabınıza giriş yaparak devam edin.</p>
                </div>

                {error && <div className="error-banner mb-4">{error}</div>}

                <form onSubmit={handleSubmit} className="auth-form">
                    <div className="form-group">
                        <label>E-posta Adresi</label>
                        <div className="input-wrapper">
                            <Mail size={18} className="input-icon" />
                            <input
                                type="email"
                                name="email"
                                placeholder="ornek@email.com"
                                value={credentials.email}
                                onChange={handleChange}
                                required
                            />
                        </div>
                    </div>

                    <div className="form-group">
                        <div className="flex-between">
                            <label>Şifre</label>
                            <a href="#" className="text-xs text-primary" onClick={(e) => e.preventDefault()}>Şifremi Unuttum</a>
                        </div>
                        <div className="input-wrapper">
                            <Lock size={18} className="input-icon" />
                            <input
                                type="password"
                                name="password"
                                placeholder="••••••••"
                                value={credentials.password}
                                onChange={handleChange}
                                required
                            />
                        </div>
                    </div>

                    <button type="submit" className="btn btn-primary w-100 mt-2" disabled={loading}>
                        {loading ? 'Giriş Yapılıyor...' : 'Giriş Yap'}
                    </button>
                </form>

                <div className="auth-footer text-center mt-4 pt-4">
                    <p className="text-muted text-sm">
                        Henüz hesabınız yok mu?{' '}
                        <Link to="/register" className="text-primary font-bold">Kayıt Ol</Link>
                    </p>
                </div>
            </div>
        </div>
    );
};

export default Login;
