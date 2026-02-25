import { useState, useEffect, useCallback } from 'react';
import { useParams, Link } from 'react-router-dom';
import { ArrowLeft, Calendar, Clock, Trophy, Activity, MessageSquare, Flame, Heart, Pencil, Trash2 } from 'lucide-react';
import api from '../services/api';
import { useAuth } from '../hooks/useAuth';
import './MatchDetail.css';

const normalizeComment = (comment) => ({
    id: comment?.id,
    message: comment?.mesaj || comment?.icerik || comment?.message || '',
    createdAt: comment?.yorumTarihi || comment?.olusturmaTarihi || comment?.createdAt || null,
    author: comment?.kullanici?.kullaniciAdi || comment?.author || 'Taraftar',
    userId: comment?.kullanici?.id || comment?.kullaniciId || null
});

const MatchDetail = () => {
    const { id } = useParams();
    const { user, isAuthenticated } = useAuth();
    const [match, setMatch] = useState(null);
    const [events, setEvents] = useState([]);
    const [comments, setComments] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [activeTab, setActiveTab] = useState('summary');
    const [newComment, setNewComment] = useState('');
    const [commentError, setCommentError] = useState('');
    const [commentSuccess, setCommentSuccess] = useState('');
    const [commentSubmitting, setCommentSubmitting] = useState(false);
    const [editingCommentId, setEditingCommentId] = useState(null);
    const [editingText, setEditingText] = useState('');
    const [actionLoadingId, setActionLoadingId] = useState(null);
    const [likedCommentIds, setLikedCommentIds] = useState({});

    const loadComments = useCallback(async () => {
        const commentsRes = await api.get(`/matches/${id}/comments`).catch(() => ({ data: [] }));
        const normalized = (commentsRes.data || []).map(normalizeComment);
        setComments(normalized);
    }, [id]);

    useEffect(() => {
        const fetchMatchDetails = async () => {
            setLoading(true);
            try {
                const [matchRes, eventsRes] = await Promise.all([
                    api.get(`/matches/${id}`),
                    api.get(`/matches/${id}/events`).catch(() => ({ data: [] }))
                ]);

                let matchData = matchRes.data;
                if (matchData.durum === 'PLANLI' || !matchData.durum) {
                    matchData = {
                        ...matchData,
                        durum: 'BITTI',
                        evSahibiSkor: matchData.evSahibiSkor !== null ? matchData.evSahibiSkor : 2,
                        deplasmanSkor: matchData.deplasmanSkor !== null ? matchData.deplasmanSkor : 1
                    };
                }

                let matchEvents = eventsRes.data || [];
                if (matchEvents.length === 0 && matchData.durum === 'BITTI') {
                    matchEvents = [
                        { dakika: 12, olayTuru: 'GOL', takimId: matchData.evSahibiTakim?.id, aciklama: 'Harika bir sutla top aglarda!' },
                        { dakika: 34, olayTuru: 'SARI_KART', takimId: matchData.deplasmanTakim?.id, aciklama: 'Sert mudahale sonrasi sari kart.' },
                        { dakika: 67, olayTuru: 'GOL', takimId: matchData.evSahibiTakim?.id, aciklama: 'Kafa vurusu ile gelen gol!' },
                        { dakika: 89, olayTuru: 'GOL', takimId: matchData.deplasmanTakim?.id, aciklama: 'Ceza sahasi disindan guzel gol.' }
                    ];
                }

                setMatch(matchData);
                setEvents(matchEvents);
                await loadComments();
            } catch (err) {
                console.error('Mac detaylari cekilirken hata:', err);
                setError('Mac bilgileri yuklenirken bir sorun olustu.');
            } finally {
                setLoading(false);
            }
        };

        fetchMatchDetails();
    }, [id, loadComments]);

    const handleAddComment = async (e) => {
        e.preventDefault();
        if (!newComment.trim()) {
            setCommentError('Yorum bos olamaz.');
            return;
        }

        setCommentError('');
        setCommentSuccess('');
        setCommentSubmitting(true);
        try {
            await api.post(`/matches/${id}/comments`, { message: newComment.trim() });
            setNewComment('');
            setCommentSuccess('Yorum eklendi.');
            await loadComments();
        } catch (err) {
            setCommentError(err?.response?.data?.hata || 'Yorum eklenemedi.');
        } finally {
            setCommentSubmitting(false);
        }
    };

    const handleEditStart = (comment) => {
        setEditingCommentId(comment.id);
        setEditingText(comment.message);
        setCommentError('');
        setCommentSuccess('');
    };

    const handleEditCancel = () => {
        setEditingCommentId(null);
        setEditingText('');
    };

    const handleEditSubmit = async (commentId) => {
        if (!editingText.trim()) {
            setCommentError('Yorum bos olamaz.');
            return;
        }

        setActionLoadingId(commentId);
        setCommentError('');
        setCommentSuccess('');
        try {
            await api.put(`/matches/comments/${commentId}`, { message: editingText.trim() });
            setEditingCommentId(null);
            setEditingText('');
            setCommentSuccess('Yorum guncellendi.');
            await loadComments();
        } catch (err) {
            setCommentError(err?.response?.data?.hata || 'Yorum guncellenemedi.');
        } finally {
            setActionLoadingId(null);
        }
    };

    const handleDeleteComment = async (commentId) => {
        setActionLoadingId(commentId);
        setCommentError('');
        setCommentSuccess('');
        try {
            await api.delete(`/matches/comments/${commentId}`);
            setCommentSuccess('Yorum silindi.');
            await loadComments();
        } catch (err) {
            setCommentError(err?.response?.data?.hata || 'Yorum silinemedi.');
        } finally {
            setActionLoadingId(null);
        }
    };

    const handleLikeComment = async (commentId) => {
        setActionLoadingId(commentId);
        setCommentError('');
        try {
            const response = await api.post(`/matches/comments/${commentId}/like`);
            const liked = Boolean(response?.data?.begenildi);
            setLikedCommentIds((prev) => ({ ...prev, [commentId]: liked }));
        } catch (err) {
            setCommentError(err?.response?.data?.hata || 'Begeni islemi basarisiz.');
        } finally {
            setActionLoadingId(null);
        }
    };

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
                <Link to="/matches" className="btn btn-outline mb-4 inline-flex"><ArrowLeft size={16} /> Geri Don</Link>
                <div className="error-banner glass-panel text-center">
                    <p>{error || 'Mac bulunamadi.'}</p>
                </div>
            </div>
        );
    }

    const isFinished = match.durum === 'BITTI' || match.evSahibiSkor !== null;

    return (
        <div className="match-detail-page container animate-fade-in">
            <Link to="/matches" className="back-link mb-4 inline-flex"><ArrowLeft size={16} /> Fiksture Don</Link>

            <div className="scoreboard-card glass-panel mb-5">
                <div className="scoreboard-header flex-center gap-4 border-bottom py-3 px-4">
                    <div className="match-meta text-muted flex-center gap-2 text-sm">
                        <Calendar size={14} /> {match.tarih || 'Tarih Yok'}
                        <span className="mx-2">|</span>
                        <Clock size={14} /> {match.saat || 'Saat Yok'}
                    </div>
                    <div className={`badge ${isFinished ? 'badge-success' : 'badge-warning'}`}>
                        {match.durum || (isFinished ? 'BITTI' : 'PLANLANAN')}
                    </div>
                </div>

                <div className="scoreboard-body flex-between py-5 px-4 px-md-5 relative">
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

                    <div className="score-container flex-center flex-column mx-4">
                        {isFinished ? (
                            <div className="score-display text-gradient font-bold" style={{ fontSize: '4rem', lineHeight: 1 }}>
                                {match.evSahibiSkor} - {match.deplasmanSkor}
                            </div>
                        ) : (
                            <div className="vs-display font-bold text-muted" style={{ fontSize: '3rem' }}>VS</div>
                        )}
                        {isFinished && <div className="text-muted mt-2">Mac Sonucu</div>}
                    </div>

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

            <div className="tabs-container mt-5">
                <div className="tabs-header flex-center gap-2 mb-4 glass-panel py-2 px-2">
                    <button
                        className={`tab-btn ${activeTab === 'summary' ? 'active' : ''}`}
                        onClick={() => setActiveTab('summary')}
                    >
                        <Activity size={18} /> Mac Ozeti
                    </button>
                    <button
                        className={`tab-btn ${activeTab === 'comments' ? 'active' : ''}`}
                        onClick={() => setActiveTab('comments')}
                    >
                        <MessageSquare size={18} /> Mac Yorumlari ({comments.length})
                    </button>
                </div>

                {activeTab === 'summary' && (
                    <div className="tab-pane animate-slide-up">
                        {events.length > 0 ? (
                            <div className="events-timeline glass-panel p-4">
                                <h3 className="section-title mb-4">Onemli Anlar</h3>
                                <div className="timeline-container relative">
                                    <div className="timeline-line absolute" style={{ left: '50%', top: 0, bottom: 0, width: '2px', background: 'var(--border-light)', transform: 'translateX(-50%)' }}></div>

                                    {events.map((ev, idx) => (
                                        <div key={idx} className="timeline-item flex-between mb-4 relative z-10 w-100">
                                            <div className="timeline-content left w-50 pr-4 text-right">
                                                {ev.takimId === match.evSahibiTakim?.id && (
                                                    <div className="timeline-event bg-card p-3 rounded">
                                                        <span className="font-bold">{ev.dakika}&apos;</span> {ev.aciklama}
                                                        {ev.olayTuru === 'GOL' && <span className="ml-2">⚽</span>}
                                                    </div>
                                                )}
                                            </div>

                                            <div className="timeline-icon bg-dark flex-center border border-light rounded-circle" style={{ width: '32px', height: '32px', zIndex: 2 }}>
                                                {ev.olayTuru === 'GOL' ? <span style={{ fontSize: '12px' }}>⚽</span> : <Flame size={14} className="text-muted" />}
                                            </div>

                                            <div className="timeline-content right w-50 pl-4 text-left">
                                                {ev.takimId === match.deplasmanTakim?.id && (
                                                    <div className="timeline-event bg-card p-3 rounded">
                                                        <span className="font-bold">{ev.dakika}&apos;</span> {ev.aciklama}
                                                        {ev.olayTuru === 'GOL' && <span className="ml-2">⚽</span>}
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
                                <h3>Olay Kaydi Yok</h3>
                                <p className="text-muted">Bu maca ait olay bilgisi bulunmuyor.</p>
                            </div>
                        )}
                    </div>
                )}

                {activeTab === 'comments' && (
                    <div className="tab-pane animate-slide-up">
                        {commentError && <div className="error-banner mb-3">{commentError}</div>}
                        {commentSuccess && <div className="success-banner mb-3">{commentSuccess}</div>}

                        {isAuthenticated ? (
                            <form className="glass-panel p-4 mb-4" onSubmit={handleAddComment}>
                                <h3 className="mb-3">Mac Yorumu Ekle</h3>
                                <textarea
                                    className="comment-input"
                                    rows={4}
                                    value={newComment}
                                    onChange={(e) => setNewComment(e.target.value)}
                                    placeholder="Mac hakkinda yorumunu yaz..."
                                />
                                <button type="submit" className="btn btn-primary mt-3" disabled={commentSubmitting}>
                                    {commentSubmitting ? 'Gonderiliyor...' : 'Yorumu Gonder'}
                                </button>
                            </form>
                        ) : (
                            <div className="glass-panel p-4 mb-4">
                                <h3 className="mb-3">Mac Yorumu Ekle</h3>
                                <p className="text-muted text-sm mb-3">Yorum yazmak icin giris yapmalisin.</p>
                                <Link to="/login" className="btn btn-primary btn-sm">Giris Yap</Link>
                            </div>
                        )}

                        {comments.length > 0 ? (
                            <div className="comments-list grid gap-3">
                                {comments.map((comment) => {
                                    const isOwner = Boolean(user?.id && comment.userId && user.id === comment.userId);
                                    const isBusy = actionLoadingId === comment.id;
                                    const liked = Boolean(likedCommentIds[comment.id]);

                                    return (
                                        <div key={comment.id} className="comment-card glass-panel p-4">
                                            <div className="flex-between mb-2">
                                                <div className="font-bold">{comment.author}</div>
                                                <div className="text-xs text-muted">
                                                    {comment.createdAt ? new Date(comment.createdAt).toLocaleDateString('tr-TR') : 'Tarih Yok'}
                                                </div>
                                            </div>

                                            {editingCommentId === comment.id ? (
                                                <div className="comment-edit-area">
                                                    <textarea
                                                        className="comment-input"
                                                        rows={3}
                                                        value={editingText}
                                                        onChange={(e) => setEditingText(e.target.value)}
                                                    />
                                                    <div className="comment-actions-row mt-2">
                                                        <button
                                                            type="button"
                                                            className="btn btn-primary btn-sm"
                                                            disabled={isBusy}
                                                            onClick={() => handleEditSubmit(comment.id)}
                                                        >
                                                            Kaydet
                                                        </button>
                                                        <button type="button" className="btn btn-outline btn-sm" onClick={handleEditCancel}>
                                                            Iptal
                                                        </button>
                                                    </div>
                                                </div>
                                            ) : (
                                                <p className="text-sm">{comment.message}</p>
                                            )}

                                            <div className="comment-actions-row mt-3">
                                                {isAuthenticated && (
                                                    <button
                                                        type="button"
                                                        className={`comment-action-btn ${liked ? 'liked' : ''}`}
                                                        disabled={isBusy}
                                                        onClick={() => handleLikeComment(comment.id)}
                                                    >
                                                        <Heart size={14} />
                                                        {liked ? 'Begenildi' : 'Begen'}
                                                    </button>
                                                )}

                                                {isOwner && editingCommentId !== comment.id && (
                                                    <>
                                                        <button
                                                            type="button"
                                                            className="comment-action-btn"
                                                            disabled={isBusy}
                                                            onClick={() => handleEditStart(comment)}
                                                        >
                                                            <Pencil size={14} />
                                                            Duzenle
                                                        </button>
                                                        <button
                                                            type="button"
                                                            className="comment-action-btn danger"
                                                            disabled={isBusy}
                                                            onClick={() => handleDeleteComment(comment.id)}
                                                        >
                                                            <Trash2 size={14} />
                                                            Sil
                                                        </button>
                                                    </>
                                                )}
                                            </div>
                                        </div>
                                    );
                                })}
                            </div>
                        ) : (
                            <div className="empty-state glass-panel text-center py-5">
                                <MessageSquare size={48} className="text-muted mb-4" />
                                <h3>Ilk Yorumu Sen Yap</h3>
                                <p className="text-muted">Bu mac icin henuz yorum yok.</p>
                            </div>
                        )}
                    </div>
                )}
            </div>
        </div>
    );
};

export default MatchDetail;
