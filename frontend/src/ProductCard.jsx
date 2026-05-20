import StockBadge from "./StockBadge.jsx";
import Pret from "./Pret.jsx";

//prop-ul onActualizeazaStoc
function ProductCard({ product, onSelect, onActualizeazaStoc }) {
  return (
    <div className="product-card">
      <h3>{product.name}</h3>
      <Pret valoare={product.price} />

      <div
        className="product-stock"
        style={{
          display: "flex",
          alignItems: "center",
          gap: "10px",
          marginTop: "10px",
        }}
      >
        <div>
          Stoc: {product.stock} buc
          <StockBadge stock={product.stock} />
        </div>

        <div style={{ display: "flex", gap: "4px" }}>
          <button
            onClick={() => onActualizeazaStoc(product.id, -1)}
            disabled={product.stock === 0}
            style={{
              padding: "2px 8px",
              cursor: product.stock === 0 ? "not-allowed" : "pointer",
            }}
          >
            −
          </button>
          <button
            onClick={() => onActualizeazaStoc(product.id, 1)}
            style={{ padding: "2px 8px", cursor: "pointer" }}
          >
            +
          </button>
        </div>
      </div>

      <button
        className="btn-select"
        onClick={() => onSelect(product)}
        disabled={product.stock === 0}
        style={{ marginTop: "12px" }}
      >
        Selectează
      </button>
    </div>
  );
}

export default ProductCard;
