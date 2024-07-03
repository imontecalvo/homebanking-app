import { BACKEND_URL } from "../constants";
import axios from "axios";

export const getUserBalances = async () => {
  const token = localStorage.getItem("token");

  return await axios.get(BACKEND_URL + `/api/balance`, {
    headers: {
      Authorization: `Bearer ${token}`,
    },
  });
};
