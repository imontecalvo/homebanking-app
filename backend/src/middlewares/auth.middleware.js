import jwt from "jsonwebtoken";
import { TOKEN_KEY } from "../constants.js";

export const auth = (req, res, next) => {
  try {
    let token = req.headers.authorization;
    if (!token)
      return res
        .status(401)
        .json({ message: "No token. Authorization denied" });

    token = token.split(" ")[1];

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
