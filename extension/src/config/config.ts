export interface ExtensionConfig {
  backendApiUrl: string;
  dashboardUrl: string;
  version: string;
}

export const config: ExtensionConfig = {
  backendApiUrl: 'http://localhost:8080',
  dashboardUrl: 'http://localhost:3000/dashboard',
  version: '1.0.0-MVP',
};
