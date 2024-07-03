import App from "./app.js";
import { sequelize } from "./db.js";
import { config } from "dotenv";
import database from "./dbMethods/database.js";

config();

async function main() {
  let app = App(database);
  try {
    await sequelize.sync();
    app.listen(process.env.API_LOCAL_PORT, () => {
      console.log(`Server is listening on port ${process.env.API_LOCAL_PORT}`);
    });
    console.log("Connection has been established successfully.");
  } catch (error) {
    console.error("Unable to connect to the database:", error);
  }
}

main();
