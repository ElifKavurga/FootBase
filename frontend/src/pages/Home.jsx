import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { ChevronRight, Users, Trophy, MessageSquare, Calendar } from 'lucide-react';
import api from '../services/api';
import './Home.css';

const Home = () => {
    const [data, setData] = useState({
        upcomingMatches: [],
        pastMatches: [],
        comments: [],
        playerCount: 0,
        teamCount: 0
    });
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        const fetchHomeData = async () => {
            try {
                const response = await api.get('/home');
                setData(response.data);
            } catch (err) {
                console.error('Veri çekme hatası:', err);
                setError('Veriler yüklenirken bir sorun oluştu.');
            } finally {
                setLoading(false);
            }
        };

        fetchHomeData();
    }, []);

    if (loading) {
        return (
            <div className="container flex-center" style={{ minHeight: '60vh' }}>
                <div className="loading-spinner"></div>
            </div>
        );
    }

    return (
        <div className="home-page">
            {/* Hero Section */}
            <section className="hero-section">
                <div className="container hero-container animate-fade-in">
                    <div className="hero-content">
                        <span className="badge badge-success mb-4 inline-block">Süper Lig Başladı</span>
                        <h1 className="hero-title">
                            Futbolun <span className="text-gradient">Kalbi</span> Burada Atıyor
                        </h1>
                        <p className="hero-subtitle text-muted">
                            En güncel maç sonuçları, takım kadroları ve detaylı istatistiklerle
                            futbol dünyasının nabzını tutun.
                        </p>
                        <div className="hero-actions">
                            <Link to="/matches" className="btn btn-primary">Maçları Gör</Link>
                            <Link to="/teams" className="btn btn-outline">Takımları İncele</Link>
                        </div>
                    </div>

                    <div className="hero-stats grid-2 glass-panel animate-slide-up delay-200">
                        <div className="stat-card">
                            <div className="stat-icon"><Trophy size={28} /></div>
                            <div className="stat-info">
                                <h3>{data.teamCount}</h3>
                                <p className="text-muted">Kayıtlı Takım</p>
                            </div>
                        </div>
                        <div className="stat-card">
                            <div className="stat-icon"><Users size={28} /></div>
                            <div className="stat-info">
                                <h3>{data.playerCount}</h3>
                                <p className="text-muted">Oyuncu</p>
                            </div>
                        </div>
                    </div>
                </div>
            </section>

            <div className="container page-grid">
                {/* Main Column */}
                <div className="main-column">
                    {error && <div className="error-banner glass-panel">{error}</div>}

                    {/* Upcoming Matches */}
                    <section className="section-block animate-slide-up delay-300">
                        <div className="section-header flex-between">
                            <h2 className="section-title flex-center gap-2">
                                <Calendar className="text-primary" /> Gelecek Maçlar
                            </h2>
                            <Link to="/matches" className="view-all-link">Tümünü Gör <ChevronRight size={16} /></Link>
                        </div>

                        <div className="match-cards-v">
                            {data.upcomingMatches && data.upcomingMatches.length > 0 ? (
                                data.upcomingMatches.slice(0, 3).map(match => (
                                    <Link to={`/matches/${match.id}`} key={match.id} className="match-card-h glass-panel">
                                        <div className="match-time flex-center flex-column">
                                            <span className="match-date">{match.date}</span>
                                            <span className="match-hour">{match.time}</span>
                                        </div>
                                        <div className="match-teams flex-between w-100">
                                            <div className="team home">
                                                <span className="team-name">{match.homeTeam}</span>
                                                {match.homeTeamLogo && <img src={match.homeTeamLogo} alt={match.homeTeam} className="team-logo-sm" />}
                                            </div>
                                            <div className="vs-badge">VS</div>
                                            <div className="team away">
                                                {match.awayTeamLogo && <img src={match.awayTeamLogo} alt={match.awayTeam} className="team-logo-sm" />}
                                                <span className="team-name">{match.awayTeam}</span>
                                            </div>
                                        </div>
                                    </Link>
                                ))
                            ) : (
                                <div className="empty-state glass-panel text-center">
                                    <p className="text-muted">Yaklaşan maç bulunmuyor.</p>
                                </div>
                            )}
                        </div>
                    </section>

                    {/* Past Matches */}
                    <section className="section-block animate-slide-up delay-400">
                        <div className="section-header flex-between">
                            <h2 className="section-title">Sonuçlanan Maçlar</h2>
                        </div>

                        <div className="match-cards-v">
                            {data.pastMatches && data.pastMatches.length > 0 ? (
                                data.pastMatches.slice(0, 3).map(match => (
                                    <Link to={`/matches/${match.id}`} key={match.id} className="match-card-h glass-panel">
                                        <div className="match-time flex-center flex-column">
                                            <span className="match-date text-muted">{match.date}</span>
                                            <span className="badge badge-success" style={{ fontSize: '0.65rem' }}>BİTTİ</span>
                                        </div>
                                        <div className="match-teams flex-between w-100">
                                            <div className="team home">
                                                <span className="team-name {match.homeScore > match.awayScore ? 'winner' : ''}">{match.homeTeam}</span>
                                            </div>
                                            <div className="score flex-center">
                                                <span className={match.homeScore > match.awayScore ? 'winner' : ''}>{match.homeScore}</span>
                                                <span className="mx-2">-</span>
                                                <span className={match.awayScore > match.homeScore ? 'winner' : ''}>{match.awayScore}</span>
                                            </div>
                                            <div className="team away text-right">
                                                <span className="team-name {match.awayScore > match.homeScore ? 'winner' : ''}">{match.awayTeam}</span>
                                            </div>
                                        </div>
                                    </Link>
                                ))
                            ) : (
                                <div className="empty-state glass-panel text-center">
                                    <p className="text-muted">Sonuçlanan maç bulunmuyor.</p>
                                </div>
                            )}
                        </div>
                    </section>
                </div>

                {/* Sidebar */}
                <div className="sidebar animate-slide-up delay-400">
                    <div className="glass-panel sidebar-widget active-comments-widget">
                        <h3 className="widget-title flex-center gap-2 mb-4">
                            <MessageSquare size={18} className="text-primary" /> Son Yorumlar
                        </h3>
                        <div className="comments-list">
                            {data.comments && data.comments.length > 0 ? (
                                data.comments.slice(0, 5).map(comment => (
                                    <div key={comment.id} className="comment-item">
                                        <div className="comment-header flex-between">
                                            <span className="comment-author">{comment.author || 'Anonim'}</span>
                                            <span className="comment-date text-muted" style={{ fontSize: '0.75rem' }}>
                                                {new Date(comment.createdAt).toLocaleDateString()}
                                            </span>
                                        </div>
                                        {comment.macBilgisi && (
                                            <div className="comment-match text-muted" style={{ fontSize: '0.8rem', marginBottom: '4px' }}>
                                                Maç: {comment.macBilgisi}
                                            </div>
                                        )}
                                        <p className="comment-text">{comment.message}</p>
                                    </div>
                                ))
                            ) : (
                                <p className="text-muted text-center" style={{ padding: '1rem' }}>Henüz yorum yapılmamış.</p>
                            )}
                        </div>
                        <button className="btn btn-outline w-100 mt-4" style={{ padding: '0.5rem' }}>Tümünü Gör</button>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default Home;
