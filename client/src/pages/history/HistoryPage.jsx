import * as React from "react";
import NavBar from "../../components/navbar/NavBar";
import axios from "axios";
import Pagination from "@mui/material/Pagination";
import HistoryItem from "../../components/HistoryItem";

import TransactionsTable from "../../components/TransactionsTable";

const ITEMS_PER_PAGE = 5;

const HistoryPage = () => {
  const token = localStorage.getItem("token");

  const [nOfTransactions, setNOfTransactions] = React.useState(0);
  const [transactions, setTransactions] = React.useState([]);
  const [page, setPage] = React.useState(1);

  const [itemsPerPage, setItemsPerPage] = React.useState(0)


  const [winHeight, setWinHeight] = React.useState(window.innerHeight);

  function calculateItemsPerPage(h){
    return Math.ceil((h - 225 - 40) / 70)
  }

  React.useEffect(() => {
    const updateWinHeight = () => {
      setWinHeight(window.innerHeight);
    };

    window.addEventListener("resize", updateWinHeight);
    setItemsPerPage(calculateItemsPerPage(winHeight))

    return () => {
      window.removeEventListener("resize", updateWinHeight);
    };
  }, []);




  React.useState(() => {
    const getNOfTransactions = async () => {
      try {
        const res = await axios.get(
          `http://localhost:3001/history/n-transactions`,
          {
            headers: {
              Authorization: `Bearer ${token}`,
            },
          }
        );
        setNOfTransactions(res.data.msg);
      } catch (e) {
        console.log(e);
      }
    };
    getNOfTransactions();
  },[]);

  React.useEffect(() => {
    const getTransactions = async () => {
      try {
        const res = await axios.get(
          `http://localhost:3001/history?page=${page}&items=${itemsPerPage}`,
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
    console.log("page ", page);
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
        <TransactionsTable transactions={transactions} />
        <div style={{ display: "flex", justifyContent: "center" }}>
          <Pagination
            style={{
              position: "absolute",
              bottom: 10,
            }}
            count={itemsPerPage==0 ? 0 : Math.ceil(nOfTransactions / itemsPerPage)}
            onChange={handleChange}
          />
        </div>
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

const itemsContainer = {
  backgroundColor: "violet",
  marginLeft: "70px",
  marginRight: "70px",
};
