import Sequelize from "sequelize";

export const sequelize = new Sequelize(
  "homebanking", // db name,
  "postgres", // username
  "admin", // password
  {
    host: "localhost",
    dialect: "postgres",
  }
);
