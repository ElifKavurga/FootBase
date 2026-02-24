import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { Search, MapPin, Users, Heart } from 'lucide-react';
import api from '../services/api';
import './Players.css';

const Players = () => {
    const [players, setPlayers] = useState([]);
    const [searchTerm, setSearchTerm] = useState('');
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        const fetchPlayers = async () => {
            try {
                const response = await api.get('/players');
                setPlayers(response.data);
            } catch (err) {
                console.error('Oyuncuları çekerken hata:', err);
                setError('Oyuncular yüklenirken bir sorun oluştu.');
            } finally {
                setLoading(false);
            }
        };

        fetchPlayers();
    }, []);

    const filteredPlayers = players.filter(player => {
        const adSoyad = `${player.ad || ''} ${player.soyad || ''}`.trim().toLowerCase();
        const searchTermLower = searchTerm.toLowerCase();
        return adSoyad.includes(searchTermLower) ||
            (player.pozisyon || '').toLowerCase().includes(searchTermLower);
    });

    if (loading) {
        return (
            <div className="container flex-center" style={{ minHeight: '60vh' }}>
                <div className="loading-spinner"></div>
            </div>
        );
    }

    return (
        <div className="players-page container animate-fade-in">
            <div className="page-header flex-between flex-wrap gap-4">
                <div>
                    <h1 className="page-title text-gradient">Oyuncular</h1>
                    <p className="text-muted">Ligdeki tüm oyuncular ve kariyer detayları</p>
                </div>

                <div className="search-box glass-panel flex-center px-4 py-2">
                    <Search size={20} className="text-muted" />
                    <input
                        type="text"
                        placeholder="İsim veya pozisyon ara..."
                        value={searchTerm}
                        onChange={(e) => setSearchTerm(e.target.value)}
                        className="search-input"
                    />
                </div>
            </div>

            {error ? (
                <div className="error-banner glass-panel">{error}</div>
            ) : (
                <div className="players-grid mt-4">
                    {filteredPlayers.length > 0 ? (
                        filteredPlayers.map((player, index) => (
                            <Link
                                to={`/players/${player.id}`}
                                key={player.id}
                                className={`player-card-detailed glass-panel animate-slide-up`}
                                style={{ animationDelay: `${(index % 10) * 50}ms` }}
                            >
                                <div className="player-image-wrapper">
                                    {player.fotograf ? (
                                        <img src={player.fotograf} alt={`${player.ad} ${player.soyad}`} className="player-image" />
                                    ) : (
                                        <Users size={48} className="text-muted opacity-50" />
                                    )}
                                    <span className="player-number">{player.formaNumarasi || '-'}</span>
                                </div>

                                <div className="player-info p-4">
                                    <div className="flex-between mb-2">
                                        <span className="badge badge-success text-xs">{player.pozisyon || 'Belirtilmemiş'}</span>
                                        <span className="text-muted text-xs flex-center gap-1"><MapPin size={12} /> {player.milliyet || '?'}</span>
                                    </div>

                                    <h3 className="player-name mb-1">{player.ad} {player.soyad}</h3>
                                    <div className="player-team text-primary font-bold text-sm mb-3">
                                        {player.takim ? player.takim.ad : 'Serbest Oyuncu'}
                                    </div>

                                    <div className="player-stats-mini grid-2 gap-2 mt-auto">
                                        <div className="stat-pill glass-panel text-center py-1 rounded">
                                            <div className="text-muted text-xs">Yaş</div>
                                            <div className="font-bold">{player.yas || '-'}</div>
                                        </div>
                                        <div className="stat-pill glass-panel text-center py-1 rounded">
                                            <div className="text-muted text-xs">Puan</div>
                                            <div className="font-bold flex-center gap-1 justify-center">
                                                {player.ortalamaPuan ? player.ortalamaPuan.toFixed(1) : '-'}
                                                {player.ortalamaPuan && <Heart size={10} className="text-danger" />}
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </Link>
                        ))
                    ) : (
                        <div className="empty-state glass-panel col-span-full">
                            <Users size={48} className="text-muted mb-4" />
                            <h3>Oyuncu Bulunamadı</h3>
                            <p className="text-muted">Arama kriterlerinize uygun oyuncu bulunamadı.</p>
                        </div>
                    )}
                </div>
            )}
        </div>
    );
};

export default Players;
