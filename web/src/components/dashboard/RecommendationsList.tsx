import { Recommendation } from '@/lib/api/types';

const PriorityBadge = ({ priority }: { priority: string }) => {
  if (priority === 'HIGH') {
    return <span className="bg-rose-900/40 text-rose-400 border border-rose-800/50 px-2 py-0.5 rounded text-xs font-bold">HIGH PRIORITY</span>;
  }
  if (priority === 'MEDIUM') {
    return <span className="bg-amber-900/40 text-amber-400 border border-amber-800/50 px-2 py-0.5 rounded text-xs font-bold">MEDIUM PRIORITY</span>;
  }
  return <span className="bg-slate-800 text-slate-400 border border-slate-700 px-2 py-0.5 rounded text-xs font-bold">LOW PRIORITY</span>;
};

export default function RecommendationsList({ recommendations }: { recommendations: Recommendation[] }) {
  if (recommendations.length === 0) return null;

  return (
    <div className="bg-slate-800/30 border border-slate-700/50 rounded-xl overflow-hidden">
      <div className="px-6 py-4 border-b border-slate-700/50 bg-slate-800/50">
        <h2 className="text-lg font-semibold text-white">Recommendations</h2>
      </div>
      
      <div className="p-4 space-y-4">
        {recommendations.map((rec) => (
          <div key={rec.concept} className="bg-slate-900/50 border border-slate-700/50 rounded-lg p-5">
            <div className="flex justify-between items-start mb-3">
              <h3 className="font-semibold text-slate-200">{rec.displayName}</h3>
              <PriorityBadge priority={rec.priority} />
            </div>
            
            <div className="space-y-3 text-sm">
              <div>
                <span className="block text-slate-500 mb-0.5 font-medium">Why?</span>
                <p className="text-slate-300">{rec.reason}</p>
              </div>
              
              <div>
                <span className="block text-sky-500 mb-0.5 font-medium">Suggested Action</span>
                <p className="text-sky-100/90">{rec.suggestedAction}</p>
              </div>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}
