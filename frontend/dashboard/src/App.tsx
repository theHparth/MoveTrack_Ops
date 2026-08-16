import { BrowserRouter, Routes, Route, Link } from "react-router-dom";
import MovesPage from "./pages/MovesPage";
import ClaimsPage from "./pages/ClaimsPage";
import LiveFeedPage from "./pages/LiveFeedPage";
import "./App.css";

function App() {
  return (
    <BrowserRouter>
      <nav>
        <Link to="/moves">Moves</Link>
        <Link to="/claims">Claims</Link>
        <Link to="/live-feed">Live Feed</Link>
      </nav>
      <Routes>
        <Route path="/moves" element={<MovesPage />} />
        <Route path="/claims" element={<ClaimsPage />} />
        <Route path="/live-feed" element={<LiveFeedPage />} />
        <Route path="/" element={<MovesPage />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;
