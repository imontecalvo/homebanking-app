import register_style from "./register_style.css";

import * as React from "react";
import TextField from "@mui/material/TextField";
import Container from "@mui/material/Container";
import { Button } from "@mui/material";

import { useNavigate } from "react-router-dom";
import CurrencyList from "../../components/CurrencyList";

const RegisterForm = () => {
  const [username, setUsername] = React.useState("");
  const [password, setPassword] = React.useState("");
  const [confirmPassword, setConfirmPassword] = React.useState("");
  const [currency, setCurrency] = React.useState("");

  const navigate = useNavigate();

  const nav2Login = () => {
    navigate("/login");
  };

  const handleRegister = () => {
    console.log(`Register: ${username} ${password} ${confirmPassword} ${currency}`); //TODO: Send Request to backend
  };

  return (
    <Container style={registerContainerStyle}>
      <h1 className="title" style={{ fontSize: 45, marginBottom: 40 }}>
        Sign up!
      </h1>
      <div className="input-container">
        <TextField
          onChange={(e) => {
            setUsername(e.target.value);
          }}
          label="Username"
          variant="outlined"
          style={{ marginBottom: 15 }}
        />
        <TextField
          onChange={(e) => {
            setPassword(e.target.value);
          }}
          type="password"
          label="Password"
          variant="outlined"
          style={{ marginBottom: 15 }}
          password={true}
        />
        <TextField
          onChange={(e) => {
            setConfirmPassword(e.target.value);
          }}
          type="password"
          label="Confirm password"
          variant="outlined"
          style={{ marginBottom: 15 }}
          password={true}

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
          <CurrencyList onChange={setCurrency} />
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
  );
};

export default RegisterForm;

const registerContainerStyle = {
  position: "absolute",
  top: "50%",
  left: "50%",
  transform: "translate(-50%, -50%)",
  borderRadius: "20px",
  padding: "30px",
  maxWidth: "30%",
  minWidth: "350px",
  border: "2px solid #2e2e2e",
};
