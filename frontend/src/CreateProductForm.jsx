import ProductForm from "./ProductForm.jsx";
import { productApi } from "./api/productApi";

function CreateProductForm({ onSuccess, onCancel }) {
  return (
    <div className="form-panel">
      <h2>Adăugare produs</h2>
      <ProductForm
        initialValues={{ name: "", price: "", stock: "", category: "", categoryId: "" }}
        submitLabel="Creează produs"
        onSubmit={(payload) => productApi.create(payload)}
        onSuccess={onSuccess}
        onCancel={onCancel}
      />
    </div>
  );
}

export default CreateProductForm;
