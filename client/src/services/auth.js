import { BACKEND_URL } from "../constants";
import axios from "axios";

export const login = async (username, password) => {
  console.log(BACKEND_URL + "/api/user/login");
  const response = await axios.post(BACKEND_URL + "/api/user/login", {
    username: username,
    password: password,
  });

  return response;
};

export const register = async (username, password, confirmPassword, currency) => {
  const response = await axios.post(BACKEND_URL + "/api/user/register", {
    username: username,
    password: password,
    confirmPassword: confirmPassword,
    currency: currency,
    roles: ["USER"],
  });

  console.log(response)

  return response;
};
