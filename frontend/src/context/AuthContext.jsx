import { useCallback, useEffect, useState } from 'react';
import PropTypes from 'prop-types';
import api from '../services/api';
import { TOKEN_KEY, USER_KEY } from '../services/authStorage';
import { AuthContext } from './authContext';

const getErrorMessage = (error, fallback) => {
    const message = error?.response?.data?.hata || error?.response?.data?.message;
    return message || fallback;
};

export const AuthProvider = ({ children }) => {
    const [token, setToken] = useState(() => localStorage.getItem(TOKEN_KEY));
    const [user, setUser] = useState(() => {
        try {
            const rawUser = localStorage.getItem(USER_KEY);
            return rawUser ? JSON.parse(rawUser) : null;
        } catch {
            return null;
        }
    });
    const [isAuthLoading, setIsAuthLoading] = useState(false);

    const normalizeUser = (rawUser) => {
        if (!rawUser) {
            return null;
        }

        return {
            ...rawUser,
            kullaniciAdi: rawUser.kullaniciAdi || rawUser.username || rawUser.email || '',
            email: rawUser.email || '',
            rol: rawUser.rol || ''
        };
    };

    useEffect(() => {
        if (token) {
            localStorage.setItem(TOKEN_KEY, token);
        } else {
            localStorage.removeItem(TOKEN_KEY);
        }
    }, [token]);

    useEffect(() => {
        if (user) {
            localStorage.setItem(USER_KEY, JSON.stringify(user));
        } else {
            localStorage.removeItem(USER_KEY);
        }
    }, [user]);

    const login = async (credentials) => {
        try {
            const response = await api.post('/auth/login', credentials);
            const nextToken = response?.data?.token;
            const nextUser = response?.data?.kullanici || null;

            if (!nextToken) {
                throw new Error('Token donmedi.');
            }

            setToken(nextToken);
            setUser(normalizeUser(nextUser));
            return { success: true };
        } catch (error) {
            return { success: false, message: getErrorMessage(error, 'Giris basarisiz.') };
        }
    };

    const register = async (payload) => {
        try {
            const response = await api.post('/auth/register', payload);
            const nextToken = response?.data?.token;
            const nextUser = response?.data?.kullanici || null;

            if (!nextToken) {
                throw new Error('Token donmedi.');
            }

            setToken(nextToken);
            setUser(normalizeUser(nextUser));
            return { success: true };
        } catch (error) {
            return { success: false, message: getErrorMessage(error, 'Kayit basarisiz.') };
        }
    };

    const resetPassword = async (payload) => {
        try {
            await api.post('/auth/reset-password', payload);
            return { success: true };
        } catch (error) {
            return { success: false, message: getErrorMessage(error, 'Sifre sifirlama basarisiz.') };
        }
    };

    const logout = () => {
        setToken(null);
        setUser(null);
    };

    const fetchCurrentUser = useCallback(async (activeToken = token) => {
        if (!activeToken) {
            setUser(null);
            return { success: false, message: 'Token bulunamadi.' };
        }

        setIsAuthLoading(true);
        try {
            const response = await api.get('/users/me', {
                headers: { Authorization: `Bearer ${activeToken}` }
            });
            setUser(normalizeUser(response?.data));
            return { success: true };
        } catch (error) {
            const status = error?.response?.status;
            if (status === 401 || status === 403) {
                setToken(null);
                setUser(null);
            }
            return { success: false, message: getErrorMessage(error, 'Kullanici bilgisi alinamadi.') };
        } finally {
            setIsAuthLoading(false);
        }
    }, [token]);

    useEffect(() => {
        if (!token) {
            setUser(null);
            return;
        }

        fetchCurrentUser(token);
    }, [token, fetchCurrentUser]);

    const value = {
        token,
        user,
        isAuthLoading,
        isAuthenticated: Boolean(token),
        login,
        register,
        resetPassword,
        logout,
        fetchCurrentUser
    };

    return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};

AuthProvider.propTypes = {
    children: PropTypes.node.isRequired
};
