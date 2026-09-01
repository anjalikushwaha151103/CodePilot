import { describe, it, expect, beforeEach, afterEach } from 'vitest';
import { LeetCodeAdapter } from '../src/content/adapters/LeetCodeAdapter';
import { CodeforcesAdapter } from '../src/content/adapters/CodeforcesAdapter';
import { platformRegistry } from '../src/content/adapters/PlatformRegistry';
import { JSDOM } from 'jsdom';

describe('Platform Detection', () => {
  it('LeetCodeAdapter should match valid LeetCode problem URLs', () => {
    const adapter = new LeetCodeAdapter();
    expect(adapter.canHandle('https://leetcode.com/problems/two-sum/')).toBe(true);
    expect(adapter.canHandle('https://leetcode.com/problems/add-two-numbers/description/')).toBe(true);
    
    expect(adapter.canHandle('https://leetcode.com/discuss/')).toBe(false);
    expect(adapter.canHandle('https://codeforces.com/problemset/problem/1/A')).toBe(false);
  });

  it('CodeforcesAdapter should match valid Codeforces problem URLs', () => {
    const adapter = new CodeforcesAdapter();
    expect(adapter.canHandle('https://codeforces.com/problemset/problem/1/A')).toBe(true);
    expect(adapter.canHandle('https://codeforces.com/contest/123/problem/A')).toBe(true);
    
    expect(adapter.canHandle('https://codeforces.com/blog/entry/123')).toBe(false);
    expect(adapter.canHandle('https://leetcode.com/problems/two-sum/')).toBe(false);
  });

  it('PlatformRegistry should return correct adapter', () => {
    const lc = platformRegistry.getAdapterForUrl('https://leetcode.com/problems/two-sum/');
    expect(lc).toBeInstanceOf(LeetCodeAdapter);

    const cf = platformRegistry.getAdapterForUrl('https://codeforces.com/problemset/problem/1/A');
    expect(cf).toBeInstanceOf(CodeforcesAdapter);

    const unknown = platformRegistry.getAdapterForUrl('https://google.com');
    expect(unknown).toBeNull();
  });
});

describe('Problem Context Extraction via DOM', () => {
  
  afterEach(() => {
    // Clean up JSDOM globals if we injected them
    delete (global as any).window;
    delete (global as any).document;
  });

  it('LeetCodeAdapter should conservatively extract ProblemContext', async () => {
    const dom = new JSDOM(`
      <!DOCTYPE html>
      <html>
        <head><title>Two Sum - LeetCode</title></head>
        <body>
          <div data-cy="question-title">Two Sum</div>
          <div class="text-difficulty-easy">Easy</div>
          <div data-track-load="description_content"><p>Given an array...</p></div>
        </body>
      </html>
    `, { url: "https://leetcode.com/problems/two-sum/" });
    
    (global as any).window = dom.window;
    (global as any).document = dom.window.document;

    const adapter = new LeetCodeAdapter();
    const context = await adapter.extractProblemContext();

    expect(context).not.toBeNull();
    expect(context?.platform).toBe('LEETCODE');
    expect(context?.problemId).toBe('two-sum');
    expect(context?.title).toBe('Two Sum');
    expect(context?.difficulty).toBe('Easy');
    expect(context?.description).toContain('Given an array');
  });

  it('CodeforcesAdapter should conservatively extract ProblemContext', async () => {
    const dom = new JSDOM(`
      <!DOCTYPE html>
      <html>
        <head><title>Problem - 1A - Codeforces</title></head>
        <body>
          <div class="problem-statement">
            <div class="header">
              <div class="title">A. Theatre Square</div>
            </div>
            <div>
              <p>Theatre Square in the capital city of Berland...</p>
            </div>
          </div>
          <div class="tag-box" title="math">math</div>
        </body>
      </html>
    `, { url: "https://codeforces.com/problemset/problem/1/A" });
    
    (global as any).window = dom.window;
    (global as any).document = dom.window.document;

    const adapter = new CodeforcesAdapter();
    const context = await adapter.extractProblemContext();

    expect(context).not.toBeNull();
    expect(context?.platform).toBe('CODEFORCES');
    expect(context?.problemId).toBe('1A');
    expect(context?.title).toBe('A. Theatre Square');
    expect(context?.description).toContain('Theatre Square');
    expect(context?.tags).toEqual(['math']);
  });
});
