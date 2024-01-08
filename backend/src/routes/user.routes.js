import { Router } from "express";
import { createUser, loginUser, getUserBalance } from "../controllers/user.controller.js";
import { auth } from "../middlewares/auth.middleware.js";

const router = Router();

//Routes
// router.get("/:id");
router.post("/register", createUser);
router.post("/login",loginUser);
router.get("/balance", auth, getUserBalance);

export default router;