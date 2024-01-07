import register_style from "./register_style.css";
import * as React from "react";
import TextField from "@mui/material/TextField";
import Container from "@mui/material/Container";
import { Button } from "@mui/material";
import Snackbar from "@mui/material/Snackbar";

import axios from "axios";

import { useNavigate } from "react-router-dom";
import CurrencyList from "../../components/CurrencyList";

import MuiAlert from "@mui/material/Alert";

const Alert = React.forwardRef(function Alert(props, ref) {
  return <MuiAlert elevation={6} ref={ref} variant="filled" {...props} />;
});

const RegisterForm = () => {
  const [username, setUsername] = React.useState("");
  const [password, setPassword] = React.useState("");
  const [confirmPassword, setConfirmPassword] = React.useState("");
  const [currency, setCurrency] = React.useState("");

  const [errorUsername, setErrorUsername] = React.useState(false);
  const [errorPassword, setErrorPassword] = React.useState(false);
  const [errorConfirmPassword, setErrorConfirmPassword] = React.useState(false);
  const [errorCurrency, setErrorCurrency] = React.useState(false);

  const [open, setOpen] = React.useState(false);
  const [message, setMessage] = React.useState("");
  const [severity, setSeverity] = React.useState("success");

  const navigate = useNavigate();

  const nav2Login = () => {
    navigate("/login");
  };

  const updateErrorState = () => {
    setErrorUsername(username == "");
    setErrorPassword(password == "" || password != confirmPassword);
    setErrorConfirmPassword(
      confirmPassword == "" || password != confirmPassword
    );
    setErrorCurrency(currency == "");
  };

  const handleRegister = async () => {
    updateErrorState();
    if (
      username == "" ||
      password == "" ||
      confirmPassword == "" ||
      currency == "" ||
      password != confirmPassword
    ) {
      return;
    }
    try {
      const res = await axios.post("http://localhost:3001/users/register", {
        username: username,
        password: password,
        confirmPassword: confirmPassword,
        currency: currency,
      });

      setMessage("User created successfully!");
      setSeverity("success");
      setOpen(true);

      setTimeout(() => {
        navigate("/login");
      }, 1000);
    } catch (e) {
      setMessage(e.response.data.msg);
      setSeverity("error");
      setOpen(true);
    }
  };

  const handleClose = () => {
    setOpen(false);
  };

  return (
    <>
      <Container style={registerContainerStyle}>
        <h1 className="title" style={{ fontSize: 45, marginBottom: 40 }}>
          Sign up!
        </h1>
        <div className="input-container">
          <TextField
            label="Username"
            error={errorUsername}
            onChange={(e) => {
              setUsername(e.target.value);
            }}
            variant="outlined"
            style={{ marginBottom: 15 }}
          />
          <TextField
            label="Password"
            error={errorPassword}
            onChange={(e) => {
              setPassword(e.target.value);
            }}
            type="password"
            variant="outlined"
            style={{ marginBottom: 15 }}
          />
          <TextField
            label="Confirm password"
            error={errorConfirmPassword}
            onChange={(e) => {
              setConfirmPassword(e.target.value);
            }}
            type="password"
            variant="outlined"
            style={{ marginBottom: 15 }}
          />
          <div
            style={{
              display: "flex",
              justifyContent: "space-between",
              marginBottom: 30,
            }}
          >
            <h1
              style={{
                fontFamily: "system-ui",
                fontWeight: 300,
                fontSize: 17,
                marginRight: 30,
              }}
            >
              Main currency{" "}
            </h1>
            <CurrencyList onChange={setCurrency} error={errorCurrency} />
          </div>
          <div
            style={{
              marginTop: "16px",
              display: "flex",
              justifyContent: "space-between",
            }}
          >
            <Button
              onClick={nav2Login}
              variant="outlined"
              color="primary"
              style={{ width: 130, borderRadius: 20 }}
            >
              Login
            </Button>
            <Button
              onClick={handleRegister}
              variant="contained"
              color="primary"
              style={{ width: 170, borderRadius: 20 }}
            >
              Create account
            </Button>
          </div>
        </div>
      </Container>
      <Snackbar open={open} autoHideDuration={6000} onClose={handleClose}>
        <Alert onClose={handleClose} severity={severity} sx={{ width: "100%" }}>
          {message}
        </Alert>
      </Snackbar>
    </>
  );
};

export default RegisterForm;

const registerContainerStyle = {
  position: "absolute",
  top: "49%",
  left: "50%",
  transform: "translate(-50%, -50%)",
  borderRadius: "20px",
  padding: "30px",
  width: "420px",
  border: "2px solid #2e2e2e",
};
