import ProductForm from "./ProductForm.jsx";
import { productApi } from "./api/productApi";

function EditProductForm({ product, onSuccess, onCancel }) {
  return (
    <div className="form-panel">
      <h2>Editare produs</h2>
      <ProductForm
        initialValues={{
          name: product?.name ?? "",
          price: product?.price ?? "",
          stock: product?.stock ?? "",
          category: product?.category ?? "",
        }}
        submitLabel="Salvează modificările"
        onSubmit={(payload) => productApi.update(product.id, payload)}
        onSuccess={onSuccess}
        onCancel={onCancel}
      />
    </div>
  );
}

export default EditProductForm;
