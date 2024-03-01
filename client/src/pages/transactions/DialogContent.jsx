import { useEffect } from "react";
import CurrencyList from "../../components/CurrencyList";
import * as React from "react";
import TextField from "@mui/material/TextField";
import { Button } from "@mui/material";
import NumberInput from "../../components/NumericInput";

import axios from "axios";

import {BACKEND_URL} from "../../constants.js"
// import {config} from "dotenv";
// config();

export const DepositContent = ({ onClose, showSnackBar }) => {
  const [currency, setCurrency] = React.useState("");
  const [amount, setAmount] = React.useState(0);

  const token = localStorage.getItem("token");

  const handleDeposit = async () => {
    try {
      const res = await axios.post(
        BACKEND_URL+`/transactions/deposit`,
        {
          amount: amount,
          currency: currency,
        },
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );
      onClose();
      showSnackBar(res.data.msg, "success");
    } catch (e) {
      showSnackBar(e.response.data.msg, "error");
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

export const WithdrawContent = ({ onClose, showSnackBar }) => {
  const [currency, setCurrency] = React.useState("");
  const [amount, setAmount] = React.useState(0);

  const token = localStorage.getItem("token");

  const handleWithdraw = async () => {
    try {
      const res = await axios.post(
        BACKEND_URL+`/transactions/withdraw`,
        {
          amount: amount,
          currency: currency,
        },
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );
      onClose();
      showSnackBar(res.data.msg, "success");
    } catch (e) {
      showSnackBar(e.response.data.msg, "error");
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

export const TransferContent = ({ onClose, showSnackBar }) => {
  const [currency, setCurrency] = React.useState("");
  const [amount, setAmount] = React.useState(0);
  const [username, setUsername] = React.useState("");

  const token = localStorage.getItem("token");

  const handleTransfer = async () => {
    try {
      const res = await axios.post(
        BACKEND_URL+`/transactions/transfer`,
        {
          amount: amount,
          currency: currency,
          destUsername: username,
        },
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );
      onClose();
      showSnackBar(res.data.msg, "success");
    } catch (e) {
      showSnackBar(e.response.data.msg, "error");
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
