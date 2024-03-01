import "./container_style.css";

const CurrencyBalance = ({ currency, balance, flag, symbol }) => (
  <div className="container">
    <h3 className="currency-name">{`${currency} ${flag}`}</h3>
    <h3 className="currency-balance">{`${symbol}${balance}`}</h3>
  </div>
);
export default CurrencyBalance;
