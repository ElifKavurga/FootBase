import { useState, useEffect } from 'react';
import { useParams, Link } from 'react-router-dom';
import { ArrowLeft, Calendar, Clock, Trophy, Activity, MessageSquare, Flame } from 'lucide-react';
import api from '../services/api';
import './MatchDetail.css';

const MatchDetail = () => {
    const { id } = useParams();
    const [match, setMatch] = useState(null);
    const [events, setEvents] = useState([]);
    const [comments, setComments] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [activeTab, setActiveTab] = useState('summary');

    useEffect(() => {
        const fetchMatchDetails = async () => {
            setLoading(true);
            try {
                // Fetch basic match data, events and comments
                // The new /api/matches/{id}/detayli endpoint seems available based on controller,
                // but we will fetch individually if not sure, or try the facade one. Let's use individual endpoints to be safe.
                const [matchRes, eventsRes, commentsRes] = await Promise.all([
                    api.get(`/matches/${id}`),
                    api.get(`/matches/${id}/events`).catch(() => ({ data: [] })),
                    api.get(`/matches/${id}/comments`).catch(() => ({ data: [] }))
                ]);

                let matchData = matchRes.data;
                // MOCKING FOR TESTING: Eğer maç planlıysa bitmiş gibi göster
                if (matchData.durum === 'PLANLI' || !matchData.durum) {
                    matchData = {
                        ...matchData,
                        durum: 'BİTTİ',
                        evSahibiSkor: matchData.evSahibiSkor !== null ? matchData.evSahibiSkor : 2,
                        deplasmanSkor: matchData.deplasmanSkor !== null ? matchData.deplasmanSkor : 1
                    };
                }

                let matchEvents = eventsRes.data || [];
                // MOCKING FOR TESTING: Eğer olay yoksa sahte olaylar ekleyelim
                if (matchEvents.length === 0 && matchData.durum === 'BİTTİ') {
                    matchEvents = [
                        { dakika: 12, olayTuru: 'GOL', takimId: matchData.evSahibiTakim?.id, aciklama: 'Harika bir şutla top ağlarda!' },
                        { dakika: 34, olayTuru: 'SARI_KART', takimId: matchData.deplasmanTakim?.id, aciklama: 'Sert müdahale sonrası sarı kart.' },
                        { dakika: 67, olayTuru: 'GOL', takimId: matchData.evSahibiTakim?.id, aciklama: 'Kafa vuruşuyla gelen gol!' },
                        { dakika: 89, olayTuru: 'GOL', takimId: matchData.deplasmanTakim?.id, aciklama: 'Ceza sahası dışından mükemmel gol.' }
                    ];
                }

                setMatch(matchData);
                setEvents(matchEvents);
                setComments(commentsRes.data || []);
            } catch (err) {
                console.error('Maç detayları çekilirken hata:', err);
                setError('Maç bilgileri yüklenirken bir sorun oluştu.');
            } finally {
                setLoading(false);
            }
        };

        fetchMatchDetails();
    }, [id]);

    if (loading) {
        return (
            <div className="container flex-center" style={{ minHeight: '60vh' }}>
                <div className="loading-spinner"></div>
            </div>
        );
    }

    if (error || !match) {
        return (
            <div className="container" style={{ marginTop: '2rem' }}>
                <Link to="/matches" className="btn btn-outline mb-4 inline-flex"><ArrowLeft size={16} /> Geri Dön</Link>
                <div className="error-banner glass-panel text-center">
                    <p>{error || 'Maç bulunamadı.'}</p>
                </div>
            </div>
        );
    }

    // Is match finished?
    const isFinished = match.durum === 'BİTTİ' || match.evSahibiSkor !== null;

    return (
        <div className="match-detail-page container animate-fade-in">
            <Link to="/matches" className="back-link mb-4 inline-flex"><ArrowLeft size={16} /> Fikstüre Dön</Link>

            {/* Match Header Scoreboard */}
            <div className="scoreboard-card glass-panel mb-5">
                <div className="scoreboard-header flex-center gap-4 border-bottom py-3 px-4">
                    <div className="match-meta text-muted flex-center gap-2 text-sm">
                        <Calendar size={14} /> {match.tarih || 'Tarih Yok'}
                        <span className="mx-2">|</span>
                        <Clock size={14} /> {match.saat || 'Saat Yok'}
                    </div>
                    <div className={`badge ${isFinished ? 'badge-success' : 'badge-warning'}`}>
                        {match.durum || (isFinished ? 'BİTTİ' : 'PLANLANAN')}
                    </div>
                </div>

                <div className="scoreboard-body flex-between py-5 px-4 px-md-5 relative">
                    {/* Home Team */}
                    <Link to={`/teams/${match.evSahibiTakim?.id}`} className="team-container home flex-center flex-column">
                        <div className="team-logo-xl-wrapper mb-3">
                            {match.evSahibiTakim?.logo ? (
                                <img src={match.evSahibiTakim.logo} alt={match.evSahibiTakim.ad} className="team-logo-xl" />
                            ) : (
                                <Trophy size={48} className="text-muted opacity-50" />
                            )}
                        </div>
                        <h2 className="team-name text-center mx-3">{match.evSahibiTakim?.ad || 'Ev Sahibi'}</h2>
                    </Link>

                    {/* Score Segment */}
                    <div className="score-container flex-center flex-column mx-4">
                        {isFinished ? (
                            <div className="score-display text-gradient font-bold" style={{ fontSize: '4rem', lineHeight: 1 }}>
                                {match.evSahibiSkor} - {match.deplasmanSkor}
                            </div>
                        ) : (
                            <div className="vs-display font-bold text-muted" style={{ fontSize: '3rem' }}>VS</div>
                        )}
                        {isFinished && <div className="text-muted mt-2">Maç Sonucu</div>}
                    </div>

                    {/* Away Team */}
                    <Link to={`/teams/${match.deplasmanTakim?.id}`} className="team-container away flex-center flex-column">
                        <div className="team-logo-xl-wrapper mb-3">
                            {match.deplasmanTakim?.logo ? (
                                <img src={match.deplasmanTakim.logo} alt={match.deplasmanTakim.ad} className="team-logo-xl" />
                            ) : (
                                <Trophy size={48} className="text-muted opacity-50" />
                            )}
                        </div>
                        <h2 className="team-name text-center mx-3">{match.deplasmanTakim?.ad || 'Deplasman'}</h2>
                    </Link>
                </div>
            </div>

            {/* Tabs */}
            <div className="tabs-container mt-5">
                <div className="tabs-header flex-center gap-2 mb-4 glass-panel py-2 px-2">
                    <button
                        className={`tab-btn ${activeTab === 'summary' ? 'active' : ''}`}
                        onClick={() => setActiveTab('summary')}
                    >
                        <Activity size={18} /> Maç Özeti
                    </button>
                    <button
                        className={`tab-btn ${activeTab === 'comments' ? 'active' : ''}`}
                        onClick={() => setActiveTab('comments')}
                    >
                        <MessageSquare size={18} /> Maç Sonu Yorumları ({comments.length})
                    </button>
                </div>

                {/* Tab Content: Summary */}
                {activeTab === 'summary' && (
                    <div className="tab-pane animate-slide-up">
                        {events.length > 0 ? (
                            <div className="events-timeline glass-panel p-4">
                                <h3 className="section-title mb-4">Önemli Anlar</h3>
                                <div className="timeline-container relative">
                                    <div className="timeline-line absolute" style={{ left: '50%', top: 0, bottom: 0, width: '2px', background: 'var(--border-light)', transform: 'translateX(-50%)' }}></div>

                                    {events.map((ev, idx) => (
                                        <div key={idx} className="timeline-item flex-between mb-4 relative z-10 w-100">
                                            {/* Left side (Home team events) */}
                                            <div className="timeline-content left w-50 pr-4 text-right">
                                                {ev.takimId === match.evSahibiTakim?.id && (
                                                    <div className="timeline-event bg-card p-3 rounded">
                                                        <span className="font-bold">{ev.dakika}'</span> {ev.aciklama}
                                                        {ev.olayTuru === 'GOL' && <span className="ml-2">⚽️</span>}
                                                    </div>
                                                )}
                                            </div>

                                            {/* Center icon */}
                                            <div className="timeline-icon bg-dark flex-center border border-light rounded-circle" style={{ width: '32px', height: '32px', zIndex: 2 }}>
                                                {ev.olayTuru === 'GOL' ? <span style={{ fontSize: '12px' }}>⚽</span> : <Flame size={14} className="text-muted" />}
                                            </div>

                                            {/* Right side (Away team events) */}
                                            <div className="timeline-content right w-50 pl-4 text-left">
                                                {ev.takimId === match.deplasmanTakim?.id && (
                                                    <div className="timeline-event bg-card p-3 rounded">
                                                        <span className="font-bold">{ev.dakika}'</span> {ev.aciklama}
                                                        {ev.olayTuru === 'GOL' && <span className="ml-2">⚽️</span>}
                                                    </div>
                                                )}
                                            </div>
                                        </div>
                                    ))}
                                </div>
                            </div>
                        ) : (
                            <div className="empty-state glass-panel text-center py-5">
                                <Activity size={48} className="text-muted mb-4" />
                                <h3>Olay Kaydı Yok</h3>
                                <p className="text-muted">Bu maça ait dakika dakika olay (gol, kart) bilgisi bulunmuyor.</p>
                            </div>
                        )}
                    </div>
                )}

                {/* Tab Content: Comments */}
                {activeTab === 'comments' && (
                    <div className="tab-pane animate-slide-up">
                        <div className="glass-panel p-4 mb-4">
                            <h3 className="mb-3">Maç Yorumu Ekle</h3>
                            <p className="text-muted text-sm mb-3">Bu maç hakkında düşüncelerini paylaşabilmek için giriş yapmalısın.</p>
                            <Link to="/login" className="btn btn-primary btn-sm">Giriş Yap</Link>
                        </div>

                        {comments.length > 0 ? (
                            <div className="comments-list grid gap-3">
                                {comments.map((comment, idx) => (
                                    <div key={idx} className="comment-card glass-panel p-4">
                                        <div className="flex-between mb-2">
                                            <div className="font-bold">Taraftar</div>
                                            <div className="text-xs text-muted">
                                                {comment.olusturmaTarihi ? new Date(comment.olusturmaTarihi).toLocaleDateString('tr-TR') : 'Tarih Yok'}
                                            </div>
                                        </div>
                                        <p className="text-sm">{comment.mesaj || comment.icerik}</p>
                                    </div>
                                ))}
                            </div>
                        ) : (
                            <div className="empty-state glass-panel text-center py-5">
                                <MessageSquare size={48} className="text-muted mb-4" />
                                <h3>İlk Yorumu Sen Yap</h3>
                                <p className="text-muted">Bu ateşli maç hakkında kimse konuşmamış. Hemen fikrini paylaş!</p>
                            </div>
                        )}
                    </div>
                )}
            </div>
        </div>
    );
};

export default MatchDetail;
