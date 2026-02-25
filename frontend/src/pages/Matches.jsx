import { useState, useEffect, useMemo } from 'react';
import { Link } from 'react-router-dom';
import { Calendar, Filter, Image as ImageIcon } from 'lucide-react';
import api from '../services/api';
import './Matches.css';

const normalizeStatus = (value) => (value || '')
    .toString()
    .toUpperCase()
    .replaceAll('Ä°', 'I')
    .replaceAll('İ', 'I')
    .replaceAll('İ', 'I')
    .replaceAll('Ü', 'U')
    .replaceAll('Ö', 'O')
    .replaceAll('Ş', 'S')
    .replaceAll('Ç', 'C')
    .replaceAll('Ğ', 'G');

const getKickoffDate = (match) => {
    if (!match?.tarih) {
        return null;
    }
    const isoValue = `${match.tarih}T${match.saat || '00:00:00'}`;
    const kickoff = new Date(isoValue);
    return Number.isNaN(kickoff.getTime()) ? null : kickoff;
};

const isPastMatch = (match) => {
    const status = normalizeStatus(match?.durum || match?.onayDurumu);
    const explicitFinished = ['BITTI', 'TAMAMLANDI', 'SONUCLANDI', 'MAC_SONUCU'].includes(status);
    if (explicitFinished) {
        return true;
    }

    const explicitUpcoming = ['PLANLI', 'PLANLANDI', 'YAKINDA', 'ERTELENDI', 'ONAY_BEKLIYOR'].includes(status);
    if (explicitUpcoming) {
        return false;
    }

    const kickoff = getKickoffDate(match);
    return kickoff ? kickoff <= new Date() : false;
};

const getDisplayStatus = (match) => {
    const status = normalizeStatus(match?.durum || match?.onayDurumu);
    if (status) {
        return status;
    }
    return isPastMatch(match) ? 'BITTI' : 'PLANLI';
};

const Matches = () => {
    const [matches, setMatches] = useState([]);
    const [matchPhotos, setMatchPhotos] = useState({});
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [activeTab, setActiveTab] = useState('upcoming');

    useEffect(() => {
        const fetchMatches = async () => {
            try {
                const response = await api.get('/matches');
                const rawMatches = Array.isArray(response.data) ? response.data : [];

                const sortedMatches = [...rawMatches].sort((a, b) => {
                    const dateA = getKickoffDate(a);
                    const dateB = getKickoffDate(b);
                    if (!dateA && !dateB) {
                        return 0;
                    }
                    if (!dateA) {
                        return 1;
                    }
                    if (!dateB) {
                        return -1;
                    }
                    return dateB - dateA;
                });

                setMatches(sortedMatches);

                const photoEntries = await Promise.all(
                    sortedMatches.map(async (match) => {
                        try {
                            const mediaRes = await api.get(`/matches/${match.id}/media`);
                            const mediaList = Array.isArray(mediaRes.data) ? mediaRes.data : [];
                            const firstPhoto = mediaList.find((item) => {
                                const tip = normalizeStatus(item?.tip);
                                return !tip || tip.includes('FOTO') || tip.includes('IMAGE');
                            });
                            return [match.id, firstPhoto?.url || null];
                        } catch {
                            return [match.id, null];
                        }
                    })
                );

                setMatchPhotos(Object.fromEntries(photoEntries));
            } catch (err) {
                console.error('Maclari cekerken hata:', err);
                setError('Maclar yuklenirken bir sorun olustu.');
            } finally {
                setLoading(false);
            }
        };

        fetchMatches();
    }, []);

    const { upcomingMatches, pastMatches } = useMemo(() => {
        const upcoming = [];
        const past = [];

        matches.forEach((match) => {
            if (isPastMatch(match)) {
                past.push(match);
            } else {
                upcoming.push(match);
            }
        });

        return {
            upcomingMatches: upcoming,
            pastMatches: past
        };
    }, [matches]);

    const displayMatches = activeTab === 'upcoming' ? upcomingMatches : pastMatches;

    if (loading) {
        return (
            <div className="container flex-center" style={{ minHeight: '60vh' }}>
                <div className="loading-spinner"></div>
            </div>
        );
    }

    return (
        <div className="matches-page container animate-fade-in">
            <div className="page-header flex-between flex-wrap gap-4">
                <div>
                    <h1 className="page-title text-gradient">Fikstur ve Maclar</h1>
                    <p className="text-muted">Ligdeki tum karsilasmalar</p>
                </div>
            </div>

            {error ? (
                <div className="error-banner glass-panel">{error}</div>
            ) : (
                <div className="matches-content">
                    <div className="matches-tabs flex-center gap-4 mb-5">
                        <button
                            className={`match-tab-btn ${activeTab === 'upcoming' ? 'active' : ''}`}
                            onClick={() => setActiveTab('upcoming')}
                        >
                            Gelecek Maclar ({upcomingMatches.length})
                        </button>
                        <button
                            className={`match-tab-btn ${activeTab === 'past' ? 'active' : ''}`}
                            onClick={() => setActiveTab('past')}
                        >
                            Oynanan ve Tamamlanan ({pastMatches.length})
                        </button>
                    </div>

                    <div className="matches-list grid gap-4 animate-slide-up">
                        {displayMatches.length > 0 ? (
                            displayMatches.map((match) => (
                                <Link to={`/matches/${match.id}`} key={match.id} className="match-list-card glass-panel flex-between flex-wrap hover-scale">
                                    <div className="match-list-time flex-center flex-column">
                                        <Calendar size={18} className="text-primary mb-1" />
                                        <span className="font-bold">{match.tarih || 'Tarih Belirsiz'}</span>
                                        <span className="text-muted text-sm">{match.saat || ''}</span>
                                        <span className={`badge mt-2 ${activeTab === 'past' ? 'badge-success' : 'badge-warning'}`}>
                                            {getDisplayStatus(match)}
                                        </span>
                                    </div>

                                    <div className="match-photo-box">
                                        {matchPhotos[match.id] ? (
                                            <img src={matchPhotos[match.id]} alt="Mac fotografi" className="match-photo" />
                                        ) : (
                                            <div className="match-photo-placeholder">
                                                <ImageIcon size={20} />
                                            </div>
                                        )}
                                    </div>

                                    <div className="match-list-teams flex-1 flex-center w-100">
                                        <div className="team home text-right pr-4 w-50 flex-center" style={{ justifyContent: 'flex-end', gap: '1rem' }}>
                                            <span className="font-bold text-lg">{match.evSahibiTakim?.ad || 'Ev Sahibi'}</span>
                                            {match.evSahibiTakim?.logo && (
                                                <img src={match.evSahibiTakim.logo} alt="logo" className="team-logo-md" />
                                            )}
                                        </div>

                                        <div className="score-box glass-panel px-4 py-2 mx-4 flex-center font-bold text-2xl" style={{ minWidth: '100px' }}>
                                            {activeTab === 'past'
                                                ? `${match.evSahibiSkor ?? 0} - ${match.deplasmanSkor ?? 0}`
                                                : 'VS'}
                                        </div>

                                        <div className="team away pl-4 w-50 flex-center" style={{ justifyContent: 'flex-start', gap: '1rem' }}>
                                            {match.deplasmanTakim?.logo && (
                                                <img src={match.deplasmanTakim.logo} alt="logo" className="team-logo-md" />
                                            )}
                                            <span className="font-bold text-lg">{match.deplasmanTakim?.ad || 'Deplasman'}</span>
                                        </div>
                                    </div>
                                </Link>
                            ))
                        ) : (
                            <div className="empty-state glass-panel text-center py-5 w-100">
                                <Filter size={48} className="text-muted mb-4" />
                                <h3>Mac Bulunamadi</h3>
                                <p className="text-muted">Bu kategoride gosterilecek mac bulunmuyor.</p>
                            </div>
                        )}
                    </div>
                </div>
            )}
        </div>
    );
};

export default Matches;
