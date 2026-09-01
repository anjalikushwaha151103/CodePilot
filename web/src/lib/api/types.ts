export type Trend = 'IMPROVING' | 'STABLE' | 'DECLINING' | 'NEW';

export interface ConceptMastery {
  concept: string;
  displayName: string;
  masteryScore: number;
  level: string;
  trend: Trend;
  attempts: number;
  successfulSessions: number;
  solutionReveals: number;
  averageHintLevel: number;
  lastPracticedAt: string;
}

export interface HintStatistics {
  averageHintLevel: number;
  totalSolutionReveals: number;
  highHintDependencyCount: number;
}

export interface RecentActivity {
  concept: string;
  displayName: string;
  hintLevel: number;
  masteryDelta: number;
  date: string;
}

export interface Recommendation {
  concept: string;
  displayName: string;
  priority: 'HIGH' | 'MEDIUM' | 'LOW';
  reason: string;
  suggestedAction: string;
}

export interface LearningProfile {
  totalSessions: number;
  conceptsTracked: number;
  averageMastery: number;
  hintStatistics: HintStatistics;
  strongestConcepts: ConceptMastery[];
  weakestConcepts: ConceptMastery[];
  conceptMastery: ConceptMastery[];
  recentActivity: RecentActivity[];
  recommendations: Recommendation[];
}

export interface User {
  id: string;
  email: string;
  fullName: string;
  role: string;
}

export interface AuthResponse {
  accessToken: string;
  tokenType: string;
  expiresIn: number;
  user: User;
}

export interface ApiResponse<T> {
  success: boolean;
  data?: T;
  error?: string;
  message?: string;
}
