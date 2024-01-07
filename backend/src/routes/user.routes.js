import { Router } from "express";
import { createUser, loginUser } from "../controllers/user.controller.js";

const router = Router();

//Routes
// router.get("/:id");
router.post("/register",createUser);
router.post("/login",loginUser);

export default router;