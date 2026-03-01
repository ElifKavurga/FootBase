import { useCallback, useEffect, useMemo, useState } from 'react';
import PropTypes from 'prop-types';
import { Bell, CheckCircle2, MessageSquareWarning, PlusCircle, RefreshCcw, Shield, Trash2, UserPlus, Users, X, XCircle } from 'lucide-react';
import api from '../services/api';
import './AdminPanel.css';

const normalizeStatus = (value) => (value || '')
    .toString()
    .toUpperCase()
    .replaceAll('Ä°', 'I')
    .replaceAll('IÌ‡', 'I');

const initialTeamForm = {
    ad: '',
    logo: '',
    kurulusYili: '',
    aciklama: ''
};

const initialPlayerForm = {
    ad: '',
    soyad: '',
    milliyet: '',
    pozisyon: '',
    fotograf: '',
    takimId: ''
};

const AdminPanel = () => {
    const [activeTab, setActiveTab] = useState('overview');
    const [pendingMatches, setPendingMatches] = useState([]);
    const [notifications, setNotifications] = useState([]);
    const [unreadCount, setUnreadCount] = useState(0);
    const [teams, setTeams] = useState([]);
    const [players, setPlayers] = useState([]);
    const [moderationComments, setModerationComments] = useState([]);
    const [loading, setLoading] = useState(true);
    const [busyMatchId, setBusyMatchId] = useState(null);
    const [busyCommentId, setBusyCommentId] = useState(null);
    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');
    const [showTeamModal, setShowTeamModal] = useState(false);
    const [showPlayerModal, setShowPlayerModal] = useState(false);
    const [teamSubmitting, setTeamSubmitting] = useState(false);
    const [playerSubmitting, setPlayerSubmitting] = useState(false);
    const [teamForm, setTeamForm] = useState(initialTeamForm);
    const [playerForm, setPlayerForm] = useState(initialPlayerForm);

    const loadPanelData = useCallback(async () => {
        const [pendingRes, notificationRes, unreadRes, teamsRes, playersRes, moderationRes] = await Promise.all([
            api.get('/admin/matches/pending').catch(() => ({ data: [] })),
            api.get('/notifications/recent?limit=8').catch(() => ({ data: [] })),
            api.get('/notifications/unread/count').catch(() => ({ data: { count: 0 } })),
            api.get('/teams').catch(() => ({ data: [] })),
            api.get('/players').catch(() => ({ data: [] })),
            api.get('/admin/comments/moderation').catch(() => ({ data: [] }))
        ]);

        setPendingMatches(Array.isArray(pendingRes?.data) ? pendingRes.data : []);
        setNotifications(Array.isArray(notificationRes?.data) ? notificationRes.data : []);
        setUnreadCount(Number(unreadRes?.data?.count || 0));
        setTeams(Array.isArray(teamsRes?.data) ? teamsRes.data : []);
        setPlayers(Array.isArray(playersRes?.data) ? playersRes.data : []);
        setModerationComments(Array.isArray(moderationRes?.data) ? moderationRes.data : []);
    }, []);

    useEffect(() => {
        const fetchData = async () => {
            setLoading(true);
            setError('');
            try {
                await loadPanelData();
            } catch (err) {
                setError(err?.response?.data?.hata || 'Admin paneli yuklenemedi.');
            } finally {
                setLoading(false);
            }
        };

        fetchData();
    }, [loadPanelData]);

    const handleDecision = async (matchId, action) => {
        setBusyMatchId(matchId);
        setError('');
        setSuccess('');
        try {
            await api.post(`/admin/matches/${matchId}/${action}`);
            setSuccess(action === 'approve' ? 'Mac onaylandi.' : 'Mac reddedildi.');
            await loadPanelData();
        } catch (err) {
            setError(err?.response?.data?.hata || 'Islem tamamlanamadi.');
        } finally {
            setBusyMatchId(null);
        }
    };

    const handleModerationAction = async (commentId, action) => {
        setBusyCommentId(commentId);
        setError('');
        setSuccess('');
        try {
            if (action === 'approve') {
                await api.post(`/admin/comments/${commentId}/approve`);
                setSuccess('Yorum onaylandi.');
            } else {
                await api.delete(`/admin/comments/${commentId}`);
                setSuccess('Yorum silindi.');
            }
            await loadPanelData();
        } catch (err) {
            setError(err?.response?.data?.hata || 'Yorum moderasyon islemi tamamlanamadi.');
        } finally {
            setBusyCommentId(null);
        }
    };

    const handleCreateTeam = async (event) => {
        event.preventDefault();
        setError('');
        setSuccess('');
        if (!teamForm.ad.trim()) {
            setError('Takim adi zorunlu.');
            return;
        }

        setTeamSubmitting(true);
        try {
            await api.post('/admin/teams', {
                ad: teamForm.ad.trim(),
                logo: teamForm.logo.trim() || null,
                kurulusYili: teamForm.kurulusYili ? Number(teamForm.kurulusYili) : null,
                aciklama: teamForm.aciklama.trim() || null
            });
            setShowTeamModal(false);
            setTeamForm(initialTeamForm);
            setSuccess('Takim basariyla eklendi.');
            await loadPanelData();
        } catch (err) {
            setError(err?.response?.data?.hata || 'Takim eklenemedi.');
        } finally {
            setTeamSubmitting(false);
        }
    };

    const handleCreatePlayer = async (event) => {
        event.preventDefault();
        setError('');
        setSuccess('');

        if (!playerForm.ad.trim() || !playerForm.soyad.trim() || !playerForm.milliyet.trim() || !playerForm.pozisyon.trim()) {
            setError('Ad, soyad, uyruk ve pozisyon zorunlu.');
            return;
        }

        setPlayerSubmitting(true);
        try {
            await api.post('/admin/players', {
                ad: playerForm.ad.trim(),
                soyad: playerForm.soyad.trim(),
                milliyet: playerForm.milliyet.trim(),
                pozisyon: playerForm.pozisyon.trim(),
                fotograf: playerForm.fotograf.trim() || null,
                takim: playerForm.takimId ? { id: Number(playerForm.takimId) } : null
            });
            setShowPlayerModal(false);
            setPlayerForm(initialPlayerForm);
            setSuccess('Oyuncu profili olusturuldu.');
            await loadPanelData();
        } catch (err) {
            setError(err?.response?.data?.hata || 'Oyuncu eklenemedi.');
        } finally {
            setPlayerSubmitting(false);
        }
    };

    const pendingCount = useMemo(() => pendingMatches.length, [pendingMatches]);

    if (loading) {
        return (
            <div className="container flex-center" style={{ minHeight: '60vh' }}>
                <div className="loading-spinner"></div>
            </div>
        );
    }

    return (
        <div className="admin-panel-page container animate-fade-in">
            <div className="admin-panel-header glass-panel">
                <div>
                    <h1 className="text-gradient">Admin Paneli</h1>
                    <p className="text-muted">Mac, takim ve oyuncu yonetimini tek yerden yap.</p>
                </div>
                <button type="button" className="btn btn-outline" onClick={loadPanelData}>
                    <RefreshCcw size={16} />
                    Yenile
                </button>
            </div>

            <div className="admin-tabs glass-panel mb-3">
                <button type="button" className={`admin-tab-btn ${activeTab === 'overview' ? 'active' : ''}`} onClick={() => setActiveTab('overview')}>
                    <Shield size={16} /> Mac Onay ve Bildirim
                </button>
                <button type="button" className={`admin-tab-btn ${activeTab === 'teams' ? 'active' : ''}`} onClick={() => setActiveTab('teams')}>
                    <Users size={16} /> Takim Yonetimi
                </button>
                <button type="button" className={`admin-tab-btn ${activeTab === 'players' ? 'active' : ''}`} onClick={() => setActiveTab('players')}>
                    <UserPlus size={16} /> Oyuncu Yonetimi
                </button>
                <button type="button" className={`admin-tab-btn ${activeTab === 'moderation' ? 'active' : ''}`} onClick={() => setActiveTab('moderation')}>
                    <MessageSquareWarning size={16} /> Yorum Moderasyonu
                </button>
            </div>

            {error && <div className="error-banner mb-3">{error}</div>}
            {success && <div className="success-banner mb-3">{success}</div>}

            {activeTab === 'overview' && (
                <div className="admin-grid">
                    <section className="glass-panel admin-section">
                        <div className="admin-section-title">
                            <h2>Onay Bekleyen Maclar</h2>
                            <span className="badge badge-warning">{pendingCount}</span>
                        </div>

                        {pendingCount === 0 ? (
                            <div className="admin-empty">Onay bekleyen mac yok.</div>
                        ) : (
                            <div className="admin-pending-list">
                                {pendingMatches.map((match) => {
                                    const status = normalizeStatus(match?.onayDurumu || match?.durum || 'ONAY_BEKLIYOR');
                                    return (
                                        <article key={match.id} className="admin-pending-item">
                                            <div>
                                                <div className="admin-match-title">
                                                    {match.evSahibiTakim?.ad || 'Ev Sahibi'} vs {match.deplasmanTakim?.ad || 'Deplasman'}
                                                </div>
                                                <div className="text-muted text-sm">{match.tarih || '-'} {match.saat || ''}</div>
                                                <div className="text-muted text-xs">Durum: {status}</div>
                                            </div>
                                            <div className="admin-actions">
                                                <button
                                                    type="button"
                                                    className="btn btn-primary"
                                                    disabled={busyMatchId === match.id}
                                                    onClick={() => handleDecision(match.id, 'approve')}
                                                >
                                                    <CheckCircle2 size={16} />
                                                    Onayla
                                                </button>
                                                <button
                                                    type="button"
                                                    className="btn btn-outline admin-reject-btn"
                                                    disabled={busyMatchId === match.id}
                                                    onClick={() => handleDecision(match.id, 'reject')}
                                                >
                                                    <XCircle size={16} />
                                                    Reddet
                                                </button>
                                            </div>
                                        </article>
                                    );
                                })}
                            </div>
                        )}
                    </section>

                    <section className="glass-panel admin-section">
                        <div className="admin-section-title">
                            <h2>Son Bildirimler</h2>
                            <span className="badge badge-success">Okunmamis: {unreadCount}</span>
                        </div>

                        {notifications.length === 0 ? (
                            <div className="admin-empty">Bildirim bulunmuyor.</div>
                        ) : (
                            <div className="admin-notification-list">
                                {notifications.map((notification) => (
                                    <article key={notification.id} className={`admin-notification ${notification.okundu ? '' : 'unread'}`}>
                                        <div className="admin-notification-header">
                                            <div className="admin-notification-title">
                                                <Bell size={14} />
                                                {notification.baslik || 'Bildirim'}
                                            </div>
                                            <span className="text-xs text-muted">
                                                {notification.olusturmaZamani ? new Date(notification.olusturmaZamani).toLocaleString('tr-TR') : '-'}
                                            </span>
                                        </div>
                                        <p>{notification.icerik || '-'}</p>
                                    </article>
                                ))}
                            </div>
                        )}
                    </section>
                </div>
            )}

            {activeTab === 'teams' && (
                <section className="glass-panel admin-section">
                    <div className="admin-section-title">
                        <h2>Takim Yonetimi</h2>
                        <button type="button" className="btn btn-primary btn-sm" onClick={() => setShowTeamModal(true)}>
                            <PlusCircle size={14} />
                            Yeni Takim
                        </button>
                    </div>

                    {teams.length === 0 ? (
                        <div className="admin-empty">Kayitli takim bulunmuyor.</div>
                    ) : (
                        <div className="admin-entity-list">
                            {teams.map((team) => (
                                <article key={team.id} className="admin-entity-item">
                                    <div>
                                        <div className="admin-match-title">{team.ad || 'Adsiz Takim'}</div>
                                        <div className="text-muted text-sm">Kurulus: {team.kurulusYili || '-'}</div>
                                        <div className="text-muted text-xs">Lig: {team.lig?.ligAdi || '-'}</div>
                                    </div>
                                    <span className="badge badge-warning">ID: {team.id}</span>
                                </article>
                            ))}
                        </div>
                    )}
                </section>
            )}

            {activeTab === 'players' && (
                <section className="glass-panel admin-section">
                    <div className="admin-section-title">
                        <h2>Oyuncu Yonetimi</h2>
                        <button type="button" className="btn btn-primary btn-sm" onClick={() => setShowPlayerModal(true)}>
                            <PlusCircle size={14} />
                            Yeni Oyuncu
                        </button>
                    </div>

                    {players.length === 0 ? (
                        <div className="admin-empty">Kayitli oyuncu bulunmuyor.</div>
                    ) : (
                        <div className="admin-entity-list">
                            {players.map((player) => (
                                <article key={player.id} className="admin-entity-item">
                                    <div>
                                        <div className="admin-match-title">{player.ad} {player.soyad}</div>
                                        <div className="text-muted text-sm">{player.pozisyon || '-'} | {player.milliyet || '-'}</div>
                                        <div className="text-muted text-xs">Takim: {player.takim?.ad || 'Serbest Oyuncu'}</div>
                                    </div>
                                    <span className="badge badge-success">ID: {player.id}</span>
                                </article>
                            ))}
                        </div>
                    )}
                </section>
            )}

            {activeTab === 'moderation' && (
                <section className="glass-panel admin-section">
                    <div className="admin-section-title">
                        <h2>Yorum Moderasyonu</h2>
                        <span className="badge badge-warning">{moderationComments.length}</span>
                    </div>

                    {moderationComments.length === 0 ? (
                        <div className="admin-empty">Moderasyon bekleyen yorum bulunmuyor.</div>
                    ) : (
                        <div className="admin-entity-list">
                            {moderationComments.map((comment) => {
                                const commentStatus = normalizeStatus(comment?.yorumTipi || 'ONAY_BEKLIYOR');
                                return (
                                    <article key={comment.id} className="admin-entity-item">
                                        <div className="admin-comment-content">
                                            <div className="admin-match-title">{comment?.kullanici?.kullaniciAdi || 'Kullanici'}</div>
                                            <div className="text-muted text-sm mb-1">
                                                {comment?.mac?.id ? `Mac #${comment.mac.id}` : 'Mac bilgisi yok'}
                                            </div>
                                            <p className="admin-comment-text">{comment?.mesaj || '-'}</p>
                                            <div className="text-muted text-xs">
                                                Durum: {commentStatus} | Tarih: {comment?.yorumTarihi ? new Date(comment.yorumTarihi).toLocaleString('tr-TR') : '-'}
                                            </div>
                                        </div>
                                        <div className="admin-actions">
                                            <button
                                                type="button"
                                                className="btn btn-primary btn-sm"
                                                disabled={busyCommentId === comment.id}
                                                onClick={() => handleModerationAction(comment.id, 'approve')}
                                            >
                                                <CheckCircle2 size={14} />
                                                Onayla
                                            </button>
                                            <button
                                                type="button"
                                                className="btn btn-outline admin-reject-btn btn-sm"
                                                disabled={busyCommentId === comment.id}
                                                onClick={() => handleModerationAction(comment.id, 'delete')}
                                            >
                                                <Trash2 size={14} />
                                                Sil
                                            </button>
                                        </div>
                                    </article>
                                );
                            })}
                        </div>
                    )}
                </section>
            )}

            {showTeamModal && (
                <Modal title="Yeni Takim Ekle" onClose={() => setShowTeamModal(false)}>
                    <form className="admin-modal-form" onSubmit={handleCreateTeam}>
                        <label>
                            Takim Adi
                            <input
                                value={teamForm.ad}
                                onChange={(e) => setTeamForm((prev) => ({ ...prev, ad: e.target.value }))}
                                required
                            />
                        </label>
                        <label>
                            Logo URL
                            <input
                                value={teamForm.logo}
                                onChange={(e) => setTeamForm((prev) => ({ ...prev, logo: e.target.value }))}
                            />
                        </label>
                        <label>
                            Kurulus Yili
                            <input
                                type="number"
                                min={1800}
                                max={2100}
                                value={teamForm.kurulusYili}
                                onChange={(e) => setTeamForm((prev) => ({ ...prev, kurulusYili: e.target.value }))}
                            />
                        </label>
                        <label>
                            Aciklama
                            <textarea
                                rows={3}
                                value={teamForm.aciklama}
                                onChange={(e) => setTeamForm((prev) => ({ ...prev, aciklama: e.target.value }))}
                            />
                        </label>
                        <button type="submit" className="btn btn-primary" disabled={teamSubmitting}>
                            {teamSubmitting ? 'Kaydediliyor...' : 'Takimi Kaydet'}
                        </button>
                    </form>
                </Modal>
            )}

            {showPlayerModal && (
                <Modal title="Yeni Oyuncu Profili" onClose={() => setShowPlayerModal(false)}>
                    <form className="admin-modal-form" onSubmit={handleCreatePlayer}>
                        <div className="admin-modal-grid">
                            <label>
                                Ad
                                <input
                                    value={playerForm.ad}
                                    onChange={(e) => setPlayerForm((prev) => ({ ...prev, ad: e.target.value }))}
                                    required
                                />
                            </label>
                            <label>
                                Soyad
                                <input
                                    value={playerForm.soyad}
                                    onChange={(e) => setPlayerForm((prev) => ({ ...prev, soyad: e.target.value }))}
                                    required
                                />
                            </label>
                        </div>
                        <div className="admin-modal-grid">
                            <label>
                                Uyruk
                                <input
                                    value={playerForm.milliyet}
                                    onChange={(e) => setPlayerForm((prev) => ({ ...prev, milliyet: e.target.value }))}
                                    required
                                />
                            </label>
                            <label>
                                Pozisyon
                                <input
                                    value={playerForm.pozisyon}
                                    onChange={(e) => setPlayerForm((prev) => ({ ...prev, pozisyon: e.target.value }))}
                                    required
                                />
                            </label>
                        </div>
                        <label>
                            Fotograf URL
                            <input
                                value={playerForm.fotograf}
                                onChange={(e) => setPlayerForm((prev) => ({ ...prev, fotograf: e.target.value }))}
                            />
                        </label>
                        <label>
                            Takim (Opsiyonel)
                            <select
                                value={playerForm.takimId}
                                onChange={(e) => setPlayerForm((prev) => ({ ...prev, takimId: e.target.value }))}
                            >
                                <option value="">Takim sec</option>
                                {teams.map((team) => (
                                    <option key={team.id} value={team.id}>{team.ad}</option>
                                ))}
                            </select>
                        </label>
                        <button type="submit" className="btn btn-primary" disabled={playerSubmitting}>
                            {playerSubmitting ? 'Kaydediliyor...' : 'Oyuncuyu Kaydet'}
                        </button>
                    </form>
                </Modal>
            )}
        </div>
    );
};

const Modal = ({ title, onClose, children }) => (
    <div className="admin-modal-overlay" onClick={onClose}>
        <div className="admin-modal glass-panel" onClick={(e) => e.stopPropagation()}>
            <div className="admin-modal-header">
                <h3>{title}</h3>
                <button type="button" className="admin-modal-close" onClick={onClose}>
                    <X size={16} />
                </button>
            </div>
            {children}
        </div>
    </div>
);

Modal.propTypes = {
    title: PropTypes.string.isRequired,
    onClose: PropTypes.func.isRequired,
    children: PropTypes.node.isRequired
};

export default AdminPanel;
