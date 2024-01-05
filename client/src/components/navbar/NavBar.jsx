import * as React from "react";
import { AppBar, Tabs, Tab, Container, Button } from "@mui/material";
import Box from "@mui/material/Box";
import navbar_style from "./navbar_style.css";
import { useNavigate } from "react-router-dom";

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
          color: "Home" == active ? "yellow" : "white",
          fontSize: 15,
          marginLeft: 20,
          marginRight: 20,
        }}
      >
        Home
      </Button>
      <Button
        id="transactions"
        onClick={handleNavClick}
        style={{
          color: "Transactions" == active ? "yellow" : "white",
          fontSize: 15,
          marginLeft: 20,
          marginRight: 20,
        }}
      >
        Transactions
      </Button>
      <Button
        id="exchange"
        onClick={handleNavClick}
        style={{
          color: "Exchange" == active ? "yellow" : "white",
          fontSize: 15,
          marginLeft: 20,
          marginRight: 20,
        }}
      >
        Exchange
      </Button>
    </div>
  );
};

export default NavBar;
