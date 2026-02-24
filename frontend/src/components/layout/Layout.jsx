import { Outlet } from 'react-router-dom';
import Navbar from './Navbar';
import Footer from './Footer';

const Layout = () => {
    return (
        <div className="app-wrapper">
            <Navbar />
            <main className="main-content" style={{ minHeight: 'calc(100vh - 80px - 300px)', paddingTop: '80px' }}>
                <Outlet />
            </main>
            <Footer />
        </div>
    );
};

export default Layout;
