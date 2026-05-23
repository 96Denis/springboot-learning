import { useEffect, useState } from "react";
import CreateProductForm from "./CreateProductForm.jsx";
import EditProductForm from "./EditProductForm.jsx";
import ProductsList from "./ProductsList.jsx";
import Toast from "./Toast.jsx";
import "./App.css";

function App() {
  const [filtru, setFiltru] = useState("");
  const [mode, setMode] = useState("list");
  const [editProduct, setEditProduct] = useState(null);
  const [refreshKey, setRefreshKey] = useState(0);
  const [toast, setToast] = useState(null);

  useEffect(() => {
    if (!toast) {
      return undefined;
    }

    const timer = window.setTimeout(() => setToast(null), 3000);
    return () => window.clearTimeout(timer);
  }, [toast]);

  const showToast = (nextToast) => {
    setToast(nextToast);
  };

  const handleCreateClick = () => {
    setEditProduct(null);
    setMode("create");
  };

  const handleEdit = (product) => {
    setEditProduct(product);
    setMode("edit");
  };

  const handleFormSuccess = (message) => {
    setMode("list");
    setEditProduct(null);
    setRefreshKey((current) => current + 1);
    showToast({ type: "success", message });
  };

  const handleFormCancel = () => {
    setMode("list");
    setEditProduct(null);
  };

  return (
    <div className="app-shell">
      {toast ? (
        <Toast message={toast.message} type={toast.type} onDismiss={() => setToast(null)} />
      ) : null}

      <header className="app-header">
        <div>
          <h1>Catalog produse</h1>
          <p className="app-subtitle">Listă, creare, editare și ștergere produse</p>
        </div>
        <div className="header-actions">
          <input
            className="search"
            placeholder="Caută..."
            value={filtru}
            onChange={(e) => setFiltru(e.target.value)}
          />
          <button type="button" className="btn-primary" onClick={handleCreateClick}>
            + Adaugă produs
          </button>
        </div>
      </header>

      <main className="app-main">
        <section className="panel panel-list">
          <ProductsList
            filtru={filtru}
            refreshKey={refreshKey}
            onEdit={handleEdit}
            onNotify={showToast}
          />
        </section>

        <aside className="panel panel-detail">
          {mode === "create" ? (
            <CreateProductForm
              key="create"
              onSuccess={() => handleFormSuccess("Produsul a fost creat cu succes.")}
              onCancel={handleFormCancel}
            />
          ) : null}

          {mode === "edit" && editProduct ? (
            <EditProductForm
              key={editProduct.id}
              product={editProduct}
              onSuccess={() => handleFormSuccess("Produsul a fost actualizat cu succes.")}
              onCancel={handleFormCancel}
            />
          ) : null}

          {mode === "list" ? (
            <div className="empty-state">
              <h2>Acțiuni rapide</h2>
              <p className="empty">Alege „Adaugă produs” sau folosește „Editează” din listă.</p>
            </div>
          ) : null}
        </aside>
      </main>
    </div>
  );
}

export default App;
