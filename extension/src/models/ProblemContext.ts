export type Platform = 'LEETCODE' | 'CODEFORCES' | 'UNKNOWN';  
export interface ProblemContext {  
  platform: Platform;  
  problemId: string | null;  
  title: string | null;  
  url: string;  
  description: string | null;  
  constraints: string[] | null;  
  examples: string[] | null;  
  tags: string[] | null;  
  difficulty: string | null;  
  source: string | null;  
} 
