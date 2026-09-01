import React, { useEffect, useState } from 'react';
import { ShadowRootWrapper } from '../components/ShadowRootWrapper';
import { apiClient, TutoringResponse } from '../api/client';
import { MessageType } from '../messaging/types';
import { ProblemContext } from '../models/ProblemContext';
import { config } from '../config/config';

type UIState = 'UNAUTHENTICATED' | 'NO_PROBLEM' | 'READY' | 'ANALYZING' | 'RESPONSE' | 'ERROR';

export const SidePanelApp: React.FC = () => {
  const [problemContext, setProblemContext] = useState<ProblemContext | null>(null);
  const [uiState, setUiState] = useState<UIState>('NO_PROBLEM');
  const [errorMessage, setErrorMessage] = useState<string>('');
  
  // Auth state
  const [token, setToken] = useState<string | null>(null);
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');

  // Tutoring state
  const [code, setCode] = useState<string>('');
  const [language, setLanguage] = useState<string>('python');
  const [hintLevel, setHintLevel] = useState<number>(1);
  const [tutorResponse, setTutorResponse] = useState<TutoringResponse | null>(null);
  const [pendingLevel4, setPendingLevel4] = useState(false);

  useEffect(() => {
    if (typeof chrome !== 'undefined' && chrome.storage) {
      chrome.storage.local.get(['jwtToken'], (result) => {
        if (result.jwtToken) {
          setToken(result.jwtToken);
        } else {
          setUiState('UNAUTHENTICATED');
        }
      });
    }
  }, []);

  useEffect(() => {
    if (token) {
      detectProblem();
    }
  }, [token]);

  const detectProblem = () => {
    if (typeof chrome !== 'undefined' && chrome.tabs) {
      // Query all active tabs across all windows. 
      // This bypasses Side Panel window focus bugs and URL permission issues.
      chrome.tabs.query({ active: true }, (tabs) => {
        tabs.forEach(tab => {
          if (tab.id) {
            chrome.tabs.sendMessage(tab.id, { type: MessageType.GET_PROBLEM_CONTEXT }, (response) => {
              // Ignore lastError since we are broadcasting to all active tabs (some won't have the script)
              if (!chrome.runtime.lastError && response && response.context) {
                setProblemContext(response.context);
                setUiState(tutorResponse ? 'RESPONSE' : 'READY');
              }
            });
          }
        });

        // If after a short delay no tab responded, we assume no problem was found
        setTimeout(() => {
          setUiState(prevState => (prevState === 'ANALYZING' || prevState === 'RESPONSE' || prevState === 'READY') ? prevState : 'NO_PROBLEM');
        }, 500);
      });
    }
  };

  const handleLogin = async (e: React.FormEvent) => {
    e.preventDefault();
    setErrorMessage('');
    try {
      const jwt = await apiClient.login(email, password);
      setToken(jwt);
      if (typeof chrome !== 'undefined' && chrome.storage) {
        chrome.storage.local.set({ jwtToken: jwt });
      }
      setUiState('NO_PROBLEM'); // Let the effect re-detect
    } catch (e: any) {
      setErrorMessage(e.message);
    }
  };

  const handleLogout = () => {
    setToken(null);
    setProblemContext(null);
    setTutorResponse(null);
    if (typeof chrome !== 'undefined' && chrome.storage) {
      chrome.storage.local.remove(['jwtToken']);
    }
    setUiState('UNAUTHENTICATED');
  };

  const submitTutoringRequest = async (level: number) => {
    if (!token || !problemContext || !code) return;
    setUiState('ANALYZING');
    setErrorMessage('');
    
    try {
      const response = await apiClient.submitTutoringRequest(token, {
        problemContext,
        code,
        language,
        hintLevel: level
      });
      setTutorResponse(response);
      setHintLevel(response.hintLevel);
      setUiState('RESPONSE');
      setPendingLevel4(false);
    } catch (e: any) {
      setErrorMessage(e.message);
      if (e.message.includes('Session expired') || e.message.includes('log in again')) {
        handleLogout();
      } else {
        setUiState('ERROR');
      }
    }
  };

  const handleGetHint = () => submitTutoringRequest(1);

  const handleStrongerHint = () => {
    if (hintLevel < 3) {
      submitTutoringRequest(hintLevel + 1);
    } else if (hintLevel === 3) {
      setPendingLevel4(true);
    }
  };

  const confirmLevel4 = () => submitTutoringRequest(4);

  const renderContent = () => {
    if (uiState === 'UNAUTHENTICATED') {
      return (
        <form onSubmit={handleLogin} style={{ display: 'flex', flexDirection: 'column', gap: '12px' }}>
          <p style={{ fontSize: '14px', color: '#f8fafc', marginBottom: '8px' }}>Sign in to start tutoring.</p>
          <input type="email" placeholder="Email" value={email} onChange={e => setEmail(e.target.value)} required style={inputStyle} />
          <input type="password" placeholder="Password" value={password} onChange={e => setPassword(e.target.value)} required style={inputStyle} />
          {errorMessage && <div style={{ color: '#f87171', fontSize: '12px' }}>{errorMessage}</div>}
          <button type="submit" style={buttonStyle}>Sign In</button>
        </form>
      );
    }

    if (uiState === 'NO_PROBLEM') {
      return (
        <div style={{ textAlign: 'center', color: '#94a3b8', padding: '20px 0' }}>
          CodePilot supports LeetCode and Codeforces.<br/>Open a problem to begin.
          <br/><button onClick={detectProblem} style={{...buttonStyle, marginTop: '16px'}}>Retry Detection</button>
        </div>
      );
    }

    if (uiState === 'ERROR') {
      return (
        <div style={{ textAlign: 'center', color: '#f87171', padding: '20px 0' }}>
          {errorMessage || 'An unknown error occurred.'}
          <br/><button onClick={() => setUiState('READY')} style={{...buttonStyle, marginTop: '16px'}}>Go Back</button>
        </div>
      );
    }

    return (
      <div style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
        <div>
          <h4 style={labelStyle}>Problem:</h4>
          <p style={valueStyle}>{problemContext?.title}</p>
          <h4 style={labelStyle}>Platform:</h4>
          <p style={valueStyle}>{problemContext?.platform}</p>
        </div>

        {uiState === 'READY' && (
          <div style={{ display: 'flex', flexDirection: 'column', gap: '8px' }}>
            <h4 style={labelStyle}>Language:</h4>
            <select value={language} onChange={e => setLanguage(e.target.value)} style={inputStyle}>
              <option value="cpp">C++</option>
              <option value="java">Java</option>
              <option value="python">Python</option>
              <option value="javascript">JavaScript</option>
            </select>
            
            <h4 style={labelStyle}>Your Code:</h4>
            <textarea 
              value={code} 
              onChange={e => setCode(e.target.value)} 
              placeholder="Paste your code here..." 
              style={{ ...inputStyle, height: '120px', resize: 'vertical', fontFamily: 'monospace' }}
            />
            
            <button onClick={handleGetHint} style={buttonStyle} disabled={!code.trim()}>
              Ask CodePilot
            </button>
          </div>
        )}

        {uiState === 'ANALYZING' && (
          <div style={{ textAlign: 'center', padding: '20px', color: '#60a5fa' }}>
            Analyzing your code...
          </div>
        )}

        {uiState === 'RESPONSE' && tutorResponse && (
          <div style={{ display: 'flex', flexDirection: 'column', gap: '12px' }}>
            <div style={{ background: '#1e293b', padding: '12px', borderRadius: '6px', border: '1px solid #334155' }}>
              <h4 style={{ ...labelStyle, color: '#38bdf8' }}>CodePilot Hint (Level {tutorResponse.hintLevel}):</h4>
              <p style={{ fontSize: '14px', color: '#f8fafc', whiteSpace: 'pre-wrap', margin: '8px 0' }}>{tutorResponse.message}</p>
              {tutorResponse.concept && (
                <div style={{ marginTop: '12px', fontSize: '12px', color: '#94a3b8' }}>
                  <strong>Concept:</strong> {tutorResponse.concept}
                </div>
              )}
            </div>

            {pendingLevel4 ? (
              <div style={{ padding: '12px', background: '#451a03', border: '1px solid #78350f', borderRadius: '6px', textAlign: 'center' }}>
                <p style={{ color: '#fcd34d', fontSize: '13px', margin: '0 0 12px 0' }}>Level 4 reveals the complete solution. Continue?</p>
                <div style={{ display: 'flex', gap: '8px', justifyContent: 'center' }}>
                  <button onClick={() => setPendingLevel4(false)} style={{...buttonStyle, background: '#334155'}}>Cancel</button>
                  <button onClick={confirmLevel4} style={{...buttonStyle, background: '#b45309'}}>Reveal Solution</button>
                </div>
              </div>
            ) : tutorResponse.hintLevel < 4 ? (
              <button onClick={handleStrongerHint} style={buttonStyle}>Get Stronger Hint</button>
            ) : null}
            
            <button onClick={() => setUiState('READY')} style={{...buttonStyle, background: '#334155', marginTop: '8px'}}>Edit Code / Restart</button>
          </div>
        )}
      </div>
    );
  };

  return (
    <ShadowRootWrapper>
      <div className="codepilot-panel" style={{ display: 'flex', flexDirection: 'column', height: '100%', padding: '16px', boxSizing: 'border-box' }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '16px' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
            <div style={{ width: '24px', height: '24px', background: '#0284c7', borderRadius: '6px', display: 'flex', alignItems: 'center', justifyContent: 'center', fontWeight: 'bold', fontSize: '12px', color: 'white' }}>CP</div>
            <h2 style={{ fontSize: '18px', fontWeight: 'bold', color: '#ffffff', margin: 0 }}>CodePilot</h2>
          </div>
          {token && (
            <div style={{ display: 'flex', gap: '12px', alignItems: 'center' }}>
              <a href={config.dashboardUrl} target="_blank" rel="noopener noreferrer" style={{ color: '#38bdf8', fontSize: '12px', textDecoration: 'none' }}>
                View Progress
              </a>
              <button onClick={handleLogout} style={{ background: 'transparent', border: 'none', color: '#94a3b8', cursor: 'pointer', fontSize: '12px', textDecoration: 'underline' }}>Sign Out</button>
            </div>
          )}
        </div>
        <hr style={{ borderColor: '#334155', marginBottom: '16px' }} />
        {renderContent()}
      </div>
    </ShadowRootWrapper>
  );
};

const inputStyle: React.CSSProperties = {
  background: '#1e293b',
  border: '1px solid #475569',
  borderRadius: '6px',
  padding: '8px',
  color: '#f8fafc',
  fontSize: '14px',
  width: '100%',
  boxSizing: 'border-box'
};

const buttonStyle: React.CSSProperties = {
  background: '#0284c7',
  color: '#ffffff',
  border: 'none',
  padding: '10px',
  borderRadius: '6px',
  fontWeight: 'bold',
  cursor: 'pointer',
  width: '100%',
  transition: 'background 0.2s'
};

const labelStyle: React.CSSProperties = {
  fontSize: '12px',
  textTransform: 'uppercase',
  color: '#94a3b8',
  margin: '0 0 4px 0'
};

const valueStyle: React.CSSProperties = {
  fontSize: '14px',
  color: '#f8fafc',
  margin: '0 0 12px 0'
};
