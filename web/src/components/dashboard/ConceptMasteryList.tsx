import { ConceptMastery } from '@/lib/api/types';
import MasteryChart from './MasteryChart';

const TrendIcon = ({ trend }: { trend: string }) => {
  if (trend === 'IMPROVING') return <span className="text-emerald-400 font-bold" title="Improving">↗</span>;
  if (trend === 'DECLINING') return <span className="text-rose-400 font-bold" title="Declining">↘</span>;
  if (trend === 'STABLE') return <span className="text-slate-400 font-bold" title="Stable">→</span>;
  return <span className="text-sky-400 text-xs font-bold px-1.5 py-0.5 bg-sky-900/50 rounded" title="New">NEW</span>;
};

export default function ConceptMasteryList({ concepts }: { concepts: ConceptMastery[] }) {
  if (concepts.length === 0) return null;

  return (
    <div className="bg-slate-800/30 border border-slate-700/50 rounded-xl overflow-hidden">
      <div className="px-6 py-4 border-b border-slate-700/50 bg-slate-800/50">
        <h2 className="text-lg font-semibold text-white">Concept Mastery</h2>
      </div>
      
      <div className="divide-y divide-slate-700/50">
        {concepts.map((concept) => (
          <div key={concept.concept} className="p-6 hover:bg-slate-800/20 transition-colors">
            <div className="flex flex-col md:flex-row md:items-center justify-between mb-4 gap-4">
              <div className="flex items-center space-x-3">
                <h3 className="font-medium text-slate-200">{concept.displayName}</h3>
                <div className="flex items-center space-x-1.5">
                  <TrendIcon trend={concept.trend} />
                </div>
              </div>
              
              <div className="flex items-center space-x-6 text-sm">
                <div className="text-slate-400">
                  Level: <span className="text-slate-200">{concept.level}</span>
                </div>
                <div className="text-slate-400">
                  Score: <span className="font-mono text-sky-400">{concept.masteryScore.toFixed(1)}</span>
                </div>
              </div>
            </div>
            
            <MasteryChart score={concept.masteryScore} />
            
            <div className="mt-4 flex flex-wrap gap-4 text-xs text-slate-500">
              <span>{concept.attempts} sessions</span>
              <span>•</span>
              <span>{concept.successfulSessions} successful</span>
              <span>•</span>
              <span>Avg Hint Level: {concept.averageHintLevel.toFixed(1)}</span>
              {concept.solutionReveals > 0 && (
                <>
                  <span>•</span>
                  <span className="text-rose-400/80">{concept.solutionReveals} reveals</span>
                </>
              )}
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}
