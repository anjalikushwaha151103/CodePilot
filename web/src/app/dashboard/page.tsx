"use client";

import { useEffect, useState } from 'react';
import { useRouter } from 'next/navigation';
import { useAuth } from '@/contexts/AuthContext';
import { ApiClient } from '@/lib/api/client';
import { LearningProfile } from '@/lib/api/types';
import OverviewCards from '@/components/dashboard/OverviewCards';
import ConceptMasteryList from '@/components/dashboard/ConceptMasteryList';
import RecentActivityTable from '@/components/dashboard/RecentActivityTable';
import RecommendationsList from '@/components/dashboard/RecommendationsList';

export default function DashboardPage() {
  const { isAuthenticated, isLoading: authLoading } = useAuth();
  const [profile, setProfile] = useState<LearningProfile | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const router = useRouter();

  useEffect(() => {
    if (!authLoading && !isAuthenticated) {
      router.push('/login');
    }
  }, [authLoading, isAuthenticated, router]);

  useEffect(() => {
    if (isAuthenticated) {
      loadProfile();
    }
  }, [isAuthenticated]);

  const loadProfile = async () => {
    try {
      setLoading(true);
      const data = await ApiClient.getLearningProfile();
      setProfile(data);
    } catch (err: any) {
      setError(err.message || 'Failed to load learning profile');
    } finally {
      setLoading(false);
    }
  };

  if (authLoading || (loading && !error)) {
    return (
      <div className="flex items-center justify-center h-64 text-slate-400">
        Loading dashboard...
      </div>
    );
  }

  if (error) {
    return (
      <div className="bg-red-900/30 border border-red-500/30 text-red-400 p-6 rounded-lg text-center mt-10">
        <h2 className="text-lg font-semibold mb-2">Error Loading Profile</h2>
        <p>{error}</p>
        <button 
          onClick={loadProfile}
          className="mt-4 px-4 py-2 bg-slate-800 hover:bg-slate-700 rounded text-slate-200 transition-colors"
        >
          Try Again
        </button>
      </div>
    );
  }

  if (!profile || profile.totalSessions === 0) {
    return (
      <div className="flex flex-col items-center justify-center h-96 text-center">
        <div className="w-24 h-24 mb-6 text-sky-500 opacity-50">
          <svg fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={1.5}>
            <path strokeLinecap="round" strokeLinejoin="round" d="M15.362 5.214A8.252 8.252 0 0112 21 8.25 8.25 0 016.038 7.048 8.287 8.287 0 009 9.6a8.983 8.983 0 013.361-6.867 8.21 8.21 0 003 2.48z" />
            <path strokeLinecap="round" strokeLinejoin="round" d="M12 18a3.75 3.75 0 00.495-7.467 5.99 5.99 0 00-1.925 3.546 5.974 5.974 0 01-2.133-1A3.75 3.75 0 0012 18z" />
          </svg>
        </div>
        <h2 className="text-2xl font-bold text-white mb-2">Welcome to your Learning Dashboard</h2>
        <p className="text-slate-400 max-w-md">
          Your learning profile is waiting for your first tutoring session. Go to your IDE and solve a problem with CodePilot to get started!
        </p>
      </div>
    );
  }

  return (
    <div className="space-y-8 animate-in fade-in duration-500">
      <div>
        <h1 className="text-2xl font-bold text-white mb-1">Learning Dashboard</h1>
        <p className="text-slate-400 text-sm">Your AI-driven personalized learning progress.</p>
      </div>

      <OverviewCards profile={profile} />

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
        <div className="lg:col-span-2 space-y-8">
          <ConceptMasteryList concepts={profile.conceptMastery} />
          <RecentActivityTable activities={profile.recentActivity} />
        </div>
        
        <div className="space-y-8">
          <RecommendationsList recommendations={profile.recommendations} />
        </div>
      </div>
    </div>
  );
}
