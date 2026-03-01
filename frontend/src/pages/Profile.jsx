import { useEffect, useMemo, useState } from 'react';
import { Link, Navigate } from 'react-router-dom';
import { History, Lock, Mail, User } from 'lucide-react';
import api from '../services/api';
import { useAuth } from '../hooks/useAuth';
import './Profile.css';

const Profile = () => {
    const { isAuthenticated, user, fetchCurrentUser, resetPassword } = useAuth();
    const [profile, setProfile] = useState(null);
    const [history, setHistory] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');
    const [savingProfile, setSavingProfile] = useState(false);
    const [savingPassword, setSavingPassword] = useState(false);
    const [profileForm, setProfileForm] = useState({
        kullaniciAdi: '',
        email: ''
    });
    const [passwordForm, setPasswordForm] = useState({
        newPassword: '',
        confirmPassword: ''
    });

    useEffect(() => {
        const loadProfile = async () => {
            setLoading(true);
            setError('');
            try {
                const [meRes, historyRes] = await Promise.all([
                    api.get('/users/me'),
                    api.get('/users/me/history').catch(() => ({ data: [] }))
                ]);
                setProfile(meRes?.data || null);
                setHistory(Array.isArray(historyRes?.data) ? historyRes.data : []);
                setProfileForm({
                    kullaniciAdi: meRes?.data?.kullaniciAdi || '',
                    email: meRes?.data?.email || ''
                });
            } catch (err) {
                setError(err?.response?.data?.hata || 'Profil bilgileri yuklenemedi.');
            } finally {
                setLoading(false);
            }
        };

        if (isAuthenticated) {
            loadProfile();
        }
    }, [isAuthenticated]);

    const displayRole = useMemo(() => (profile?.rol || user?.rol || 'USER').toString().toUpperCase(), [profile?.rol, user?.rol]);

    const handleProfileUpdate = async (e) => {
        e.preventDefault();
        setError('');
        setSuccess('');
        if (!profileForm.kullaniciAdi.trim() || !profileForm.email.trim()) {
            setError('Kullanici adi ve email zorunlu.');
            return;
        }

        setSavingProfile(true);
        try {
            await api.put('/users/me', {
                kullaniciAdi: profileForm.kullaniciAdi.trim(),
                email: profileForm.email.trim()
            });
            await fetchCurrentUser();
            const meRes = await api.get('/users/me');
            setProfile(meRes?.data || null);
            setSuccess('Profil bilgileri guncellendi.');
        } catch (err) {
            setError(err?.response?.data?.hata || 'Profil guncellenemedi.');
        } finally {
            setSavingProfile(false);
        }
    };

    const handlePasswordUpdate = async (e) => {
        e.preventDefault();
        setError('');
        setSuccess('');
        const newPassword = passwordForm.newPassword.trim();
        if (newPassword.length < 6) {
            setError('Yeni sifre en az 6 karakter olmali.');
            return;
        }
        if (newPassword !== passwordForm.confirmPassword.trim()) {
            setError('Sifreler eslesmiyor.');
            return;
        }

        const email = profile?.email || user?.email;
        if (!email) {
            setError('Kullanici email bilgisi bulunamadi.');
            return;
        }

        setSavingPassword(true);
        try {
            const result = await resetPassword({ email, newPassword });
            if (!result.success) {
                setError(result.message);
                return;
            }
            setPasswordForm({ newPassword: '', confirmPassword: '' });
            setSuccess('Sifre basariyla guncellendi.');
        } catch (err) {
            setError(err?.response?.data?.hata || 'Sifre guncellenemedi.');
        } finally {
            setSavingPassword(false);
        }
    };

    if (!isAuthenticated) {
        return <Navigate to="/login" replace />;
    }

    if (loading) {
        return (
            <div className="container flex-center" style={{ minHeight: '60vh' }}>
                <div className="loading-spinner"></div>
            </div>
        );
    }

    return (
        <div className="profile-page container animate-fade-in">
            <div className="profile-header glass-panel">
                <h1 className="text-gradient">Profilim</h1>
                <p className="text-muted">Hesap bilgilerini guncelle ve gecmis hareketlerini gor.</p>
            </div>

            {error && <div className="error-banner mb-3">{error}</div>}
            {success && <div className="success-banner mb-3">{success}</div>}

            <div className="profile-grid">
                <section className="glass-panel profile-card">
                    <h2>Temel Bilgiler</h2>
                    <div className="profile-meta mt-3 mb-3">
                        <div className="profile-meta-item"><User size={16} /> Kullanici Adi: <strong>{profile?.kullaniciAdi || '-'}</strong></div>
                        <div className="profile-meta-item"><Mail size={16} /> Email: <strong>{profile?.email || '-'}</strong></div>
                        <div className="profile-meta-item"><Lock size={16} /> Rol: <strong>{displayRole}</strong></div>
                    </div>

                    <form className="profile-form" onSubmit={handleProfileUpdate}>
                        <label>
                            Kullanici Adi
                            <input
                                value={profileForm.kullaniciAdi}
                                onChange={(e) => setProfileForm((prev) => ({ ...prev, kullaniciAdi: e.target.value }))}
                                required
                            />
                        </label>
                        <label>
                            Email
                            <input
                                type="email"
                                value={profileForm.email}
                                onChange={(e) => setProfileForm((prev) => ({ ...prev, email: e.target.value }))}
                                required
                            />
                        </label>
                        <button type="submit" className="btn btn-primary" disabled={savingProfile}>
                            {savingProfile ? 'Kaydediliyor...' : 'Bilgileri Guncelle'}
                        </button>
                    </form>

                    <form className="profile-form mt-4" onSubmit={handlePasswordUpdate}>
                        <h3>Sifre Guncelle</h3>
                        <label>
                            Yeni Sifre
                            <input
                                type="password"
                                minLength={6}
                                value={passwordForm.newPassword}
                                onChange={(e) => setPasswordForm((prev) => ({ ...prev, newPassword: e.target.value }))}
                                required
                            />
                        </label>
                        <label>
                            Yeni Sifre Tekrar
                            <input
                                type="password"
                                minLength={6}
                                value={passwordForm.confirmPassword}
                                onChange={(e) => setPasswordForm((prev) => ({ ...prev, confirmPassword: e.target.value }))}
                                required
                            />
                        </label>
                        <button type="submit" className="btn btn-outline" disabled={savingPassword}>
                            {savingPassword ? 'Guncelleniyor...' : 'Sifreyi Guncelle'}
                        </button>
                    </form>
                </section>

                <section className="glass-panel profile-card">
                    <div className="profile-history-head">
                        <h2><History size={18} /> Gecmis</h2>
                    </div>
                    {history.length === 0 ? (
                        <div className="profile-empty">Henuz yorum veya puanlama gecmisi yok.</div>
                    ) : (
                        <div className="profile-history-list">
                            {history.map((item) => (
                                <article key={item.commentId} className="profile-history-item">
                                    <div className="profile-history-top">
                                        <span className="badge badge-warning">{item.type === 'OYUNCU_PUANLAMASI' ? 'Oyuncu Puanlamasi' : 'Mac Yorumu'}</span>
                                        <span className="text-xs text-muted">
                                            {item.createdAt ? new Date(item.createdAt).toLocaleString('tr-TR') : '-'}
                                        </span>
                                    </div>
                                    <p>{item.message || '-'}</p>
                                    {item.matchId && (
                                        <Link to={`/matches/${item.matchId}`} className="text-sm text-primary">
                                            Maca Git: {item.matchTitle || `Mac #${item.matchId}`}
                                        </Link>
                                    )}
                                    {item.playerId && (
                                        <Link to={`/players/${item.playerId}`} className="text-sm text-primary">
                                            Oyuncuya Git: {item.matchTitle || `Oyuncu #${item.playerId}`}
                                        </Link>
                                    )}
                                </article>
                            ))}
                        </div>
                    )}
                </section>
            </div>
        </div>
    );
};

export default Profile;
