import express from "express";
import cors from "cors";

import userRoutes from "./routes/user.routes.js";
import transactionsRoutes from "./routes/transactions.routes.js";
import exchangeRoutes from "./routes/exchange.routes.js";
import historyRoutes from "./routes/history.routes.js";

const app = express();

//Middlewares
app.use(cors());
app.use(express.json());

//Routes
app.use("/users", userRoutes);
app.use("/transactions", transactionsRoutes);
app.use("/exchange", exchangeRoutes);
app.use("/history", historyRoutes);

export default app;
