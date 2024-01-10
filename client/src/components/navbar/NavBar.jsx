import * as React from "react";
import { AppBar, Tabs, Tab, Container, Button } from "@mui/material";
import Box from "@mui/material/Box";
import navbar_style from "./navbar_style.css";
import { useNavigate } from "react-router-dom";
import LogoutIcon from "@mui/icons-material/Logout";

const NavBar = ({ active }) => {
  const navigate = useNavigate();

  const handleNavClick = (event) => {
    navigate("/" + event.target.id);
  };

  return (
    <div className="bar">
      <Button
        id="home"
        onClick={handleNavClick}
        style={{
          ...buttonStyle,
          color: "Home" == active ? "yellow" : "white",
        }}
      >
        Home
      </Button>
      <Button
        id="transactions"
        onClick={handleNavClick}
        style={{
          ...buttonStyle,
          color: "Transactions" == active ? "yellow" : "white",
        }}
      >
        Transactions
      </Button>
      <Button
        id="exchange"
        onClick={handleNavClick}
        style={{
          ...buttonStyle,
          color: "Exchange" == active ? "yellow" : "white",
        }}
      >
        Exchange
      </Button>
      <Button
        id="history"
        onClick={handleNavClick}
        style={{
          ...buttonStyle,
          color: "History" == active ? "yellow" : "white",
        }}
      >
        History
      </Button>
      <Button
        style={{ ...buttonStyle, color: "white", marginLeft: "auto" }}
        startIcon={<LogoutIcon />}
        onClick={() => {
          navigate("/login");
          localStorage.clear();
        }}
      ></Button>
    </div>
  );
};

export default NavBar;

const buttonStyle = {
  fontSize: 15,
  marginLeft: 20,
  marginRight: 20,
};
