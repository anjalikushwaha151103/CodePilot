import { render, screen } from '@testing-library/react';
import OverviewCards from '../src/components/dashboard/OverviewCards';
import { LearningProfile } from '../src/lib/api/types';

describe('OverviewCards', () => {
  const mockProfile: LearningProfile = {
    totalSessions: 10,
    conceptsTracked: 5,
    averageMastery: 75.5,
    hintStatistics: {
      averageHintLevel: 1.2,
      totalSolutionReveals: 0,
      highHintDependencyCount: 0
    },
    strongestConcepts: [],
    weakestConcepts: [],
    conceptMastery: [],
    recentActivity: [],
    recommendations: []
  };

  it('renders overall mastery correctly', () => {
    render(<OverviewCards profile={mockProfile} />);
    expect(screen.getByText('Overall Mastery')).toBeInTheDocument();
    expect(screen.getByText('75.5')).toBeInTheDocument();
  });

  it('renders concepts tracked correctly', () => {
    render(<OverviewCards profile={mockProfile} />);
    expect(screen.getByText('Concepts Tracked')).toBeInTheDocument();
    expect(screen.getByText('5')).toBeInTheDocument();
  });

  it('renders total sessions correctly', () => {
    render(<OverviewCards profile={mockProfile} />);
    expect(screen.getByText('Total Sessions')).toBeInTheDocument();
    expect(screen.getByText('10')).toBeInTheDocument();
  });
});
