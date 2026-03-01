import PropTypes from 'prop-types';
import { useState } from 'react';
import { Upload, X } from 'lucide-react';
import { MEDIA_TYPE_OPTIONS } from '../../services/mediaService';
import './MediaUploadModal.css';

const MediaUploadModal = ({ title, busy, error, onClose, onSubmit }) => {
    const [tip, setTip] = useState(MEDIA_TYPE_OPTIONS[0].value);
    const [url, setUrl] = useState('');
    const [aciklama, setAciklama] = useState('');
    const [file, setFile] = useState(null);
    const [localError, setLocalError] = useState('');

    const handleSubmit = async (event) => {
        event.preventDefault();
        setLocalError('');

        const cleanUrl = url.trim();
        if (!cleanUrl && !file) {
            setLocalError('URL veya dosya secimi zorunlu.');
            return;
        }

        await onSubmit({
            tip,
            url: cleanUrl || null,
            aciklama: aciklama.trim() || null,
            file
        });
    };

    return (
        <div className="media-modal-overlay" onClick={onClose}>
            <div className="media-modal glass-panel" onClick={(e) => e.stopPropagation()}>
                <div className="media-modal-header">
                    <h3>{title}</h3>
                    <button type="button" className="media-modal-close" onClick={onClose}>
                        <X size={16} />
                    </button>
                </div>

                {(error || localError) && <div className="error-banner mb-3">{error || localError}</div>}

                <form className="media-modal-form" onSubmit={handleSubmit}>
                    <label>
                        Medya Tipi
                        <select value={tip} onChange={(e) => setTip(e.target.value)} required>
                            {MEDIA_TYPE_OPTIONS.map((type) => (
                                <option key={type.value} value={type.value}>{type.label}</option>
                            ))}
                        </select>
                    </label>

                    <label>
                        Medya URL
                        <input
                            type="url"
                            placeholder="https://..."
                            value={url}
                            onChange={(e) => setUrl(e.target.value)}
                        />
                    </label>

                    <label>
                        Dosya Yukle (Opsiyonel)
                        <input
                            type="file"
                            onChange={(e) => setFile(e.target.files?.[0] || null)}
                        />
                    </label>

                    <label>
                        Aciklama (Opsiyonel)
                        <textarea
                            rows={3}
                            value={aciklama}
                            onChange={(e) => setAciklama(e.target.value)}
                        />
                    </label>

                    <button type="submit" className="btn btn-primary" disabled={busy}>
                        <Upload size={14} />
                        {busy ? 'Yukleniyor...' : 'Medyayi Ekle'}
                    </button>
                </form>
            </div>
        </div>
    );
};

MediaUploadModal.propTypes = {
    title: PropTypes.string.isRequired,
    busy: PropTypes.bool,
    error: PropTypes.string,
    onClose: PropTypes.func.isRequired,
    onSubmit: PropTypes.func.isRequired
};

MediaUploadModal.defaultProps = {
    busy: false,
    error: ''
};

export default MediaUploadModal;
