import { AuthResponse, LearningProfile, ApiResponse } from './types';

const API_BASE_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080/api/v1';

export class ApiClient {
  private static getToken(): string | null {
    if (typeof window !== 'undefined') {
      return localStorage.getItem('token');
    }
    return null;
  }

  private static async request<T>(endpoint: string, options: RequestInit = {}): Promise<T> {
    const token = this.getToken();
    
    const headers: Record<string, string> = {
      'Content-Type': 'application/json',
      ...((options.headers as Record<string, string>) || {})
    };

    if (token) {
      headers['Authorization'] = `Bearer ${token}`;
    }

    const config: RequestInit = {
      ...options,
      headers
    };

    let response: Response;
    try {
      response = await fetch(`${API_BASE_URL}${endpoint}`, config);
    } catch (error) {
      throw new Error('Network error. Please ensure the backend is running.');
    }

    if (!response.ok) {
      if (response.status === 401 || response.status === 403) {
        if (typeof window !== 'undefined') {
          localStorage.removeItem('token');
          window.location.href = '/login';
        }
        throw new Error('Authentication required');
      }
      
      let errorMessage = 'An unexpected error occurred';
      try {
        const errorData = await response.json();
        errorMessage = errorData.error || errorData.message || errorMessage;
      } catch (e) {
        // Fallback to status text
        errorMessage = response.statusText;
      }
      throw new Error(errorMessage);
    }

    const data: ApiResponse<T> = await response.json();
    if (!data.success || data.data === undefined) {
      throw new Error(data.error || 'API response was unsuccessful');
    }

    return data.data;
  }

  static async login(email: string, password: string):Promise<AuthResponse> {
    const res = await fetch(`${API_BASE_URL}/auth/login`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ email, password })
    });
    
    if (!res.ok) throw new Error('Invalid credentials');
    
    const data: ApiResponse<AuthResponse> = await res.json();
    if (data.data) {
      if (typeof window !== 'undefined') {
        localStorage.setItem('token', data.data.accessToken);
      }
      return data.data;
    }
    throw new Error('Login failed');
  }

  static logout() {
    if (typeof window !== 'undefined') {
      localStorage.removeItem('token');
    }
  }

  static getLearningProfile(): Promise<LearningProfile> {
    return this.request<LearningProfile>('/learning/profile');
  }
}
