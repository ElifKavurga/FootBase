import { useState, useEffect } from 'react';
import { useParams, Link } from 'react-router-dom';
import { ArrowLeft, User, Calendar, MapPin, Trophy, Activity, MessageSquare } from 'lucide-react';
import api from '../services/api';
import './PlayerDetail.css';

const PlayerDetail = () => {
    const { id } = useParams();
    const [player, setPlayer] = useState(null);
    const [stats, setStats] = useState(null);
    const [comments, setComments] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [activeTab, setActiveTab] = useState('overview');

    useEffect(() => {
        const fetchPlayerDetails = async () => {
            setLoading(true);
            try {
                // Fetch player details, stats, and comments from respective endpoints
                const [playerRes, statsRes, commentsRes] = await Promise.all([
                    api.get(`/players/${id}`),
                    api.get(`/players/${id}/statistics`).catch(() => ({ data: null })),
                    api.get(`/players/${id}/comments`).catch(() => ({ data: [] }))
                ]);

                setPlayer(playerRes.data);
                setStats(statsRes.data);
                setComments(commentsRes.data);
            } catch (err) {
                console.error('Oyuncu detayları çekilirken hata:', err);
                setError('Oyuncu bilgileri yüklenirken bir sorun oluştu.');
            } finally {
                setLoading(false);
            }
        };

        fetchPlayerDetails();
    }, [id]);

    if (loading) {
        return (
            <div className="container flex-center" style={{ minHeight: '60vh' }}>
                <div className="loading-spinner"></div>
            </div>
        );
    }

    if (error || !player) {
        return (
            <div className="container" style={{ marginTop: '2rem' }}>
                <Link to="/players" className="btn btn-outline mb-4 inline-flex"><ArrowLeft size={16} /> Geri Dön</Link>
                <div className="error-banner glass-panel text-center">
                    <p>{error || 'Oyuncu bulunamadı.'}</p>
                </div>
            </div>
        );
    }

    return (
        <div className="player-detail-page container animate-fade-in">
            <Link to="/players" className="back-link mb-4 inline-flex"><ArrowLeft size={16} /> Oyunculara Dön</Link>

            {/* Player Header Card */}
            <div className="player-header-card glass-panel flex-center flex-column py-5 relative overflow-hidden">
                <div className="player-bg-number text-muted opacity-50 absolute right-0 bottom-0 select-none" style={{ fontSize: '10rem', lineHeight: 1, zIndex: 0 }}>
                    {player.formaNumarasi || ''}
                </div>

                <div className="player-avatar-lg-wrapper mb-4 z-10">
                    {player.fotograf ? (
                        <img src={player.fotograf} alt={player.ad} className="player-avatar-lg" />
                    ) : (
                        <div className="player-avatar-lg-placeholder"><User size={64} /></div>
                    )}
                </div>

                <h1 className="player-name-lg text-gradient z-10">{player.ad} {player.soyad}</h1>

                <div className="player-meta-row flex-center gap-4 mt-4 flex-wrap z-10">
                    <div className="meta-pill text-muted glass-panel"><Activity size={16} /> Pozisyon: {player.pozisyon}</div>
                    <div className="meta-pill text-muted glass-panel"><MapPin size={16} /> Uyruk: {player.milliyet}</div>
                    {player.takim && (
                        <Link to={`/teams/${player.takim.id}`} className="meta-pill text-primary glass-panel hover-scale">
                            <Trophy size={16} /> Takım: {player.takim.ad}
                        </Link>
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
                        <Activity size={18} /> İstatistikler
                    </button>
                    <button
                        className={`tab-btn ${activeTab === 'comments' ? 'active' : ''}`}
                        onClick={() => setActiveTab('comments')}
                    >
                        <MessageSquare size={18} /> Yorumlar ({comments.length})
                    </button>
                </div>

                {/* Tab Content: Stats */}
                {activeTab === 'overview' && (
                    <div className="tab-pane animate-slide-up">
                        <div className="grid-3 gap-4">
                            <div className="stat-box glass-panel text-center p-4">
                                <h3 className="text-muted text-sm mb-2">Toplam Gol</h3>
                                <div className="text-3xl font-bold text-gradient">{stats?.toplam_gol || 0}</div>
                            </div>
                            <div className="stat-box glass-panel text-center p-4">
                                <h3 className="text-muted text-sm mb-2">Sarı Kart</h3>
                                <div className="text-3xl font-bold" style={{ color: '#fbbf24' }}>{stats?.toplam_sari_kart || 0}</div>
                            </div>
                            <div className="stat-box glass-panel text-center p-4">
                                <h3 className="text-muted text-sm mb-2">Kırmızı Kart</h3>
                                <div className="text-3xl font-bold" style={{ color: '#ef4444' }}>{stats?.toplam_kirmizi_kart || 0}</div>
                            </div>

                            <div className="glass-panel p-4 col-span-full">
                                <h3 className="section-title mb-4">Kişisel Bilgiler</h3>
                                <div className="grid-2 gap-4">
                                    <div className="flex-between border-bottom py-2">
                                        <span className="text-muted">Doğum Tarihi / Yaş</span>
                                        <span className="font-bold">{player.dogumTarihi || 'Bilinmiyor'} {player.yas ? `(${player.yas})` : ''}</span>
                                    </div>
                                    <div className="flex-between border-bottom py-2">
                                        <span className="text-muted">Güncel Takım</span>
                                        <span className="font-bold text-primary">{player.takim?.ad || 'Serbest'}</span>
                                    </div>
                                    <div className="flex-between border-bottom py-2">
                                        <span className="text-muted">Boy / Kilo</span>
                                        <span className="font-bold">-</span> {/* Backend doesn't have these fields yet */}
                                    </div>
                                    <div className="flex-between border-bottom py-2">
                                        <span className="text-muted">Güncel Puanı</span>
                                        <span className="font-bold text-gradient">{player.ortalamaPuan ? player.ortalamaPuan.toFixed(1) : 'Değerlendirilmedi'}</span>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                )}

                {/* Tab Content: Comments */}
                {activeTab === 'comments' && (
                    <div className="tab-pane animate-slide-up">
                        <div className="glass-panel p-4 mb-4">
                            <h3 className="mb-3">Yorum Yap</h3>
                            <p className="text-muted text-sm mb-3">Bu oyuncu hakkında yorum yapabilmek için giriş yapmış olmalısınız.</p>
                            <Link to="/login" className="btn btn-primary btn-sm">Giriş Yap</Link>
                        </div>

                        {comments.length > 0 ? (
                            <div className="comments-list grid gap-3">
                                {comments.map((comment, idx) => (
                                    <div key={idx} className="comment-card glass-panel p-4">
                                        <div className="flex-between mb-2">
                                            <div className="font-bold">Kullanıcı</div>
                                            <div className="text-xs text-muted">
                                                {comment.olusturmaTarihi ? new Date(comment.olusturmaTarihi).toLocaleDateString('tr-TR') : 'Tarih Yok'}
                                            </div>
                                        </div>
                                        <p className="text-sm">{comment.icerik}</p>
                                    </div>
                                ))}
                            </div>
                        ) : (
                            <div className="empty-state glass-panel text-center py-5">
                                <MessageSquare size={48} className="text-muted mb-4" />
                                <h3>Yorum Bulunamadı</h3>
                                <p className="text-muted">Bu oyuncu için henüz bir yorum yapılmamış. İlk yorumu sen yap!</p>
                            </div>
                        )}
                    </div>
                )}
            </div>
        </div>
    );
};

export default PlayerDetail;
