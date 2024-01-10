import React, { useEffect, useRef } from "react";
// import { makeStyles } from "@material-ui/core/styles";
import {
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Paper,
} from "@mui/material";
import { EmojiSymbols } from "@mui/icons-material";

const ROW_HEIGHT = 0;

// const useStyles = makeStyles({
//   customTableRow: {
//     height: "50px",
//   },
// });

const TransactionsTable = ({ transactions }) => {
  const symbols = {
    ARS: ["$"],
    CLP: ["$"],
    EUR: ["€"],
    TRY: ["₺"],
    USD: ["$"],
    GBP: ["£"],
  };

  return (
    <TableContainer component={Paper}>
      <Table>
        <TableHead>
          <TableRow style={{ backgroundColor: "black" }}>
            <TableCell style={{ color: "white" }}>Type</TableCell>
            <TableCell style={{ color: "white" }}>Amount</TableCell>
            <TableCell style={{ color: "white" }}>Date</TableCell>
          </TableRow>
        </TableHead>
        <TableBody>
          {transactions.map((transaction, index) => (
            <TableRow key={index}>
              <TableCell>{transaction.type}</TableCell>
              <TableCell style={{ ...amountStyle(transaction.amount) }}>
                {transaction.amount > 0 ? "+ " : "- "}
                {symbols[transaction.currency]}
                {transaction.amount > 0
                  ? transaction.amount
                  : -1 * transaction.amount}{" "}
                ({transaction.currency})
              </TableCell>
              <TableCell>
                {transaction.date.getDate()}/{transaction.date.getMonth()+1}/
                {transaction.date.getFullYear()}{" - "}{transaction.date.getHours()}
                {":"}
                {transaction.date.getMinutes()}
                {" hs"}
              </TableCell>
            </TableRow>
          ))}
        </TableBody>
      </Table>
    </TableContainer>
  );
};

export default TransactionsTable;

const amountStyle = (amount) => {
  return {
    color: amount > 0 ? "green" : "red",
  };
};
