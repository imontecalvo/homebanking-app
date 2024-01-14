import Transaction from "../models/Transaction.js";

// Se obtiene la pagina y cantidad de items por pagina desde la query
// Se devuelve un array con las transacciones del usuario en la pagina indicada
export const getHistory = async (req, res) => {
  const { user_id } = req.user;
  const { page, items } = req.query;

  try {
    const transactions = await Transaction.findAll({
      where: { user_id },
      limit: items,
      offset: (page - 1) * items,
      order: [["date", "DESC"]],
    });
    return res.status(201).json({ msg: transactions, ok: true });
  } catch (error) {
    console.log(error);
    return res.status(500).json({ msg: "Server error", ok: false });
  }
};

// Se obtiene la cantidad de transacciones del usuario
export const nOfTransactions = async (req, res) => {
  const { user_id } = req.user;

  try {
    const nOfTransactions = await Transaction.count({ where: { user_id } });
    return res.status(201).json({ msg: nOfTransactions, ok: true });
  } catch (error) {
    console.log(error);
    return res.status(500).json({ msg: "Server error", ok: false });
  }
};
