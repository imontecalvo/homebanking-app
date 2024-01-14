import { TOKEN_KEY } from "./constants.js";
import jwt from "jsonwebtoken";

const DURATION_TOKEN = "1h";

// Funcion para crear el token a partir de un payload
// El token se genera con una duracion de 1 hora
// Devuelve una promesa con el token
export async function createAccessToken(payload) {
  return new Promise((resolve, reject) => {
    jwt.sign(
      payload,
      TOKEN_KEY,
      { expiresIn: DURATION_TOKEN },
      (err, token) => {
        if (err) reject(err);
        resolve(token);
      }
    );
  });
}
