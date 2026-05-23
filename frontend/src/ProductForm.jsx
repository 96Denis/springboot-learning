import { useEffect, useState } from "react";
import { productApi } from "./api/productApi";

const normalize = (values = {}) => ({
  name: values.name ?? "",
  price: values.price ?? "",
  stock: values.stock ?? "",
  category: values.category ?? "",
  categoryId: values.categoryId ?? "",
});

function ProductForm({
  initialValues,
  onSubmit,
  submitLabel,
  onSuccess,
  onCancel,
}) {
  const [categories, setCategories] = useState([]);
  const [categoriesLoading, setCategoriesLoading] = useState(true);
  const [categoriesError, setCategoriesError] = useState(null);
  const normalizedInitialValues = normalize(initialValues);
  const [name, setName] = useState(normalizedInitialValues.name);
  const [price, setPrice] = useState(normalizedInitialValues.price);
  const [stock, setStock] = useState(normalizedInitialValues.stock);
  const [categoryId, setCategoryId] = useState(normalizedInitialValues.categoryId);
  const [errors, setErrors] = useState({});
  const [submitError, setSubmitError] = useState(null);
  const [submitting, setSubmitting] = useState(false);

  useEffect(() => {
    const controller = new AbortController();

    productApi
      .getCategories({ signal: controller.signal })
      .then((data) => {
        if (!controller.signal.aborted) {
          setCategories(Array.isArray(data) ? data : []);
        }
      })
      .catch((error) => {
        if (!controller.signal.aborted) {
          setCategoriesError(error);
        }
      })
      .finally(() => {
        if (!controller.signal.aborted) {
          setCategoriesLoading(false);
        }
      });

    return () => {
      controller.abort();
    };
  }, []);

  useEffect(() => {
    const task = window.queueMicrotask
      ? window.queueMicrotask
      : (callback) => window.setTimeout(callback, 0);

    task(() => {
      setErrors((current) => {
        const next = { ...current };
        const selectedCategory = categories.find(
          (category) => String(category.id) === String(categoryId),
        );
        const currentName = name.trim();
        const currentPrice = Number(price);
        const currentStock = Number(stock);

        if (next.name && currentName) {
          delete next.name;
        }

        if (next.price && Number.isFinite(currentPrice) && currentPrice > 0) {
          delete next.price;
        }

        if (next.stock && Number.isInteger(currentStock) && currentStock >= 0) {
          delete next.stock;
        }

        if (next.categoryId && selectedCategory) {
          delete next.categoryId;
        }

        if (next.category && selectedCategory) {
          delete next.category;
        }

        return next;
      });
    });
  }, [categories, categoryId, name, price, stock]);

  const validate = () => {
    const nextErrors = {};
    const selectedCategory = categories.find(
      (category) => String(category.id) === String(effectiveCategoryId),
    );

    if (!name.trim()) {
      nextErrors.name = "Numele este obligatoriu.";
    }

    const parsedPrice = Number(price);
    if (!Number.isFinite(parsedPrice) || parsedPrice <= 0) {
      nextErrors.price = "Prețul trebuie să fie un număr pozitiv.";
    }

    const parsedStock = Number(stock);
    if (!Number.isInteger(parsedStock) || parsedStock < 0) {
      nextErrors.stock = "Stocul trebuie să fie un număr întreg nenegativ.";
    }

    if (!effectiveCategoryId) {
      nextErrors.categoryId = "Selectează o categorie.";
    } else if (!selectedCategory) {
      nextErrors.categoryId = "Categoria selectată nu există.";
    }

    return nextErrors;
  };

  const handleSubmit = async (event) => {
    event.preventDefault();

    const nextErrors = validate();
    setErrors(nextErrors);
    setSubmitError(null);

    if (Object.keys(nextErrors).length > 0) {
      return;
    }

    const selectedCategory = categories.find(
      (category) => String(category.id) === String(effectiveCategoryId),
    );
    const payload = {
      name: name.trim(),
      price: Number(price),
      stock: Number(stock),
      category: selectedCategory.name,
      categoryId: selectedCategory.id,
    };

    setSubmitting(true);

    try {
      const result = await onSubmit(payload);
      onSuccess(result);
    } catch (error) {
      setSubmitError(error?.response?.data?.message || error.message || "A apărut o eroare la salvare.");
    } finally {
      setSubmitting(false);
    }
  };

  const initialCategoryMatch = categories.find(
    (category) =>
      String(category.id) === String(normalizedInitialValues.categoryId) ||
      category.name.toLowerCase() === String(normalizedInitialValues.category).toLowerCase(),
  );
  const effectiveCategoryId = categoryId || String(initialCategoryMatch?.id ?? categories[0]?.id ?? "");
  const selectedCategory = categories.find((category) => String(category.id) === String(effectiveCategoryId));

  return (
    <form className="product-form" onSubmit={handleSubmit} noValidate>
      {categoriesLoading ? <p className="status">Se încarcă categoriile...</p> : null}
      {categoriesError ? (
        <p className="status status-error">Nu am putut încărca categoriile: {categoriesError.message}</p>
      ) : null}
      {submitError ? <p className="status status-error">{submitError}</p> : null}

      <label className="field">
        <span>Nume</span>
        <input value={name} onChange={(event) => setName(event.target.value)} />
        {errors.name ? <small className="field-error">{errors.name}</small> : null}
      </label>

      <label className="field">
        <span>Preț</span>
        <input
          type="number"
          min="0.01"
          step="0.01"
          value={price}
          onChange={(event) => setPrice(event.target.value)}
        />
        {errors.price ? <small className="field-error">{errors.price}</small> : null}
      </label>

      <label className="field">
        <span>Stoc</span>
        <input
          type="number"
          min="0"
          step="1"
          value={stock}
          onChange={(event) => setStock(event.target.value)}
        />
        {errors.stock ? <small className="field-error">{errors.stock}</small> : null}
      </label>

      <label className="field">
        <span>Categorie</span>
        <select
          value={effectiveCategoryId}
          onChange={(event) => setCategoryId(event.target.value)}
          disabled={categoriesLoading || categories.length === 0}
        >
          <option value="">Alege o categorie</option>
          {categories.map((category) => (
            <option key={category.id} value={category.id}>
              {category.name}
            </option>
          ))}
        </select>
        {errors.categoryId ? <small className="field-error">{errors.categoryId}</small> : null}
        {selectedCategory ? <small className="field-hint">Selectat: {selectedCategory.name}</small> : null}
      </label>

      <div className="form-actions">
        <button type="button" className="btn-secondary" onClick={onCancel} disabled={submitting}>
          Anulează
        </button>
        <button type="submit" className="btn-primary" disabled={submitting || categories.length === 0}>
          {submitting ? "Se salvează..." : submitLabel}
        </button>
      </div>
    </form>
  );
}

export default ProductForm;
