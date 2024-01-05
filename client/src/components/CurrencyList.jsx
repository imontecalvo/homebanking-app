import * as React from "react";
import MenuItem from "@mui/material/MenuItem";
import Select from "@mui/material/Select";
import InputLabel from "@mui/material/InputLabel";
import FormControl from "@mui/material/FormControl";

const CurrencyList = ({ onChange }) => {
  const [currency, setCurrency] = React.useState("");

  const handleChange = (event) => {
    const selectedCurrency = event.target.value;
    setCurrency(selectedCurrency);
    onChange(selectedCurrency);
  };

  return (
    <div style={{width:110}}>
      <FormControl fullWidth>
        <InputLabel id="demo-simple-select-label">Currency</InputLabel>
        <Select
          labelId="demo-simple-select-label"
          id="demo-simple-select"
          value={currency}
          label="Currency"
          onChange={handleChange}
        >
          <MenuItem value={"USD"}>USD ($)</MenuItem>
          <MenuItem value={"CLP"}>CLP ($)</MenuItem>
          <MenuItem value={"ARS"}>ARS ($)</MenuItem>
          <MenuItem value={"TRY"}>TRY (₺)</MenuItem>
          <MenuItem value={"EUR"}>EUR (€)</MenuItem>
          <MenuItem value={"GBP"}>GBP (£)</MenuItem>
        </Select>
      </FormControl>
    </div>
  );
};

export default CurrencyList;
