import { useState, useEffect } from 'react';
import { useParams, Link } from 'react-router-dom';
import { ArrowLeft, MapPin, Calendar, ExternalLink, Trophy, Users, Activity } from 'lucide-react';
import api from '../services/api';
import './TeamDetail.css';

const TeamDetail = () => {
    const { id } = useParams();
    const [team, setTeam] = useState(null);
    const [players, setPlayers] = useState([]);
    const [matches, setMatches] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [activeTab, setActiveTab] = useState('overview');

    useEffect(() => {
        const fetchTeamDetails = async () => {
            setLoading(true);
            try {
                const [teamRes, playersRes, matchesRes] = await Promise.all([
                    api.get(`/teams/${id}`),
                    api.get(`/teams/${id}/players`),
                    api.get(`/teams/${id}/matches`)
                ]);

                setTeam(teamRes.data);
                setPlayers(playersRes.data);
                setMatches(matchesRes.data);
            } catch (err) {
                console.error('Takım detayları çekilirken hata:', err);
                setError('Takım bilgileri yüklenirken bir sorun oluştu.');
            } finally {
                setLoading(false);
            }
        };

        fetchTeamDetails();
    }, [id]);

    if (loading) {
        return (
            <div className="container flex-center" style={{ minHeight: '60vh' }}>
                <div className="loading-spinner"></div>
            </div>
        );
    }

    if (error || !team) {
        return (
            <div className="container" style={{ marginTop: '2rem' }}>
                <Link to="/teams" className="btn btn-outline mb-4 inline-flex"><ArrowLeft size={16} /> Geri Dön</Link>
                <div className="error-banner glass-panel text-center">
                    <p>{error || 'Takım bulunamadı.'}</p>
                </div>
            </div>
        );
    }

    return (
        <div className="team-detail-page container animate-fade-in">
            <Link to="/teams" className="back-link mb-4 inline-flex"><ArrowLeft size={16} /> Takımlara Dön</Link>

            {/* Team Header */}
            <div className="team-header-card glass-panel flex-center flex-column py-5">
                <div className="team-logo-lg-wrapper mb-4">
                    {team.logo ? (
                        <img src={team.logo} alt={team.ad} className="team-logo-lg" />
                    ) : (
                        <div className="team-logo-lg-placeholder"><Trophy size={64} /></div>
                    )}
                </div>
                <h1 className="team-name-lg text-gradient">{team.ad} – {team.kisaAd}</h1>

                <div className="team-meta-row flex-center gap-4 mt-4 flex-wrap">
                    {team.kurulusYili && (
                        <div className="meta-pill text-muted glass-panel"><Calendar size={16} /> Kuruluş: {team.kurulusYili}</div>
                    )}
                    {team.stadyum && (
                        <div className="meta-pill text-muted glass-panel"><MapPin size={16} /> Stadyum: {team.stadyum.stadyumAdi}</div>
                    )}
                </div>
            </div>

            {/* Tabs */}
            <div className="tabs-container mt-5">
                <div className="tabs-header flex-center gap-2 mb-4 glass-panel py-2 px-2">
                    <button
                        className={`tab-btn ${activeTab === 'overview' ? 'active' : ''}`}
                        onClick={() => setActiveTab('overview')}
                    >
                        <Activity size={18} /> Genel Bakış
                    </button>
                    <button
                        className={`tab-btn ${activeTab === 'players' ? 'active' : ''}`}
                        onClick={() => setActiveTab('players')}
                    >
                        <Users size={18} /> Oyuncular ({players.length})
                    </button>
                    <button
                        className={`tab-btn ${activeTab === 'matches' ? 'active' : ''}`}
                        onClick={() => setActiveTab('matches')}
                    >
                        <Trophy size={18} /> Fikstür & Maçlar ({matches.length})
                    </button>
                </div>

                {/* Tab Content: Overview */}
                {activeTab === 'overview' && (
                    <div className="tab-pane animate-slide-up">
                        <div className="grid-2 gap-4">
                            <div className="glass-panel p-4">
                                <h3 className="section-title mb-4">Takım İstatistikleri</h3>
                                <div className="stats-list">
                                    <div className="stat-row flex-between py-2 border-bottom">
                                        <span className="text-muted">Toplam Oyuncu</span>
                                        <span className="font-bold">{players.length}</span>
                                    </div>
                                    <div className="stat-row flex-between py-2 border-bottom">
                                        <span className="text-muted">Oynanan/Gelecek Maç</span>
                                        <span className="font-bold">{matches.length}</span>
                                    </div>
                                </div>
                            </div>

                            <div className="glass-panel p-4">
                                <h3 className="section-title mb-4">Son Maçlar</h3>
                                {matches.length > 0 ? (
                                    <div className="matches-mini-list">
                                        {matches.slice(0, 3).map(m => (
                                            <div key={m.id} className="match-mini-row flex-between py-2 border-bottom">
                                                <span className="text-muted text-sm">{m.tarih || 'Tarih Yok'}</span>
                                                <div className="font-bold flex-center gap-2">
                                                    <span className={m.evSahibiTakim?.id === team.id ? 'text-primary' : ''}>{m.evSahibiTakim?.kisaAd || '?'}</span>
                                                    <span className="text-muted">vs</span>
                                                    <span className={m.deplasmanTakim?.id === team.id ? 'text-primary' : ''}>{m.deplasmanTakim?.kisaAd || '?'}</span>
                                                </div>
                                            </div>
                                        ))}
                                    </div>
                                ) : (
                                    <p className="text-muted">Maç verisi bulunamadı.</p>
                                )}
                                <button onClick={() => setActiveTab('matches')} className="btn btn-outline w-100 mt-4 py-2">Tüm Fikstür</button>
                            </div>
                        </div>
                    </div>
                )}

                {/* Tab Content: Players */}
                {activeTab === 'players' && (
                    <div className="tab-pane animate-slide-up">
                        {players.length > 0 ? (
                            <div className="grid-cards gap-4">
                                {players.map(player => (
                                    <Link key={player.id} to={`/players/${player.id}`} className="player-card glass-panel flex-center flex-column p-4 text-center hover-scale">
                                        <div className="player-avatar mb-3">
                                            {player.fotograf ? <img src={player.fotograf} alt={`${player.ad} ${player.soyad}`} /> : <Users size={32} className="text-muted" />}
                                        </div>
                                        <h4 className="player-name font-bold">{player.ad} {player.soyad}</h4>
                                        <p className="text-primary font-bold">{player.pozisyon || 'Bilinmiyor'}</p>
                                        <div className="player-meta text-muted text-sm mt-2 flex-center gap-2">
                                            <span>Maça Çıkma: {player.macaCikma || 0}</span>
                                            <span>|</span>
                                            <span>Gol: {player.gol || 0}</span>
                                        </div>
                                    </Link>
                                ))}
                            </div>
                        ) : (
                            <div className="empty-state glass-panel text-center py-5">
                                <Users size={48} className="text-muted mb-4" />
                                <h3>Oyuncu Bulunamadı</h3>
                                <p className="text-muted">Bu takımın kadrosunda henüz oyuncu yok.</p>
                            </div>
                        )}
                    </div>
                )}

                {/* Tab Content: Matches */}
                {activeTab === 'matches' && (
                    <div className="tab-pane animate-slide-up">
                        {matches.length > 0 ? (
                            <div className="match-cards-v gap-3">
                                {matches.map(m => (
                                    <div key={m.id} className="match-card-h glass-panel">
                                        <div className="match-time flex-center flex-column border-right pr-4">
                                            <span className="match-date text-sm">{m.tarih || 'Tarih Yok'}</span>
                                            <span className="badge badge-warning text-xs mt-1">{m.durum || m.onayDurumu || 'PLANLI'}</span>
                                        </div>
                                        <div className="match-teams flex-between w-100 px-4">
                                            <div className={`team home ${m.evSahibiTakim?.id === team.id ? 'font-bold text-primary' : ''}`}>
                                                {m.evSahibiTakim?.ad || 'Bilinmiyor'}
                                            </div>
                                            <div className="score flex-center font-bold px-3 py-1 glass-panel radius-md">
                                                <span>{m.evSahibiSkor !== null ? m.evSahibiSkor : '-'}</span>
                                                <span className="mx-2">:</span>
                                                <span>{m.deplasmanSkor !== null ? m.deplasmanSkor : '-'}</span>
                                            </div>
                                            <div className={`team away text-right ${m.deplasmanTakim?.id === team.id ? 'font-bold text-primary' : ''}`}>
                                                {m.deplasmanTakim?.ad || 'Bilinmiyor'}
                                            </div>
                                        </div>
                                    </div>
                                ))}
                            </div>
                        ) : (
                            <div className="empty-state glass-panel text-center py-5">
                                <Trophy size={48} className="text-muted mb-4" />
                                <h3>Fikstür Bulunamadı</h3>
                                <p className="text-muted">Bu takıma ait henüz bir maç kaydı yok.</p>
                            </div>
                        )}
                    </div>
                )}

            </div>
        </div>
    );
};

export default TeamDetail;
