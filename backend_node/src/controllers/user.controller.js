import { createAccessToken } from "../jwt.js";
import bcrypt from "bcrypt";

// Crea el balance inicial del usuario
// El balance inicial de la moneda del usuario es 2000, el resto es 0
const initialize_balance = async (user_id, user_currency, db) => {
  const currencies = ["USD", "CLP", "ARS", "GBP", "TRY", "EUR"];
  const INIT_BALANCE = 2000;

  currencies.forEach(async (currency) => {
    const balance = currency == user_currency ? INIT_BALANCE : 0;
    await db.user.newBalance(user_id, currency, balance);
  });
};

//En caso de que los parametros sean correctos, crea un usuario y su balance inicial
export const createUser = async (req, res) => {
  const { username, password, confirmPassword, currency } = req.body;

  //Chequear parametros
  if (!username || !password || !confirmPassword || !currency) {
    return res.status(400).json({ msg: "Missing fields", ok: false });
  }
  if (password !== confirmPassword) {
    return res.status(400).json({ msg: "Passwords don't match", ok: false });
  }

  const db = req.app.db;

  try {
    //Chequear si existe usuario
    if (await db.user.existUser(username)) {
      return res.status(400).json({ msg: "Username already taken", ok: false });
    }

    //Crear usuario
    const user_password = bcrypt.hashSync(password, 10);
    const user = await db.user.newUser(username, user_password);
    initialize_balance(user.user_id, currency, db);
    return res.status(201).json({ msg: user, ok: true });
  } catch (error) {
    console.log(error);
    return res.status(500).json({ error: "Server error", ok: false });
  }
};

//En caso de que los parametros sean correctos, loguea al usuario
export const loginUser = async (req, res) => {
  const { username, password } = req.body;

  //Chequear parametros
  if (!username || !password) {
    return res.status(400).json({ msg: "Missing fields", ok: false });
  }

  const db = req.app.db;

  try {
    //Chequear si existe usuario
    const user = await db.user.getUser(username);
    if (!user) {
      return res.status(400).json({ msg: "User not found", ok: false });
    }

    //Chequear si la cuenta esta bloqueada
    if (user.login_failed == 3) {
      return res
        .status(401)
        .json({ msg: "Account blocked, contact support", ok: false });
    }

    //Chequear si la contraseña es correcta
    const pwdMatching = bcrypt.compareSync(password, user.password);
    if (!pwdMatching) {
      await db.user.newLoginFailed(user.user_id);
      //Si ya tenia previamente 2 intentos fallidos, se bloquea la cuenta al ser este el tercero
      if (user.login_failed == 2) {
        return res
          .status(401)
          .json({ msg: "Wrong password, account blocked", ok: false });
      }
      //Si no, se devuelve un mensaje de error
      return res.status(401).json({ msg: "Wrong password", ok: false });
    }

    //Si la contraseña es correcta, se resetea el contador de intentos fallidos
    await db.user.newLoginSuccess(user.user_id);

    //Se crea el token y se devuelve
    const token = await createAccessToken({
      user_id: user.user_id,
      username: user.username,
    });
    return res.status(200).json({
      msg: { token, username },
      ok: true,
    });
  } catch (error) {
    console.log(error);
    return res.status(500).json({ error: "Server error", ok: false });
  }
};

//Se obtienen todos los balances de las distintas monedas del usuario
export const getUserBalance = async (req, res) => {
  const { user_id } = req.user;

  const db = req.app.db;

  try {
    if (!await db.user.existUserById(user_id)) {
      return res.status(400).json({ msg: "User not found", ok: false });
    }

    const balance = await db.user.getAllUserBalance(user_id);

    return res.status(200).json({ msg: balance, ok: true });
  } catch (error) {
    console.log(error);
    return res.status(500).json({ error: "Server error", ok: false });
  }
};
