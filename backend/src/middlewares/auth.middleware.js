import jwt from "jsonwebtoken";
import { TOKEN_KEY } from "../constants.js";
import { loggedUser } from "../../tests/user.test.js";

// Middleware para verificar el token
// Si no existe o es invalido, se devuelve error 401
// Si existe, se guarda el usuario en req.user y se llama a next()
export const auth = (req, res, next) => {
  if (process.env.NODE_ENV === "test") {
    req.user = loggedUser;
    return next();
  }

  try {
    let token = req.headers.authorization;
    // Si no existe el token, se devuelve un error
    if (!token)
      return res
        .status(401)
        .json({ message: "No token. Authorization denied" });

    token = token.split(" ")[1];

    // Si existe el token, se verifica
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
