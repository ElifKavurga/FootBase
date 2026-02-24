import { Link, useLocation } from 'react-router-dom';
import { Menu, X, Home, Users, Trophy, Activity } from 'lucide-react';
import { useState, useEffect } from 'react';
import './Navbar.css';

const Navbar = () => {
    const [isScrolled, setIsScrolled] = useState(false);
    const [mobileMenuOpen, setMobileMenuOpen] = useState(false);
    const location = useLocation();

    useEffect(() => {
        const handleScroll = () => {
            setIsScrolled(window.scrollY > 20);
        };
        window.addEventListener('scroll', handleScroll);
        return () => window.removeEventListener('scroll', handleScroll);
    }, []);

    const navLinks = [
        { name: 'Ana Sayfa', path: '/', icon: <Home size={18} /> },
        { name: 'Maçlar', path: '/matches', icon: <Activity size={18} /> },
        { name: 'Takımlar', path: '/teams', icon: <Trophy size={18} /> },
        { name: 'Oyuncular', path: '/players', icon: <Users size={18} /> }
    ];

    return (
        <nav className={`navbar ${isScrolled ? 'scrolled glass-panel' : ''}`}>
            <div className="container navbar-container">

                {/* Logo */}
                <Link to="/" className="navbar-logo">
                    <div className="logo-icon" style={{ background: 'transparent', boxShadow: 'none' }}>
                        <img src="/logo.png" alt="FootBase" style={{ width: '100%', height: '100%', objectFit: 'contain' }} />
                    </div>
                    <span className="logo-text text-gradient">FootBase</span>
                </Link>

                {/* Desktop Navigation */}
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

                {/* Auth Buttons */}
                <div className="navbar-actions desktop-only">
                    <Link to="/login" className="btn btn-outline" style={{ padding: '0.5rem 1rem', fontSize: '0.85rem' }}>Giriş Yap</Link>
                    <Link to="/register" className="btn btn-primary" style={{ padding: '0.5rem 1rem', fontSize: '0.85rem' }}>Kayıt Ol</Link>
                </div>

                {/* Mobile menu button */}
                <button
                    className="mobile-menu-btn mobile-only"
                    onClick={() => setMobileMenuOpen(!mobileMenuOpen)}
                >
                    {mobileMenuOpen ? <X size={24} /> : <Menu size={24} />}
                </button>

            </div>

            {/* Mobile Navigation */}
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
                        <Link to="/login" className="btn btn-outline" onClick={() => setMobileMenuOpen(false)} style={{ width: '100%', marginBottom: '1rem', display: 'block', textAlign: 'center' }}>Giriş Yap</Link>
                        <Link to="/register" className="btn btn-primary" onClick={() => setMobileMenuOpen(false)} style={{ width: '100%', display: 'block', textAlign: 'center' }}>Kayıt Ol</Link>
                    </div>
                </div>
            </div>
        </nav>
    );
};

export default Navbar;
