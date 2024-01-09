import User from "../models/User.js";
import Balance from "../models/Balance.js";
import Transaction from "../models/Transaction.js";

export const newDeposit = async (req, res) => {
  const { currency, amount } = req.body;
  const user_id = req.user.user_id;

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

    await Transaction.create({
      user_id,
      type: "Deposit",
      currency,
      amount,
      date: new Date().toISOString(),
    });

    return res.status(201).json({ msg: "Deposit successful", ok: true });
  } catch (error) {
    console.log(error);
    return res.status(500).json({ error: "Server error", ok: false });
  }
};

export const newWithdraw = async (req, res) => {
  const { currency, amount } = req.body;
  const user_id = req.user.user_id;

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

    //Chequear si hay fondos suficientes
    if (balance.amount < amount) {
      return res.status(400).json({ msg: "Insufficient funds", ok: false });
    }

    //Actualizar balance
    const newAmount = balance.amount - amount;
    await Balance.update(
      { amount: newAmount },
      { where: { user_id, currency } }
    );

    await Transaction.create({
      user_id,
      type: "Withdraw",
      currency,
      amount,
      date: new Date().toISOString(),
    });

    return res.status(201).json({ msg: "Withdraw successful", ok: true });
  } catch (error) {
    console.log(error);
    return res.status(500).json({ error: "Server error", ok: false });
  }
};

export const newTransfer = async (req, res) => {
  const { currency, amount, destUsername } = req.body;
  const user_id = req.user.user_id;

  //Chequear parametros
  if (!user_id || !currency || !amount || !destUsername) {
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

    //Chequear si existe usuario destino
    const destUserID = await User.findOne({
      where: { username: destUsername },
      attributes: ["user_id"],
    });
    if (!destUserID) {
      return res
        .status(400)
        .json({ msg: "Destionation user not found", ok: false });
    }

    //Chequear si existe balance
    const balance = await Balance.findOne({ where: { user_id, currency } });
    if (!balance) {
      return res.status(400).json({ msg: "Invalid currency", ok: false });
    }

    //Chequear si hay fondos suficientes
    if (balance.amount < amount) {
      return res.status(400).json({ msg: "Insufficient funds", ok: false });
    }

    //Actualizar balance origen
    const newAmount = balance.amount - amount;
    await Balance.update(
      { amount: newAmount },
      { where: { user_id, currency } }
    );

    //Actualizar balance destino
    const destBalance = await Balance.findOne({
      where: { user_id: destUserID.user_id, currency },
    });
    const newDestAmount = destBalance.amount + amount;
    await Balance.update(
      { amount: newDestAmount },
      { where: { user_id: destUserID.user_id, currency } }
    );

    //Crear transferencia en ambos usuarios
    await Transaction.create({
      user_id,
      type: "Transfer",
      currency,
      amount: -amount,
      date: new Date().toISOString(),
    });

    await Transaction.create({
      user_id: destUserID.user_id,
      type: "Transfer",
      currency,
      amount,
      date: new Date().toISOString(),
    });
    return res.status(201).json({ msg: "Withdraw successful", ok: true });
  } catch (error) {
    console.log(error);
    return res.status(500).json({ error: "Server error", ok: false });
  }
};
