import Balance from "../models/Balance.js";
import Transaction from "../models/Transaction.js";

export const newDeposit = async (user_id, amount, currency) => {
  const balance = await Balance.findOne({ where: { user_id, currency } });
  const newAmount = balance.amount + amount;
  balance.amount = newAmount;
  await balance.save();

  await Transaction.create({
    user_id,
    type: "Deposit",
    currency,
    amount,
    date: new Date().toISOString(),
  });
};

export const newWithdraw = async (user_id, amount, currency) => {
  const balance = await Balance.findOne({ where: { user_id, currency } });
  const newAmount = balance.amount - amount;
  balance.amount = newAmount;
  await balance.save();

  await Transaction.create({
    user_id,
    type: "Withdraw",
    currency,
    amount,
    date: new Date().toISOString(),
  });
};

export const newTransfer = async (
  originUser_id,
  destUser_id,
  amount,
  currency
) => {
  //Actualizar balance origen
  const originBalance = await Balance.findOne({
    where: { user_id: originUser_id, currency },
  });
  originBalance.amount -= amount;
  await originBalance.save();

  //Actualizar balance destino
  const destBalance = await Balance.findOne({
    where: { user_id: destUser_id, currency },
  });
  destBalance.amount += amount;
  await destBalance.save();

  //Crear transferencia en ambos usuarios
  await Transaction.create({
    user_id: originUser_id,
    type: "Transfer",
    currency,
    amount: -amount,
    date: new Date().toISOString(),
  });

  await Transaction.create({
    user_id: destUser_id,
    type: "Transfer",
    currency,
    amount,
    date: new Date().toISOString(),
  });
};

export const getHistoryPaginated = async (user_id, limit, offset, order) => {
  return await Transaction.findAll({
    where: { user_id },
    limit,
    offset,
    order,
  });
};

export const getNOfTransactions = async (user_id) => {
  return await Transaction.count({ where: { user_id } });
};
