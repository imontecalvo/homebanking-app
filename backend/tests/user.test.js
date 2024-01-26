import request from "supertest";
import { jest } from "@jest/globals";
import App from "../src/app.js";
import db from "./utilsTests.js";
import bcrypt from "bcrypt";

const app = App(db);

const currencies = ["USD", "CLP", "ARS", "GBP", "TRY", "EUR"];

const successNewUserBody = {
  username: "usuarioMock",
  password: "passwordMock",
  confirmPassword: "passwordMock",
  currency: "USD",
};
const newUser = {
  user_id: 1,
  username: "usuarioMock",
  password: "passwordMock",
};

const successLoginBody = {
  username: "usuarioMock",
  password: "passwordMock",
};

const getUser = {
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

const balance = {
  user_id: 1,
  user_currency: "USD",
  balance: 1000,
};

const AllUserBalance = [balance];

describe("Register", () => {
  // Resetear mocks
  beforeEach(() => {
    for (const type in db) {
      for (const method in db[type]) {
        db[type][method].mockReset();
      }
    }
  });

  test("nuevo usuario - exito", async () => {
    db.user.existUser.mockResolvedValue(false);
    db.user.newUser.mockResolvedValue(newUser);

    const response = await request(app)
      .post("/users/register")
      .send(successNewUserBody);

    expect(response.statusCode).toBe(201);
    expect(db.user.newBalance.mock.calls.length).toBe(currencies.length);
    expect(db.user.existUser.mock.calls.length).toBe(1);
    expect(db.user.newUser.mock.calls.length).toBe(1);
    expect(response.body.msg).toEqual(newUser);
  });

  test("nuevo usuario - campo faltante", async () => {
    const body = { ...successNewUserBody };
    delete body.username;

    const response = await request(app).post("/users/register").send(body);

    expect(response.statusCode).toBe(400);
    expect(db.user.existUser.mock.calls.length).toBe(0);
    expect(db.user.newUser.mock.calls.length).toBe(0);
    expect(response.body.msg).toEqual("Missing fields");
  });

  test("nuevo usuario - password no coincide", async () => {
    const body = { ...successNewUserBody };
    body.confirmPassword = "otroPassword";

    const response = await request(app).post("/users/register").send(body);

    expect(response.statusCode).toBe(400);
    expect(db.user.existUser.mock.calls.length).toBe(0);
    expect(db.user.newUser.mock.calls.length).toBe(0);
    expect(response.body.msg).toEqual("Passwords don't match");
  });

  test("nuevo usuario - usuario ya existe", async () => {
    db.user.existUser.mockResolvedValue(true);

    const response = await request(app)
      .post("/users/register")
      .send(successNewUserBody);

    expect(response.statusCode).toBe(400);
    expect(db.user.existUser.mock.calls.length).toBe(1);
    expect(db.user.newUser.mock.calls.length).toBe(0);
    expect(response.body.msg).toEqual("Username already taken");
  });

  test("nuevo usuario - error de servidor", async () => {
    db.user.existUser.mockRejectedValue(new Error("Error de servidor"));

    const response = await request(app)
      .post("/users/register")
      .send(successNewUserBody);

    expect(response.statusCode).toBe(500);
    expect(db.user.existUser.mock.calls.length).toBe(1);
    expect(db.user.newUser.mock.calls.length).toBe(0);
    expect(response.body.error).toEqual("Server error");
  });
});

describe("Login", () => {
  // Resetear mocks
  beforeEach(() => {
    for (const type in db) {
      for (const method in db[type]) {
        db[type][method].mockReset();
      }
    }
  });

  test("login - exito", async () => {
    db.user.getUser.mockResolvedValue(getUser);

    const response = await request(app)
      .post("/users/login")
      .send(successLoginBody);

    expect(response.statusCode).toBe(200);
    expect(db.user.getUser.mock.calls.length).toBe(1);
    expect(db.user.newLoginSuccess.mock.calls.length).toBe(1);
    expect(response.body.msg.username).toEqual(getUser.username);
    expect(response.body.msg).toHaveProperty("token");
  });

  test("login - campo faltante", async () => {
    const body = { ...successLoginBody };
    delete body.username;

    const response = await request(app).post("/users/login").send(body);

    expect(response.statusCode).toBe(400);
    expect(db.user.getUser.mock.calls.length).toBe(0);
    expect(response.body.msg).toEqual("Missing fields");
  });

  test("login - usuario no existe", async () => {
    db.user.getUser.mockResolvedValue(null);

    const response = await request(app)
      .post("/users/login")
      .send(successNewUserBody);

    expect(response.statusCode).toBe(400);
    expect(db.user.getUser.mock.calls.length).toBe(1);
    expect(response.body.msg).toEqual("User not found");
  });

  test("login - usuario bloqueado", async () => {
    const user = { ...getUser };
    user.login_failed = 3;
    db.user.getUser.mockResolvedValue(user);

    const response = await request(app)
      .post("/users/login")
      .send(successNewUserBody);

    expect(response.statusCode).toBe(401);
    expect(db.user.getUser.mock.calls.length).toBe(1);
    expect(response.body.msg).toEqual("Account blocked, contact support");
  });

  test("login - contraseña incorrecta", async () => {
    const user = { ...getUser, login_failed: 1 };
    db.user.getUser.mockResolvedValue(user);

    const response = await request(app)
      .post("/users/login")
      .send({ ...successNewUserBody, password: "otraPassword" });

    expect(response.statusCode).toBe(401);
    expect(db.user.getUser.mock.calls.length).toBe(1);
    expect(db.user.newLoginFailed.mock.calls.length).toBe(1);
    expect(response.body.msg).toEqual("Wrong password");
  });

  test("login - contraseña incorrecta + bloqueo", async () => {
    const user = { ...getUser, login_failed: 2 };
    db.user.getUser.mockResolvedValue(user);

    const response = await request(app)
      .post("/users/login")
      .send({ ...successNewUserBody, password: "otraPassword" });

    expect(response.statusCode).toBe(401);
    expect(db.user.getUser.mock.calls.length).toBe(1);
    expect(db.user.newLoginFailed.mock.calls.length).toBe(1);
    expect(response.body.msg).toEqual("Wrong password, account blocked");
  });

  test("login - error de servidor", async () => {
    db.user.getUser.mockRejectedValue(new Error("Error de servidor"));

    const response = await request(app)
      .post("/users/login")
      .send(successNewUserBody);

    expect(response.statusCode).toBe(500);
    expect(db.user.getUser.mock.calls.length).toBe(1);
    expect(response.body.error).toEqual("Server error");
  });
});

describe("Balance", () => {
  // Resetear mocks
  beforeEach(() => {
    for (const type in db) {
      for (const method in db[type]) {
        db[type][method].mockReset();
      }
    }
  });

  test("balance - exito", async () => {
    db.user.existUserById.mockResolvedValue(true);
    db.user.getAllUserBalance.mockResolvedValue(AllUserBalance);

    const response = await request(app).get("/users/balance");

    expect(response.statusCode).toBe(200);
    expect(db.user.getAllUserBalance.mock.calls.length).toBe(1);
    expect(db.user.existUserById.mock.calls.length).toBe(1);
    expect(response.body.msg).toEqual(AllUserBalance);
  });

  test("balance - usuario no existe", async () => {
    db.user.existUserById.mockResolvedValue(false);

    const response = await request(app).get("/users/balance");

    expect(response.statusCode).toBe(400);
    expect(db.user.existUserById.mock.calls.length).toBe(1);
    expect(db.user.getAllUserBalance.mock.calls.length).toBe(0);
    expect(response.body.msg).toEqual("User not found");
  });

  test("balance - error de servidor", async () => {
    db.user.getAllUserBalance.mockRejectedValue(new Error("Error de servidor"));
    db.user.existUserById.mockResolvedValue(true);

    const response = await request(app).get("/users/balance");

    expect(response.statusCode).toBe(500);
    expect(db.user.getAllUserBalance.mock.calls.length).toBe(1);
    expect(response.body.error).toEqual("Server error");
  });
});
