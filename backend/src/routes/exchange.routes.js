import { Router } from "express";
import { newExchange } from "../controllers/exchange.controller.js";

const router = Router();

//Routes
router.post("/",newExchange);

export default router;