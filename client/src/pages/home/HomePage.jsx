import * as React from "react";
import NavBar from "../../components/navbar/NavBar";
import home_style from "./home_style.css";
import { Subtitles } from "@mui/icons-material";
import CurrencyBalance from "../../components/currency_balance/CurrencyBalance";

const HomePage = () => {
  const username = "John Doe";
  return (
    <>
      <div style={{ display: "flex", direction: "column" }}>
        <NavBar active="Home" />
        <div style={{marginTop:30, width:"100%"}}>
          <h1 className="page-title">Hi, {username}!</h1>
          <h1 className="page-subtitle">Your balance is</h1>
          <div style={{display:"flex", gap:"30px", flexWrap:"wrap",marginLeft:70, marginRight:70, marginTop:50}}>
            <CurrencyBalance currency="USD" symbol="$" flag="🇺🇸"  balance="1000"  />
            <CurrencyBalance currency="CLP" symbol="$" flag="🇨🇱"  balance="1000" />
            <CurrencyBalance currency="ARS" symbol="$" flag="🇦🇷"  balance="1000" />
            <CurrencyBalance currency="TRY" symbol="₺" flag="🇹🇷"  balance="1000" />
            <CurrencyBalance currency="EUR" symbol="€" flag="🇪🇺"  balance="1000" />
            <CurrencyBalance currency="GBP" symbol="£" flag="🇬🇧"  balance="1000" />
          </div>
        </div>
      </div>
    </>
  );
};

export default HomePage;
