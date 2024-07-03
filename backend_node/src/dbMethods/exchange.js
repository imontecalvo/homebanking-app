import Balance from "../models/Balance.js";

export const newExchange = async (
  user_id,
  origin_currency,
  origin_amount,
  destiny_currency,
  destiny_amount
) => {
  //Actualizar balance origen
  const originBalance = await Balance.findOne({
    where: { user_id, currency: origin_currency },
  });
  originBalance.amount -= origin_amount;
  await originBalance.save();

  //Actualizar balance destino
  const destinyBalance = await Balance.findOne({
    where: { user_id, currency: destiny_currency },
  });
  destinyBalance.amount += destiny_amount;
  await destinyBalance.save();
};
