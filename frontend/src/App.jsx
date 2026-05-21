import { useState } from "react";
import ProductsList from "./ProductsList.jsx";
import ProductDetail from "./ProductDetail.jsx";
import "./App.css";

function App() {
  const [filtru, setFiltru] = useState("");
  const [selectatId, setSelectatId] = useState(null);

  return (
    <div className="app">
      <header className="app-header">
        <div>
          <h1>Catalog produse</h1>
          <p className="app-subtitle">Listă, detalii și filtrare client-side</p>
        </div>
        <input
          className="search"
          placeholder="Caută..."
          value={filtru}
          onChange={(e) => setFiltru(e.target.value)}
        />
      </header>

      <main className="app-main">
        <section className="panel panel-list">
          <ProductsList
            filtru={filtru}
            onSelect={setSelectatId}
            selectedId={selectatId}
          />
        </section>

        <aside className="panel panel-detail">
          {selectatId ? (
            <ProductDetail key={selectatId} id={selectatId} />
          ) : (
            <p className="empty">Selectează un produs pentru detalii.</p>
          )}
        </aside>
      </main>
    </div>
  );
}

export default App;
