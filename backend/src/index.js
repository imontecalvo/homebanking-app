import app from "./app.js";
import {sequelize} from "./db.js";

async function main(){
    try {
        await sequelize.sync();
        app.listen(3001, () => {
          console.log("Server is listening on port 3000");
        });
        console.log("Connection has been established successfully.");
      } catch (error) {
        console.error("Unable to connect to the database:", error);
      }
}

main();