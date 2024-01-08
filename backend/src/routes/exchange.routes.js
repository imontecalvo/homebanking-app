import { Router } from "express";
import { newExchange } from "../controllers/exchange.controller.js";
import { auth } from "../middlewares/auth.middleware.js";

const router = Router();

//Routes
router.post("/", auth, newExchange);

export default router;
