import React, { useState, useCallback, useEffect } from 'react';
import Editor from '@monaco-editor/react';
import { yLanguageApi } from '../services/api';
import { copyToClipboard, downloadFile, formatExecutionTime } from '../utils/helpers';
import { Play, Download, Copy, AlertCircle, Settings, Book, Code } from 'lucide-react';
import { CONFIG } from '../config';

const DEFAULT_Y_CODE = `// Welcome to Y Language Playground!
// Y language is designed to be as readable as natural English

create function addNumbers with parameters a as number and b as number that returns number
begin
    return a plus b
end

create function greetUser with parameters name as string and age as number that returns string
begin
    return "Hello " plus name plus ", you are " plus age plus " years old"
end

create variable message as string equals "Hello World"
create variable result as number equals addNumbers with 5 and 3

print message
print result
print greetUser with "Alice" and 25`;

const Y_LANGUAGE_EXAMPLES = {
  'Basic Functions': DEFAULT_Y_CODE,
  'Variables and Types': `create variable name as string equals "John"
create variable age as number equals 25
create variable isActive as boolean equals true

create function introduce with parameters person as string and years as number that returns string
begin
    return person plus " is " plus years plus " years old"
end

print introduce with name and age`,
  
  'Control Flow': `create function checkAge with parameters age as number that returns string
begin
    if age is greater than or equal to 18
    begin
        return "Adult"
    end
    otherwise
    begin
        return "Minor"
    end
end

print checkAge with 20
print checkAge with 15`,
  
  'Loops': `create function findMax with parameters numbers as list of number that returns number
begin
    create variable max as number equals numbers at 0
    loop through each number in numbers
    begin
        if number is greater than max
        begin
            set max equals number
        end
    end
    return max
end

create variable numbers as list of number equals list of 3 and 7 and 2 and 9 and 1
print findMax with numbers`,
};

export default function LanguagePlayground() {
  const [yCode, setYCode] = useState(DEFAULT_Y_CODE);
  const [typescriptCode, setTypescriptCode] = useState('');
  const [rustCode, setRustCode] = useState('');
  const [isCompiling, setIsCompiling] = useState(false);
  const [errors, setErrors] = useState<string[]>([]);
  const [warnings, setWarnings] = useState<string[]>([]);
  const [selectedExample, setSelectedExample] = useState('Basic Functions');
  const [autoCompile, setAutoCompile] = useState(false);
  const [compilationTime, setCompilationTime] = useState(0);

  const compileCode = useCallback(async (targetLanguage: 'typescript' | 'rust') => {
    try {
      setIsCompiling(true);
      setErrors([]);
      setWarnings([]);

      const response = await yLanguageApi.compile({
        code: yCode,
        targetLanguage,
      });

      setCompilationTime(response.executionTimeMs);

      if (response.success && response.compiledCode) {
        if (targetLanguage === 'typescript') {
          setTypescriptCode(response.compiledCode);
        } else {
          setRustCode(response.compiledCode);
        }
        setWarnings(response.warnings);
      } else {
        setErrors(response.errors);
      }
    } catch (error) {
      setErrors([error instanceof Error ? error.message : 'Unknown error occurred']);
    } finally {
      setIsCompiling(false);
    }
  }, [yCode]);

  const compileAll = useCallback(async () => {
    await Promise.all([
      compileCode('typescript'),
      compileCode('rust'),
    ]);
  }, [compileCode]);

  useEffect(() => {
    if (autoCompile) {
      const timeoutId = setTimeout(() => {
        compileAll();
      }, 1000);
      return () => clearTimeout(timeoutId);
    }
  }, [yCode, autoCompile, compileAll]);

  useEffect(() => {
    // Load initial examples
    const example = Y_LANGUAGE_EXAMPLES[selectedExample as keyof typeof Y_LANGUAGE_EXAMPLES];
    setYCode(example);
  }, [selectedExample]);

  const handleCopyToClipboard = async (text: string) => {
    const success = await copyToClipboard(text);
    if (success) {
      // You could add a toast notification here
      console.log('Copied to clipboard!');
    }
  };

  const handleDownloadFile = (content: string, filename: string) => {
    downloadFile(content, filename);
  };

  return (
    <div className="min-h-screen bg-gray-50">
      {/* Header */}
      <header className="bg-white shadow-sm border-b">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex justify-between items-center h-16">
            <div className="flex items-center space-x-4">
              <div className="flex items-center space-x-2">
                <Code className="h-8 w-8 text-blue-600" />
                <h1 className="text-2xl font-bold text-gray-900">Y Language Playground</h1>
              </div>
            </div>
            <div className="flex items-center space-x-4">
              <select
                value={selectedExample}
                onChange={(e) => setSelectedExample(e.target.value)}
                className="px-3 py-2 border border-gray-300 rounded-md text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
              >
                {Object.keys(Y_LANGUAGE_EXAMPLES).map((example) => (
                  <option key={example} value={example}>
                    {example}
                  </option>
                ))}
              </select>
              <button
                onClick={compileAll}
                disabled={isCompiling}
                className="inline-flex items-center px-4 py-2 border border-transparent text-sm font-medium rounded-md text-white bg-blue-600 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 disabled:opacity-50 disabled:cursor-not-allowed"
              >
                <Play className="h-4 w-4 mr-2" />
                {isCompiling ? 'Compiling...' : 'Compile All'}
              </button>
              <div className="flex items-center space-x-2">
                <input
                  type="checkbox"
                  id="autoCompile"
                  checked={autoCompile}
                  onChange={(e) => setAutoCompile(e.target.checked)}
                  className="rounded border-gray-300 text-blue-600 focus:ring-blue-500"
                />
                <label htmlFor="autoCompile" className="text-sm text-gray-700">
                  Auto-compile
                </label>
              </div>
            </div>
          </div>
        </div>
      </header>

      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {/* Status Bar */}
        {compilationTime > 0 && (
          <div className="mb-4 p-3 bg-blue-50 border border-blue-200 rounded-md">
            <p className="text-sm text-blue-800">
              Compilation completed in {formatExecutionTime(compilationTime)}
            </p>
          </div>
        )}

        {/* Errors and Warnings */}
        {errors.length > 0 && (
          <div className="mb-4 p-4 bg-red-50 border border-red-200 rounded-md">
            <div className="flex items-center mb-2">
              <AlertCircle className="h-5 w-5 text-red-500 mr-2" />
              <h3 className="text-sm font-medium text-red-800">Compilation Errors</h3>
            </div>
            <ul className="text-sm text-red-700 space-y-1">
              {errors.map((error, index) => (
                <li key={index}>• {error}</li>
              ))}
            </ul>
          </div>
        )}

        {warnings.length > 0 && (
          <div className="mb-4 p-4 bg-yellow-50 border border-yellow-200 rounded-md">
            <div className="flex items-center mb-2">
              <AlertCircle className="h-5 w-5 text-yellow-500 mr-2" />
              <h3 className="text-sm font-medium text-yellow-800">Warnings</h3>
            </div>
            <ul className="text-sm text-yellow-700 space-y-1">
              {warnings.map((warning, index) => (
                <li key={index}>• {warning}</li>
              ))}
            </ul>
          </div>
        )}

        {/* Main Content */}
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
          {/* Y Language Editor */}
          <div className="lg:col-span-1">
            <div className="bg-white rounded-lg shadow-sm border">
              <div className="px-4 py-3 border-b bg-gray-50 rounded-t-lg">
                <h2 className="text-lg font-semibold text-gray-900">Y Language Source</h2>
                <p className="text-sm text-gray-600">Write your Y language code here</p>
              </div>
              <div className="p-4">
                <Editor
                  height="400px"
                  defaultLanguage={CONFIG.LANGUAGES.Y_LANGUAGE.id}
                  value={yCode}
                  onChange={(value) => setYCode(value || '')}
                  theme={CONFIG.MONACO_THEME}
                  options={CONFIG.EDITOR_OPTIONS}
                />
              </div>
            </div>
          </div>

          {/* TypeScript Output */}
          <div className="lg:col-span-1">
            <div className="bg-white rounded-lg shadow-sm border">
              <div className="px-4 py-3 border-b bg-gray-50 rounded-t-lg flex justify-between items-center">
                <div>
                  <h2 className="text-lg font-semibold text-gray-900">TypeScript Output</h2>
                  <p className="text-sm text-gray-600">Generated TypeScript code</p>
                </div>
                <div className="flex space-x-2">
                  <button
                    onClick={() => handleCopyToClipboard(typescriptCode)}
                    className="p-2 text-gray-500 hover:text-gray-700 focus:outline-none focus:ring-2 focus:ring-blue-500 rounded"
                    title="Copy to clipboard"
                  >
                    <Copy className="h-4 w-4" />
                  </button>
                  <button
                    onClick={() => handleDownloadFile(typescriptCode, 'output.ts')}
                    className="p-2 text-gray-500 hover:text-gray-700 focus:outline-none focus:ring-2 focus:ring-blue-500 rounded"
                    title="Download TypeScript file"
                  >
                    <Download className="h-4 w-4" />
                  </button>
                </div>
              </div>
              <div className="p-4">
                <Editor
                  height="400px"
                  defaultLanguage={CONFIG.LANGUAGES.TYPESCRIPT.id}
                  value={typescriptCode}
                  theme={CONFIG.MONACO_THEME}
                  options={{ ...CONFIG.EDITOR_OPTIONS, readOnly: true }}
                />
              </div>
            </div>
          </div>

          {/* Rust Output */}
          <div className="lg:col-span-1">
            <div className="bg-white rounded-lg shadow-sm border">
              <div className="px-4 py-3 border-b bg-gray-50 rounded-t-lg flex justify-between items-center">
                <div>
                  <h2 className="text-lg font-semibold text-gray-900">Rust Output</h2>
                  <p className="text-sm text-gray-600">Generated Rust code</p>
                </div>
                <div className="flex space-x-2">
                  <button
                    onClick={() => handleCopyToClipboard(rustCode)}
                    className="p-2 text-gray-500 hover:text-gray-700 focus:outline-none focus:ring-2 focus:ring-blue-500 rounded"
                    title="Copy to clipboard"
                  >
                    <Copy className="h-4 w-4" />
                  </button>
                  <button
                    onClick={() => handleDownloadFile(rustCode, 'output.rs')}
                    className="p-2 text-gray-500 hover:text-gray-700 focus:outline-none focus:ring-2 focus:ring-blue-500 rounded"
                    title="Download Rust file"
                  >
                    <Download className="h-4 w-4" />
                  </button>
                </div>
              </div>
              <div className="p-4">
                <Editor
                  height="400px"
                  defaultLanguage={CONFIG.LANGUAGES.RUST.id}
                  value={rustCode}
                  theme={CONFIG.MONACO_THEME}
                  options={{ ...CONFIG.EDITOR_OPTIONS, readOnly: true }}
                />
              </div>
            </div>
          </div>
        </div>

        {/* Quick Actions */}
        <div className="mt-8 bg-white rounded-lg shadow-sm border p-6">
          <h3 className="text-lg font-semibold text-gray-900 mb-4">Quick Actions</h3>
          <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
            <button
              onClick={() => compileCode('typescript')}
              disabled={isCompiling}
              className="flex items-center justify-center px-4 py-2 border border-transparent text-sm font-medium rounded-md text-white bg-blue-600 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 disabled:opacity-50 disabled:cursor-not-allowed"
            >
              <Play className="h-4 w-4 mr-2" />
              Compile to TypeScript
            </button>
            <button
              onClick={() => compileCode('rust')}
              disabled={isCompiling}
              className="flex items-center justify-center px-4 py-2 border border-transparent text-sm font-medium rounded-md text-white bg-orange-600 hover:bg-orange-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-orange-500 disabled:opacity-50 disabled:cursor-not-allowed"
            >
              <Play className="h-4 w-4 mr-2" />
              Compile to Rust
            </button>
            <button
              onClick={() => {
                setYCode('');
                setTypescriptCode('');
                setRustCode('');
                setErrors([]);
                setWarnings([]);
                setCompilationTime(0);
              }}
              className="flex items-center justify-center px-4 py-2 border border-gray-300 text-sm font-medium rounded-md text-gray-700 bg-white hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500"
            >
              Clear All
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}