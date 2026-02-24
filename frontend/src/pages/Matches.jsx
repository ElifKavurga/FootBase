import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { Calendar, Filter } from 'lucide-react';
import api from '../services/api';
import './Matches.css';

const Matches = () => {
    const [matches, setMatches] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [activeTab, setActiveTab] = useState('upcoming'); // 'upcoming' or 'past'

    useEffect(() => {
        const fetchMatches = async () => {
            try {
                const response = await api.get('/matches');

                // MOCKING FOR TESTING: Planlı maçları Bitti yapalım ve skor atayalım
                const mockedData = response.data.map(m => {
                    if (m.durum === 'PLANLI' || !m.durum) {
                        return {
                            ...m,
                            durum: 'BİTTİ',
                            evSahibiSkor: m.evSahibiSkor !== null ? m.evSahibiSkor : Math.floor(Math.random() * 4),
                            deplasmanSkor: m.deplasmanSkor !== null ? m.deplasmanSkor : Math.floor(Math.random() * 3)
                        };
                    }
                    return m;
                });

                // Sort matches by date
                const sorted = mockedData.sort((a, b) => {
                    if (!a.tarih) return 1;
                    if (!b.tarih) return -1;
                    return new Date(b.tarih) - new Date(a.tarih);
                });
                setMatches(sorted);
            } catch (err) {
                console.error('Maçları çekerken hata:', err);
                setError('Maçlar yüklenirken bir sorun oluştu.');
            } finally {
                setLoading(false);
            }
        };

        fetchMatches();
    }, []);

    // Oynanan (canlı) ve bitmiş maçları "pastMatches", sadece planlananları "upcomingMatches" listesine koyuyoruz
    const upcomingMatches = matches.filter(m =>
        m.durum === 'PLANLI' || m.durum === 'ERTELENDİ' || m.durum === 'YAKINDA' || (!m.durum && m.evSahibiSkor === null)
    );

    const pastMatches = matches.filter(m => !upcomingMatches.includes(m));

    const displayMatches = activeTab === 'upcoming' ? upcomingMatches : pastMatches;

    if (loading) {
        return (
            <div className="container flex-center" style={{ minHeight: '60vh' }}>
                <div className="loading-spinner"></div>
            </div>
        );
    }

    return (
        <div className="matches-page container animate-fade-in">
            <div className="page-header flex-between flex-wrap gap-4">
                <div>
                    <h1 className="page-title text-gradient">Fikstür ve Maçlar</h1>
                    <p className="text-muted">Ligdeki tüm heyecan verici karşılaşmalar</p>
                </div>
            </div>

            {error ? (
                <div className="error-banner glass-panel">{error}</div>
            ) : (
                <div className="matches-content">
                    <div className="matches-tabs flex-center gap-4 mb-5">
                        <button
                            className={`match-tab-btn ${activeTab === 'upcoming' ? 'active' : ''}`}
                            onClick={() => setActiveTab('upcoming')}
                        >
                            Gelecek Maçlar ({upcomingMatches.length})
                        </button>
                        <button
                            className={`match-tab-btn ${activeTab === 'past' ? 'active' : ''}`}
                            onClick={() => setActiveTab('past')}
                        >
                            Oynanan & Tamamlanan ({pastMatches.length})
                        </button>
                    </div>

                    <div className="matches-list grid gap-4 animate-slide-up">
                        {displayMatches.length > 0 ? (
                            displayMatches.map(match => (
                                <Link to={`/matches/${match.id}`} key={match.id} className="match-list-card glass-panel flex-between flex-wrap hover-scale">
                                    <div className="match-list-time flex-center flex-column">
                                        <Calendar size={18} className="text-primary mb-1" />
                                        <span className="font-bold">{match.tarih || 'Tarih Belirsiz'}</span>
                                        <span className="text-muted text-sm">{match.saat || ''}</span>
                                        <span className={`badge mt-2 ${activeTab === 'past' ? 'badge-success' : 'badge-warning'}`}>
                                            {match.durum || (activeTab === 'past' ? 'BİTTİ' : 'PLANLI')}
                                        </span>
                                    </div>

                                    <div className="match-list-teams flex-1 flex-center w-100">
                                        <div className="team home text-right pr-4 w-50 flex-center" style={{ justifyContent: 'flex-end', gap: '1rem' }}>
                                            <span className="font-bold text-lg">{match.evSahibiTakim?.ad || 'Ev Sahibi'}</span>
                                            {match.evSahibiTakim?.logo && (
                                                <img src={match.evSahibiTakim.logo} alt="logo" className="team-logo-md" />
                                            )}
                                        </div>

                                        <div className="score-box glass-panel px-4 py-2 mx-4 flex-center font-bold text-2xl" style={{ minWidth: '100px' }}>
                                            {activeTab === 'past' ? (
                                                `${match.evSahibiSkor !== null ? match.evSahibiSkor : '0'} - ${match.deplasmanSkor !== null ? match.deplasmanSkor : '0'}`
                                            ) : (
                                                'VS'
                                            )}
                                        </div>

                                        <div className="team away pl-4 w-50 flex-center" style={{ justifyContent: 'flex-start', gap: '1rem' }}>
                                            {match.deplasmanTakim?.logo && (
                                                <img src={match.deplasmanTakim.logo} alt="logo" className="team-logo-md" />
                                            )}
                                            <span className="font-bold text-lg">{match.deplasmanTakim?.ad || 'Deplasman'}</span>
                                        </div>
                                    </div>
                                </Link>
                            ))
                        ) : (
                            <div className="empty-state glass-panel text-center py-5 w-100">
                                <Filter size={48} className="text-muted mb-4" />
                                <h3>Maç Bulunamadı</h3>
                                <p className="text-muted">Bu kategoride gösterilecek maç bulunmuyor.</p>
                            </div>
                        )}
                    </div>
                </div>
            )}
        </div>
    );
};

export default Matches;
