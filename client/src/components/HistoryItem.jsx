const HistoryItem = ({ id, transaction }) => {
  return (
    <>
      <div style={containerStyle}>
        <h1 style={titleStyle}> Transaction {id}</h1>
        <h1 style={{ ...dataStyle }}> - $300 (USD)</h1>
        <h1 style={{ ...dataStyle }}> 9/01/2024 10:40</h1>
      </div>
    </>
  );
};

export default HistoryItem;

// const containerStyle = {
//   backgroundColor: "gray",
//   borderRadius: "8px",
//   height: "30px",
//   display: "flex",
//   alignItems: "center",
//   marginBottom: 10,
//   padding: "10px",
// };

// const titleStyle = {
//   fontFamily: "system-ui",
//   fontWeight: 400,
//   fontSize: "25px",
//   marginLeft: "10px",
// };

// const dataStyle = {
//   fontFamily: "system-ui",
//   fontWeight: 300,
//   fontSize: "20px",
// };

const containerStyle = {
  display: "flex",
  flexDirection: "row",
  alignItems: "center", // Alinear verticalmente al centro para que los montos y fechas estén alineados
  border: "1px solid #ccc",
  padding: "10px",
  margin: "5px",
};

const titleStyle = {
  marginRight: "10px", // Ajusta el espacio entre el tipo y el monto/fecha
};

const dataStyle = {
  marginRight: "10px", // Ajusta el espacio entre el monto/fecha y el siguiente elemento
};
