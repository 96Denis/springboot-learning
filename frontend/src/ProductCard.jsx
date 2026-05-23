import StockBadge from "./StockBadge.jsx";
import Pret from "./Pret.jsx";

function ProductCard({ product, onEdit, onDelete }) {
  return (
    <div className="product-card">
      <h3>{product.name}</h3>
      <Pret valoare={product.price} />

      <div className="product-stock">
        <span>Stoc: {product.stock} buc</span>
        <StockBadge stock={product.stock} />
      </div>

      <div className="product-actions">
        <button type="button" className="btn-secondary" onClick={() => onEdit(product)}>
          Editează
        </button>
        <button type="button" className="btn-danger" onClick={() => onDelete(product)}>
          Șterge
        </button>
      </div>
    </div>
  );
}

export default ProductCard;
