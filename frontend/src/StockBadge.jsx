function StockBadge({ stock }) {
  const isAvailable = stock > 0;
  const className = "stock-badge " + (isAvailable ? "stock-in" : "stock-out");
  const text = isAvailable ? "În stoc" : "Indisponibil";
  return <span className={className}>{text}</span>;
}
export default StockBadge;
