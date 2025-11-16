// Configuration for the Y Language Playground
export const CONFIG = {
  API_BASE_URL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api/v1',
  MONACO_THEME: 'vs-dark',
  EDITOR_OPTIONS: {
    fontSize: 14,
    minimap: { enabled: false },
    lineNumbers: 'on' as const,
    wordWrap: 'on' as const,
    automaticLayout: true,
    scrollBeyondLastLine: false,
  },
  LANGUAGES: {
    TYPESCRIPT: {
      id: 'typescript',
      name: 'TypeScript',
      extensions: ['.ts', '.tsx'],
    },
    RUST: {
      id: 'rust',
      name: 'Rust',
      extensions: ['.rs'],
    },
    Y_LANGUAGE: {
      id: 'plaintext',
      name: 'Y Language',
      extensions: ['.y', '.yl'],
    },
  },
} as const;