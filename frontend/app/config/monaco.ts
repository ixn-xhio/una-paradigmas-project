import { languages, editor } from "monaco-editor";

// Adjusted config to only highlight curly braces
export const languageDef: languages.IMonarchLanguage = {
    defaultToken: '',
    brackets: [{ open: '{', close: '}', token: 'delimiter.curly' }],
    tokenizer: {
      root: [
        [/{/, 'delimiter.curly'], // Token for opening curly brace
        [/}/, 'delimiter.curly'], // Token for closing curly brace
        [/var/, 'variable'], // Token for variables
        [/const/, 'variable'],
        [/function|class|public|return/, 'keyword'], // Keywords
        [/\b int \b|\b float \b/, 'type', '@variableDeclaration'], // Types that transition to variable declaration
        [/read|print/, 'function'], // Function names
        [/'.*?'|".*?"/, 'string'], // Strings
        [/\d+(\.\d+)?/, 'number'], // Numbers (integers and floats)
      ],
      variableDeclaration: [
        [/[a-zA-Z_]\w*/, 'variable.name', '@pop'], // Matches variable names after types
        [/\s*/, '', '@pop'] // Pops the state if no identifier is found
      ],
    },
}
  
export const theme: editor.IColors = {  }
  
export const configuration: editor.IStandaloneThemeData = {
    base: 'vs-dark',
    inherit: true,
    rules: [
      { token: 'delimiter.curly', foreground: '#ffbf00', fontStyle: 'italic' }, // Styling for curly braces
      { token: 'variable', foreground: '#ff1caf', fontStyle: 'italic' }, // Styling for curly braces
      { token: 'keyword', foreground: '#569cd6', fontStyle: 'italic' }, // Blue keywords
      { token: 'type', foreground: '#4ec9b0', fontStyle: 'italic' }, // Green data types
      { token: 'variable.name', foreground: '#9cdcfe', fontStyle: 'italic' }, // Light blue variable names
      { token: 'function', foreground: '#dcdcaa', fontStyle: 'italic' }, // Yellow functions
      { token: 'string', foreground: '#ce9178', fontStyle: 'italic' }, // Orange strings
      { token: 'number', foreground: '#b5cea8', fontStyle: 'italic' }, // Light green numbers
    ],
    colors: theme
}