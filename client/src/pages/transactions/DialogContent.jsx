import { useEffect } from "react";
import CurrencyList from "../../components/CurrencyList";
import * as React from "react";
import TextField from "@mui/material/TextField";
import { Button } from "@mui/material";
import NumberInput from "../../components/NumericInput";

import axios from "axios";

export const DepositContent = ({ onClose, showSnackBar }) => {
  const [currency, setCurrency] = React.useState("");
  const [amount, setAmount] = React.useState(0);

  const user_id = localStorage.getItem("user_id");

  const handleDeposit = async () => {
    try {
      const res = await axios.post(
        `http://localhost:3001/transactions/deposit`,
        {
          user_id: user_id,
          amount: amount,
          currency: currency,
        }
      );
      onClose();
      showSnackBar(res.data.msg, "success");
    } catch (e) {
      showSnackBar(e.response.data.msg, "error");
      console.log(e);
    }
  };

  return (
    <div
      style={{
        display: "flex",
        flexDirection: "column",
        marginTop: 20,
        height: 200,
      }}
    >
      <div
        style={{
          display: "flex",
          justifyContent: "center",
        }}
      >
        <CurrencyList onChange={setCurrency} />
        <NumberInput
          aria-label="Amount"
          placeholder="Amount"
          value={amount}
          onChange={(event, val) => setAmount(val)}
          style={{ marginBottom: 10, marginLeft: 10, height: 46 }}
        />
      </div>
      <Button
        onClick={handleDeposit}
        variant="contained"
        color="primary"
        style={{
          width: 130,
          borderRadius: 20,
          marginRight: 75,
          marginLeft: "auto",
          marginTop: "auto",
        }}
      >
        Confirm
      </Button>
    </div>
  );
};

export const WithdrawContent = (onClose) => {
  const [currency, setCurrency] = React.useState("");
  const [amount, setAmount] = React.useState(0);

  const handleWithdraw = () => {
    console.log(`Withdraw ${amount} (${currency})`); //TODO: Send Request to backend
  };

  return (
    <div
      style={{
        display: "flex",
        flexDirection: "column",
        marginTop: 20,
        height: 200,
      }}
    >
      <div
        style={{
          display: "flex",
          justifyContent: "center",
        }}
      >
        <CurrencyList onChange={setCurrency} />
        <NumberInput
          aria-label="Amount"
          placeholder="Amount"
          value={amount}
          onChange={(event, val) => setAmount(val)}
          style={{ marginBottom: 10, marginLeft: 10, height: 46 }}
        />
      </div>
      <Button
        onClick={handleWithdraw}
        variant="contained"
        color="primary"
        style={{
          width: 130,
          borderRadius: 20,
          marginRight: 75,
          marginLeft: "auto",
          marginTop: "auto",
        }}
      >
        Confirm
      </Button>
    </div>
  );
};

export const TransferContent = ({ onClose }) => {
  const [currency, setCurrency] = React.useState("");
  const [amount, setAmount] = React.useState(0);
  const [username, setUsername] = React.useState("");

  const handleTransfer = () => {
    console.log(`Transfer ${amount} (${currency}) to ${username}`); //TODO: Send Request to backend
  };

  return (
    <div
      style={{
        display: "flex",
        flexDirection: "column",
        marginTop: 20,
        height: 200,
      }}
    >
      <div
        style={{
          display: "flex",
          justifyContent: "center",
        }}
      >
        <CurrencyList onChange={setCurrency} />
        <NumberInput
          aria-label="Amount"
          placeholder="Amount"
          value={amount}
          onChange={(event, val) => setAmount(val)}
          style={{ marginBottom: 10, marginLeft: 10, height: 46 }}
        />
      </div>
      <div
        style={{
          display: "flex",
          justifyContent: "center",
          marginTop: 10,
        }}
      >
        <h1
          style={{
            fontFamily: "system-ui",
            fontWeight: 300,
            fontSize: 25,
            marginRight: 30,
          }}
        >
          To{" "}
        </h1>
        <TextField
          label="Username"
          variant="outlined"
          style={{ marginLeft: 60 }}
          onChange={(e) => setUsername(e.target.value)}
        />
      </div>

      <Button
        onClick={handleTransfer}
        variant="contained"
        color="primary"
        style={{
          width: 130,
          borderRadius: 20,
          marginRight: 75,
          marginLeft: "auto",
          marginTop: "auto",
        }}
      >
        Confirm
      </Button>
    </div>
  );
};
