import { PlatformAdapter } from "./PlatformAdapter";
import { ProblemContext, Platform } from "../../models/ProblemContext";

export class LeetCodeAdapter implements PlatformAdapter {
  getPlatform(): Platform {
    return 'LEETCODE';
  }

  canHandle(url: string): boolean {
    return url.includes('leetcode.com/problems/');
  }

  async extractProblemContext(): Promise<ProblemContext | null> {
    try {
      const url = window.location.href;
      
      // Extract problem slug from URL
      const match = url.match(/leetcode\.com\/problems\/([^/]+)/);
      const problemId = match ? match[1] : null;

      if (!problemId) return null;

      // Title: Try multiple possible selectors in modern LeetCode UI
      let title = document.querySelector('div[data-cy="question-title"]')?.textContent || 
                  document.querySelector('.text-title-large a')?.textContent ||
                  document.title.split('-')[0].trim() ||
                  problemId.replace(/-/g, ' ');

      // Description: We look for the main description body
      const descElement = document.querySelector('div[data-track-load="description_content"]');
      let description = descElement ? descElement.innerHTML : null;
      
      // Extract difficulty
      let difficulty: string | null = null;
      const diffElements = document.querySelectorAll('.text-difficulty-easy, .text-difficulty-medium, .text-difficulty-hard, .bg-olive, .bg-yellow, .bg-pink');
      for (const el of diffElements) {
        if (el.textContent && ['Easy', 'Medium', 'Hard'].includes(el.textContent)) {
          difficulty = el.textContent;
          break;
        }
      }

      // We conservatively extract what we can without breaking on subtle UI changes
      return {
        platform: this.getPlatform(),
        problemId,
        title,
        url,
        description,
        constraints: null, // Harder to isolate purely from DOM without brittle selectors
        examples: null,
        tags: null,
        difficulty,
        source: 'DOM_EXTRACTION'
      };
    } catch (e) {
      console.error('LeetCode extraction failed', e);
      return null;
    }
  }

  async extractCode(): Promise<string | null> {
    // Phase 3: Do NOT implement heavy code extraction logic, just stub.
    return null;
  }
}
