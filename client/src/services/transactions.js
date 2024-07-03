import { BACKEND_URL } from "../constants";
import axios from "axios";


export const convertCurrency = async (originAmount, originCurrency, destinyCurrency) => {
  const token = localStorage.getItem("token");

  return await axios.get(
    BACKEND_URL + `/api/transaction/convert?from=${originCurrency}&to=${destinyCurrency}&amount=${originAmount}`,
    {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    }
  );
};

export const getHistory = async (page, itemsPerPage) => {
  const token = localStorage.getItem("token");

  return await axios.get(
    BACKEND_URL + `/api/transaction/history?page=${page-1}&items=${itemsPerPage}`,
    {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    }
  );
};

export const getNOfTransactions = async () => {
  const token = localStorage.getItem("token");

  return await axios.get(BACKEND_URL + "/api/transaction/history-size", {
    headers: {
      Authorization: `Bearer ${token}`,
    },
  });
};


export const deposit = async (amount, currency) => {
  const token = localStorage.getItem("token");

  return await axios.post(
    BACKEND_URL + `/api/transaction/deposit`,
    {
      amount: amount,
      currency: currency,
    },
    {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    }
  );
};

export const withdraw = async (amount, currency) => {
  const token = localStorage.getItem("token");

  return await axios.post(
    BACKEND_URL + `/api/transaction/withdraw`,
    {
      amount: amount,
      currency: currency,
    },
    {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    }
  );
};

export const transfer = async (amount, currency, username) => {
  const token = localStorage.getItem("token");

  return await axios.post(
    BACKEND_URL + `/api/transaction/transfer`,
    {
      amount: amount,
      currency: currency,
      destinationUsername: username,
    },
    {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    }
  );
};

export const exchange = async (
  originCurrency,
  originAmount,
  destinyCurrency
) => {
  const token = localStorage.getItem("token");

  return await axios.post(
    BACKEND_URL + "/api/transaction/exchange",
    {
      originCurrency: originCurrency,
      originAmount: originAmount,
      destinationCurrency: destinyCurrency,
    },
    {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    }
  );
};
