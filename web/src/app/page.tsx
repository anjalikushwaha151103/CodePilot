import React from 'react';

export default function Home() {
  return (
    <div className="space-y-8 py-8">
      <section className="bg-slate-950 border border-slate-800 rounded-xl p-8 space-y-4">
        <h1 className="text-3xl font-bold tracking-tight text-white">
          CodePilot <span className="text-sky-400">Dashboard</span> Placeholder
        </h1>
        <p className="text-slate-400 max-w-2xl leading-relaxed">
          Welcome to the CodePilot Web Application Foundation. This dashboard will serve as the longitudinal analytics center for student concept mastery profiles, tutoring history, and practice recommendations.
        </p>
        <div className="pt-2 flex items-center space-x-4">
          <div className="flex items-center space-x-2 text-xs bg-slate-900 border border-slate-700 px-3 py-1.5 rounded-md text-emerald-400">
            <span className="w-2 h-2 rounded-full bg-emerald-500 animate-pulse"></span>
            <span>Phase 1 Scaffolding Ready</span>
          </div>
        </div>
      </section>

      <section className="grid md:grid-cols-3 gap-6">
        <div className="bg-slate-950/40 border border-slate-800 p-6 rounded-lg space-y-2">
          <h3 className="font-semibold text-slate-200">Browser Extension</h3>
          <p className="text-sm text-slate-400">Manifest V3 Side Panel foundation loadable in Chrome/Chromium.</p>
        </div>

        <div className="bg-slate-950/40 border border-slate-800 p-6 rounded-lg space-y-2">
          <h3 className="font-semibold text-slate-200">Spring Boot Core</h3>
          <p className="text-sm text-slate-400">Java 21 backend providing REST APIs, PostgreSQL & Redis infrastructure.</p>
        </div>

        <div className="bg-slate-950/40 border border-slate-800 p-6 rounded-lg space-y-2">
          <h3 className="font-semibold text-slate-200">FastAPI AI Service</h3>
          <p className="text-sm text-slate-400">Python 3.12 microservice for AST static code intelligence & LLM routing.</p>
        </div>
      </section>
    </div>
  );
}
