import { Link } from 'react-router-dom';
import { Activity, Instagram, Twitter, Youtube, Github } from 'lucide-react';
import './Footer.css';

const Footer = () => {
    return (
        <footer className="footer">
            <div className="container footer-container">
                <div className="footer-top">
                    <div className="footer-brand">
                        <Link to="/" className="navbar-logo">
                            <div className="logo-icon" style={{ background: 'transparent', boxShadow: 'none' }}>
                                <img src="/logo.png" alt="FootBase" style={{ width: '100%', height: '100%', objectFit: 'contain' }} />
                            </div>
                            <span className="logo-text text-gradient">FootBase</span>
                        </Link>
                        <p className="footer-description text-muted">
                            Modern futbol veri ve istatistik platformu. Takımları, oyuncuları ve maçları anlık takip edin.
                        </p>
                        <div className="social-links">
                            <a href="#" className="social-link"><Twitter size={20} /></a>
                            <a href="#" className="social-link"><Instagram size={20} /></a>
                            <a href="#" className="social-link"><Youtube size={20} /></a>
                            <a href="#" className="social-link"><Github size={20} /></a>
                        </div>
                    </div>

                    <div className="footer-links-group">
                        <h4 className="footer-heading">Platform</h4>
                        <div className="footer-links">
                            <Link to="/matches">Canlı Maçlar</Link>
                            <Link to="/teams">Takımlar</Link>
                            <Link to="/players">Oyuncular</Link>
                            <Link to="/stats">İstatistikler</Link>
                        </div>
                    </div>

                    <div className="footer-links-group">
                        <h4 className="footer-heading">Kurumsal</h4>
                        <div className="footer-links">
                            <Link to="#">Hakkımızda</Link>
                            <Link to="#">Kariyer</Link>
                            <Link to="#">İletişim</Link>
                            <Link to="#">Basın Odası</Link>
                        </div>
                    </div>

                    <div className="footer-links-group">
                        <h4 className="footer-heading">Yasal</h4>
                        <div className="footer-links">
                            <Link to="#">Kullanım Koşulları</Link>
                            <Link to="#">Gizlilik Politikası</Link>
                            <Link to="#">Çerez Politikası</Link>
                        </div>
                    </div>
                </div>

                <div className="footer-bottom flex-between">
                    <p className="text-muted">
                        &copy; {new Date().getFullYear()} FootBase. Tüm hakları saklıdır.
                    </p>
                    <div className="flex-center" style={{ gap: '0.5rem', color: 'var(--text-muted)' }}>
                        <span>Made with</span>
                        <Activity size={16} color="var(--primary-color)" />
                        <span>in Turkey</span>
                    </div>
                </div>
            </div>
        </footer>
    );
};

export default Footer;
