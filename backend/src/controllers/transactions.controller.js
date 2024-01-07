import User from "../models/User.js";
import Balance from "../models/Balance.js";

export const newDeposit = async (req, res) => {
  const { user_id, currency, amount } = req.body;

  //Chequear parametros
  if (!user_id || !currency || !amount) {
    return res.status(400).json({ msg: "Missing fields", ok: false });
  }

  if (amount <= 0) {
    return res.status(400).json({ msg: "Invalid amount", ok: false });
  }

  try {
    //Chequear si existe usuario
    if (!(await User.findOne({ where: { user_id } }))) {
      return res.status(400).json({ msg: "User not found", ok: false });
    }

    //Chequear si existe balance
    const balance = await Balance.findOne({ where: { user_id, currency } });
    if (!balance) {
      return res.status(400).json({ msg: "Invalid currency", ok: false });
    }

    //Actualizar balance
    const newAmount = balance.amount + amount;
    await Balance.update(
      { amount: newAmount },
      { where: { user_id, currency } }
    );

    return res.status(201).json({ msg: "Deposit successful", ok: true });
  } catch (error) {
    console.log(error);
    return res.status(500).json({ error: "Server error", ok: false });
  }
};
