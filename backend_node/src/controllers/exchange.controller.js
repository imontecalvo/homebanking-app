const FEE = 0.01;

// Recibe moneda de origen, monto y moneda de destino
// Devuelve la conversion sin comision
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

// Recibe moneda de origen, monto y moneda de destino
// Devuelve el monto a recibir descontando la comision. El monto se redondea a 2 decimales
const exchangeWithFee = (amount, origin, destiny) => {
  return parseFloat(
    (exchangeRate(amount, origin, destiny) * (1 - FEE)).toFixed(2)
  );
};

// Recibe moneda de origen, monto y moneda de destino
// Realiza el cambio modificando los balances en caso de exito
export const newExchange = async (req, res) => {
  const { origin_currency, origin_amount, destiny_currency } = req.body;

  const user_id = req.user.user_id;

  //Chequear parametros
  if (!user_id || !origin_currency || !origin_amount || !destiny_currency) {
    return res.status(400).json({ msg: "Missing fields", ok: false });
  }

  if (origin_amount <= 0) {
    return res.status(400).json({ msg: "Invalid amount", ok: false });
  }

  const db = req.app.db;

  try {
    //Chequear si existe usuario
    if (!(await db.user.existUserById(user_id))) {
      return res.status(400).json({ msg: "User not found", ok: false });
    }

    //Chequear si existe balance
    const balance = await db.user.getBalance(user_id, origin_currency);
    if (!balance) {
      return res.status(400).json({ msg: "Invalid currency", ok: false });
    }

    //Chequear si hay fondos suficientes
    if (balance.amount < origin_amount) {
      return res.status(400).json({ msg: "Insufficient funds", ok: false });
    }

    const destiny_amount = exchangeWithFee(
      origin_amount,
      origin_currency,
      destiny_currency
    );

    await db.exchange.newExchange(
      user_id,
      origin_currency,
      origin_amount,
      destiny_currency,
      destiny_amount
    );

    return res.status(201).json({ msg: "Withdraw successful", ok: true });
  } catch (error) {
    console.log(error);
    return res.status(500).json({ error: "Server error", ok: false });
  }
};
