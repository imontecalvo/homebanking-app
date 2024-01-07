import express from "express";
import cors from "cors";

import userRoutes from "./routes/user.routes.js";
import transactionsRoutes from "./routes/transactions.routes.js";
import exchangeRoutes from "./routes/exchange.routes.js";

const app = express();

//Middlewares
app.use(cors())
app.use(express.json());
// app.use(express.urlencoded({ extended: false }));

//Routes
app.use("/users", userRoutes);
app.use("/transactions", transactionsRoutes);
app.use("/exchange", exchangeRoutes);


export default app;