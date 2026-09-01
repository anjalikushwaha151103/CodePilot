import { RecentActivity } from '@/lib/api/types';

export default function RecentActivityTable({ activities }: { activities: RecentActivity[] }) {
  if (activities.length === 0) return null;

  return (
    <div className="bg-slate-800/30 border border-slate-700/50 rounded-xl overflow-hidden mt-8">
      <div className="px-6 py-4 border-b border-slate-700/50 bg-slate-800/50">
        <h2 className="text-lg font-semibold text-white">Recent Activity</h2>
      </div>
      
      <div className="overflow-x-auto">
        <table className="w-full text-sm text-left text-slate-400">
          <thead className="text-xs text-slate-500 uppercase bg-slate-900/50">
            <tr>
              <th className="px-6 py-3 font-medium">Concept</th>
              <th className="px-6 py-3 font-medium">Date</th>
              <th className="px-6 py-3 font-medium">Hint Level</th>
              <th className="px-6 py-3 font-medium text-right">Mastery Impact</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-slate-800">
            {activities.map((activity, idx) => {
              const delta = activity.masteryDelta;
              const isPositive = delta > 0;
              const isZero = delta === 0;
              
              return (
                <tr key={idx} className="hover:bg-slate-800/30">
                  <td className="px-6 py-4 font-medium text-slate-200">
                    {activity.displayName}
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap">
                    {new Date(activity.date).toLocaleDateString(undefined, { 
                      month: 'short', day: 'numeric', hour: 'numeric', minute: '2-digit' 
                    })}
                  </td>
                  <td className="px-6 py-4">
                    {activity.hintLevel === 0 ? 'No Hints' : `Level ${activity.hintLevel}`}
                  </td>
                  <td className="px-6 py-4 text-right font-mono">
                    <span className={
                      isPositive ? 'text-emerald-400' : isZero ? 'text-slate-500' : 'text-rose-400'
                    }>
                      {isPositive ? '+' : ''}{delta.toFixed(1)}
                    </span>
                  </td>
                </tr>
              );
            })}
          </tbody>
        </table>
      </div>
    </div>
  );
}
