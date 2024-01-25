import User from "../models/User.js";
import Balance from "../models/Balance.js";
import { createAccessToken } from "../jwt.js";
import bcrypt from "bcrypt";

// Crea el balance inicial del usuario
// El balance inicial de la moneda del usuario es 2000, el resto es 0
const initialize_balance = async (user_id, user_currency) => {
  const currencies = ["USD", "CLP", "ARS", "GBP", "TRY", "EUR"];
  const INIT_BALANCE = 2000;

  currencies.forEach(async (currency) => {
    const balance = currency == user_currency ? INIT_BALANCE : 0;
    await Balance.create({ user_id, currency, amount: balance });
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

  try {
    //Chequear si existe usuario
    if (await User.findOne({ where: { username } })) {
      return res.status(400).json({ msg: "Username already taken", ok: false });
    }

    //Crear usuario
    const user_password = bcrypt.hashSync(password, 10);
    const user = await User.create({ username, password:user_password });
    initialize_balance(user.user_id, currency);
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

  try {
    //Chequear si existe usuario
    const user = await User.findOne({ where: { username } });
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
    if (!pwdMatching){
      await User.update(
        { login_failed: user.login_failed + 1 },
        { where: { username } }
      );
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
    await User.update({ login_failed: 0 }, { where: { username } });

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

  try {
    const user = await User.findOne({ where: { user_id } });
    if (!user) {
      return res.status(400).json({ msg: "User not found", ok: false });
    }

    const balance = await Balance.findAll({
      where: { user_id },
      attributes: ["currency", "amount"],
    });
    return res.status(200).json({ msg: balance, ok: true });
  } catch (error) {
    console.log(error);
    return res.status(500).json({ error: "Server error", ok: false });
  }
};
