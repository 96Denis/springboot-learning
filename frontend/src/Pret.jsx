function Pret({ valoare, moneda = "lei" }) {
  const valoareNumerica = Number(valoare) || 0;

  const pretFormatat = valoareNumerica.toFixed(2);

  return (
    <span className="product-price">
      {pretFormatat} {moneda}
    </span>
  );
}

export default Pret;
