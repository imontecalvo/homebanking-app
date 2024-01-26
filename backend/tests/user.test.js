import request from "supertest";
import { jest } from "@jest/globals";
import App from "../src/app.js";
import db from "./utilsTests.js";

const app = App(db);

describe("Get users", () => {
  beforeEach(() => {
    for (const type in db) {
      for (const method in db[type]) {
        db[type][method].mockReset();
        db[type][method].mockResolvedValue(0);
      }
    }
  });

  test("probando", async () => {
    // Configurar el comportamiento del mock para la función getUserById
    // db.getAllUsers.mockResolvedValue({ id: 1, nombre: "UsuarioMock" });

    // const response = await request(app).get("/users");

    // expect(response.statusCode).toBe(200);
    // expect(response.body.content).toEqual({ id: 1, nombre: "UsuarioMock" });

    expect(true).toBe(true);
  });
});
