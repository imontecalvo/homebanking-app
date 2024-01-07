import * as React from "react";
import NavBar from "../../components/navbar/NavBar";
import TransactionTypeCard from "../../components/transaction_type_card/TransactionTypeCard";
import Modal from "@mui/material/Modal";
import Dialog from "@mui/material/Dialog";
import DialogTitle from "@mui/material/DialogTitle";
import transactions_style from "./transactions_style.css";

import {
  DepositContent,
  WithdrawContent,
  TransferContent,
} from "./DialogContent";


import Snackbar from "@mui/material/Snackbar";
import MuiAlert from "@mui/material/Alert";
const Alert = React.forwardRef(function Alert(props, ref) {
  return <MuiAlert elevation={6} ref={ref} variant="filled" {...props} />;
});

const TransactionsPage = () => {
  //Modales
  const [open, setOpen] = React.useState(false);
  const [activeModal, setActiveModal] = React.useState(null);

  const handleClose = () => setOpen(false);

  //Snackbar
  const [openSnackBar, setOpenSnackBar] = React.useState(false);
  const [message, setMessage] = React.useState("");
  const [severity, setSeverity] = React.useState("success");

  const handleCloseSnackBar = (e,reason) => {
    if (reason === 'clickaway') {
      return;
    }
    setOpenSnackBar(false);
  };

  const showSnackBar = (message, severity) => {
    setMessage(message);
    setSeverity(severity);
    setOpenSnackBar(true);
  }


  return (
    <div style={{ alignItems: "center" }}>
      <NavBar active="Transactions" />
      <div style={{ marginTop: 30, width: "100%" }}>
        <div style={divCardContainer}>
          <TransactionTypeCard
            transaction="DEPOSIT"
            handleClick={() => {
              setOpen(true);
              setActiveModal("Deposit");
            }}
          />
          <TransactionTypeCard
            transaction="WITHDRAW"
            handleClick={() => {
              setOpen(true);
              setActiveModal("Withdraw");
            }}
          />
          <TransactionTypeCard
            transaction="TRANSFER"
            handleClick={() => {
              setOpen(true);
              setActiveModal("Transfer");
            }}
          />
          <Dialog
            open={open}
            onClose={handleClose}
            style={{ borderRadius: 50 }}
          >
            <DialogTitle>{activeModal + " funds"}</DialogTitle>

            <div style={modalStyle}>
              {activeModal === "Deposit" ? (
                <DepositContent onClose={handleClose} showSnackBar={showSnackBar} />
              ) : activeModal === "Withdraw" ? (
                <WithdrawContent onClose={handleClose} showSnackBar={showSnackBar}/>
              ) : activeModal === "Transfer" ? (
                <TransferContent onClose={handleClose} showSnackBar={showSnackBar}/>
              ) : (
                <></>
              )}
            </div>
          </Dialog>
        </div>
      </div>
      <Snackbar open={openSnackBar} autoHideDuration={5000} onClose={handleCloseSnackBar}>
        <Alert onClose={handleCloseSnackBar} severity={severity} sx={{ width: "100%" }}>
          {message}
        </Alert>
      </Snackbar>
    </div>
  );
};

export default TransactionsPage;

const modalStyle = {
  width: 500,
  height: "250px",
};

const divCardContainer = {
  display: "flex",
  gap: "50px",
  flexWrap: "wrap",
  marginLeft: 70,
  marginRight: 70,
  marginTop: 150,
  justifyContent: "center",
};
