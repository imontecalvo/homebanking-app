import User from "../models/User.js";
import Balance from "../models/Balance.js";

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
    const user = await User.create({ username, password });
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

    //Chequear si la contraseña es correcta
    if (password !== user.password) {
      return res.status(400).json({ msg: "Wrong password", ok: false });
    }

    return res
      .status(200)
      .json({
        msg: { username: user.username, user_id: user.user_id },
        ok: true,
      });
  } catch (error) {
    console.log(error);
    return res.status(500).json({ error: "Server error", ok: false });
  }
};

export const getUserBalance = async (req, res) => {
  const { id } = req.params;

  try {
    const user = await User.findOne({ where: { user_id: id } });
    if (!user) {
      return res.status(400).json({ msg: "User not found", ok: false });
    }

    const balance = await Balance.findAll({
      where: { user_id: id },
      attributes: ["currency", "amount"],
    });
    return res.status(200).json({ msg: balance, ok: true });
  } catch (error) {
    console.log(error);
    return res.status(500).json({ error: "Server error", ok: false });
  }
};
