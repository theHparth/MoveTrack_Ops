import { BrowserRouter, Routes, Route, Link } from "react-router-dom";
import MovesPage from "./pages/MovesPage";
import ClaimsPage from "./pages/ClaimsPage";
import "./App.css";

function App() {
  return (
    <BrowserRouter>
      <nav>
        <Link to="/moves">Moves</Link>
        <Link to="/claims">Claims</Link>
      </nav>
      <Routes>
        <Route path="/moves" element={<MovesPage />} />
        <Route path="/claims" element={<ClaimsPage />} />
        <Route path="/" element={<MovesPage />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;
