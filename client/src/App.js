import {BrowserRouter, Routes, Route} from "react-router-dom";

import LoginForm from "./pages/login/LoginPage";
import RegisterForm from "./pages/register/RegisterPage";
import HomePage from "./pages/home/HomePage";
import TransactionsPage from "./pages/transactions/TransactionsPage";
import ExchangePage from "./pages/exchange/ExchangePage";

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/login" element={<LoginForm/>}/>
        <Route path="/register" element={<RegisterForm/>}/>
        <Route path="/home" element={<HomePage/>}/>
        <Route path="/transactions" element={<TransactionsPage/>}/>
        <Route path="/exchange" element={<ExchangePage/>}/>
        
      </Routes>
    </BrowserRouter>
  );
}

export default App;
