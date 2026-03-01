import PropTypes from 'prop-types';
import { useEffect, useMemo, useState } from 'react';
import { PlusCircle, ShieldCheck, X } from 'lucide-react';
import api from '../../services/api';
import { useAuth } from '../../hooks/useAuth';

const EVENT_TYPES = [
    { value: 'GOL', label: 'Gol' },
    { value: 'SARI_KART', label: 'Sari Kart' },
    { value: 'KIRMIZI_KART', label: 'Kirmizi Kart' },
    { value: 'ASIST', label: 'Asist' },
    { value: 'PENALTI', label: 'Penalti' }
];

const normalizeRole = (value) => (value || '').toString().trim().toUpperCase();

const MatchManagementPanel = ({ matchId, match, onChanged }) => {
    const { user, isAuthenticated } = useAuth();
    const role = normalizeRole(user?.rol);
    const isAuthorized = isAuthenticated && (role === 'ADMIN' || role === 'EDITOR');
    const isFinished = (match?.durum || '').toUpperCase() === 'BITTI';
    const canManage = isAuthorized && !isFinished;

    const [players, setPlayers] = useState([]);
    const [busy, setBusy] = useState(false);
    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');
    const [showScore, setShowScore] = useState(false);
    const [showEvent, setShowEvent] = useState(false);
    const [showFinish, setShowFinish] = useState(false);
    const [scoreForm, setScoreForm] = useState({ evSahibiSkor: '0', deplasmanSkor: '0', aciklama: '' });
    const [eventForm, setEventForm] = useState({ oyuncuId: '', dakika: '', olayTuru: 'GOL' });
    const [finishForm, setFinishForm] = useState({ evSahibiSkor: '0', deplasmanSkor: '0', aciklama: '' });

    useEffect(() => {
        const home = String(match?.evSahibiSkor ?? 0);
        const away = String(match?.deplasmanSkor ?? 0);
        setScoreForm({ evSahibiSkor: home, deplasmanSkor: away, aciklama: '' });
        setFinishForm({ evSahibiSkor: home, deplasmanSkor: away, aciklama: '' });
    }, [match?.evSahibiSkor, match?.deplasmanSkor]);

    useEffect(() => {
        const loadPlayers = async () => {
            if (!isAuthorized) {
                setPlayers([]);
                return;
            }
            const response = await api.get('/players').catch(() => ({ data: [] }));
            setPlayers(Array.isArray(response?.data) ? response.data : []);
        };
        loadPlayers();
    }, [isAuthorized]);

    const matchPlayers = useMemo(() => {
        const teamIds = [match?.evSahibiTakim?.id, match?.deplasmanTakim?.id].filter(Boolean);
        if (teamIds.length === 0) {
            return players;
        }
        return players.filter((p) => teamIds.includes(p?.takim?.id));
    }, [players, match?.evSahibiTakim?.id, match?.deplasmanTakim?.id]);

    if (!isAuthorized) {
        return null;
    }

    const closeAll = () => {
        setShowScore(false);
        setShowEvent(false);
        setShowFinish(false);
    };

    const submitScore = async (e) => {
        e.preventDefault();
        const home = Number(scoreForm.evSahibiSkor);
        const away = Number(scoreForm.deplasmanSkor);
        if (!Number.isInteger(home) || !Number.isInteger(away) || home < 0 || away < 0) {
            setError('Skorlar sifir veya pozitif tam sayi olmali.');
            return;
        }
        setBusy(true); setError(''); setSuccess('');
        try {
            const endpoint = role === 'ADMIN' ? '/admin/matches/score' : '/editor/matches/score-command';
            await api.post(endpoint, { macId: Number(matchId), evSahibiSkor: home, deplasmanSkor: away, aciklama: scoreForm.aciklama || undefined });
            closeAll();
            setSuccess('Skor guncellendi.');
            await onChanged();
        } catch (err) {
            setError(err?.response?.data?.hata || err?.response?.data?.mesaj || 'Skor guncellenemedi.');
        } finally {
            setBusy(false);
        }
    };

    const submitEvent = async (e) => {
        e.preventDefault();
        const dakika = Number(eventForm.dakika);
        const oyuncuId = Number(eventForm.oyuncuId);
        if (!Number.isInteger(dakika) || dakika < 1 || dakika > 130) return setError('Dakika 1-130 arasinda olmali.');
        if (!Number.isInteger(oyuncuId) || oyuncuId <= 0) return setError('Oyuncu secimi zorunlu.');
        setBusy(true); setError(''); setSuccess('');
        try {
            await api.post(`/editor/matches/${matchId}/events`, { oyuncuId, olayTuru: eventForm.olayTuru, dakika });
            closeAll();
            setEventForm({ oyuncuId: '', dakika: '', olayTuru: 'GOL' });
            setSuccess('Mac olayi eklendi.');
            await onChanged();
        } catch (err) {
            setError(err?.response?.data?.hata || 'Mac olayi eklenemedi.');
        } finally {
            setBusy(false);
        }
    };

    const submitFinish = async (e) => {
        e.preventDefault();
        const home = Number(finishForm.evSahibiSkor);
        const away = Number(finishForm.deplasmanSkor);
        if (!Number.isInteger(home) || !Number.isInteger(away) || home < 0 || away < 0) {
            setError('Final skorlar sifir veya pozitif tam sayi olmali.');
            return;
        }
        setBusy(true); setError(''); setSuccess('');
        try {
            const endpoint = role === 'ADMIN' ? '/admin/matches/finish' : '/editor/matches/finish-command';
            await api.post(endpoint, { macId: Number(matchId), evSahibiSkor: home, deplasmanSkor: away, durum: 'BITTI', aciklama: finishForm.aciklama || undefined });
            closeAll();
            setSuccess('Mac BITTI durumuna gecti.');
            await onChanged();
        } catch (err) {
            setError(err?.response?.data?.hata || err?.response?.data?.mesaj || 'Mac sonlandirilamadi.');
        } finally {
            setBusy(false);
        }
    };

    return (
        <section className="match-control-panel glass-panel mb-4">
            <div className="match-control-header">
                <div>
                    <h3 className="match-control-title"><ShieldCheck size={18} /> Mac Olaylari ve Skor Yonetimi</h3>
                    <p className="text-muted text-sm">{canManage ? 'Skor guncelle, olay ekle ve maci sonlandir.' : 'Mac BITTI oldugu icin islemler kapali.'}</p>
                </div>
                <div className="match-control-actions">
                    <button type="button" className="btn btn-outline btn-sm" onClick={() => setShowScore(true)} disabled={!canManage}>Skor Guncelle</button>
                    <button type="button" className="btn btn-outline btn-sm" onClick={() => setShowEvent(true)} disabled={!canManage}><PlusCircle size={14} /> Olay Ekle</button>
                    <button type="button" className="btn btn-primary btn-sm" onClick={() => setShowFinish(true)} disabled={!canManage}>Maci Sonlandir</button>
                </div>
            </div>
            {error && <div className="error-banner mt-2">{error}</div>}
            {success && <div className="success-banner mt-2">{success}</div>}

            {showScore && <Modal title="Skor Guncelle" onClose={closeAll}><ScoreForm form={scoreForm} setForm={setScoreForm} onSubmit={submitScore} busy={busy} /></Modal>}
            {showEvent && <Modal title="Mac Olayi Ekle" onClose={closeAll}><EventForm form={eventForm} setForm={setEventForm} players={matchPlayers} onSubmit={submitEvent} busy={busy} /></Modal>}
            {showFinish && <Modal title="Maci Sonlandir" onClose={closeAll}><FinishForm form={finishForm} setForm={setFinishForm} onSubmit={submitFinish} busy={busy} /></Modal>}
        </section>
    );
};

const Modal = ({ title, onClose, children }) => (
    <div className="match-modal-overlay" onClick={onClose}>
        <div className="match-modal glass-panel" onClick={(e) => e.stopPropagation()}>
            <div className="match-modal-header">
                <h3>{title}</h3>
                <button type="button" className="match-modal-close" onClick={onClose}><X size={16} /></button>
            </div>
            {children}
        </div>
    </div>
);

const ScoreForm = ({ form, setForm, onSubmit, busy }) => (
    <form className="match-modal-form" onSubmit={onSubmit}>
        <label>Ev Sahibi Skor<input type="number" min={0} value={form.evSahibiSkor} onChange={(e) => setForm((p) => ({ ...p, evSahibiSkor: e.target.value }))} required /></label>
        <label>Deplasman Skor<input type="number" min={0} value={form.deplasmanSkor} onChange={(e) => setForm((p) => ({ ...p, deplasmanSkor: e.target.value }))} required /></label>
        <label>Aciklama (Opsiyonel)<textarea rows={3} value={form.aciklama} onChange={(e) => setForm((p) => ({ ...p, aciklama: e.target.value }))} /></label>
        <button type="submit" className="btn btn-primary" disabled={busy}>{busy ? 'Kaydediliyor...' : 'Skoru Kaydet'}</button>
    </form>
);

const EventForm = ({ form, setForm, players, onSubmit, busy }) => (
    <form className="match-modal-form" onSubmit={onSubmit}>
        <label>Dakika<input type="number" min={1} max={130} value={form.dakika} onChange={(e) => setForm((p) => ({ ...p, dakika: e.target.value }))} required /></label>
        <label>Olay Turu<select value={form.olayTuru} onChange={(e) => setForm((p) => ({ ...p, olayTuru: e.target.value }))} required>{EVENT_TYPES.map((t) => <option key={t.value} value={t.value}>{t.label}</option>)}</select></label>
        <label>Oyuncu<select value={form.oyuncuId} onChange={(e) => setForm((p) => ({ ...p, oyuncuId: e.target.value }))} required><option value="">Oyuncu sec</option>{players.map((player) => <option key={player.id} value={player.id}>{player.ad} {player.soyad} {player?.takim?.ad ? `(${player.takim.ad})` : ''}</option>)}</select></label>
        <button type="submit" className="btn btn-primary" disabled={busy}>{busy ? 'Ekleniyor...' : 'Olaya Ekle'}</button>
    </form>
);

const FinishForm = ({ form, setForm, onSubmit, busy }) => (
    <form className="match-modal-form" onSubmit={onSubmit}>
        <label>Ev Sahibi Final Skor<input type="number" min={0} value={form.evSahibiSkor} onChange={(e) => setForm((p) => ({ ...p, evSahibiSkor: e.target.value }))} required /></label>
        <label>Deplasman Final Skor<input type="number" min={0} value={form.deplasmanSkor} onChange={(e) => setForm((p) => ({ ...p, deplasmanSkor: e.target.value }))} required /></label>
        <label>Aciklama (Opsiyonel)<textarea rows={3} value={form.aciklama} onChange={(e) => setForm((p) => ({ ...p, aciklama: e.target.value }))} /></label>
        <button type="submit" className="btn btn-primary" disabled={busy}>{busy ? 'Sonlandiriliyor...' : 'Maci BITTI Yap'}</button>
    </form>
);

Modal.propTypes = {
    title: PropTypes.string.isRequired,
    onClose: PropTypes.func.isRequired,
    children: PropTypes.node.isRequired
};

ScoreForm.propTypes = {
    form: PropTypes.shape({ evSahibiSkor: PropTypes.string, deplasmanSkor: PropTypes.string, aciklama: PropTypes.string }).isRequired,
    setForm: PropTypes.func.isRequired,
    onSubmit: PropTypes.func.isRequired,
    busy: PropTypes.bool.isRequired
};

EventForm.propTypes = {
    form: PropTypes.shape({ oyuncuId: PropTypes.string, dakika: PropTypes.string, olayTuru: PropTypes.string }).isRequired,
    setForm: PropTypes.func.isRequired,
    players: PropTypes.arrayOf(PropTypes.object).isRequired,
    onSubmit: PropTypes.func.isRequired,
    busy: PropTypes.bool.isRequired
};

FinishForm.propTypes = {
    form: PropTypes.shape({ evSahibiSkor: PropTypes.string, deplasmanSkor: PropTypes.string, aciklama: PropTypes.string }).isRequired,
    setForm: PropTypes.func.isRequired,
    onSubmit: PropTypes.func.isRequired,
    busy: PropTypes.bool.isRequired
};

MatchManagementPanel.propTypes = {
    matchId: PropTypes.oneOfType([PropTypes.string, PropTypes.number]).isRequired,
    match: PropTypes.object,
    onChanged: PropTypes.func.isRequired
};

MatchManagementPanel.defaultProps = {
    match: null
};

export default MatchManagementPanel;
