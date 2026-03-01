import { useCallback, useEffect, useMemo, useState } from 'react';
import { PlusCircle, RefreshCcw } from 'lucide-react';
import api from '../services/api';
import './EditorPanel.css';

const normalizeStatus = (value) => (value || '')
    .toString()
    .toUpperCase()
    .replaceAll('İ', 'I')
    .replaceAll('İ', 'I');

const statusMap = {
    ONAY_BEKLIYOR: { label: 'Onay Bekliyor', className: 'badge-warning' },
    YAYINDA: { label: 'Onaylandi', className: 'badge-success' },
    REDDEDILDI: { label: 'Reddedildi', className: 'badge-danger' }
};

const getRefereeLabel = (referee) => referee?.adSoyad || referee?.name || referee?.ad || `Hakem #${referee?.id}`;
const getStadiumLabel = (stadium) => stadium?.stadyumAdi || stadium?.ad || stadium?.name || `Stadyum #${stadium?.id}`;
const getLeagueLabel = (league) => league?.ligAdi || league?.ad || league?.name || `Lig #${league?.id}`;

const EditorPanel = () => {
    const [teams, setTeams] = useState([]);
    const [referees, setReferees] = useState([]);
    const [stadiums, setStadiums] = useState([]);
    const [leagues, setLeagues] = useState([]);
    const [myMatches, setMyMatches] = useState([]);
    const [form, setForm] = useState({
        evSahibiTakimId: '',
        deplasmanTakimId: '',
        tarih: '',
        saat: '',
        hakemId: '',
        stadyumId: '',
        ligId: ''
    });
    const [loading, setLoading] = useState(true);
    const [submitting, setSubmitting] = useState(false);
    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');

    const canSubmit = useMemo(() => {
        const { evSahibiTakimId, deplasmanTakimId, tarih, saat, hakemId, stadyumId, ligId } = form;
        return Boolean(
            evSahibiTakimId &&
            deplasmanTakimId &&
            tarih &&
            saat &&
            hakemId &&
            stadyumId &&
            ligId &&
            evSahibiTakimId !== deplasmanTakimId
        );
    }, [form]);

    const loadPanelData = useCallback(async () => {
        const [teamsRes, refereesRes, stadiumsRes, leaguesRes, myMatchesRes] = await Promise.all([
            api.get('/teams').catch(() => ({ data: [] })),
            api.get('/referees').catch(() => ({ data: [] })),
            api.get('/stadiums').catch(() => ({ data: [] })),
            api.get('/leagues').catch(() => ({ data: [] })),
            api.get('/editor/matches/my-matches').catch(() => ({ data: [] }))
        ]);

        setTeams(Array.isArray(teamsRes?.data) ? teamsRes.data : []);
        setReferees(Array.isArray(refereesRes?.data) ? refereesRes.data : []);
        setStadiums(Array.isArray(stadiumsRes?.data) ? stadiumsRes.data : []);
        setLeagues(Array.isArray(leaguesRes?.data) ? leaguesRes.data : []);
        setMyMatches(Array.isArray(myMatchesRes?.data) ? myMatchesRes.data : []);
    }, []);

    useEffect(() => {
        const fetchData = async () => {
            setLoading(true);
            setError('');
            try {
                await loadPanelData();
            } catch (err) {
                setError(err?.response?.data?.hata || 'Editor paneli yuklenemedi.');
            } finally {
                setLoading(false);
            }
        };

        fetchData();
    }, [loadPanelData]);

    const handleSubmit = async (event) => {
        event.preventDefault();
        setError('');
        setSuccess('');

        if (!canSubmit) {
            setError('Tum alanlari dogru doldurman gerekiyor.');
            return;
        }

        setSubmitting(true);
        try {
            const payload = {
                evSahibiTakim: { id: Number(form.evSahibiTakimId) },
                deplasmanTakim: { id: Number(form.deplasmanTakimId) },
                tarih: form.tarih,
                saat: form.saat.length === 5 ? `${form.saat}:00` : form.saat,
                hakem: { id: Number(form.hakemId) },
                stadyum: { id: Number(form.stadyumId) },
                lig: { id: Number(form.ligId) },
                evSahibiSkor: 0,
                deplasmanSkor: 0
            };

            await api.post('/editor/matches', payload);
            setSuccess('Mac eklendi. Admin paneline onay bildirimi gonderildi.');
            setForm({
                evSahibiTakimId: '',
                deplasmanTakimId: '',
                tarih: '',
                saat: '',
                hakemId: '',
                stadyumId: '',
                ligId: ''
            });
            await loadPanelData();
        } catch (err) {
            setError(err?.response?.data?.hata || 'Mac eklenirken hata olustu.');
        } finally {
            setSubmitting(false);
        }
    };

    if (loading) {
        return (
            <div className="container flex-center" style={{ minHeight: '60vh' }}>
                <div className="loading-spinner"></div>
            </div>
        );
    }

    return (
        <div className="editor-panel-page container animate-fade-in">
            <div className="editor-panel-header glass-panel">
                <div>
                    <h1 className="text-gradient">Editor Paneli</h1>
                    <p className="text-muted">Yeni mac ekle, onay surecini takip et.</p>
                </div>
                <button type="button" className="btn btn-outline" onClick={loadPanelData}>
                    <RefreshCcw size={16} />
                    Yenile
                </button>
            </div>

            <div className="editor-grid">
                <section className="glass-panel editor-form-card">
                    <h2>Yeni Mac Ekle</h2>
                    <p className="text-muted">Kayit olunca admin onayi icin bildirim olusur.</p>

                    {error && <div className="error-banner mt-3">{error}</div>}
                    {success && <div className="success-banner mt-3">{success}</div>}

                    <form className="editor-form" onSubmit={handleSubmit}>
                        <label>
                            Ev Sahibi Takim
                            <select
                                value={form.evSahibiTakimId}
                                onChange={(e) => setForm((prev) => ({ ...prev, evSahibiTakimId: e.target.value }))}
                                required
                            >
                                <option value="">Takim sec</option>
                                {teams.map((team) => (
                                    <option key={team.id} value={team.id}>{team.ad}</option>
                                ))}
                            </select>
                        </label>

                        <label>
                            Deplasman Takimi
                            <select
                                value={form.deplasmanTakimId}
                                onChange={(e) => setForm((prev) => ({ ...prev, deplasmanTakimId: e.target.value }))}
                                required
                            >
                                <option value="">Takim sec</option>
                                {teams.map((team) => (
                                    <option key={team.id} value={team.id}>{team.ad}</option>
                                ))}
                            </select>
                        </label>

                        <div className="editor-form-inline">
                            <label>
                                Tarih
                                <input
                                    type="date"
                                    value={form.tarih}
                                    onChange={(e) => setForm((prev) => ({ ...prev, tarih: e.target.value }))}
                                    required
                                />
                            </label>

                            <label>
                                Saat
                                <input
                                    type="time"
                                    value={form.saat}
                                    onChange={(e) => setForm((prev) => ({ ...prev, saat: e.target.value }))}
                                    required
                                />
                            </label>
                        </div>

                        <label>
                            Hakem
                            <select
                                value={form.hakemId}
                                onChange={(e) => setForm((prev) => ({ ...prev, hakemId: e.target.value }))}
                                required
                            >
                                <option value="">Hakem sec</option>
                                {referees.map((referee) => (
                                    <option key={referee.id} value={referee.id}>
                                        {getRefereeLabel(referee)}
                                    </option>
                                ))}
                            </select>
                        </label>

                        <label>
                            Stadyum
                            <select
                                value={form.stadyumId}
                                onChange={(e) => setForm((prev) => ({ ...prev, stadyumId: e.target.value }))}
                                required
                            >
                                <option value="">Stadyum sec</option>
                                {stadiums.map((stadium) => (
                                    <option key={stadium.id} value={stadium.id}>
                                        {getStadiumLabel(stadium)}
                                    </option>
                                ))}
                            </select>
                        </label>

                        <label>
                            Lig
                            <select
                                value={form.ligId}
                                onChange={(e) => setForm((prev) => ({ ...prev, ligId: e.target.value }))}
                                required
                            >
                                <option value="">Lig sec</option>
                                {leagues.map((league) => (
                                    <option key={league.id} value={league.id}>
                                        {getLeagueLabel(league)}
                                    </option>
                                ))}
                            </select>
                        </label>

                        <button type="submit" className="btn btn-primary" disabled={!canSubmit || submitting}>
                            <PlusCircle size={16} />
                            {submitting ? 'Ekleniyor...' : 'Maci Kaydet'}
                        </button>
                    </form>
                </section>

                <section className="glass-panel editor-list-card">
                    <h2>Benim Maclarim</h2>
                    <p className="text-muted">Onay bekleyen ve reddedilen kayitlarin.</p>

                    {myMatches.length === 0 ? (
                        <div className="editor-empty-state">Henuz mac eklemedin.</div>
                    ) : (
                        <div className="editor-match-list">
                            {myMatches.map((match) => {
                                const status = normalizeStatus(match?.onayDurumu || match?.durum);
                                const meta = statusMap[status] || { label: status || 'Bilinmiyor', className: 'badge-warning' };
                                return (
                                    <article key={match.id} className="editor-match-item">
                                        <div>
                                            <div className="editor-match-title">
                                                {match.evSahibiTakim?.ad || 'Ev Sahibi'} vs {match.deplasmanTakim?.ad || 'Deplasman'}
                                            </div>
                                            <div className="text-muted text-sm">{match.tarih || '-'} {match.saat || ''}</div>
                                        </div>
                                        <div className="editor-match-meta">
                                            <span className={`badge ${meta.className}`}>{meta.label}</span>
                                            <span className="editor-score">{match.evSahibiSkor ?? 0} - {match.deplasmanSkor ?? 0}</span>
                                        </div>
                                    </article>
                                );
                            })}
                        </div>
                    )}
                </section>
            </div>
        </div>
    );
};

export default EditorPanel;
