import { ProblemContext, Platform } from "../../models/ProblemContext";

export interface PlatformAdapter {
  /** Returns the platform identifier */
  getPlatform(): Platform;

  /** Checks if this adapter can handle the given URL */
  canHandle(url: string): boolean;

  /** 
   * Detects and extracts the problem context from the current page.
   * Returns a promise that resolves to the ProblemContext or null if not found.
   */
  extractProblemContext(): Promise<ProblemContext | null>;
  
  /**
   * Optional: Extract current code from the editor (if technically possible)
   */
  extractCode?(): Promise<string | null>;
}
