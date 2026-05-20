import Pret from "./Pret.jsx";

function ProductSummary({ produse }) {
  const totalProduse = produse.length;

  const valoareTotalaStoc = produse.reduce(
    (acc, produs) => acc + produs.price * produs.stock,
    0,
  );

  const sumaPreturi = produse.reduce((acc, produs) => acc + produs.price, 0);
  const pretMediu = totalProduse > 0 ? sumaPreturi / totalProduse : 0;

  const produseIndisponibile = produse.filter(
    (produs) => produs.stock === 0,
  ).length;

  return (
    <div
      className="product-summary"
      style={{
        background: "#f7fafc",
        border: "1px solid #ddd",
        padding: "16px",
        borderRadius: "6px",
        marginBottom: "24px",
        display: "flex",
        gap: "20px",
        justifyContent: "space-between",
        flexWrap: "wrap",
      }}
    >
      <div>
        <strong>Total produse:</strong> {totalProduse}
      </div>
      <div>
        <strong>Valoare stoc:</strong> <Pret valoare={valoareTotalaStoc} />
      </div>
      <div>
        <strong>Preț mediu:</strong> <Pret valoare={pretMediu} />
      </div>
      <div>
        <strong>Indisponibile:</strong>{" "}
        <span style={{ color: "#742a2a", fontWeight: "bold" }}>
          {produseIndisponibile}
        </span>
      </div>
    </div>
  );
}

export default ProductSummary;
