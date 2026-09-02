import type { Metadata } from "next";
import "./globals.css";
import { AuthProvider } from "@/contexts/AuthContext";
import Link from "next/link";

export const metadata: Metadata = {
  title: "CodePilot - AI-Powered Coding Mentor",
  description: "Teach the student how to think, not just what to type.",
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="en">
      <body className="antialiased min-h-screen bg-slate-900 text-slate-100 flex flex-col">
        <AuthProvider>
          <header className="border-b border-slate-800 bg-slate-950/50 backdrop-blur px-6 py-4 flex items-center justify-between">
            <div className="flex items-center space-x-3">
              <div className="w-8 h-8 rounded-lg bg-sky-500 flex items-center justify-center font-bold text-slate-950">
                CP
              </div>
              <span className="font-semibold text-lg tracking-tight">CodePilot</span>
              <span className="text-xs px-2 py-0.5 rounded bg-sky-950 text-sky-400 border border-sky-800">
                CodePilot
              </span>
            </div>
            <nav className="flex items-center space-x-6 text-sm text-slate-400">
              <Link href="/dashboard" className="hover:text-slate-200 transition-colors">Dashboard</Link>
            </nav>
          </header>

          <main className="flex-1 max-w-7xl w-full mx-auto p-6">
            {children}
          </main>

          <footer className="border-t border-slate-800 py-6 text-center text-xs text-slate-500">
            CodePilot Project Learning Engine &copy; 2026
          </footer>
        </AuthProvider>
      </body>
    </html>
  );
}
