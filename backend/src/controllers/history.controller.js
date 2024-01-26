// Se obtiene la pagina y cantidad de items por pagina desde la query
// Se devuelve un array con las transacciones del usuario en la pagina indicada
export const getHistory = async (req, res) => {
  const { user_id } = req.user;
  let { page, items } = req.query;

  if (!page || !items) {
    return res.status(400).json({ msg: "Missing fields", ok: false });
  }

  try {
    page = parseInt(page);
    items = parseInt(items);
  } catch (e) {
    return res.status(400).json({ msg: "Invalid fields", ok: false });
  }

  if (
    !Number.isInteger(page) ||
    !Number.isInteger(items) ||
    page <= 0 ||
    items <= 0
  ) {
    return res.status(400).json({ msg: "Invalid fields", ok: false });
  }

  const db = req.app.db;

  try {
    if (!(await db.user.existUserById(user_id))) {
      return res.status(400).json({ msg: "User not found", ok: false });
    }

    const transactions = await db.transaction.getHistoryPaginated(
      user_id,
      items,
      (page - 1) * items,
      [["date", "DESC"]]
    );

    return res.status(200).json({ msg: transactions, ok: true });
  } catch (error) {
    console.log(error);
    return res.status(500).json({ error: "Server error", ok: false });
  }
};

// Se obtiene la cantidad de transacciones del usuario
export const nOfTransactions = async (req, res) => {
  const { user_id } = req.user;

  const db = req.app.db;

  try {
    const nOfTransactions = await db.transaction.getNOfTransactions(user_id);
    return res.status(200).json({ msg: nOfTransactions, ok: true });
  } catch (error) {
    console.log(error);
    return res.status(500).json({ msg: "Server error", ok: false });
  }
};
