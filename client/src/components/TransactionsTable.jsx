import {
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Paper,
} from "@mui/material";

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
              <TableCell
                style={{ ...amountStyle(transaction.amount, transaction.type) }}
              >
                {transaction.amount > 0 && transaction.type !== "Withdraw"
                  ? "+ "
                  : "- "}
                {symbols[transaction.currency]}
                {transaction.amount > 0
                  ? transaction.amount
                  : -1 * transaction.amount}{" "}
                ({transaction.currency})
              </TableCell>
              <TableCell>
                {transaction.date.getDate().toString().padStart(2, "0")}/
                {(transaction.date.getMonth() + 1).toString().padStart(2, "0")}/
                {transaction.date.getFullYear()}
                {" - "}
                {transaction.date.getHours().toString().padStart(2, "0")}
                {":"}
                {transaction.date.getMinutes().toString().padStart(2, "0")}
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

const amountStyle = (amount, type) => {
  return {
    color: amount > 0 && type !== "Withdraw" ? "green" : "red",
  };
};
