import { render } from '@testing-library/react';
import MasteryChart from '../src/components/dashboard/MasteryChart';

describe('MasteryChart', () => {
  it('renders correctly with high score', () => {
    const { container } = render(<MasteryChart score={90} />);
    const bar = container.querySelector('.bg-emerald-500');
    expect(bar).toBeInTheDocument();
    expect(bar).toHaveStyle('width: 90%');
  });

  it('renders correctly with medium score', () => {
    const { container } = render(<MasteryChart score={50} />);
    const bar = container.querySelector('.bg-amber-500');
    expect(bar).toBeInTheDocument();
    expect(bar).toHaveStyle('width: 50%');
  });

  it('renders correctly with low score', () => {
    const { container } = render(<MasteryChart score={20} />);
    const bar = container.querySelector('.bg-rose-500');
    expect(bar).toBeInTheDocument();
    expect(bar).toHaveStyle('width: 20%');
  });

  it('bounds the score between 0 and 100', () => {
    const { container } = render(<MasteryChart score={150} />);
    const bar = container.querySelector('.bg-emerald-500');
    expect(bar).toHaveStyle('width: 100%');
  });
});
