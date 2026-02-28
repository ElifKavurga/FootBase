import PropTypes from 'prop-types';
import { Link, Navigate, Outlet, useLocation } from 'react-router-dom';
import { useAuth } from '../../hooks/useAuth';

const normalizeRole = (role) => (role || '').toString().trim().toUpperCase();

const RoleRoute = ({ allowedRoles }) => {
    const location = useLocation();
    const { isAuthenticated, isAuthLoading, user } = useAuth();

    if (isAuthLoading && isAuthenticated) {
        return (
            <div className="container flex-center" style={{ minHeight: '50vh' }}>
                <div className="loading-spinner"></div>
            </div>
        );
    }

    if (!isAuthenticated) {
        return <Navigate to="/login" replace state={{ from: location }} />;
    }

    const currentRole = normalizeRole(user?.rol);
    const isAllowed = allowedRoles.map(normalizeRole).includes(currentRole);

    if (!isAllowed) {
        return (
            <div className="container" style={{ paddingTop: '2rem', paddingBottom: '2rem' }}>
                <div className="glass-panel" style={{ padding: '2rem', textAlign: 'center' }}>
                    <h2 className="mb-2">Bu sayfaya erisimin yok</h2>
                    <p className="text-muted mb-4">Bu panel sadece yetkili roller icin aciktir.</p>
                    <Link to="/" className="btn btn-primary">Ana Sayfaya Don</Link>
                </div>
            </div>
        );
    }

    return <Outlet />;
};

RoleRoute.propTypes = {
    allowedRoles: PropTypes.arrayOf(PropTypes.string).isRequired
};

export default RoleRoute;
