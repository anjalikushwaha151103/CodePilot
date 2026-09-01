'use client';

import { useEffect } from 'react';

export default function Error({
  error,
  reset,
}: {
  error: Error & { digest?: string };
  reset: () => void;
}) {
  useEffect(() => {
    console.error('Unhandled Next.js application error:', error);
  }, [error]);

  return (
    <div className="min-h-[400px] flex flex-col items-center justify-center space-y-4 text-center">
      <h2 className="text-xl font-bold text-red-400">Something went wrong!</h2>
      <p className="text-slate-400 max-w-md text-sm">{error.message || 'An unexpected error occurred.'}</p>
      <button
        onClick={() => reset()}
        className="px-4 py-2 bg-sky-600 hover:bg-sky-500 text-white rounded-md text-sm font-medium transition-colors"
      >
        Try again
      </button>
    </div>
  );
}
