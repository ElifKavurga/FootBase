import { useCallback, useEffect, useMemo, useState } from 'react';
import { Bell, CheckCircle2, RefreshCcw, XCircle } from 'lucide-react';
import api from '../services/api';
import './AdminPanel.css';

const normalizeStatus = (value) => (value || '')
    .toString()
    .toUpperCase()
    .replaceAll('İ', 'I')
    .replaceAll('İ', 'I');

const AdminPanel = () => {
    const [pendingMatches, setPendingMatches] = useState([]);
    const [notifications, setNotifications] = useState([]);
    const [unreadCount, setUnreadCount] = useState(0);
    const [loading, setLoading] = useState(true);
    const [busyMatchId, setBusyMatchId] = useState(null);
    const [error, setError] = useState('');

    const loadPanelData = useCallback(async () => {
        const [pendingRes, notificationRes, unreadRes] = await Promise.all([
            api.get('/admin/matches/pending').catch(() => ({ data: [] })),
            api.get('/notifications/recent?limit=8').catch(() => ({ data: [] })),
            api.get('/notifications/unread/count').catch(() => ({ data: { count: 0 } }))
        ]);

        setPendingMatches(Array.isArray(pendingRes?.data) ? pendingRes.data : []);
        setNotifications(Array.isArray(notificationRes?.data) ? notificationRes.data : []);
        setUnreadCount(Number(unreadRes?.data?.count || 0));
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
        try {
            await api.post(`/admin/matches/${matchId}/${action}`);
            await loadPanelData();
        } catch (err) {
            setError(err?.response?.data?.hata || 'Islem tamamlanamadi.');
        } finally {
            setBusyMatchId(null);
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
                    <p className="text-muted">Onay bekleyen maclar ve editor bildirimleri.</p>
                </div>
                <button type="button" className="btn btn-outline" onClick={loadPanelData}>
                    <RefreshCcw size={16} />
                    Yenile
                </button>
            </div>

            {error && <div className="error-banner mb-3">{error}</div>}

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
        </div>
    );
};

export default AdminPanel;
