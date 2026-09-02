"use client";

import React, { createContext, useContext, useEffect, useState } from 'react';
import { ApiClient } from '@/lib/api/client';
import { useRouter, usePathname } from 'next/navigation';

interface AuthContextType {
  isAuthenticated: boolean;
  isLoading: boolean;
  login: (email: string, pass: string) => Promise<void>;
  logout: () => void;
}

const AuthContext = createContext<AuthContextType>({
  isAuthenticated: false,
  isLoading: true,
  login: async () => {},
  logout: () => {}
});

function isTokenExpired(token: string): boolean {
  try {
    const payload = JSON.parse(atob(token.split('.')[1]));
    return payload.exp ? payload.exp * 1000 < Date.now() : false;
  } catch {
    return true;
  }
}

export const AuthProvider = ({ children }: { children: React.ReactNode }) => {
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [isLoading, setIsLoading] = useState(true);
  const router = useRouter();
  const pathname = usePathname();

  useEffect(() => {
    const token = localStorage.getItem('token');
    if (token && !isTokenExpired(token)) {
      setIsAuthenticated(true);
    } else {
      if (token) {
        localStorage.removeItem('token');
      }
      setIsAuthenticated(false);
      if (pathname.startsWith('/dashboard')) {
        router.push('/login');
      }
    }
    setIsLoading(false);
  }, [pathname, router]);

  const login = async (email: string, pass: string) => {
    setIsLoading(true);
    try {
      await ApiClient.login(email, pass);
      setIsAuthenticated(true);
      router.push('/dashboard');
    } catch (e) {
      throw e;
    } finally {
      setIsLoading(false);
    }
  };

  const logout = () => {
    ApiClient.logout();
    setIsAuthenticated(false);
    router.push('/login');
  };

  return (
    <AuthContext.Provider value={{ isAuthenticated, isLoading, login, logout }}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => useContext(AuthContext);
