import { useCallback, useEffect, useRef, useState } from "react";
import { productApi } from "./api/productApi";
import ConfirmModal from "./ConfirmModal.jsx";
import ProductCard from "./ProductCard.jsx";
import ProductSummary from "./ProductSummary.jsx";

function ProductsList({ filtru = "", refreshKey = 0, onEdit, onNotify }) {
  const [products, setProducts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [deleteTarget, setDeleteTarget] = useState(null);
  const abortRef = useRef(null);
  const snapshotRef = useRef([]);

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
  }, [loadProducts, refreshKey]);

  const filteredProducts = products.filter((product) =>
    product.name.toLowerCase().includes(filtru.toLowerCase()),
  );

  const handleDeleteRequest = (product) => {
    setDeleteTarget(product);
  };

  const handleDeleteCancel = () => {
    setDeleteTarget(null);
  };

  const handleDeleteConfirm = async () => {
    if (!deleteTarget) {
      return;
    }

    const productToDelete = deleteTarget;
    snapshotRef.current = products;
    setDeleteTarget(null);
    setProducts((current) => current.filter((product) => product.id !== productToDelete.id));

    try {
      await productApi.remove(productToDelete.id);
      onNotify?.({
        type: "success",
        message: `Produsul "${productToDelete.name}" a fost șters.`,
      });
    } catch (error) {
      setProducts(snapshotRef.current);
      onNotify?.({
        type: "error",
        message: error?.response?.data?.message || error.message || "Ștergerea a eșuat.",
      });
    }
  };

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
              onEdit={onEdit}
              onDelete={handleDeleteRequest}
            />
          ))}
        </div>
      )}

      <ConfirmModal
        isOpen={Boolean(deleteTarget)}
        message={
          deleteTarget
            ? `Sigur vrei să ștergi produsul "${deleteTarget.name}"? Această acțiune nu poate fi anulată.`
            : ""
        }
        onConfirm={handleDeleteConfirm}
        onCancel={handleDeleteCancel}
      />
    </div>
  );
}

export default ProductsList;
