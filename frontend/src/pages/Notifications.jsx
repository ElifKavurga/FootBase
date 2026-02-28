import { useCallback, useEffect, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { Bell, CheckCheck, Trash2, ArrowLeft, MailOpen } from 'lucide-react';
import api from '../services/api';
import { useAuth } from '../hooks/useAuth';
import './Notifications.css';

const normalizeTargetUrl = (url) => {
    if (!url) {
        return null;
    }
    if (url.startsWith('/app/')) {
        return url.replace('/app', '');
    }
    return url;
};

const Notifications = () => {
    const navigate = useNavigate();
    const { isAuthenticated } = useAuth();
    const [notifications, setNotifications] = useState([]);
    const [unreadCount, setUnreadCount] = useState(0);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [actionLoadingId, setActionLoadingId] = useState(null);
    const [busyAll, setBusyAll] = useState(false);

    const loadNotifications = useCallback(async () => {
        const [listRes, countRes] = await Promise.all([
            api.get('/notifications').catch(() => ({ data: [] })),
            api.get('/notifications/unread/count').catch(() => ({ data: { count: 0 } }))
        ]);
        setNotifications(listRes?.data || []);
        setUnreadCount(Number(countRes?.data?.count || 0));
    }, []);

    useEffect(() => {
        if (!isAuthenticated) {
            setLoading(false);
            return;
        }

        const fetchData = async () => {
            setLoading(true);
            setError('');
            try {
                await loadNotifications();
            } catch (err) {
                setError(err?.response?.data?.hata || 'Bildirimler yuklenemedi.');
            } finally {
                setLoading(false);
            }
        };

        fetchData();
    }, [isAuthenticated, loadNotifications]);

    const handleOpenNotification = async (notification) => {
        const target = normalizeTargetUrl(notification?.hedefUrl);
        if (!notification?.okundu) {
            try {
                await api.put(`/notifications/${notification.id}/read`);
                await loadNotifications();
            } catch {
                // no-op
            }
        }

        if (target) {
            navigate(target);
        }
    };

    const handleMarkRead = async (notificationId) => {
        setActionLoadingId(notificationId);
        try {
            await api.put(`/notifications/${notificationId}/read`);
            await loadNotifications();
        } finally {
            setActionLoadingId(null);
        }
    };

    const handleDelete = async (notificationId) => {
        setActionLoadingId(notificationId);
        try {
            await api.delete(`/notifications/${notificationId}`);
            await loadNotifications();
        } finally {
            setActionLoadingId(null);
        }
    };

    const handleReadAll = async () => {
        setBusyAll(true);
        try {
            await api.put('/notifications/read-all');
            await loadNotifications();
        } finally {
            setBusyAll(false);
        }
    };

    if (!isAuthenticated) {
        return (
            <div className="notifications-page container animate-fade-in">
                <div className="glass-panel p-5 text-center">
                    <h2 className="mb-3">Bildirim Merkezi</h2>
                    <p className="text-muted mb-4">Bildirimleri gorebilmek icin giris yapmalisin.</p>
                    <Link to="/login" className="btn btn-primary">Giris Yap</Link>
                </div>
            </div>
        );
    }

    if (loading) {
        return (
            <div className="container flex-center" style={{ minHeight: '60vh' }}>
                <div className="loading-spinner"></div>
            </div>
        );
    }

    return (
        <div className="notifications-page container animate-fade-in">
            <Link to="/" className="btn btn-outline mb-4 inline-flex"><ArrowLeft size={16} /> Ana Sayfa</Link>

            <div className="notifications-header glass-panel p-4 mb-4">
                <div className="flex-between">
                    <div>
                        <h1 className="text-gradient">Bildirim Merkezi</h1>
                        <p className="text-muted mt-1">Okunmamis: {unreadCount}</p>
                    </div>
                    <button type="button" className="btn btn-outline" onClick={handleReadAll} disabled={busyAll || unreadCount === 0}>
                        <CheckCheck size={16} />
                        Tumunu Okundu Yap
                    </button>
                </div>
            </div>

            {error && <div className="error-banner mb-3">{error}</div>}

            {notifications.length === 0 ? (
                <div className="empty-state glass-panel text-center py-5">
                    <Bell size={48} className="text-muted mb-4" />
                    <h3>Bildirim Yok</h3>
                    <p className="text-muted">Henuz bildirimin bulunmuyor.</p>
                </div>
            ) : (
                <div className="notifications-list grid gap-3">
                    {notifications.map((notification) => {
                        const isBusy = actionLoadingId === notification.id;
                        return (
                            <div
                                key={notification.id}
                                className={`notification-card glass-panel p-4 ${notification.okundu ? '' : 'unread'}`}
                            >
                                <div className="flex-between mb-2">
                                    <div className="font-bold">{notification.baslik}</div>
                                    <div className="text-xs text-muted">
                                        {notification.olusturmaZamani ? new Date(notification.olusturmaZamani).toLocaleString('tr-TR') : 'Tarih yok'}
                                    </div>
                                </div>

                                <p className="text-sm mb-3">{notification.icerik}</p>

                                <div className="notification-actions">
                                    {!notification.okundu && (
                                        <button
                                            type="button"
                                            className="comment-action-btn"
                                            disabled={isBusy}
                                            onClick={() => handleMarkRead(notification.id)}
                                        >
                                            <MailOpen size={14} />
                                            Okundu
                                        </button>
                                    )}
                                    {notification.hedefUrl && (
                                        <button
                                            type="button"
                                            className="comment-action-btn"
                                            onClick={() => handleOpenNotification(notification)}
                                        >
                                            Ac
                                        </button>
                                    )}
                                    <button
                                        type="button"
                                        className="comment-action-btn danger"
                                        disabled={isBusy}
                                        onClick={() => handleDelete(notification.id)}
                                    >
                                        <Trash2 size={14} />
                                        Sil
                                    </button>
                                </div>
                            </div>
                        );
                    })}
                </div>
            )}
        </div>
    );
};

export default Notifications;
