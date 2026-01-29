import { motion } from "framer-motion";
import { useNavigate } from "react-router-dom";
import {
  ArrowRight,
  Boxes,
  BarChart3,
  ShieldCheck,
  Warehouse,
  Activity,
  FileText,
  Lock,
  Users,
} from "lucide-react";

/* ===================== HOME ===================== */

export default function Home() {
  const navigate = useNavigate();

  return (
    <div className="min-h-screen bg-gradient-to-br from-slate-200 via-slate-300 to-slate-500 text-white">

      {/* ================= NAVBAR ================= */}
      <nav className="top-0 z-50 flex justify-between items-center px-10 py-6 ">
        <h1 className="font-bold tracking-wide text-blue-950 text-3xl">
          The Merchant's Ledger
        </h1>

        <div className="space-x-6 text-sm text-blue-950">
          <a href="#features" className="hover:text-blue-600">Features</a>
          <a href="#analytics" className="hover:text-blue-600">Analytics</a>
          <a href="#security" className="hover:text-blue-600">Security</a>
          <button onClick={()=>navigate("/login")}
          className="px-5 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition">
            Login
          </button>
        </div>
      </nav>

      {/* ================= HERO ================= */}
      <section className="flex flex-col items-center text-center px-6 mt-24">
        <motion.h2
          initial={{ opacity: 0, y: 30 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.8 }}
          className="text-5xl font-extrabold leading-tight max-w-4xl text-blue-950"
        >
          Unified Inventory Management <br />
          <span className="text-blue-600">for Enterprise-Scale Operations</span>
        </motion.h2>

        <motion.p
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.3 }}
          className="mt-6 max-w-2xl text-lg text-slate-700"
        >
          Centralize inventory, automate workflows, gain deep insights,
          and protect your data with enterprise-grade security.
        </motion.p>

        <motion.div
          initial={{ opacity: 0 }}
          animate={{ opacity: 1 }}
          transition={{ delay: 0.6 }}
          className="mt-10 flex gap-4"
        >
          <button
            type="button"
            onClick={() => navigate("/signup")}
            className="flex items-center gap-2 px-7 py-3 bg-blue-600 rounded-lg hover:bg-blue-700 transition"
          >
            Get Started <ArrowRight size={18} />
          </button>
          <a
            href="#analytics"
            className="px-7 py-3 border border-blue-600 text-blue-600 rounded-lg hover:bg-blue-600 hover:text-white transition"
          >
            View Insights
          </a>
        </motion.div>
      </section>

      {/* ================= FEATURES ================= */}
      <section id="features" className="mt-40 px-10 max-w-6xl mx-auto scroll-mt-32">
        <SectionTitle
          title="Core Platform Features"
          subtitle="Designed to scale with growing businesses and complex supply chains."
        />

        <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
          <FeatureItem
            icon={Boxes}
            delay={0.2}
            title="Real-Time Stock Tracking"
            description="Instant visibility into stock levels across all locations with live updates."
          />
          <FeatureItem
            icon={Warehouse}
            delay={0.4}
            title="Multi-Warehouse Control"
            description="Centralized management of inventory transfers, allocation, and availability."
          />
          <FeatureItem
            icon={Activity}
            delay={0.6}
            title="Event-Driven Inventory"
            description="Every stock movement is logged as an event for traceability and audits."
          />
          <FeatureItem
            icon={Users}
            delay={0.8}
            title="Team Collaboration"
            description="Enable warehouse managers, admins, and staff to work seamlessly together."
          />
          <FeatureItem
            icon={FileText}
            delay={1.0}
            title="Automated Documentation"
            description="Generate invoices, transfer slips, and inventory reports automatically."
          />
          <FeatureItem
            icon={Boxes}
            delay={1.2}
            title="Low Stock Alerts"
            description="Smart alerts prevent stockouts and overstocking before they happen."
          />
        </div>
      </section>

      {/* ================= ANALYTICS ================= */}
      <section id="analytics" className="mt-40 px-10 max-w-6xl mx-auto scroll-mt-32">
        <SectionTitle
          delay={0.2}
          title="Advanced Analytics & Insights"
          subtitle="Make data-driven decisions with actionable intelligence."
        />

        <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
          <FeatureItem
            icon={BarChart3}
            delay={0.2}
            title="Sales & Demand Forecasting"
            description="Predict demand trends using historical sales and seasonal data."
          />
          <FeatureItem
            icon={Activity}
            delay={0.4}
            title="Inventory Turnover Metrics"
            description="Track fast-moving and slow-moving products to optimize storage."
          />
          <FeatureItem
            icon={BarChart3}
            delay={0.6}
            title="Warehouse Performance"
            description="Measure dispatch speed, fulfillment accuracy, and operational efficiency."
          />
          <FeatureItem
            icon={FileText}
            delay={0.8}
            title="Custom Reports"
            description="Generate downloadable reports tailored for finance and operations teams."
          />
          <FeatureItem
            icon={Activity}
            delay={1.0}
            title="Real-Time Dashboards"
            description="Live KPIs with visual charts for instant decision-making."
          />
          <FeatureItem
            icon={BarChart3}
            delay={1.2}
            title="Event Analytics"
            description="Analyze stock movement patterns using event-based data streams."
          />
        </div>
      </section>

      {/* ================= SECURITY ================= */}
      <section id="security" className="mt-40 px-10 max-w-6xl mx-auto scroll-mt-32">
        <SectionTitle
          delay={0.2}
          title="Enterprise-Grade Security"
          subtitle="Built with compliance, reliability, and trust at the core."
        />

        <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
          <FeatureItem
            icon={ShieldCheck}
            delay={0.2}
            title="Role-Based Access Control"
            description="Granular permissions for admins, managers, and warehouse staff."
          />
          <FeatureItem
            icon={Lock}
            delay={0.4}
            title="Data Encryption"
            description="All sensitive data is encrypted at rest and in transit."
          />
          <FeatureItem
            icon={FileText} 
            delay={0.6}
            title="Audit Logs"
            description="Complete audit trails for every inventory and user action."
          />
          <FeatureItem
            icon={ShieldCheck}
            delay={0.8}
            title="Compliance Ready"
            description="Designed to meet enterprise compliance and audit requirements."
          />
          <FeatureItem
            icon={Users}
            delay={1.0}
            title="User Activity Monitoring"
            description="Track user behavior to detect anomalies and prevent misuse."
          />
          <FeatureItem
            icon={Lock}
            delay={1.2}
            title="Secure Authentication"
            description="Multi-factor authentication and secure session handling."
          />
        </div>
      </section>

      {/* ================= FOOTER ================= */}
      <footer className="mt-40 py-10 text-center text-slate-600 text-sm">
        (c) 2026 The Merchant's Ledger. Built for modern enterprises.
      </footer>
    </div>
  );
}

/* ================= SECTION TITLE ================= */

function SectionTitle({ title, subtitle }) {
  return (
    <motion.div
      initial={{ opacity: 0, y: 20 }}
      whileInView={{ opacity: 1, y: 0 }}
      viewport={{ once: true }}
      transition={{ duration: 0.5 }}
      className="mb-14 text-center"
    >
      <h2 className="text-3xl font-bold text-blue-950">{title}</h2>
      <p className="mt-3 text-slate-600 max-w-2xl mx-auto">{subtitle}</p>
    </motion.div>
  );
}

/* ================= FEATURE ITEM ================= */

export function FeatureItem({ icon: Icon, title, description,delay=0 }) {
  return (
    <motion.div
      initial={{ opacity: 0, y: 24 }}
      whileInView={{ opacity: 1, y: 0 }}
      viewport={{ once: true }}
      transition={{ delay, duration: 0.4, ease: "easeOut" }}
      
      whileHover={{ y: -6 }}
      className="
        group relative rounded-xl border border-slate-800 
        bg-gradient-to-b from-slate-900 to-slate-950 
        p-6 transition-all duration-300
        hover:border-blue-500/60 hover:shadow-lg hover:shadow-blue-500/10
      "
    >
      <div className="mb-4 inline-flex items-center justify-center rounded-lg bg-blue-500/10 p-2">
        <Icon size={26} className="text-blue-500" />
      </div>

      <h3 className="text-lg font-semibold text-white">{title}</h3>
      <p className="mt-2 text-sm text-slate-400">{description}</p>
    </motion.div>
  );
}
