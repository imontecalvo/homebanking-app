// Se obtiene la moneda y el monto desde el body
// Se realiza un deposito actualizando el balance del usuario en caso de exito
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

  const db = req.app.db;

  try {
    //Chequear si existe usuario
    if (!(await db.user.existUserById(user_id))) {
      return res.status(400).json({ msg: "User not found", ok: false });
    }

    //Chequear si existe balance
    const balance = await db.user.getBalance(user_id, currency);
    if (!balance) {
      return res.status(400).json({ msg: "Invalid currency", ok: false });
    }

    //Actualizar balance y registro deposito
    await db.transaction.newDeposit(user_id, amount, currency);

    return res.status(201).json({ msg: "Deposit successful", ok: true });
  } catch (error) {
    console.log(error);
    return res.status(500).json({ error: "Server error", ok: false });
  }
};

// Se obtiene la moneda y el monto desde el body
// Se realiza un retiro actualizando el balance del usuario en caso de exito
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

  const db = req.app.db;

  try {
    //Chequear si existe usuario
    if (!(await db.user.existUserById(user_id))) {
      return res.status(400).json({ msg: "User not found", ok: false });
    }

    //Chequear si existe balance
    const balance = await db.user.getBalance(user_id, currency);
    if (!balance) {
      return res.status(400).json({ msg: "Invalid currency", ok: false });
    }

    //Chequear si hay fondos suficientes
    if (balance.amount < amount) {
      return res.status(400).json({ msg: "Insufficient funds", ok: false });
    }

    //Actualizar balance y registra el retiro
    await db.transaction.newWithdraw(user_id, amount, currency);

    return res.status(201).json({ msg: "Withdraw successful", ok: true });
  } catch (error) {
    console.log(error);
    return res.status(500).json({ error: "Server error", ok: false });
  }
};

// Se obtiene la moneda, el monto y usuario destino desde el body
// Se realiza una transferencia actualizando el balance de ambos usuarios en caso de exito
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

  const db = req.app.db;

  try {
    //Chequear si existe usuario
    if (!(await db.user.existUserById(user_id))) {
      return res.status(400).json({ msg: "User not found", ok: false });
    }

    //Chequear si existe usuario destino
    const destUser = await db.user.getUser(destUsername);
    if (!destUser) {
      return res
        .status(400)
        .json({ msg: "Destionation user not found", ok: false });
    }

    //Chequear si existe balance
    const balance = await db.user.getBalance(user_id, currency);
    if (!balance) {
      return res.status(400).json({ msg: "Invalid currency", ok: false });
    }

    //Chequear si hay fondos suficientes
    if (balance.amount < amount) {
      return res.status(400).json({ msg: "Insufficient funds", ok: false });
    }

    await db.transaction.newTransfer(
      user_id,
      destUser.user_id,
      amount,
      currency
    );

    return res.status(201).json({ msg: "Withdraw successful", ok: true });
  } catch (error) {
    console.log(error);
    return res.status(500).json({ error: "Server error", ok: false });
  }
};
