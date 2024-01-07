import { sequelize } from "../db.js";
import { DataTypes } from "sequelize";

import Balance from "./Balance.js";
import Transaction from "./Transaction.js";

const User = sequelize.define("user", {
  user_id: {
    type: DataTypes.INTEGER,
    primaryKey: true,
    autoIncrement: true,
  },
  username: {
    type: DataTypes.STRING,
    allowNull: false,
    unique: true,
  },
  password: {
    type: DataTypes.STRING,
    allowNull: false,
  },
});


User.hasMany(Balance, { foreignKey: "user_id" });
Balance.belongsTo(User, { foreignKey: "user_id" });

User.hasMany(Transaction, { foreignKey: "user_id" });
Transaction.belongsTo(User, { foreignKey: "user_id" });

export default User;
