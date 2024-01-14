import * as React from "react";
import NavBar from "../../components/navbar/NavBar";
import axios from "axios";
import Pagination from "@mui/material/Pagination";

import TransactionsTable from "../../components/TransactionsTable";

// import {config} from "dotenv";
// config();

import { BACKEND_URL } from "../../constants";

const HistoryPage = () => {
  const token = localStorage.getItem("token");

  const [nOfTransactions, setNOfTransactions] = React.useState(0);
  const [transactions, setTransactions] = React.useState([]);
  const [page, setPage] = React.useState(1);

  const [itemsPerPage, setItemsPerPage] = React.useState(0);

  const [winHeight, setWinHeight] = React.useState(window.innerHeight);

  function calculateItemsPerPage(h) {
    return Math.ceil((h - 225 - 40) / 70);
  }

  React.useEffect(() => {
    const getNOfTransactions = async () => {
      try {
        const res = await axios.get(BACKEND_URL + "/history/n-transactions", {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });
        setNOfTransactions(res.data.msg);
      } catch (e) {
        console.log(e);
      }
    };
    setWinHeight(window.innerHeight);
    setItemsPerPage(calculateItemsPerPage(winHeight));
    getNOfTransactions();
  }, []);

  React.useEffect(() => {
    const getTransactions = async () => {
      try {
        const res = await axios.get(
          BACKEND_URL + `/history?page=${page}&items=${itemsPerPage}`,
          {
            headers: {
              Authorization: `Bearer ${token}`,
            },
          }
        );

        setTransactions(
          res.data.msg.map((t) => {
            t.date = new Date(t.date);
            return t;
          })
        );
      } catch (e) {
        console.log(e);
      }
    };
    getTransactions();
  }, [page, itemsPerPage]);

  const handleChange = (event, value) => {
    setPage(value);
  };

  return (
    <div style={{ minHeight: "90vh" }}>
      <div
        style={{
          marginTop: 50,
          display: "flex",
          flexDirection: "column",
          marginRight: 70,
          marginLeft: 70,
        }}
      >
        <NavBar active="History" />
        <h1 style={subtitleStyle}>Your history</h1>
        {transactions.length > 0 ? (
          <>
            <TransactionsTable transactions={transactions} />
            <div style={{ display: "flex", justifyContent: "center" }}>
              <Pagination
                style={{
                  position: "absolute",
                  bottom: 10,
                }}
                count={
                  itemsPerPage == 0
                    ? 0
                    : Math.ceil(nOfTransactions / itemsPerPage)
                }
                onChange={handleChange}
              />
            </div>
          </>
        ) : (
          <div style={{ display: "flex", justifyContent: "center" }}>
            <h1 style={textStyle}>No transactions yet.</h1>
          </div>
        )}
      </div>
    </div>
  );
};

export default HistoryPage;

const subtitleStyle = {
  fontFamily: "system-ui",
  fontWeight: 300,
  fontSize: "30px",
  marginTop: "50px",
};

const textStyle = {
  fontFamily: "system-ui",
  fontWeight: 300,
  fontSize: "25px",
  marginTop: "50px",
  color: "#585858",
};
