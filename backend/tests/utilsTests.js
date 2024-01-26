import { jest } from "@jest/globals";

const db = {
  user: {
    existUser: jest.fn(),
    existUserById: jest.fn(),
    newUser: jest.fn(),
    getUser: jest.fn(),
    newLoginFailed: jest.fn(),
    newLoginFailed: jest.fn(),
    newLoginSuccess: jest.fn(),
    newBalance: jest.fn(),
    getBalance: jest.fn(),
    getAllUserBalance: jest.fn(),
  },
  exchange: {
    newExchange: jest.fn(),
  },
  transaction: {
    newDeposit: jest.fn(),
    newWithdraw: jest.fn(),
    newTransfer: jest.fn(),
    getHistoryPaginated: jest.fn(),
    getNOfTransactions: jest.fn(),
  },
};

export default db;
