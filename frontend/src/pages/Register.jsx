import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { User, Mail, Lock, UserPlus } from 'lucide-react';
import { useAuth } from '../hooks/useAuth';
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
    const { register } = useAuth();

    const handleChange = (e) => {
        setFormData({ ...formData, [e.target.name]: e.target.value });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setLoading(true);
        setError('');

        if (formData.password !== formData.confirmPassword) {
            setError('Sifreler eslesmiyor.');
            setLoading(false);
            return;
        }

        const fullName = `${formData.firstName} ${formData.lastName}`.trim();
        const username = fullName || formData.email.split('@')[0];

        const result = await register({
            kullaniciAdi: username,
            email: formData.email,
            password: formData.password
        });

        setLoading(false);

        if (result.success) {
            navigate('/');
            return;
        }

        setError(result.message);
    };

    return (
        <div className="auth-page container flex-center animate-fade-in">
            <div className="auth-container glass-panel">
                <div className="auth-header text-center mb-4">
                    <div className="auth-icon-wrapper mx-auto flex-center mb-3">
                        <UserPlus size={28} className="text-primary" />
                    </div>
                    <h2>Hesap Olustur</h2>
                    <p className="text-muted">Aramiza katilin ve takimlari yakindan takip edin.</p>
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
                                    placeholder="Adiniz"
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
                                    placeholder="Soyadiniz"
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
                        <label>Sifre</label>
                        <div className="input-wrapper">
                            <Lock size={18} className="input-icon" />
                            <input
                                type="password"
                                name="password"
                                placeholder="********"
                                value={formData.password}
                                onChange={handleChange}
                                required
                                minLength={6}
                            />
                        </div>
                    </div>

                    <div className="form-group mb-4">
                        <label>Sifre Tekrar</label>
                        <div className="input-wrapper">
                            <Lock size={18} className="input-icon" />
                            <input
                                type="password"
                                name="confirmPassword"
                                placeholder="********"
                                value={formData.confirmPassword}
                                onChange={handleChange}
                                required
                                minLength={6}
                            />
                        </div>
                    </div>

                    <button type="submit" className="btn btn-primary w-100" disabled={loading}>
                        {loading ? 'Kayit Olunuyor...' : 'Ucretsiz Kayit Ol'}
                    </button>
                </form>

                <div className="auth-footer text-center mt-4 pt-4">
                    <p className="text-muted text-sm">
                        Zaten bir hesabiniz var mi?{' '}
                        <Link to="/login" className="text-primary font-bold">Giris Yap</Link>
                    </p>
                </div>
            </div>
        </div>
    );
};

export default Register;

