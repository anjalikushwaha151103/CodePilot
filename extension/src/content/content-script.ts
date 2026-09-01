import { platformRegistry } from './adapters/PlatformRegistry';
import { ProblemContext } from '../models/ProblemContext';

// Hardcoded message types to avoid importing from shared modules which causes Vite to create ESM chunks
const GET_PROBLEM_CONTEXT = "GET_PROBLEM_CONTEXT";
const GET_PLATFORM = "GET_PLATFORM";
const PROBLEM_CONTEXT_UPDATED = "PROBLEM_CONTEXT_UPDATED";

class ContentScriptContext {
  private currentProblemContext: ProblemContext | null = null;
  
  public async initialize() {
    console.log('[CodePilot] Content script initializing...');
    await this.extractAndBroadcast();

    // Listen for messages from the side panel / background worker
    chrome.runtime.onMessage.addListener((message: any, _sender, sendResponse) => {
      this.handleMessage(message, sendResponse);
      return true; // Keep message channel open for async responses if needed
    });
    
    // Very basic SPA navigation handling (e.g. LeetCode uses SPA routing)
    let lastUrl = location.href;
    new MutationObserver(() => {
      const url = location.href;
      if (url !== lastUrl) {
        lastUrl = url;
        console.log('[CodePilot] URL changed, re-evaluating context...');
        // Debounce slightly to allow DOM to render
        setTimeout(() => this.extractAndBroadcast(), 1000);
      }
    }).observe(document, { subtree: true, childList: true });
  }

  private async extractAndBroadcast() {
    const url = window.location.href;
    const adapter = platformRegistry.getAdapterForUrl(url);

    if (!adapter) {
      this.currentProblemContext = null;
      this.broadcastContext();
      return;
    }

    try {
      this.currentProblemContext = await adapter.extractProblemContext();
      this.broadcastContext();
    } catch (e) {
      console.error('[CodePilot] Failed to extract problem context', e);
    }
  }

  private broadcastContext() {
    const msg = {
      type: PROBLEM_CONTEXT_UPDATED,
      payload: { context: this.currentProblemContext }
    };
    
    chrome.runtime.sendMessage(msg).catch(() => {
      // Background script or sidepanel may not be listening yet, ignore gracefully.
    });
  }

  private handleMessage(message: any, sendResponse: (response: any) => void) {
    if (message.type === GET_PROBLEM_CONTEXT) {
      sendResponse({ context: this.currentProblemContext });
    } else if (message.type === GET_PLATFORM) {
      const adapter = platformRegistry.getAdapterForUrl(window.location.href);
      sendResponse({ platform: adapter ? adapter.getPlatform() : 'UNKNOWN' });
    }
  }
}

// Boot
const context = new ContentScriptContext();
if (document.readyState === 'loading') {
  document.addEventListener('DOMContentLoaded', () => context.initialize());
} else {
  context.initialize();
}
