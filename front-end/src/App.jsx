import Login from './Pages/Login.jsx';
import Home from './Pages/Home.jsx';
import Signup from './Pages/SignUp.jsx';
import SignUp_Details from './Pages/SignUp_Details.jsx';
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';

function App() {
  return (
    <Router>
      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/login" element={<Login />} />
        <Route path="/signup" element={<Signup />} />
        <Route path="/signup-details" element={<SignUp_Details />} />
      </Routes>
    </Router>
  );
}

export default App;