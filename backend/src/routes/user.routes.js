import { Router } from "express";
import { createUser, loginUser, getUserBalance } from "../controllers/user.controller.js";

const router = Router();

//Routes
// router.get("/:id");
router.post("/register",createUser);
router.post("/login",loginUser);
router.get("/:id/balance",getUserBalance);

export default router;