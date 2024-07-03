import User from "../models/User.js";
import Balance from "../models/Balance.js";

export const existUser = async (username) => {
  const user = await User.findOne({ where: { username } });
  return user ? true : false;
};

export const existUserById = async (user_id) => {
  const user = await User.findByPk(user_id);
  return user ? true : false;
};

export const newUser = async (username, password) => {
  return await User.create({ username, password });
};

export const getUser = async (username) => {
  return await User.findOne({ where: { username } });
};

export const newLoginFailed = async (user_id) => {
  const user = await User.findByPk(user_id);
  if (user) {
    user.login_failed += 1;
    await user.save();
  }
};

export const newLoginSuccess = async (user_id) => {
  const user = await User.findByPk(user_id);
  if (user) {
    user.login_failed = 0;
    await user.save();
  }
};

export const newBalance = async (user_id, currency, amount) => {
  await Balance.create({ user_id, currency, amount });
};

export const getBalance = async (user_id, currency) => {
  return await Balance.findOne({ where: { user_id, currency } });
};

export const getAllUserBalance = async (user_id) => {
  return await Balance.findAll({
    where: { user_id },
    attributes: ["currency", "amount"],
  });
};
