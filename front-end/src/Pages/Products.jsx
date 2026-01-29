import { useEffect, useState } from "react";
import { apiRequest } from "../lib/api";

export default function Products() {
  const [products, setProducts] = useState([]);
  const [form, setForm] = useState({
    name: "",
    sku: "",
    barcode: "",
    category: "",
    unit: "",
    reorderLevel: "",
  });
  const [barcodeQuery, setBarcodeQuery] = useState("");
  const [barcodeResult, setBarcodeResult] = useState(null);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  const load = async () => {
    try {
      const data = await apiRequest("/api/products");
      setProducts(data);
    } catch (err) {
      setError(err.message || "Failed to load products");
    }
  };

  useEffect(() => {
    load();
  }, []);

  const handleChange = (event) => {
    const { name, value } = event.target;
    setForm((prev) => ({ ...prev, [name]: value }));
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    setError("");
    setSuccess("");

    try {
      await apiRequest("/api/products", {
        method: "POST",
        body: JSON.stringify({
          ...form,
          reorderLevel: form.reorderLevel ? Number(form.reorderLevel) : 0,
        }),
      });
      setForm({ name: "", sku: "", barcode: "", category: "", unit: "", reorderLevel: "" });
      setSuccess("Product added");
      load();
    } catch (err) {
      setError(err.message || "Failed to add product");
    }
  };

  const handleBarcodeLookup = async (event) => {
    event.preventDefault();
    setError("");
    setBarcodeResult(null);
    try {
      const result = await apiRequest(`/api/products/barcode/${barcodeQuery}`);
      setBarcodeResult(result);
    } catch (err) {
      setError(err.message || "Barcode not found");
    }
  };

  return (
    <div className="space-y-8">
      <header>
        <h2 className="text-2xl font-semibold">Products</h2>
        <p className="text-slate-600">Manage SKUs, barcodes, and catalog data.</p>
      </header>

      {(error || success) && (
        <div className={`rounded-lg border p-4 ${error ? "border-rose-200 bg-rose-50 text-rose-600" : "border-emerald-200 bg-emerald-50 text-emerald-600"}`}>
          {error || success}
        </div>
      )}

      <section className="grid gap-6 lg:grid-cols-[1fr_1.4fr]">
        <form onSubmit={handleSubmit} className="rounded-2xl bg-white p-6 shadow-sm">
          <h3 className="text-lg font-semibold">Add product</h3>
          <div className="mt-6 space-y-4">
            <Field label="Name" name="name" value={form.name} onChange={handleChange} required />
            <Field label="SKU" name="sku" value={form.sku} onChange={handleChange} required />
            <Field label="Barcode" name="barcode" value={form.barcode} onChange={handleChange} />
            <Field label="Category" name="category" value={form.category} onChange={handleChange} />
            <Field label="Unit" name="unit" value={form.unit} onChange={handleChange} />
            <Field label="Reorder level" name="reorderLevel" value={form.reorderLevel} onChange={handleChange} />
          </div>
          <button
            type="submit"
            className="mt-6 w-full rounded-lg bg-slate-900 px-4 py-2.5 text-sm font-semibold text-white"
          >
            Save product
          </button>
        </form>

        <div className="rounded-2xl bg-white p-6 shadow-sm">
          <h3 className="text-lg font-semibold">Catalog</h3>
          <div className="mt-4 overflow-x-auto">
            <table className="min-w-full text-left text-sm">
              <thead className="border-b border-slate-200 text-xs uppercase text-slate-400">
                <tr>
                  <th className="py-2">Name</th>
                  <th className="py-2">SKU</th>
                  <th className="py-2">Category</th>
                  <th className="py-2">Unit</th>
                  <th className="py-2">Reorder</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100">
                {products.map((product) => (
                  <tr key={product.id}>
                    <td className="py-3 font-medium text-slate-700">{product.name}</td>
                    <td className="py-3 text-slate-500">{product.sku}</td>
                    <td className="py-3 text-slate-500">{product.category || "-"}</td>
                    <td className="py-3 text-slate-500">{product.unit || "-"}</td>
                    <td className="py-3 text-slate-700">{product.reorderLevel ?? 0}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      </section>

      <section className="rounded-2xl bg-white p-6 shadow-sm">
        <h3 className="text-lg font-semibold">Barcode lookup</h3>
        <form onSubmit={handleBarcodeLookup} className="mt-4 flex flex-wrap gap-3">
          <input
            value={barcodeQuery}
            onChange={(event) => setBarcodeQuery(event.target.value)}
            placeholder="Enter barcode"
            className="flex-1 rounded-md border border-slate-200 px-3 py-2 text-sm"
          />
          <button className="rounded-md bg-slate-900 px-4 py-2 text-sm text-white">Search</button>
        </form>
        {barcodeResult && (
          <div className="mt-4 rounded-lg border border-slate-100 p-4 text-sm text-slate-600">
            <p className="font-semibold text-slate-700">{barcodeResult.name}</p>
            <p>SKU: {barcodeResult.sku}</p>
            <p>Category: {barcodeResult.category || "-"}</p>
            <p>Unit: {barcodeResult.unit || "-"}</p>
          </div>
        )}
      </section>
    </div>
  );
}

function Field({ label, name, value, onChange, required = false }) {
  return (
    <label className="block">
      <span className="text-sm font-medium text-slate-600">{label}</span>
      <input
        name={name}
        value={value}
        onChange={onChange}
        required={required}
        className="mt-2 w-full rounded-lg border border-slate-200 px-3 py-2 text-sm"
      />
    </label>
  );
}
