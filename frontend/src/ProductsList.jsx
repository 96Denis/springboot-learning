import { useCallback, useEffect, useRef, useState } from "react";
import { productApi } from "./api/productApi";
import ProductCard from "./ProductCard.jsx";
import ProductSummary from "./ProductSummary.jsx";

function ProductsList({ filtru = "", onSelect, selectedId }) {
  const [products, setProducts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const abortRef = useRef(null);

  const loadProducts = useCallback(() => {
    if (abortRef.current) {
      abortRef.current.abort();
    }

    const controller = new AbortController();
    abortRef.current = controller;

    setLoading(true);
    setError(null);

    productApi
      .getAll({ signal: controller.signal })
      .then((data) => {
        if (!controller.signal.aborted) {
          setProducts(data);
        }
      })
      .catch((err) => {
        if (!controller.signal.aborted) {
          setError(err);
        }
      })
      .finally(() => {
        if (!controller.signal.aborted) {
          setLoading(false);
        }
      });
  }, []);

  useEffect(() => {
    const timer = setTimeout(loadProducts, 0);

    return () => {
      clearTimeout(timer);
      if (abortRef.current) {
        abortRef.current.abort();
      }
    };
  }, [loadProducts]);

  const filteredProducts = products.filter((product) =>
    product.name.toLowerCase().includes(filtru.toLowerCase()),
  );

  if (loading) {
    return <p className="status">Se încarcă produsele...</p>;
  }

  if (error) {
    return (
      <div className="status-block">
        <p className="status status-error">Eroare: {error.message}</p>
        <button className="btn-reload" onClick={loadProducts}>
          Reîncarcă lista
        </button>
      </div>
    );
  }

  return (
    <div className="products-shell">
      <div className="list-toolbar">
        <div>
          <h2>Produse</h2>
          <p className="list-meta">
            {filteredProducts.length} din {products.length} afișate
          </p>
        </div>
        <button className="btn-reload" onClick={loadProducts}>
          Reîncarcă lista
        </button>
      </div>

      <ProductSummary produse={filteredProducts} />

      {filteredProducts.length === 0 ? (
        <p className="status">Nu există produse pentru filtrul curent.</p>
      ) : (
        <div className="product-list">
          {filteredProducts.map((product) => (
            <ProductCard
              key={product.id}
              product={product}
              onSelect={onSelect}
              selected={selectedId === product.id}
            />
          ))}
        </div>
      )}
    </div>
  );
}

export default ProductsList;
