export default function MasteryChart({ score }: { score: number }) {
  const percentage = Math.max(0, Math.min(100, score));
  
  let colorClass = 'bg-sky-500';
  if (score < 40) colorClass = 'bg-rose-500';
  else if (score < 70) colorClass = 'bg-amber-500';
  else if (score >= 85) colorClass = 'bg-emerald-500';

  return (
    <div className="w-full h-2 bg-slate-900 rounded-full overflow-hidden flex">
      <div 
        className={`h-full transition-all duration-1000 ease-out ${colorClass}`} 
        style={{ width: `${percentage}%` }}
      />
    </div>
  );
}
