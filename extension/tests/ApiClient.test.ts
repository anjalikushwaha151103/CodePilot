import { describe, it, expect, vi, beforeEach } from 'vitest';
import { ApiClient } from '../src/api/client';

describe('ApiClient', () => {
  let apiClient: ApiClient;

  beforeEach(() => {
    apiClient = new ApiClient();
    global.fetch = vi.fn();
  });

  it('login sends correct payload and returns token', async () => {
    vi.mocked(global.fetch).mockResolvedValueOnce({
      ok: true,
      json: async () => ({ data: { accessToken: 'fake-jwt' } })
    } as Response);

    const token = await apiClient.login('test@test.com', 'password');
    
    expect(token).toBe('fake-jwt');
    expect(global.fetch).toHaveBeenCalledWith('http://localhost:8080/api/v1/auth/login', expect.objectContaining({
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ email: 'test@test.com', password: 'password' })
    }));
  });

  it('submitTutoringRequest sends token and returns hint', async () => {
    vi.mocked(global.fetch).mockResolvedValueOnce({
      ok: true,
      json: async () => ({
        data: { hintLevel: 1, message: 'Hint' }
      })
    } as Response);

    const request = {
      problemContext: { platform: 'LEETCODE', problemId: 'two-sum', title: 'Two Sum' },
      code: 'def twoSum(): pass',
      language: 'python',
      hintLevel: 1
    };

    const response = await apiClient.submitTutoringRequest('fake-jwt', request);
    
    expect(response.hintLevel).toBe(1);
    expect(response.message).toBe('Hint');
    expect(global.fetch).toHaveBeenCalledWith('http://localhost:8080/api/v1/tutoring', expect.objectContaining({
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': 'Bearer fake-jwt'
      },
      body: JSON.stringify(request)
    }));
  });

  it('submitTutoringRequest handles 401 gracefully', async () => {
    vi.mocked(global.fetch).mockResolvedValueOnce({
      ok: false,
      status: 401,
      json: async () => ({})
    } as Response);

    await expect(apiClient.submitTutoringRequest('expired-jwt', {
      problemContext: { platform: 'LEETCODE', problemId: 'two-sum', title: 'Two Sum' },
      code: 'def twoSum(): pass',
      language: 'python',
      hintLevel: 1
    })).rejects.toThrow('Session expired. Please log in again.');
  });
});
