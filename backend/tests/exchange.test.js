import request from "supertest";
import { jest } from "@jest/globals";
import App from "../src/app.js";
import db from "./dbMock.js";
import { successNewExchangeBody, balance } from "./utilsTests.js";

const app = App(db);

describe("Exchange", () => {
  beforeEach(() => {
    for (const type in db) {
      for (const method in db[type]) {
        db[type][method].mockReset();
      }
    }
  });

  test("nuevo exchange - exito", async () => {
    db.user.existUserById.mockResolvedValue(true);
    db.user.getBalance.mockResolvedValue(balance);
    db.exchange.newExchange.mockResolvedValue();

    const response = await request(app)
      .post("/exchange")
      .send(successNewExchangeBody);

    expect(response.statusCode).toBe(201);
    expect(db.user.existUserById.mock.calls.length).toBe(1);
    expect(db.user.getBalance.mock.calls.length).toBe(1);
    expect(db.exchange.newExchange.mock.calls.length).toBe(1);
    expect(response.body.msg).toEqual("Withdraw successful");
  });

  test("nuevo exchange - campo faltante", async () => {
    const body = { ...successNewExchangeBody };
    delete body.origin_currency;

    const response = await request(app).post("/exchange").send(body);

    expect(response.statusCode).toBe(400);
    expect(db.user.existUserById.mock.calls.length).toBe(0);
    expect(db.user.getBalance.mock.calls.length).toBe(0);
    expect(db.exchange.newExchange.mock.calls.length).toBe(0);
    expect(response.body.msg).toEqual("Missing fields");
  });

    test("nuevo exchange - usuario no existe", async () => {
        db.user.existUserById.mockResolvedValue(false);
    
        const response = await request(app)
        .post("/exchange")
        .send(successNewExchangeBody);
    
        expect(response.statusCode).toBe(400);
        expect(db.user.existUserById.mock.calls.length).toBe(1);
        expect(db.user.getBalance.mock.calls.length).toBe(0);
        expect(db.exchange.newExchange.mock.calls.length).toBe(0);
        expect(response.body.msg).toEqual("User not found");
    });

    test("nuevo exchange - moneda invalida", async () => {
        db.user.existUserById.mockResolvedValue(true);
        db.user.getBalance.mockResolvedValue(null);

        const response = await request(app).post("/exchange").send(successNewExchangeBody);

        expect(response.statusCode).toBe(400);
        expect(db.user.existUserById.mock.calls.length).toBe(1);
        expect(db.user.getBalance.mock.calls.length).toBe(1);
        expect(db.exchange.newExchange.mock.calls.length).toBe(0);
        expect(response.body.msg).toEqual("Invalid currency");
    });

    test("nuevo exchange - monto invalido", async () => {
        db.user.existUserById.mockResolvedValue(true);
        db.user.getBalance.mockResolvedValue(balance);
    
        const body = { ...successNewExchangeBody };
        body.origin_amount = -1;
    
        const response = await request(app).post("/exchange").send(body);
    
        expect(response.statusCode).toBe(400);
        expect(db.user.existUserById.mock.calls.length).toBe(0);
        expect(db.user.getBalance.mock.calls.length).toBe(0);
        expect(db.exchange.newExchange.mock.calls.length).toBe(0);
        expect(response.body.msg).toEqual("Invalid amount");
    });

    test("nuevo exchange - fondos insuficientes", async () => {
        db.user.existUserById.mockResolvedValue(true);
        db.user.getBalance.mockResolvedValue(balance);
    
        const body = { ...successNewExchangeBody };
        body.origin_amount = balance.amount + 1;
    
        const response = await request(app).post("/exchange").send(body);
    
        expect(response.statusCode).toBe(400);
        expect(db.user.existUserById.mock.calls.length).toBe(1);
        expect(db.user.getBalance.mock.calls.length).toBe(1);
        expect(db.exchange.newExchange.mock.calls.length).toBe(0);
        expect(response.body.msg).toEqual("Insufficient funds");
    });

    test("nuevo exchange - servidor falla", async () => {
        db.user.existUserById.mockRejectedValue(new Error("Server error"));
    
        const response = await request(app)
        .post("/exchange")
        .send(successNewExchangeBody);
    
        expect(response.statusCode).toBe(500);
        expect(db.user.existUserById.mock.calls.length).toBe(1);
        expect(db.user.getBalance.mock.calls.length).toBe(0);
        expect(db.exchange.newExchange.mock.calls.length).toBe(0);
        expect(response.body.error).toEqual("Server error");
    });



});
