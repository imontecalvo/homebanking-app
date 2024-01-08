import jwt from "jsonwebtoken";
import { TOKEN_KEY } from "../constants.js";

export const auth = (req, res, next) => {
  try {
    const token = req.headers.authorization.split(" ")[1];

    if (!token)
      return res
        .status(401)
        .json({ message: "No token. Authorization denied" });

    jwt.verify(token, TOKEN_KEY, (error, user) => {
      if (error) {
        return res.status(401).json({ msg: "Token is not valid" });
      }
      req.user = user;
      next();
    });
  } catch (error) {
    return res.status(500).json({ msg: error.message });
  }
};
