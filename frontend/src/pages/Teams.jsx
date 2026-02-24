import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { Search, MapPin, Calendar, Trophy } from 'lucide-react';
import api from '../services/api';
import './Teams.css';

const Teams = () => {
    const [teams, setTeams] = useState([]);
    const [searchTerm, setSearchTerm] = useState('');
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        const fetchTeams = async () => {
            try {
                const response = await api.get('/teams');
                setTeams(response.data);
            } catch (err) {
                console.error('Takımları çekerken hata:', err);
                setError('Takımlar yüklenirken bir sorun oluştu.');
            } finally {
                setLoading(false);
            }
        };

        fetchTeams();
    }, []);

    const filteredTeams = teams.filter(team =>
        team.ad?.toLowerCase().includes(searchTerm.toLowerCase()) ||
        team.kisaAd?.toLowerCase().includes(searchTerm.toLowerCase())
    );

    if (loading) {
        return (
            <div className="container flex-center" style={{ minHeight: '60vh' }}>
                <div className="loading-spinner"></div>
            </div>
        );
    }

    return (
        <div className="teams-page container animate-fade-in">
            <div className="page-header flex-between flex-wrap gap-4">
                <div>
                    <h1 className="page-title text-gradient">Takımlar</h1>
                    <p className="text-muted">Ligdeki tüm takımlar ve detayları</p>
                </div>

                <div className="search-box glass-panel flex-center px-4 py-2">
                    <Search size={20} className="text-muted" />
                    <input
                        type="text"
                        placeholder="Takım ara..."
                        value={searchTerm}
                        onChange={(e) => setSearchTerm(e.target.value)}
                        className="search-input"
                    />
                </div>
            </div>

            {error ? (
                <div className="error-banner glass-panel">{error}</div>
            ) : (
                <div className="teams-grid mt-4">
                    {filteredTeams.length > 0 ? (
                        filteredTeams.map((team, index) => (
                            <Link
                                to={`/teams/${team.id}`}
                                key={team.id}
                                className={`team-card glass-panel animate-slide-up`}
                                style={{ animationDelay: `${(index % 10) * 50}ms` }}
                            >
                                <div className="team-card-header">
                                    <div className="team-logo-wrapper">
                                        {team.logo ? (
                                            <img src={team.logo} alt={team.ad} className="team-logo" />
                                        ) : (
                                            <div className="team-logo-placeholder"><Trophy size={32} /></div>
                                        )}
                                    </div>
                                    <div className="team-badge">{team.kisaAd}</div>
                                </div>

                                <div className="team-card-body">
                                    <h3 className="team-card-title">{team.ad}</h3>
                                    <div className="team-meta">
                                        {team.kurulusYili && (
                                            <div className="meta-item text-muted">
                                                <Calendar size={14} /> <span>{team.kurulusYili}</span>
                                            </div>
                                        )}
                                        {team.stadyum && (
                                            <div className="meta-item text-muted">
                                                <MapPin size={14} /> <span>{team.stadyum.stadyumAdi}</span>
                                            </div>
                                        )}
                                    </div>
                                </div>
                            </Link>
                        ))
                    ) : (
                        <div className="empty-state glass-panel col-span-full">
                            <Trophy size={48} className="text-muted mb-4" />
                            <h3>Takım Bulunamadı</h3>
                            <p className="text-muted">Arama kriterlerinize uygun takım bulunamadı.</p>
                        </div>
                    )}
                </div>
            )}
        </div>
    );
};

export default Teams;
