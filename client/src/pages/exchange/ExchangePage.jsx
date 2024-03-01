import * as React from "react";
import NavBar from "../../components/navbar/NavBar";
import NumberInput from "../../components/NumericInput";
import { Button } from "@mui/material";
import CurrencyList from "../../components/CurrencyList";
import { TextField } from "@mui/material";
import axios from "axios";


import Snackbar from "@mui/material/Snackbar";
import MuiAlert from "@mui/material/Alert";
import { BACKEND_URL } from "../../constants.js";

// import {config} from "dotenv";
// config();

const Alert = React.forwardRef(function Alert(props, ref) {
  return <MuiAlert elevation={6} ref={ref} variant="filled" {...props} />;
});

const ExchangePage = () => {
  //Campos
  const [originCurrency, setOriginCurrency] = React.useState("");
  const [originAmount, setOriginAmount] = React.useState(0.);

  const [destinyCurrency, setDestinyCurrency] = React.useState("");
  const [destinyAmount, setDestinyAmount] = React.useState(0.);

  //Snackbar
  const [openSnackBar, setOpenSnackBar] = React.useState(false);
  const [message, setMessage] = React.useState("");
  const [severity, setSeverity] = React.useState("success");

  const symbols = {
    USD: "$",
    CLP: "$",
    ARS: "$",
    TRY: "₺",
    EUR: "€",
    GBP: "£",
  };

  function exchange(amount, currency1, currency2) {
    const currenciesInUSD = {
      USD: 1,
      CLP: 0.0011,
      ARS: 0.0012,
      TRY: 0.034,
      EUR: 1.09,
      GBP: 1.27,
    };

    return fieldsNotEmpty()
      ? (amount * currenciesInUSD[currency1]) / currenciesInUSD[currency2]
      : 0;
  }

  function calculateDestinyAmount() {
    setDestinyAmount(
      exchange(originAmount, originCurrency, destinyCurrency).toFixed(2)
    );
  }

  React.useEffect(() => {
    calculateDestinyAmount();
  }, [originCurrency, destinyCurrency, originAmount]);

  function resetFields() {
    setOriginAmount(0.);
    setDestinyAmount(0.);
  }

  const token = localStorage.getItem("token");

  const confirmExchange = async () => {
    try {
      await axios.post(
        BACKEND_URL + "/exchange",
        {
          origin_currency: originCurrency,
          origin_amount: originAmount,
          destiny_currency: destinyCurrency,
        },
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );

      resetFields();
      setOpenSnackBar(true);
      setMessage("Exchange done!");
      setSeverity("success");
    } catch {
      setOpenSnackBar(true);
      setMessage("Something went wrong");
      setSeverity("error");
    }
  };

  const fieldsNotEmpty = () => {
    return (
      originCurrency !== "" && destinyCurrency !== "" && originAmount !== 0
    );
  };

  return (
    <>
      <NavBar active="Exchange" />;
      <div
        style={{
          marginTop: 70,
          width: "100%",
          display: "flex",
          flexDirection: "column",
          alignItems: "center",
        }}
      >
        {/* Origen */}
        <div
          style={{
            display: "flex",
            justifyContent: "center",
            marginTop: 50,
          }}
        >
          <CurrencyList onChange={setOriginCurrency} />
          <NumberInput
            aria-label="Amount"
            placeholder="Amount"
            onChange={(event, val) => {
              setOriginAmount(val);
              calculateDestinyAmount();
            }}
            value={originAmount}
            style={{ marginBottom: 10, marginLeft: 10, height: 46 }}
          />
        </div>

        {/* Destino */}
        <div
          style={{
            display: "flex",
            justifyContent: "center",
            marginTop: 10,
          }}
        >
          <CurrencyList onChange={setDestinyCurrency} />
          <TextField
            variant="outlined"
            style={{
              marginBottom: 10,
              marginLeft: 10,
              marginRight: 10,
              height: 46,
            }}
            disabled={true}
            value={destinyAmount}
          />
        </div>
        {/* Fee */}
        <div
          style={{
            textAlign: "center",
            border: "dashed 1px black",
            marginTop: 40,
            marginBottom: 20,
            width: 350,
            display: "flex",
            flexDirection: "row",
            justifyContent: "space-between",
          }}
        >
          <h1 style={{ ...feeStyle, marginLeft: 20 }}>Fee (1%)</h1>
          <h1 style={{ ...feeStyle, marginRight: 20 }}>
            {fieldsNotEmpty()
              ? `${symbols[destinyCurrency]} ${(destinyAmount * 0.01).toFixed(
                  2
                )} (${destinyCurrency})`
              : ""}
          </h1>
        </div>
        <h1 style={messageStyle}>
          You'll receive{" "}
          {fieldsNotEmpty()
            ? `${symbols[destinyCurrency]} ${(
                destinyAmount -
                destinyAmount * 0.01
              ).toFixed(2)} (${destinyCurrency}) `
            : "..."}
        </h1>
        <Button
          onClick={confirmExchange}
          variant="contained"
          color="primary"
          style={{ width: 130, borderRadius: 20, marginTop: 20 }}
          disabled={!fieldsNotEmpty()}
        >
          Confirm
        </Button>
      </div>
      <Snackbar
        open={openSnackBar}
        autoHideDuration={5000}
        onClose={() => setOpenSnackBar(false)}
      >
        <Alert
          onClose={() => setOpenSnackBar(false)}
          severity={severity}
          sx={{ width: "100%" }}
        >
          {message}
        </Alert>
      </Snackbar>
    </>
  );
};

export default ExchangePage;

const feeStyle = {
  fontFamily: "system-ui",
  fontWeight: 300,
  fontSize: 17,
  marginBottom: 10,
  marginTop: 10,
};

const messageStyle = {
  fontFamily: "system-ui",
  fontWeight: 300,
  fontSize: 30,
  marginTop: 50,
};
