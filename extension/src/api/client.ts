import { config } from '../config/config';

export interface LoginResponse {
  accessToken: string;
}

export interface TutoringRequest {
  problemContext: any;
  code: string;
  language: string;
  hintLevel: number;
  userQuestion?: string;
}

export interface TutoringResponse {
  hintLevel: number;
  message: string;
  concept: string;
  confidence: number;
  shouldRevealSolution: boolean;
}

function fetchWithTimeout(url: string, options: RequestInit, timeoutMs = 15000): Promise<Response> {
  const controller = new AbortController();
  const timeout = setTimeout(() => controller.abort(), timeoutMs);
  return fetch(url, { ...options, signal: controller.signal }).finally(() => clearTimeout(timeout));
}

export class ApiClient {
  private baseUrl: string;

  constructor() {
    this.baseUrl = config.backendApiUrl;
  }

  /**
   * Performs a basic health check to the Spring Boot backend.
   */
  async checkHealth(): Promise<boolean> {
    try {
      const response = await fetchWithTimeout(`${this.baseUrl}/api/v1/health`, {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json'
        }
      });
      
      return response.ok;
    } catch (e: any) {
      console.warn('[CodePilot] Backend health check failed', e);
      return false;
    }
  }

  async login(email: string, password: string):Promise<string> {
    try {
      const response = await fetchWithTimeout(`${this.baseUrl}/api/v1/auth/login`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({ email, password })
      });

      if (!response.ok) {
        if (response.status === 401) throw new Error("Invalid email or password");
        throw new Error(`Login failed: ${response.statusText}`);
      }

      const data = await response.json();
      return data.data.accessToken;
    } catch (e: any) {
      if (e.name === 'AbortError') throw new Error("Request timed out. Please try again.");
      throw e;
    }
  }

  async submitTutoringRequest(token: string, request: TutoringRequest): Promise<TutoringResponse> {
    try {
      const response = await fetchWithTimeout(`${this.baseUrl}/api/v1/tutoring`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify(request)
      });

      if (!response.ok) {
        const errData = await response.json().catch(() => null);
        if (response.status === 401) throw new Error("Session expired. Please log in again.");
        if (response.status === 422 || response.status === 400) throw new Error("Invalid request data.");
        if (response.status === 502 || response.status === 503) throw new Error("CodePilot AI is temporarily unavailable.");
        throw new Error(errData?.message || `Tutoring request failed: ${response.statusText}`);
      }

      const data = await response.json();
      return data.data;
    } catch (e: any) {
      if (e.name === 'AbortError') throw new Error("Request timed out. Please try again.");
      throw e;
    }
  }
}

export const apiClient = new ApiClient();
