import Sequelize from "sequelize";
import { config } from "dotenv";

config();

export const sequelize = new Sequelize(
  process.env.DB_NAME, // db name,
  process.env.DB_USERNAME, // username
  process.env.DB_PASSWORD, // password
  {
    host: process.env.DB_HOST,
    port: process.env.DB_LOCAL_PORT,
    dialect: "postgres",
  }
);
