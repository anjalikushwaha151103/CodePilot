import { PlatformAdapter } from "./PlatformAdapter";
import { LeetCodeAdapter } from "./LeetCodeAdapter";
import { CodeforcesAdapter } from "./CodeforcesAdapter";

export class PlatformRegistry {
  private adapters: PlatformAdapter[] = [];

  constructor() {
    this.adapters.push(new LeetCodeAdapter());
    this.adapters.push(new CodeforcesAdapter());
  }

  public getAdapterForUrl(url: string): PlatformAdapter | null {
    for (const adapter of this.adapters) {
      if (adapter.canHandle(url)) {
        return adapter;
      }
    }
    return null;
  }
}

export const platformRegistry = new PlatformRegistry();
