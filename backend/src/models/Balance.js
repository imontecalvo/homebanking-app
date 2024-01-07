import { sequelize } from "../db.js";
import { DataTypes } from "sequelize";

const Balance = sequelize.define(
  "balance",
  {
    user_id: {
      type: DataTypes.INTEGER,
      primaryKey: true,
    },
    currency: {
      type: DataTypes.STRING,
      allowNull: false,
      primaryKey: true,
    },
    amount: {
      type: DataTypes.INTEGER,
      allowNull: false,
    },
  },
  {
    timestamps: false,
  }
);

export default Balance;
