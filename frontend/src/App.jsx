import { useState } from "react";
import ProductCard from "./ProductCard.jsx";
import ProductSummary from "./ProductSummary.jsx";
import "./App.css";

const PRODUSE_INITIALE = [
  { id: 1, name: "Tastatură mecanică", price: 349.9, stock: 12 },
  { id: 2, name: "Monitor 27 inch 4K", price: 1899.0, stock: 3 },
  { id: 3, name: "Mouse wireless", price: 159.5, stock: 0 },
  { id: 4, name: "Webcam HD", price: 229.0, stock: 7 },
];

function App() {
  //etProduse pentru a face state-ul modificabil
  const [produse, setProduse] = useState(PRODUSE_INITIALE);
  const [selectat, setSelectat] = useState(null);
  const [filtru, setFiltru] = useState("");
  const [sortBy, setSortBy] = useState("Implicit");

  const actualizeazaStoc = (productId, delta) => {
    setProduse((produseCurente) =>
      produseCurente.map((p) => {
        if (p.id === productId) {
          const noulStoc = Math.max(0, p.stock + delta);
          return { ...p, stock: noulStoc };
        }
        return p;
      }),
    );
  };

  const produseFiltrate = produse.filter((p) =>
    p.name.toLowerCase().includes(filtru.toLowerCase()),
  );

  const produseGataDeAfisat = [...produseFiltrate].sort((a, b) => {
    if (sortBy === "Preț crescător") return a.price - b.price;
    if (sortBy === "Preț descrescător") return b.price - a.price;
    return 0;
  });

  return (
    <div className="app">
      <header>
        <h1>Catalog produse</h1>
        <div style={{ display: "flex", gap: "16px" }}>
          <input
            className="search"
            placeholder="Caută..."
            value={filtru}
            onChange={(e) => setFiltru(e.target.value)}
          />
          <select
            value={sortBy}
            onChange={(e) => setSortBy(e.target.value)}
            style={{
              padding: "8px 12px",
              borderRadius: "4px",
              border: "1px solid #ccc",
            }}
          >
            <option value="Implicit">Implicit</option>
            <option value="Preț crescător">Preț crescător</option>
            <option value="Preț descrescător">Preț descrescător</option>
          </select>
        </div>
      </header>

      <main style={{ display: "block" }}>
        {" "}
        <ProductSummary produse={produse} />
        <div
          style={{
            display: "grid",
            gridTemplateColumns: "1fr 280px",
            gap: "24px",
          }}
        >
          <section className="product-list">
            {produseGataDeAfisat.length === 0 ? (
              <p className="empty">Niciun produs găsit.</p>
            ) : (
              produseGataDeAfisat.map((p) => (
                <ProductCard
                  key={p.id}
                  product={p}
                  onSelect={setSelectat}
                  onActualizeazaStoc={actualizeazaStoc}
                />
              ))
            )}
          </section>

          {selectat && (
            <aside className="selected-info">
              <h2>Produs selectat</h2>
              <p>
                <strong>{selectat.name}</strong>
              </p>
              <p>Preț: {selectat.price.toFixed(2)} lei</p>
              <p>Stoc curent: {selectat.stock} buc</p>
              <button className="btn-select" onClick={() => setSelectat(null)}>
                Închide
              </button>
            </aside>
          )}
        </div>
      </main>
    </div>
  );
}

export default App;
