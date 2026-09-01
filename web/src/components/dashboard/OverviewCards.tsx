import { LearningProfile } from '@/lib/api/types';

export default function OverviewCards({ profile }: { profile: LearningProfile }) {
  return (
    <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
      <div className="bg-slate-800/50 border border-slate-700 p-5 rounded-xl">
        <h3 className="text-slate-400 text-sm font-medium mb-1">Overall Mastery</h3>
        <div className="flex items-baseline space-x-2">
          <span className="text-3xl font-bold text-white">{profile.averageMastery}</span>
          <span className="text-slate-500 text-sm">/ 100</span>
        </div>
      </div>
      
      <div className="bg-slate-800/50 border border-slate-700 p-5 rounded-xl">
        <h3 className="text-slate-400 text-sm font-medium mb-1">Concepts Tracked</h3>
        <div className="text-3xl font-bold text-white">{profile.conceptsTracked}</div>
      </div>

      <div className="bg-slate-800/50 border border-slate-700 p-5 rounded-xl">
        <h3 className="text-slate-400 text-sm font-medium mb-1">Total Sessions</h3>
        <div className="text-3xl font-bold text-white">{profile.totalSessions}</div>
      </div>

      <div className="bg-slate-800/50 border border-slate-700 p-5 rounded-xl">
        <h3 className="text-slate-400 text-sm font-medium mb-1">Avg Hint Dependency</h3>
        <div className="flex items-baseline space-x-2">
          <span className="text-3xl font-bold text-white">{profile.hintStatistics.averageHintLevel}</span>
          <span className="text-slate-500 text-sm">hints / session</span>
        </div>
      </div>
    </div>
  );
}
