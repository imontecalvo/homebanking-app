import { TOKEN_KEY } from "./constants.js";
import jwt from "jsonwebtoken";

const DURATION_TOKEN = "1h"

export async function createAccessToken(payload) {
  return new Promise((resolve, reject) => {
    jwt.sign(payload, TOKEN_KEY, { expiresIn: DURATION_TOKEN }, (err, token) => {
      if (err) reject(err);
      resolve(token);
    });
  });
}