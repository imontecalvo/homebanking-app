import { Router } from "express";
import { getHistory} from "../controllers/history.controller.js";
import { auth } from "../middlewares/auth.middleware.js";

const router = Router();

//Routes
router.get("/", auth, getHistory);

export default router;