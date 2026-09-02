export interface ExtensionConfig {
  backendApiUrl: string;
  dashboardUrl: string;
  version: string;
}

export const config: ExtensionConfig = {
  backendApiUrl: import.meta.env.VITE_BACKEND_URL || 'http://localhost:8080',
  dashboardUrl: import.meta.env.VITE_DASHBOARD_URL || 'http://localhost:3000/dashboard',
  version: '1.0.0-MVP',
};
