import { Router } from "express";
import {
  newDeposit,
  newWithdraw,
  newTransfer,
} from "../controllers/transactions.controller.js";
import { auth } from "../middlewares/auth.middleware.js";

const router = Router();

//Routes
router.post("/deposit", auth, newDeposit);
router.post("/withdraw", auth, newWithdraw);
router.post("/transfer", auth, newTransfer);

export default router;
