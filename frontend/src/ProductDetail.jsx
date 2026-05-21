import { useEffect, useState } from "react";
import { productApi } from "./api/productApi";
import Pret from "./Pret.jsx";

function ProductDetail({ id }) {
  const [product, setProduct] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [notFound, setNotFound] = useState(false);

  useEffect(() => {
    const controller = new AbortController();

    productApi
      .getById(id, { signal: controller.signal })
      .then((data) => {
        if (!controller.signal.aborted) {
          setProduct(data);
        }
      })
      .catch((err) => {
        if (controller.signal.aborted) {
          return;
        }

        if (err?.response?.status === 404) {
          setNotFound(true);
          return;
        }

        setError(err);
      })
      .finally(() => {
        if (!controller.signal.aborted) {
          setLoading(false);
        }
      });

    return () => {
      controller.abort();
    };
  }, [id]);

  if (loading) {
    return <p className="status">Se încarcă detaliile produsului...</p>;
  }

  if (notFound) {
    return <p className="status status-error">Produsul cu id-ul {id} nu există.</p>;
  }

  if (error) {
    return (
      <p className="status status-error">
        Eroare la încărcarea produsului: {error.message}
      </p>
    );
  }

  if (!product) {
    return <p className="status">Nu există detalii disponibile.</p>;
  }

  return (
    <div>
      <h2>{product.name}</h2>
      <div className="detail-row">
        <span className="detail-label">Preț</span>
        <Pret valoare={product.price} />
      </div>
      <div className="detail-row">
        <span className="detail-label">Stoc</span>
        <strong>{product.stock} buc</strong>
      </div>
    </div>
  );
}

export default ProductDetail;
