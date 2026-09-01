import React, { useEffect, useRef, useState } from 'react';
import ReactDOM from 'react-dom';

interface ShadowRootWrapperProps {
  children: React.ReactNode;
}

export const ShadowRootWrapper: React.FC<ShadowRootWrapperProps> = ({ children }) => {
  const containerRef = useRef<HTMLDivElement>(null);
  const [shadowRoot, setShadowRoot] = useState<ShadowRoot | null>(null);

  useEffect(() => {
    if (containerRef.current && !containerRef.current.shadowRoot) {
      const root = containerRef.current.attachShadow({ mode: 'open' });
      
      // Inject base CSS rules inside shadow root for total style isolation
      const style = document.createElement('style');
      style.textContent = `
        :host {
          all: initial;
          font-family: ui-sans-serif, system-ui, -apple-system, sans-serif;
          display: block;
          color: #f8fafc;
          background-color: #0f172a;
          height: 100%;
        }
        * {
          box-sizing: border-box;
          margin: 0;
          padding: 0;
        }
        .codepilot-panel {
          padding: 16px;
          height: 100vh;
          display: flex;
          flex-direction: column;
          background: #0f172a;
        }
        .codepilot-badge {
          display: inline-block;
          font-size: 11px;
          padding: 2px 8px;
          border-radius: 4px;
          background: #0284c7;
          color: #ffffff;
          margin-top: 8px;
        }
      `;
      root.appendChild(style);
      setShadowRoot(root);
    }
  }, []);

  return (
    <div ref={containerRef} style={{ width: '100%', height: '100%' }}>
      {shadowRoot && ReactDOM.createPortal(children, shadowRoot as unknown as Element)}
    </div>
  );
};
