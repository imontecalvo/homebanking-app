import { Router } from "express";
import { getHistory, nOfTransactions} from "../controllers/history.controller.js";
import { auth } from "../middlewares/auth.middleware.js";

const router = Router();

//Routes
router.get("/", auth, getHistory);
router.get("/n-transactions", auth, nOfTransactions);

export default router;