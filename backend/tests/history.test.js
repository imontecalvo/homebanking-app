import request from "supertest";
import { jest } from "@jest/globals";
import App from "../src/app.js";
import db from "./dbMock.js";
import {} from "./utilsTests.js";

const app = App(db);

describe("History", () => {
  beforeEach(() => {
    for (const type in db) {
      for (const method in db[type]) {
        db[type][method].mockReset();
      }
    }
  });

  test("historial - exito", async () => {
    db.user.existUserById.mockResolvedValue(true);
    db.transaction.getHistoryPaginated.mockResolvedValue([]);

    const response = await request(app).get("/history?page=1&items=10");

    expect(response.statusCode).toBe(200);
    expect(db.user.existUserById.mock.calls.length).toBe(1);
    expect(db.transaction.getHistoryPaginated.mock.calls.length).toBe(1);
    expect(response.body.msg).toEqual([]);
  });

  test("historial - usuario no existe", async () => {
    db.user.existUserById.mockResolvedValue(false);

    const response = await request(app).get("/history?page=1&items=10");

    expect(response.statusCode).toBe(400);
    expect(db.user.existUserById.mock.calls.length).toBe(1);
    expect(db.transaction.getHistoryPaginated.mock.calls.length).toBe(0);
    expect(response.body.msg).toEqual("User not found");
  });

  test("historial - parametros invalidos (numero invalido)", async () => {
    const response = await request(app).get("/history?page=0&items=0");

    expect(response.statusCode).toBe(400);
    expect(db.user.existUserById.mock.calls.length).toBe(0);
    expect(db.transaction.getHistoryPaginated.mock.calls.length).toBe(0);
    expect(response.body.msg).toEqual("Invalid fields");
  });

  test("historial - parametros invalidos (texto)", async () => {
    const response = await request(app).get("/history?page=a&items=a");

    expect(response.statusCode).toBe(400);
    expect(db.user.existUserById.mock.calls.length).toBe(0);
    expect(db.transaction.getHistoryPaginated.mock.calls.length).toBe(0);
    expect(response.body.msg).toEqual("Invalid fields");
  });

  test("historial - error de servidor", async () => {
    db.transaction.getHistoryPaginated.mockRejectedValue(
      new Error("Error de servidor")
    );
    db.user.existUserById.mockResolvedValue(true);

    const response = await request(app).get("/history?page=1&items=10");

    expect(response.statusCode).toBe(500);
    expect(db.user.existUserById.mock.calls.length).toBe(1);
    expect(db.transaction.getHistoryPaginated.mock.calls.length).toBe(1);
    expect(response.body.error).toEqual("Server error");
  });
});
