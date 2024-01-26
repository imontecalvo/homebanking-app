import bcrypt from "bcrypt";

export const currencies = ["USD", "CLP", "ARS", "GBP", "TRY", "EUR"];

export const successNewUserBody = {
  username: "usuarioMock",
  password: "passwordMock",
  confirmPassword: "passwordMock",
  currency: "USD",
};
export const newUser = {
  user_id: 1,
  username: "usuarioMock",
  password: "passwordMock",
};

export const successLoginBody = {
  username: "usuarioMock",
  password: "passwordMock",
};

export const getUser = {
  user_id: 1,
  username: "usuarioMock",
  password: bcrypt.hashSync("passwordMock", 10),
  login_failed: 0,
  user_currency: "USD",
};

export const loggedUser = {
  user_id: 1,
  username: "usuarioMock",
};

export const balance = {
  currency: "USD",
  amount: 1000,
};

export const allUserBalances = [balance];

export const newDepositBody = {
  currency: "USD",
  amount: 100,
};

export const newWithdrawBody = {
  currency: "USD",
  amount: 100,
};

export const newTransferBody = {
  currency: "USD",
  amount: 100,
  destUsername: "usuarioMock2",
};
