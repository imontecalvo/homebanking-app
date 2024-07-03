import "./login_style.css";

import * as React from "react";
import TextField from "@mui/material/TextField";
import { Button } from "@mui/material";
import { useNavigate } from "react-router-dom";
import Snackbar from "@mui/material/Snackbar";
import * as authService from "../../services/auth.js";

import MuiAlert from "@mui/material/Alert";

const Alert = React.forwardRef(function Alert(props, ref) {
  return <MuiAlert elevation={6} ref={ref} variant="filled" {...props} />;
});

const LoginForm = () => {
  const [username, setUsername] = React.useState("");
  const [password, setPassword] = React.useState("");

  const [errorUsername, setErrorUsername] = React.useState(false);
  const [errorPassword, setErrorPassword] = React.useState(false);

  const [open, setOpen] = React.useState(false);
  const [message, setMessage] = React.useState("");

  const navigate = useNavigate();

  const nav2Register = () => {
    navigate("/register");
  };

  const updateErrorState = () => {
    setErrorUsername(username == "");
    setErrorPassword(password == "");
  };

  const handleLogin = async () => {
    updateErrorState();
    if (username == "" || password == "") {
      return;
    }

    try {
      console.log("login");
      const res = await authService.login(username, password);
      localStorage.setItem("username", res.data.username);
      localStorage.setItem("token", res.data.token);
      navigate("/home");
    } catch (e) {
      console.log(e)
      setOpen(true);
      setMessage("Invalid username or password");
    }
  };


  const handleClose = (e,reason) => {
    if (reason === 'clickaway') {
      return;
    }

    setOpen(false);
  };


  return (
    <>
      <div style={loginContainerStyle}>
        <h1 className="title" style={{ fontSize: 45, marginBottom: 40 }}>
          Welcome!
        </h1>
        <div className="input-container">
          <TextField
            label="Username"
            error={errorUsername}
            variant="outlined"
            style={{ marginBottom: 15 }}
            onChange={(e) => {
              setUsername(e.target.value);
            }}
          />
          <TextField
            label="Password"
            error={errorPassword}
            type="password"
            variant="outlined"
            style={{ marginBottom: 30 }}
            onChange={(e) => {
              setPassword(e.target.value);
            }}
          />
          <div
            style={{
              marginTop: "16px",
              display: "flex",
              justifyContent: "space-between",
            }}
          >
            <Button
              onClick={nav2Register}
              variant="outlined"
              color="primary"
              style={{ width: 170, borderRadius: 20 }}
            >
              Create account
            </Button>
            <Button
              onClick={handleLogin}
              variant="contained"
              color="primary"
              style={{ width: 130, borderRadius: 20 }}
            >
              Login
            </Button>
          </div>
        </div>
      </div>
      <Snackbar open={open} autoHideDuration={5000} onClose={handleClose}>
        <Alert onClose={handleClose} severity="error" sx={{ width: "100%" }}>
          {message}
        </Alert>
      </Snackbar>
    </>
  );
};

export default LoginForm;

const loginContainerStyle = {
  position: "absolute",
  top: "40%",
  left: "50%",
  transform: "translate(-50%, -50%)",
  borderRadius: "20px",
  padding: "30px",
  maxWidth: "30%",
  minWidth: "350px",
  border: "2px solid #2e2e2e",
};
