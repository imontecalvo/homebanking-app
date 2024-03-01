import "./transaction_type_card.css";
import depositImg from "./../../public/deposit_icon.png";
import transferImg from "./../../public/transfer_icon.png";
import withdrawImg from "./../../public/withdraw_icon.png";

const TransactionTypeCard = ({ transaction, handleClick }) => {
  const icon =
    transaction === "DEPOSIT"
      ? depositImg
      : transaction === "TRANSFER"
      ? transferImg
      : withdrawImg;

  return (
    <div className="card" onClick={handleClick}>
      <h1 className="title">{transaction}</h1>
      <div>
        <img src={icon} style={{ maxWidth: "250px", maxHeight: "250px" }}></img>
      </div>
    </div>
  );
};

export default TransactionTypeCard;
