import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { User, Mail, Lock, UserPlus } from 'lucide-react';
import './Auth.css';

const Register = () => {
    const [formData, setFormData] = useState({
        firstName: '',
        lastName: '',
        email: '',
        password: '',
        confirmPassword: ''
    });
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');
    const navigate = useNavigate();

    const handleChange = (e) => {
        setFormData({ ...formData, [e.target.name]: e.target.value });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setLoading(true);
        setError('');

        if (formData.password !== formData.confirmPassword) {
            setError('Şifreler eşleşmiyor!');
            setLoading(false);
            return;
        }

        try {
            // Placeholder logic for future API connection
            // const response = await api.post('/auth/register', formData);

            // Simulating API call
            setTimeout(() => {
                setLoading(false);
                // simulate successful registration
                navigate('/login');
            }, 1000);
        } catch (err) {
            setError('Kayıt başarısız. Lütfen bilgilerinizi kontrol edin.');
            setLoading(false);
        }
    };

    return (
        <div className="auth-page container flex-center animate-fade-in">
            <div className="auth-container glass-panel">
                <div className="auth-header text-center mb-4">
                    <div className="auth-icon-wrapper mx-auto flex-center mb-3">
                        <UserPlus size={28} className="text-primary" />
                    </div>
                    <h2>Hesap Oluştur</h2>
                    <p className="text-muted">Aramıza katılın ve takımları yakından takip edin.</p>
                </div>

                {error && <div className="error-banner mb-4">{error}</div>}

                <form onSubmit={handleSubmit} className="auth-form">
                    <div className="grid-2 gap-3 mb-3">
                        <div className="form-group mb-0">
                            <label>Ad</label>
                            <div className="input-wrapper">
                                <User size={18} className="input-icon" />
                                <input
                                    type="text"
                                    name="firstName"
                                    placeholder="Adınız"
                                    value={formData.firstName}
                                    onChange={handleChange}
                                    required
                                />
                            </div>
                        </div>
                        <div className="form-group mb-0">
                            <label>Soyad</label>
                            <div className="input-wrapper">
                                <User size={18} className="input-icon" />
                                <input
                                    type="text"
                                    name="lastName"
                                    placeholder="Soyadınız"
                                    value={formData.lastName}
                                    onChange={handleChange}
                                    required
                                />
                            </div>
                        </div>
                    </div>

                    <div className="form-group mb-3">
                        <label>E-posta Adresi</label>
                        <div className="input-wrapper">
                            <Mail size={18} className="input-icon" />
                            <input
                                type="email"
                                name="email"
                                placeholder="ornek@email.com"
                                value={formData.email}
                                onChange={handleChange}
                                required
                            />
                        </div>
                    </div>

                    <div className="form-group mb-3">
                        <label>Şifre</label>
                        <div className="input-wrapper">
                            <Lock size={18} className="input-icon" />
                            <input
                                type="password"
                                name="password"
                                placeholder="••••••••"
                                value={formData.password}
                                onChange={handleChange}
                                required
                                minLength={6}
                            />
                        </div>
                    </div>

                    <div className="form-group mb-4">
                        <label>Şifre Tekrar</label>
                        <div className="input-wrapper">
                            <Lock size={18} className="input-icon" />
                            <input
                                type="password"
                                name="confirmPassword"
                                placeholder="••••••••"
                                value={formData.confirmPassword}
                                onChange={handleChange}
                                required
                                minLength={6}
                            />
                        </div>
                    </div>

                    <button type="submit" className="btn btn-primary w-100" disabled={loading}>
                        {loading ? 'Kayıt Olunuyor...' : 'Ücretsiz Kayıt Ol'}
                    </button>
                </form>

                <div className="auth-footer text-center mt-4 pt-4">
                    <p className="text-muted text-sm">
                        Zaten bir hesabınız var mı?{' '}
                        <Link to="/login" className="text-primary font-bold">Giriş Yap</Link>
                    </p>
                </div>
            </div>
        </div>
    );
};

export default Register;
