import express from "express";
import cors from "cors";

import userRoutes from "./routes/user.routes.js";

const app = express();

//Middlewares
app.use(cors())
app.use(express.json());
// app.use(express.urlencoded({ extended: false }));

//Routes
app.use("/users", userRoutes);



export default app;