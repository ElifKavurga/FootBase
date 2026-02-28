import { Routes, Route } from 'react-router-dom';
import Layout from './components/layout/Layout';

import Home from './pages/Home';
import Teams from './pages/Teams';
import TeamDetail from './pages/TeamDetail';
import Players from './pages/Players';
import PlayerDetail from './pages/PlayerDetail';
import Matches from './pages/Matches';
import MatchDetail from './pages/MatchDetail';
import Login from './pages/Login';
import Register from './pages/Register';
import Notifications from './pages/Notifications';
import EditorPanel from './pages/EditorPanel';
import AdminPanel from './pages/AdminPanel';
import RoleRoute from './components/auth/RoleRoute';

function App() {
  return (
    <div className="app-container">
      <Routes>
        <Route path="/" element={<Layout />}>
          <Route index element={<Home />} />
          <Route path="teams" element={<Teams />} />
          <Route path="teams/:id" element={<TeamDetail />} />
          <Route path="matches" element={<Matches />} />
          <Route path="matches/:id" element={<MatchDetail />} />
          <Route path="players" element={<Players />} />
          <Route path="players/:id" element={<PlayerDetail />} />
          <Route path="notifications" element={<Notifications />} />
          <Route path="login" element={<Login />} />
          <Route path="register" element={<Register />} />
          <Route element={<RoleRoute allowedRoles={['EDITOR']} />}>
            <Route path="editor-panel" element={<EditorPanel />} />
          </Route>
          <Route element={<RoleRoute allowedRoles={['ADMIN']} />}>
            <Route path="admin-panel" element={<AdminPanel />} />
          </Route>
        </Route>
      </Routes>
    </div>
  );
}

export default App;
