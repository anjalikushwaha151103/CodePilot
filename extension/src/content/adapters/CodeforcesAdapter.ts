import { PlatformAdapter } from "./PlatformAdapter";
import { ProblemContext, Platform } from "../../models/ProblemContext";

export class CodeforcesAdapter implements PlatformAdapter {
  getPlatform(): Platform {
    return 'CODEFORCES';
  }

  canHandle(url: string): boolean {
    return url.includes('codeforces.com/problemset/problem/') || 
           url.includes('codeforces.com/contest/');
  }

  async extractProblemContext(): Promise<ProblemContext | null> {
    try {
      const url = window.location.href;
      
      // Determine problem ID
      let problemId = null;
      let match = url.match(/problemset\/problem\/([^/]+)\/([^/]+)/);
      if (match) {
        problemId = `${match[1]}${match[2]}`;
      } else {
        match = url.match(/contest\/([^/]+)\/problem\/([^/]+)/);
        if (match) {
          problemId = `${match[1]}${match[2]}`;
        }
      }

      if (!problemId) return null;

      // Title
      const titleEl = document.querySelector('.problem-statement .title');
      const title = titleEl ? titleEl.textContent?.trim() || null : null;

      // Description statement
      let description = null;
      const statementEl = document.querySelector('.problem-statement > div:not(.header)');
      if (statementEl) {
         // This typically grabs the first paragraph of the problem text
         description = statementEl.innerHTML;
      }

      // Tags
      const tags: string[] = [];
      const tagElements = document.querySelectorAll('.tag-box');
      tagElements.forEach(el => {
        if (el.textContent) {
          tags.push(el.textContent.trim());
        }
      });

      return {
        platform: this.getPlatform(),
        problemId,
        title,
        url,
        description,
        constraints: null,
        examples: null,
        tags: tags.length > 0 ? tags : null,
        difficulty: null, // Hard to extract reliably on CF without API
        source: 'DOM_EXTRACTION'
      };
    } catch (e) {
      console.error('Codeforces extraction failed', e);
      return null;
    }
  }
}
