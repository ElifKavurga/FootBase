import { Link, useLocation } from 'react-router-dom';
import { Menu, X, Home, Users, Trophy, Activity, Bell, ClipboardPen, ShieldCheck } from 'lucide-react';
import { useState, useEffect } from 'react';
import { useAuth } from '../../hooks/useAuth';
import api from '../../services/api';
import './Navbar.css';

const Navbar = () => {
    const [isScrolled, setIsScrolled] = useState(false);
    const [mobileMenuOpen, setMobileMenuOpen] = useState(false);
    const [unreadNotificationCount, setUnreadNotificationCount] = useState(0);
    const location = useLocation();
    const { user, isAuthenticated, isAuthLoading, logout } = useAuth();
    const normalizedRole = (user?.rol || '').toString().trim().toUpperCase();
    const isEditor = normalizedRole === 'EDITOR';
    const isAdmin = normalizedRole === 'ADMIN';

    useEffect(() => {
        const handleScroll = () => {
            setIsScrolled(window.scrollY > 20);
        };
        window.addEventListener('scroll', handleScroll);
        return () => window.removeEventListener('scroll', handleScroll);
    }, []);

    useEffect(() => {
        if (!isAuthenticated) {
            setUnreadNotificationCount(0);
            return;
        }

        let active = true;
        const fetchUnreadCount = async () => {
            try {
                const response = await api.get('/notifications/unread/count');
                if (active) {
                    setUnreadNotificationCount(Number(response?.data?.count || 0));
                }
            } catch {
                if (active) {
                    setUnreadNotificationCount(0);
                }
            }
        };

        fetchUnreadCount();
        const intervalId = setInterval(fetchUnreadCount, 30000);
        return () => {
            active = false;
            clearInterval(intervalId);
        };
    }, [isAuthenticated]);

    const navLinks = [
        { name: 'Ana Sayfa', path: '/', icon: <Home size={18} /> },
        { name: 'Maclar', path: '/matches', icon: <Activity size={18} /> },
        { name: 'Takimlar', path: '/teams', icon: <Trophy size={18} /> },
        { name: 'Oyuncular', path: '/players', icon: <Users size={18} /> }
    ];

    if (isEditor) {
        navLinks.push({ name: 'Editor Paneli', path: '/editor-panel', icon: <ClipboardPen size={18} /> });
    }

    if (isAdmin) {
        navLinks.push({ name: 'Admin Paneli', path: '/admin-panel', icon: <ShieldCheck size={18} /> });
    }

    return (
        <nav className={`navbar ${isScrolled ? 'scrolled glass-panel' : ''}`}>
            <div className="container navbar-container">
                <Link to="/" className="navbar-logo">
                    <div className="logo-icon" style={{ background: 'transparent', boxShadow: 'none' }}>
                        <img src="/logo.png" alt="FootBase" style={{ width: '100%', height: '100%', objectFit: 'contain' }} />
                    </div>
                    <span className="logo-text text-gradient">FootBase</span>
                </Link>

                <div className="navbar-links desktop-only">
                    {navLinks.map((link) => (
                        <Link
                            key={link.path}
                            to={link.path}
                            className={`nav-link ${location.pathname === link.path ? 'active' : ''}`}
                        >
                            {link.icon}
                            {link.name}
                        </Link>
                    ))}
                </div>

                <div className="navbar-actions desktop-only">
                    {isAuthLoading && isAuthenticated ? (
                        <span className="navbar-user">Yukleniyor...</span>
                    ) : isAuthenticated ? (
                        <>
                            <Link to="/notifications" className="notification-link" aria-label="Bildirimler">
                                <Bell size={18} />
                                {unreadNotificationCount > 0 && (
                                    <span className="notification-badge">{unreadNotificationCount > 99 ? '99+' : unreadNotificationCount}</span>
                                )}
                            </Link>
                            <span className="navbar-user">{user?.kullaniciAdi || user?.email || 'Kullanici'}</span>
                            <button
                                type="button"
                                className="btn btn-outline"
                                style={{ padding: '0.5rem 1rem', fontSize: '0.85rem' }}
                                onClick={logout}
                            >
                                Cikis Yap
                            </button>
                        </>
                    ) : (
                        <>
                            <Link to="/login" className="btn btn-outline" style={{ padding: '0.5rem 1rem', fontSize: '0.85rem' }}>Giris Yap</Link>
                            <Link to="/register" className="btn btn-primary" style={{ padding: '0.5rem 1rem', fontSize: '0.85rem' }}>Kayit Ol</Link>
                        </>
                    )}
                </div>

                <button
                    className="mobile-menu-btn mobile-only"
                    onClick={() => setMobileMenuOpen(!mobileMenuOpen)}
                >
                    {mobileMenuOpen ? <X size={24} /> : <Menu size={24} />}
                </button>
            </div>

            <div className={`mobile-menu glass-panel ${mobileMenuOpen ? 'open' : ''}`}>
                <div className="mobile-links">
                    {navLinks.map((link) => (
                        <Link
                            key={link.path}
                            to={link.path}
                            className={`mobile-link ${location.pathname === link.path ? 'active' : ''}`}
                            onClick={() => setMobileMenuOpen(false)}
                        >
                            {link.icon}
                            {link.name}
                        </Link>
                    ))}
                    <div className="mobile-auth-actions">
                        {isAuthLoading && isAuthenticated ? (
                            <div className="mobile-user-label">Yukleniyor...</div>
                        ) : isAuthenticated ? (
                            <>
                                <Link
                                    to="/notifications"
                                    className={`mobile-link ${location.pathname === '/notifications' ? 'active' : ''}`}
                                    onClick={() => setMobileMenuOpen(false)}
                                >
                                    <Bell size={18} />
                                    Bildirimler
                                    {unreadNotificationCount > 0 && (
                                        <span className="notification-badge">{unreadNotificationCount > 99 ? '99+' : unreadNotificationCount}</span>
                                    )}
                                </Link>
                                <div className="mobile-user-label">{user?.kullaniciAdi || user?.email || 'Kullanici'}</div>
                                <button
                                    type="button"
                                    className="btn btn-outline"
                                    onClick={() => {
                                        logout();
                                        setMobileMenuOpen(false);
                                    }}
                                    style={{ width: '100%', display: 'block', textAlign: 'center' }}
                                >
                                    Cikis Yap
                                </button>
                            </>
                        ) : (
                            <>
                                <Link to="/login" className="btn btn-outline" onClick={() => setMobileMenuOpen(false)} style={{ width: '100%', marginBottom: '1rem', display: 'block', textAlign: 'center' }}>Giris Yap</Link>
                                <Link to="/register" className="btn btn-primary" onClick={() => setMobileMenuOpen(false)} style={{ width: '100%', display: 'block', textAlign: 'center' }}>Kayit Ol</Link>
                            </>
                        )}
                    </div>
                </div>
            </div>
        </nav>
    );
};

export default Navbar;
