import api from './api';

export const MEDIA_TYPE_OPTIONS = [
    { value: 'IMAGE', label: 'Gorsel' },
    { value: 'VIDEO', label: 'Video' },
    { value: 'HIGHLIGHT', label: 'Highlight' },
    { value: 'GALLERY', label: 'Galeri' }
];

const normalizeRole = (value) => (value || '').toString().trim().toUpperCase();

const createPayload = ({ tip, url, file, aciklama }) => {
    if (file) {
        const formData = new FormData();
        formData.append('tip', tip);
        if (url) {
            formData.append('url', url);
        }
        if (aciklama) {
            formData.append('aciklama', aciklama);
        }
        formData.append('file', file);
        return { data: formData, config: { headers: { 'Content-Type': 'multipart/form-data' } } };
    }

    return {
        data: {
            tip,
            url,
            aciklama: aciklama || undefined
        },
        config: undefined
    };
};

const postWithFallback = async (endpoints, payload, config) => {
    let lastError = null;

    for (const endpoint of endpoints) {
        try {
            return await api.post(endpoint, payload, config);
        } catch (error) {
            const status = error?.response?.status;
            if (status === 404 || status === 405) {
                lastError = error;
                continue;
            }
            throw error;
        }
    }

    if (lastError) {
        throw lastError;
    }

    throw new Error('Uygun medya endpointi bulunamadi.');
};

export const addMatchMedia = async ({ matchId, tip, url, file, aciklama, userRole }) => {
    const role = normalizeRole(userRole);
    const endpoints = role === 'ADMIN'
        ? [`/admin/matches/${matchId}/media`, `/editor/matches/${matchId}/media`, `/matches/${matchId}/media`]
        : [`/editor/matches/${matchId}/media`, `/admin/matches/${matchId}/media`, `/matches/${matchId}/media`];

    const { data, config } = createPayload({ tip, url, file, aciklama });
    return postWithFallback(endpoints, data, config);
};

export const addPlayerMedia = async ({ playerId, tip, url, file, aciklama, userRole }) => {
    const role = normalizeRole(userRole);
    const endpoints = role === 'ADMIN'
        ? [`/admin/players/${playerId}/media`, `/editor/players/${playerId}/media`, `/players/${playerId}/media`]
        : [`/editor/players/${playerId}/media`, `/admin/players/${playerId}/media`, `/players/${playerId}/media`];

    const { data, config } = createPayload({ tip, url, file, aciklama });
    return postWithFallback(endpoints, data, config);
};
