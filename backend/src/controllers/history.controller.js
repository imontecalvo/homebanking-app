import Transaction from "../models/Transaction.js";

export const getHistory = async (req, res) => {
  const { user_id } = req.user;

  try {
    const transactions = await Transaction.findAll({ where: { user_id } });
    return res.status(201).json({ msg: transactions, ok: true });
  } catch (error) {
    console.log(error);
    return res.status(500).json({ error: "Server error", ok: false });
  }
};
