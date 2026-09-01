import { config } from '../config/config';
import { MessageType, ExtensionMessage } from '../messaging/types';

console.log('[CodePilot] Service Worker Initialized v' + config.version);

// Enable side panel to open on action click
chrome.sidePanel?.setPanelBehavior({ openPanelOnActionClick: true }).catch((error) => console.error(error));

chrome.runtime.onMessage.addListener((message: ExtensionMessage, _sender, sendResponse) => {
  if (message.type === MessageType.PING) {
    sendResponse({ status: 'PONG', timestamp: new Date().toISOString() });
    return false;
  }
  
  if (message.type === MessageType.PROBLEM_CONTEXT_UPDATED) {
    // Optionally log or track globally.
    // Side panel will also receive this if it is open because chrome.runtime.sendMessage broadcasts to all.
    console.log('[CodePilot] Received problem context update for:', message.payload.context?.platform);
    sendResponse({ acknowledged: true });
    return false;
  }

  return false;
});
