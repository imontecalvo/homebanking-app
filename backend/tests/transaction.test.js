import request from "supertest";
import { jest } from "@jest/globals";
import App from "../src/app.js";
import db from "./dbMock.js";
import {
  balance,
  newDepositBody,
  newWithdrawBody,
  newTransferBody,
} from "./utilsTests.js";

const app = App(db);

describe("Deposito", () => {
  // Resetear mocks
  beforeEach(() => {
    for (const type in db) {
      for (const method in db[type]) {
        db[type][method].mockReset();
      }
    }
  });

  test("nuevo deposito - exito", async () => {
    db.user.existUserById.mockReturnValueOnce(true);
    db.user.getBalance.mockReturnValueOnce(balance);
    db.transaction.newDeposit.mockReturnValueOnce(null);

    const response = await request(app)
      .post("/transactions/deposit")
      .send(newDepositBody);

    expect(response.statusCode).toBe(201);
    expect(db.user.getBalance.mock.calls.length).toBe(1);
    expect(db.user.existUserById.mock.calls.length).toBe(1);
    expect(response.body.msg).toEqual("Deposit successful");
  });

  test("nuevo deposito - campo faltante", async () => {
    const body = { ...newDepositBody };
    delete body.currency;

    const response = await request(app)
      .post("/transactions/deposit")
      .send(body);

    expect(response.statusCode).toBe(400);
    expect(db.user.existUserById.mock.calls.length).toBe(0);
    expect(response.body.msg).toEqual("Missing fields");
  });

  test("nuevo deposito - usuario no existe", async () => {
    db.user.existUserById.mockReturnValueOnce(false);

    const response = await request(app)
      .post("/transactions/deposit")
      .send(newDepositBody);

    expect(response.statusCode).toBe(400);
    expect(db.user.existUserById.mock.calls.length).toBe(1);
    expect(response.body.msg).toEqual("User not found");
  });

  test("nuevo deposito - moneda no existe", async () => {
    db.user.existUserById.mockReturnValueOnce(true);
    db.user.getBalance.mockReturnValueOnce(null);

    const response = await request(app)
      .post("/transactions/deposit")
      .send(newDepositBody);

    expect(response.statusCode).toBe(400);
    expect(db.user.existUserById.mock.calls.length).toBe(1);
    expect(db.user.getBalance.mock.calls.length).toBe(1);
    expect(response.body.msg).toEqual("Invalid currency");
  });

  test("nuevo deposito - monto invalido", async () => {
    const body = { ...newDepositBody };
    body.amount = -1;

    const response = await request(app)
      .post("/transactions/deposit")
      .send(body);

    expect(response.statusCode).toBe(400);
    expect(db.user.existUserById.mock.calls.length).toBe(0);
    expect(response.body.msg).toEqual("Invalid amount");
  });

  test("nuevo deposito - error en la db", async () => {
    db.user.existUserById.mockReturnValueOnce(true);
    db.user.getBalance.mockReturnValueOnce(balance);
    db.transaction.newDeposit.mockRejectedValueOnce(null);

    const response = await request(app)
      .post("/transactions/deposit")
      .send(newDepositBody);

    expect(response.statusCode).toBe(500);
    expect(db.user.existUserById.mock.calls.length).toBe(1);
    expect(db.user.getBalance.mock.calls.length).toBe(1);
    expect(db.transaction.newDeposit.mock.calls.length).toBe(1);
    expect(response.body.error).toEqual("Server error");
  });
});

describe("Retiro", () => {
  beforeEach(() => {
    for (const type in db) {
      for (const method in db[type]) {
        db[type][method].mockReset();
      }
    }
  });

  test("retiro - exito", async () => {
    db.user.existUserById.mockReturnValueOnce(true);
    db.user.getBalance.mockReturnValueOnce(balance);
    db.transaction.newWithdraw.mockReturnValueOnce(null);

    const response = await request(app)
      .post("/transactions/withdraw")
      .send(newWithdrawBody);

    expect(response.statusCode).toBe(201);
    expect(db.user.getBalance.mock.calls.length).toBe(1);
    expect(db.user.existUserById.mock.calls.length).toBe(1);
    expect(db.transaction.newWithdraw.mock.calls.length).toBe(1);
    expect(response.body.msg).toEqual("Withdraw successful");
  });

  test("retiro - campo faltante", async () => {
    const body = { ...newWithdrawBody };
    delete body.currency;

    const response = await request(app)
      .post("/transactions/withdraw")
      .send(body);

    expect(response.statusCode).toBe(400);
    expect(db.user.existUserById.mock.calls.length).toBe(0);
    expect(response.body.msg).toEqual("Missing fields");
  });

  test("retiro - usuario no existe", async () => {
    db.user.existUserById.mockReturnValueOnce(false);

    const response = await request(app)
      .post("/transactions/withdraw")
      .send(newWithdrawBody);

    expect(response.statusCode).toBe(400);
    expect(db.user.existUserById.mock.calls.length).toBe(1);
    expect(response.body.msg).toEqual("User not found");
  });

  test("retiro - moneda no existe", async () => {
    db.user.existUserById.mockReturnValueOnce(true);
    db.user.getBalance.mockReturnValueOnce(null);

    const response = await request(app)
      .post("/transactions/withdraw")
      .send(newWithdrawBody);

    expect(response.statusCode).toBe(400);
    expect(db.user.existUserById.mock.calls.length).toBe(1);
    expect(db.user.getBalance.mock.calls.length).toBe(1);
    expect(response.body.msg).toEqual("Invalid currency");
  });

  test("retiro - monto invalido", async () => {
    const body = { ...newWithdrawBody };
    body.amount = -1;

    const response = await request(app)
      .post("/transactions/withdraw")
      .send(body);

    expect(response.statusCode).toBe(400);
    expect(db.user.existUserById.mock.calls.length).toBe(0);
    expect(response.body.msg).toEqual("Invalid amount");
  });

  test("retiro - fondos insuficientes", async () => {
    const body = { ...newWithdrawBody };
    body.amount = balance.amount + 1;
    db.user.existUserById.mockReturnValueOnce(true);
    db.user.getBalance.mockReturnValueOnce(balance);

    const response = await request(app)
      .post("/transactions/withdraw")
      .send(body);

    expect(response.statusCode).toBe(400);
    expect(db.user.existUserById.mock.calls.length).toBe(1);
    expect(db.user.getBalance.mock.calls.length).toBe(1);
    expect(response.body.msg).toEqual("Insufficient funds");
  });

  test("retiro - error en la db", async () => {
    db.user.existUserById.mockReturnValueOnce(true);
    db.user.getBalance.mockReturnValueOnce(balance);
    db.transaction.newWithdraw.mockRejectedValueOnce(null);

    const response = await request(app)
      .post("/transactions/withdraw")
      .send(newWithdrawBody);

    expect(response.statusCode).toBe(500);
    expect(db.user.existUserById.mock.calls.length).toBe(1);
    expect(db.user.getBalance.mock.calls.length).toBe(1);
    expect(db.transaction.newWithdraw.mock.calls.length).toBe(1);
    expect(response.body.error).toEqual("Server error");
  });
});

describe("Transferencia, ", () => {
  beforeEach(() => {
    for (const type in db) {
      for (const method in db[type]) {
        db[type][method].mockReset();
      }
    }
  });

  test("transferencia - exito", async () => {
    db.user.existUserById.mockReturnValueOnce(true);
    db.user.getUser.mockReturnValueOnce({ user_id: 2 });
    db.user.getBalance.mockReturnValueOnce(balance);
    db.transaction.newTransfer.mockReturnValueOnce(null);

    const response = await request(app)
      .post("/transactions/transfer")
      .send(newTransferBody);

    expect(response.statusCode).toBe(201);
    expect(db.user.existUserById.mock.calls.length).toBe(1);
    expect(db.user.getUser.mock.calls.length).toBe(1);
    expect(db.user.getBalance.mock.calls.length).toBe(1);
    expect(db.transaction.newTransfer.mock.calls.length).toBe(1);
    expect(response.body.msg).toEqual("Transfer successful");
  });

  test("transferencia - campo faltante", async () => {
    const body = { ...newTransferBody };
    delete body.currency;

    const response = await request(app)
      .post("/transactions/transfer")
      .send(body);

    expect(response.statusCode).toBe(400);
    expect(db.user.existUserById.mock.calls.length).toBe(0);
    expect(response.body.msg).toEqual("Missing fields");
  });

  test("transferencia - usuario no existe", async () => {
    db.user.existUserById.mockReturnValueOnce(false);

    const response = await request(app)
      .post("/transactions/transfer")
      .send(newTransferBody);

    expect(response.statusCode).toBe(400);
    expect(db.user.existUserById.mock.calls.length).toBe(1);
    expect(response.body.msg).toEqual("User not found");
  });

  test("transferencia - usuario destino no existe", async () => {
    db.user.existUserById.mockReturnValueOnce(true);
    db.user.getUser.mockReturnValueOnce(null);

    const response = await request(app)
      .post("/transactions/transfer")
      .send(newTransferBody);

    expect(response.statusCode).toBe(400);
    expect(db.user.existUserById.mock.calls.length).toBe(1);
    expect(db.user.getUser.mock.calls.length).toBe(1);
    expect(response.body.msg).toEqual("Destination user not found");
  });

  test("transferencia - moneda no existe", async () => {
    db.user.existUserById.mockReturnValueOnce(true);
    db.user.getUser.mockReturnValueOnce({ user_id: 2 });
    db.user.getBalance.mockReturnValueOnce(null);

    const response = await request(app)
      .post("/transactions/transfer")
      .send(newTransferBody);

    expect(response.statusCode).toBe(400);
    expect(db.user.existUserById.mock.calls.length).toBe(1);
    expect(db.user.getUser.mock.calls.length).toBe(1);
    expect(db.user.getBalance.mock.calls.length).toBe(1);
    expect(response.body.msg).toEqual("Invalid currency");
  });

  test("transferencia - monto invalido", async () => {
    const body = { ...newTransferBody };
    body.amount = -1;

    const response = await request(app)
      .post("/transactions/transfer")
      .send(body);

    expect(response.statusCode).toBe(400);
    expect(db.user.existUserById.mock.calls.length).toBe(0);
    expect(response.body.msg).toEqual("Invalid amount");
  });

  test("transferencia - fondos insuficientes", async () => {
    const body = { ...newTransferBody };
    body.amount = balance.amount + 1;
    db.user.existUserById.mockReturnValueOnce(true);
    db.user.getUser.mockReturnValueOnce({ user_id: 2 });
    db.user.getBalance.mockReturnValueOnce(balance);

    const response = await request(app)
      .post("/transactions/transfer")
      .send(body);

    expect(response.statusCode).toBe(400);
    expect(db.user.existUserById.mock.calls.length).toBe(1);
    expect(db.user.getUser.mock.calls.length).toBe(1);
    expect(db.user.getBalance.mock.calls.length).toBe(1);
    expect(db.transaction.newTransfer.mock.calls.length).toBe(0);
    expect(response.body.msg).toEqual("Insufficient funds");
  });

  test("transferencia - error en la db", async () => {
    db.user.existUserById.mockReturnValueOnce(true);
    db.user.getUser.mockReturnValueOnce({ user_id: 2 });
    db.user.getBalance.mockReturnValueOnce(balance);
    db.transaction.newTransfer.mockRejectedValueOnce(null);

    const response = await request(app)
      .post("/transactions/transfer")
      .send(newTransferBody);

    expect(response.statusCode).toBe(500);
    expect(db.user.existUserById.mock.calls.length).toBe(1);
    expect(db.user.getUser.mock.calls.length).toBe(1);
    expect(db.user.getBalance.mock.calls.length).toBe(1);
    expect(db.transaction.newTransfer.mock.calls.length).toBe(1);
    expect(response.body.error).toEqual("Server error");
  });
});
