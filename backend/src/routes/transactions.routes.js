import { Router } from "express";
import { newDeposit, newWithdraw } from "../controllers/transactions.controller.js";

const router = Router();

//Routes
router.post("/deposit", newDeposit);
router.post("/withdraw", newWithdraw);
// router.post("/transfer", newTransfer);

export default router;
