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

export const createUser = async (req, res) => {
  const { username, password, confirmPassword, currency } = req.body;

    console.log("Entra: ", req.body)

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
    initialize_balance(user.id, currency);
    return res.status(201).json({ msg: user, ok: true });
  } catch (error) {
    console.log(error);
    return res.status(500).json({ error: "Server error", ok: false });
  }
};
