import { useState, useEffect, useCallback } from 'react';
import { useParams, Link } from 'react-router-dom';
import { ArrowLeft, User, MapPin, Trophy, Activity, MessageSquare, Pencil, Trash2, Image as ImageIcon, PlusCircle } from 'lucide-react';
import api from '../services/api';
import { useAuth } from '../hooks/useAuth';
import MediaUploadModal from '../components/media/MediaUploadModal';
import { addPlayerMedia } from '../services/mediaService';
import './PlayerDetail.css';

const normalizeRating = (item) => ({
    id: item?.id,
    comment: item?.comment || item?.icerik || '',
    score: typeof item?.score === 'number' ? item.score : null,
    author: item?.author || 'Kullanici',
    userId: item?.kullaniciId || null,
    createdAt: item?.olusturmaTarihi || null
});

const normalizeMedia = (item) => ({
    id: item?.id,
    url: item?.url || '',
    tip: (item?.tip || '').toUpperCase(),
    aciklama: item?.aciklama || ''
});

const normalizeRole = (role) => (role || '').toString().trim().toUpperCase();

const isImageMedia = (tip = '', url = '') => {
    const t = tip.toUpperCase();
    if (t.includes('IMAGE') || t.includes('FOTO')) {
        return true;
    }
    return /\.(jpg|jpeg|png|webp|gif)$/i.test(url);
};

const PlayerDetail = () => {
    const { id } = useParams();
    const { user, isAuthenticated } = useAuth();
    const [player, setPlayer] = useState(null);
    const [stats, setStats] = useState(null);
    const [ratings, setRatings] = useState([]);
    const [playerMedia, setPlayerMedia] = useState([]);
    const [scoreInfo, setScoreInfo] = useState({ score: 0, ratingCount: 0 });
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [activeTab, setActiveTab] = useState('overview');

    const [newComment, setNewComment] = useState('');
    const [newScore, setNewScore] = useState('');
    const [ratingError, setRatingError] = useState('');
    const [ratingSuccess, setRatingSuccess] = useState('');
    const [ratingSubmitting, setRatingSubmitting] = useState(false);
    const [actionLoadingId, setActionLoadingId] = useState(null);
    const [editingRatingId, setEditingRatingId] = useState(null);
    const [editingComment, setEditingComment] = useState('');
    const [editingScore, setEditingScore] = useState('');
    const [showMediaModal, setShowMediaModal] = useState(false);
    const [mediaSubmitting, setMediaSubmitting] = useState(false);
    const [mediaError, setMediaError] = useState('');
    const [mediaSuccess, setMediaSuccess] = useState('');

    const loadRatings = useCallback(async () => {
        const ratingsRes = await api.get(`/players/${id}/ratings`).catch(() => ({ data: [] }));
        setRatings((ratingsRes.data || []).map(normalizeRating));
    }, [id]);

    const loadScore = useCallback(async () => {
        const scoreRes = await api.get(`/players/${id}/score`).catch(() => ({ data: { score: 0, ratingCount: 0 } }));
        setScoreInfo({
            score: Number(scoreRes?.data?.score || 0),
            ratingCount: Number(scoreRes?.data?.ratingCount || 0)
        });
    }, [id]);

    const loadMedia = useCallback(async () => {
        const mediaRes = await api.get(`/players/${id}/media`).catch(() => ({ data: [] }));
        setPlayerMedia((mediaRes.data || []).map(normalizeMedia));
    }, [id]);

    useEffect(() => {
        const fetchPlayerDetails = async () => {
            setLoading(true);
            try {
                const [playerRes, statsRes] = await Promise.all([
                    api.get(`/players/${id}`),
                    api.get(`/players/${id}/statistics`).catch(() => ({ data: null }))
                ]);

                setPlayer(playerRes.data);
                setStats(statsRes.data);
                await Promise.all([loadRatings(), loadScore(), loadMedia()]);
            } catch (err) {
                console.error('Oyuncu detaylari cekilirken hata:', err);
                setError('Oyuncu bilgileri yuklenirken bir sorun olustu.');
            } finally {
                setLoading(false);
            }
        };

        fetchPlayerDetails();
    }, [id, loadRatings, loadScore, loadMedia]);

    const parseScoreInput = (value) => {
        if (value === '' || value == null) {
            return null;
        }
        const parsed = Number(value);
        return Number.isInteger(parsed) ? parsed : null;
    };

    const refreshRatingData = async () => {
        await Promise.all([loadRatings(), loadScore()]);
    };

    const handleAddRating = async (e) => {
        e.preventDefault();
        setRatingError('');
        setRatingSuccess('');

        const score = parseScoreInput(newScore);
        const comment = newComment.trim();
        if (!comment && score == null) {
            setRatingError('Yorum veya puan girmelisiniz.');
            return;
        }

        setRatingSubmitting(true);
        try {
            await api.post(`/players/${id}/ratings`, { score, comment });
            setNewComment('');
            setNewScore('');
            setRatingSuccess('Degerlendirme eklendi.');
            await refreshRatingData();
        } catch (err) {
            setRatingError(err?.response?.data?.hata || 'Degerlendirme eklenemedi.');
        } finally {
            setRatingSubmitting(false);
        }
    };

    const handleEditStart = (rating) => {
        setEditingRatingId(rating.id);
        setEditingComment(rating.comment || '');
        setEditingScore(rating.score == null ? '' : String(rating.score));
        setRatingError('');
        setRatingSuccess('');
    };

    const handleEditCancel = () => {
        setEditingRatingId(null);
        setEditingComment('');
        setEditingScore('');
    };

    const handleEditSubmit = async (ratingId) => {
        setRatingError('');
        setRatingSuccess('');

        const score = parseScoreInput(editingScore);
        const comment = editingComment.trim();
        if (!comment && score == null) {
            setRatingError('Yorum veya puan girmelisiniz.');
            return;
        }

        setActionLoadingId(ratingId);
        try {
            await api.put(`/players/${id}/ratings/${ratingId}`, { score, comment });
            setRatingSuccess('Degerlendirme guncellendi.');
            handleEditCancel();
            await refreshRatingData();
        } catch (err) {
            setRatingError(err?.response?.data?.hata || 'Degerlendirme guncellenemedi.');
        } finally {
            setActionLoadingId(null);
        }
    };

    const handleDeleteRating = async (ratingId) => {
        setRatingError('');
        setRatingSuccess('');
        setActionLoadingId(ratingId);
        try {
            await api.delete(`/players/${id}/ratings/${ratingId}`);
            setRatingSuccess('Degerlendirme silindi.');
            await refreshRatingData();
        } catch (err) {
            setRatingError(err?.response?.data?.hata || 'Degerlendirme silinemedi.');
        } finally {
            setActionLoadingId(null);
        }
    };

    const handlePlayerMediaSubmit = async ({ tip, url, file, aciklama }) => {
        setMediaSubmitting(true);
        setMediaError('');
        setMediaSuccess('');
        try {
            await addPlayerMedia({
                playerId: id,
                tip,
                url,
                file,
                aciklama,
                userRole: user?.rol
            });
            setShowMediaModal(false);
            setMediaSuccess('Oyuncu medyasi eklendi.');
            await loadMedia();
        } catch (err) {
            setMediaError(err?.response?.data?.hata || err?.response?.data?.mesaj || 'Oyuncu medyasi eklenemedi.');
        } finally {
            setMediaSubmitting(false);
        }
    };

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
                <Link to="/players" className="btn btn-outline mb-4 inline-flex"><ArrowLeft size={16} /> Geri Don</Link>
                <div className="error-banner glass-panel text-center">
                    <p>{error || 'Oyuncu bulunamadi.'}</p>
                </div>
            </div>
        );
    }

    const isPrivileged = isAuthenticated && ['ADMIN', 'EDITOR'].includes(normalizeRole(user?.rol));

    return (
        <div className="player-detail-page container animate-fade-in">
            <Link to="/players" className="back-link mb-4 inline-flex"><ArrowLeft size={16} /> Oyunculara Don</Link>

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
                            <Trophy size={16} /> Takim: {player.takim.ad}
                        </Link>
                    )}
                </div>
            </div>

            <div className="tabs-container mt-5">
                <div className="tabs-header flex-center gap-2 mb-4 glass-panel py-2 px-2">
                    <button
                        className={`tab-btn ${activeTab === 'overview' ? 'active' : ''}`}
                        onClick={() => setActiveTab('overview')}
                    >
                        <Activity size={18} /> Istatistikler
                    </button>
                    <button
                        className={`tab-btn ${activeTab === 'media' ? 'active' : ''}`}
                        onClick={() => setActiveTab('media')}
                    >
                        <ImageIcon size={18} /> Medya ({playerMedia.length})
                    </button>
                    <button
                        className={`tab-btn ${activeTab === 'ratings' ? 'active' : ''}`}
                        onClick={() => setActiveTab('ratings')}
                    >
                        <MessageSquare size={18} /> Degerlendirmeler ({scoreInfo.ratingCount})
                    </button>
                </div>

                {activeTab === 'overview' && (
                    <div className="tab-pane animate-slide-up">
                        <div className="grid-3 gap-4">
                            <div className="stat-box glass-panel text-center p-4">
                                <h3 className="text-muted text-sm mb-2">Toplam Gol</h3>
                                <div className="text-3xl font-bold text-gradient">{stats?.toplam_gol || 0}</div>
                            </div>
                            <div className="stat-box glass-panel text-center p-4">
                                <h3 className="text-muted text-sm mb-2">Sari Kart</h3>
                                <div className="text-3xl font-bold" style={{ color: '#fbbf24' }}>{stats?.toplam_sari_kart || 0}</div>
                            </div>
                            <div className="stat-box glass-panel text-center p-4">
                                <h3 className="text-muted text-sm mb-2">Kirmizi Kart</h3>
                                <div className="text-3xl font-bold" style={{ color: '#ef4444' }}>{stats?.toplam_kirmizi_kart || 0}</div>
                            </div>

                            <div className="glass-panel p-4 col-span-full">
                                <h3 className="section-title mb-4">Kisisel Bilgiler</h3>
                                <div className="grid-2 gap-4">
                                    <div className="flex-between border-bottom py-2">
                                        <span className="text-muted">Dogum Tarihi / Yas</span>
                                        <span className="font-bold">{player.dogumTarihi || 'Bilinmiyor'} {player.yas ? `(${player.yas})` : ''}</span>
                                    </div>
                                    <div className="flex-between border-bottom py-2">
                                        <span className="text-muted">Guncel Takim</span>
                                        <span className="font-bold text-primary">{player.takim?.ad || 'Serbest'}</span>
                                    </div>
                                    <div className="flex-between border-bottom py-2">
                                        <span className="text-muted">Boy / Kilo</span>
                                        <span className="font-bold">-</span>
                                    </div>
                                    <div className="flex-between border-bottom py-2">
                                        <span className="text-muted">Guncel Puan</span>
                                        <span className="font-bold text-gradient">
                                            {scoreInfo.ratingCount > 0 ? scoreInfo.score.toFixed(1) : 'Degerlendirilmedi'}
                                        </span>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                )}

                {activeTab === 'media' && (
                    <div className="tab-pane animate-slide-up">
                        {isPrivileged && (
                            <div className="media-action-row mb-3">
                                <button type="button" className="btn btn-primary btn-sm" onClick={() => setShowMediaModal(true)}>
                                    <PlusCircle size={14} />
                                    Yeni Medya Ekle
                                </button>
                            </div>
                        )}
                        {mediaError && <div className="error-banner mb-3">{mediaError}</div>}
                        {mediaSuccess && <div className="success-banner mb-3">{mediaSuccess}</div>}
                        {playerMedia.length > 0 ? (
                            <div className="media-grid">
                                {playerMedia.map((media) => (
                                    <a key={media.id} href={media.url} target="_blank" rel="noreferrer" className="media-card glass-panel">
                                        {isImageMedia(media.tip, media.url) ? (
                                            <img src={media.url} alt={media.aciklama || `${player.ad} ${player.soyad}`} />
                                        ) : (
                                            <div className="media-card-fallback">
                                                <ImageIcon size={24} />
                                                <span>Medyayi Ac</span>
                                            </div>
                                        )}
                                        <div className="media-card-meta">
                                            <span className="badge badge-warning">{media.tip || 'MEDIA'}</span>
                                        </div>
                                    </a>
                                ))}
                            </div>
                        ) : (
                            <div className="empty-state glass-panel text-center py-5">
                                <ImageIcon size={48} className="text-muted mb-4" />
                                <h3>Medya Bulunamadi</h3>
                                <p className="text-muted">Bu oyuncu icin veritabaninda medya kaydi yok.</p>
                            </div>
                        )}
                    </div>
                )}

                {activeTab === 'ratings' && (
                    <div className="tab-pane animate-slide-up">
                        {ratingError && <div className="error-banner mb-3">{ratingError}</div>}
                        {ratingSuccess && <div className="success-banner mb-3">{ratingSuccess}</div>}

                        {isAuthenticated ? (
                            <form className="glass-panel p-4 mb-4" onSubmit={handleAddRating}>
                                <h3 className="mb-3">Degerlendirme Yap</h3>
                                <div className="rating-form-grid">
                                    <input
                                        type="number"
                                        min="1"
                                        max="10"
                                        className="rating-score-input"
                                        placeholder="Puan (1-10)"
                                        value={newScore}
                                        onChange={(e) => setNewScore(e.target.value)}
                                    />
                                    <textarea
                                        className="comment-input"
                                        rows={3}
                                        placeholder="Oyuncu hakkindaki degerlendirmen..."
                                        value={newComment}
                                        onChange={(e) => setNewComment(e.target.value)}
                                    />
                                </div>
                                <button type="submit" className="btn btn-primary mt-3" disabled={ratingSubmitting}>
                                    {ratingSubmitting ? 'Gonderiliyor...' : 'Gonder'}
                                </button>
                            </form>
                        ) : (
                            <div className="glass-panel p-4 mb-4">
                                <h3 className="mb-3">Degerlendirme Yap</h3>
                                <p className="text-muted text-sm mb-3">Degerlendirme icin giris yapmalisin.</p>
                                <Link to="/login" className="btn btn-primary btn-sm">Giris Yap</Link>
                            </div>
                        )}

                        {ratings.length > 0 ? (
                            <div className="comments-list grid gap-3">
                                {ratings.map((rating) => {
                                    const isOwner = Boolean(user?.id && rating.userId && user.id === rating.userId);
                                    const isBusy = actionLoadingId === rating.id;

                                    return (
                                        <div key={rating.id} className="comment-card glass-panel p-4">
                                            <div className="flex-between mb-2">
                                                <div className="font-bold">{rating.author}</div>
                                                <div className="text-xs text-muted">
                                                    {rating.createdAt ? new Date(rating.createdAt).toLocaleDateString('tr-TR') : 'Tarih Yok'}
                                                </div>
                                            </div>

                                            {editingRatingId === rating.id ? (
                                                <div>
                                                    <input
                                                        type="number"
                                                        min="1"
                                                        max="10"
                                                        className="rating-score-input mb-2"
                                                        placeholder="Puan (1-10)"
                                                        value={editingScore}
                                                        onChange={(e) => setEditingScore(e.target.value)}
                                                    />
                                                    <textarea
                                                        className="comment-input"
                                                        rows={3}
                                                        value={editingComment}
                                                        onChange={(e) => setEditingComment(e.target.value)}
                                                    />
                                                    <div className="comment-actions-row mt-2">
                                                        <button
                                                            type="button"
                                                            className="btn btn-primary btn-sm"
                                                            disabled={isBusy}
                                                            onClick={() => handleEditSubmit(rating.id)}
                                                        >
                                                            Kaydet
                                                        </button>
                                                        <button type="button" className="btn btn-outline btn-sm" onClick={handleEditCancel}>
                                                            Iptal
                                                        </button>
                                                    </div>
                                                </div>
                                            ) : (
                                                <>
                                                    {rating.score != null && (
                                                        <div className="badge badge-success mb-2">{rating.score}/10</div>
                                                    )}
                                                    <p className="text-sm">{rating.comment}</p>
                                                </>
                                            )}

                                            {isOwner && editingRatingId !== rating.id && (
                                                <div className="comment-actions-row mt-3">
                                                    <button
                                                        type="button"
                                                        className="comment-action-btn"
                                                        disabled={isBusy}
                                                        onClick={() => handleEditStart(rating)}
                                                    >
                                                        <Pencil size={14} />
                                                        Duzenle
                                                    </button>
                                                    <button
                                                        type="button"
                                                        className="comment-action-btn danger"
                                                        disabled={isBusy}
                                                        onClick={() => handleDeleteRating(rating.id)}
                                                    >
                                                        <Trash2 size={14} />
                                                        Sil
                                                    </button>
                                                </div>
                                            )}
                                        </div>
                                    );
                                })}
                            </div>
                        ) : (
                            <div className="empty-state glass-panel text-center py-5">
                                <MessageSquare size={48} className="text-muted mb-4" />
                                <h3>Degerlendirme Yok</h3>
                                <p className="text-muted">Bu oyuncu icin henuz degerlendirme yapilmamis.</p>
                            </div>
                        )}
                    </div>
                )}
            </div>

            {showMediaModal && (
                <MediaUploadModal
                    title="Oyuncuya Yeni Medya Ekle"
                    busy={mediaSubmitting}
                    error={mediaError}
                    onClose={() => {
                        if (!mediaSubmitting) {
                            setShowMediaModal(false);
                        }
                    }}
                    onSubmit={handlePlayerMediaSubmit}
                />
            )}
        </div>
    );
};

export default PlayerDetail;
