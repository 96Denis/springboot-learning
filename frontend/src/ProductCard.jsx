import StockBadge from "./StockBadge.jsx";
import Pret from "./Pret.jsx";

function ProductCard({ product, onSelect, onActualizeazaStoc, selected }) {
  return (
    <div className={selected ? "product-card product-card-selected" : "product-card"}>
      <h3>{product.name}</h3>
      <Pret valoare={product.price} />

      <div className="product-stock">
        <div>
          Stoc: {product.stock} buc
          <StockBadge stock={product.stock} />
        </div>

        {onActualizeazaStoc ? (
          <div className="stock-actions">
            <button
              onClick={() => onActualizeazaStoc(product.id, -1)}
              disabled={product.stock === 0}
              className="stock-btn"
            >
              −
            </button>
            <button
              onClick={() => onActualizeazaStoc(product.id, 1)}
              className="stock-btn"
            >
              +
            </button>
          </div>
        ) : null}
      </div>

      <button
        className="btn-select"
        onClick={() => onSelect(product.id)}
        disabled={product.stock === 0}
      >
        Selectează
      </button>
    </div>
  );
}

export default ProductCard;
