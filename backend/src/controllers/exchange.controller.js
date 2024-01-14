import User from "../models/User.js";
import Balance from "../models/Balance.js";

const FEE = 0.01;

const exchangeRate = (amount, origin, destiny) => {
  const currenciesInUSD = {
    USD: 1,
    CLP: 0.0011,
    ARS: 0.0012,
    TRY: 0.034,
    EUR: 1.09,
    GBP: 1.27,
  };

  return amount * (currenciesInUSD[origin] / currenciesInUSD[destiny]);
};

const exchangeWithFee = (amount, origin, destiny) => {
  return parseFloat((exchangeRate(amount, origin, destiny) * (1 - FEE)).toFixed(2));
};

export const newExchange = async (req, res) => {
  const { origin_currency, origin_amount, destiny_currency } = req.body;

  const user_id = req.user.user_id;

  //Chequear parametros
  if (!user_id || !origin_currency || !origin_amount || !destiny_currency) {
    return res.status(400).json({ msg: "Missing fields", ok: false });
  }

  if (origin_currency <= 0) {
    return res.status(400).json({ msg: "Invalid amount", ok: false });
  }

  try {
    //Chequear si existe usuario
    if (!(await User.findOne({ where: { user_id } }))) {
      return res.status(400).json({ msg: "User not found", ok: false });
    }

    //Chequear si existe balance
    const balance = await Balance.findOne({
      where: { user_id, currency: origin_currency },
    });
    if (!balance) {
      return res.status(400).json({ msg: "Invalid currency", ok: false });
    }

    //Chequear si hay fondos suficientes
    if (balance.amount < origin_amount) {
      return res.status(400).json({ msg: "Insufficient funds", ok: false });
    }

    //Actualizar balance origen
    const newAmount = balance.amount - origin_amount;
    await Balance.update(
      { amount: newAmount },
      { where: { user_id, currency: origin_currency } }
    );

    //Actualizar balance destino
    const balanceDest = await Balance.findOne({
      where: { user_id, currency: destiny_currency },
    });
    const newAmountDest =
      balanceDest.amount +
      exchangeWithFee(origin_amount, origin_currency, destiny_currency);
    
      await Balance.update(
      { amount: newAmountDest },
      { where: { user_id, currency: destiny_currency } }
    );

    return res.status(201).json({ msg: "Withdraw successful", ok: true });
  } catch (error) {
    console.log(error);
    return res.status(500).json({ error: "Server error", ok: false });
  }
};
