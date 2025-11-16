// API service for communicating with the Y Language backend
import { CONFIG } from '../config';

export interface CompileRequest {
  code: string;
  targetLanguage: 'rust' | 'typescript';
  projectId?: string;
}

export interface CompileResponse {
  success: boolean;
  compiledCode?: string;
  errors: string[];
  warnings: string[];
  executionTimeMs: number;
}

export interface ValidateRequest {
  code: string;
}

export interface ValidateResponse {
  valid: boolean;
  errors: string[];
  warnings: string[];
  validationTimeMs: number;
}

export interface ApiInfoResponse {
  name: string;
  version: string;
  description: string;
  availableEndpoints: string[];
}

class YLanguageApiService {
  private readonly baseUrl: string;

  constructor() {
    this.baseUrl = CONFIG.API_BASE_URL;
  }

  private async fetchWithErrorHandling(url: string, options?: RequestInit) {
    try {
      const response = await fetch(url, {
        ...options,
        headers: {
          'Content-Type': 'application/json',
          ...options?.headers,
        },
      });

      if (!response.ok) {
        const errorData = await response.json().catch(() => ({ errors: ['Unknown error'] }));
        const message = Array.isArray(errorData.errors) && errorData.errors.length > 0
          ? errorData.errors.join('; ')
          : `HTTP ${response.status}: ${response.statusText}`;
        throw new Error(message);
      }

      return await response.json();
    } catch (error) {
      if (error instanceof Error) {
        throw error;
      }
      throw new Error('Network error: Failed to connect to Y Language backend');
    }
  }

  async compile(request: CompileRequest): Promise<CompileResponse> {
    return this.fetchWithErrorHandling(`${this.baseUrl}/compile`, {
      method: 'POST',
      body: JSON.stringify(request),
    });
  }

  async validate(request: ValidateRequest): Promise<ValidateResponse> {
    return this.fetchWithErrorHandling(`${this.baseUrl}/validate`, {
      method: 'POST',
      body: JSON.stringify(request),
    });
  }

  async translateToTypeScript(code: string, projectId?: string): Promise<CompileResponse> {
    return this.compile({
      code,
      targetLanguage: 'typescript',
      projectId,
    });
  }

  async translateToRust(code: string, projectId?: string): Promise<CompileResponse> {
    return this.compile({
      code,
      targetLanguage: 'rust',
      projectId,
    });
  }

  async getApiInfo(): Promise<ApiInfoResponse> {
    return this.fetchWithErrorHandling(`${this.baseUrl}/info`);
  }

  async healthCheck(): Promise<string> {
    const response = await fetch(`${this.baseUrl}/health`);
    if (!response.ok) {
      throw new Error('Backend health check failed');
    }
    return response.text();
  }
}

export const yLanguageApi = new YLanguageApiService();