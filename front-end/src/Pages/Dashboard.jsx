import { useEffect, useState } from "react";
import { apiRequest } from "../lib/api";

function isCreditType(type) {
  return type?.includes("CREDIT") || type === "COD_SETTLEMENT" || type === "CHARGEBACK_RELEASE";
}

export default function Dashboard() {
  const [summary, setSummary] = useState(null);
  const [entries, setEntries] = useState([]);
  const [error, setError] = useState("");

  useEffect(() => {
    const load = async () => {
      try {
        const [summaryData, ledgerData] = await Promise.all([
          apiRequest("/api/summary"),
          apiRequest("/api/ledger"),
        ]);
        setSummary(summaryData);
        setEntries(ledgerData);
      } catch (err) {
        setError(err.message || "Failed to load dashboard");
      }
    };

    load();
  }, []);

  return (
    <div className="space-y-8">
      <header className="rounded-2xl bg-white p-6 shadow-sm">
        <h2 className="text-2xl font-semibold">Enterprise Snapshot</h2>
        <p className="text-slate-600">
          Track customer balances, transactions, and activity across the ledger.
        </p>
      </header>

      {error && (
        <div className="rounded-lg border border-rose-200 bg-rose-50 p-4 text-rose-600">
          {error}
        </div>
      )}

      <section className="grid gap-4 md:grid-cols-3">
        <SummaryCard label="Customers" value={summary?.customerCount ?? "-"} />
        <SummaryCard label="Transactions" value={summary?.transactionCount ?? "-"} />
        <SummaryCard
          label="Total Balance"
          value={summary ? `$${Number(summary.totalBalance).toLocaleString()}` : "-"}
        />
      </section>

      <section className="rounded-2xl bg-white p-6 shadow-sm">
        <div className="flex items-center justify-between">
          <div>
            <h3 className="text-lg font-semibold">Recent ledger activity</h3>
            <p className="text-sm text-slate-500">Latest ledger entries across all customers.</p>
          </div>
        </div>
        <div className="mt-4 overflow-x-auto">
          <table className="min-w-full text-left text-sm">
            <thead className="border-b border-slate-200 text-xs uppercase text-slate-400">
              <tr>
                <th className="py-2">Customer</th>
                <th className="py-2">Type</th>
                <th className="py-2">Amount</th>
                <th className="py-2">Description</th>
                <th className="py-2">Date</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100">
              {entries.map((entry) => (
                <tr key={entry.id}>
                  <td className="py-3 font-medium text-slate-700">{entry.customerName}</td>
                  <td className="py-3">
                    <span
                      className={`rounded-full px-3 py-1 text-xs font-semibold ${
                        isCreditType(entry.type)
                          ? "bg-emerald-100 text-emerald-700"
                          : "bg-amber-100 text-amber-700"
                      }`}
                    >
                      {entry.type}
                    </span>
                  </td>
                  <td className="py-3">${Number(entry.amount).toLocaleString()}</td>
                  <td className="py-3 text-slate-500">{entry.description || "-"}</td>
                  <td className="py-3 text-slate-500">
                    {entry.createdAt ? new Date(entry.createdAt).toLocaleDateString() : "-"}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </section>
    </div>
  );
}

function SummaryCard({ label, value }) {
  return (
    <div className="rounded-2xl bg-white p-6 shadow-sm">
      <p className="text-sm text-slate-500">{label}</p>
      <p className="mt-3 text-2xl font-semibold text-slate-900">{value}</p>
    </div>
  );
}
