'use client'
import Image from "next/image";
import { Header } from './components/shared/header';
import { useCallback, useEffect, useState } from "react";
import { executeCode, sendInput } from "./actions";
import { Editor, useMonaco } from "@monaco-editor/react";
import { configuration, languageDef } from "./config";

export default function Home() {

  const [code, setCode] = useState('');
  const [logs, setLogs] = useState<string[]>([]); // Array para almacenar los logs de output
  const [input, setInput] = useState(''); // Estado para el input del usuario
  const [sessionId, setSessionId] = useState(''); // Estado para el input del usuario

  const onSubmit = useCallback(async () => {
    const result = await executeCode(code);
    if(result !== -1 && result.sessionId && result.requiresInput) {
      setSessionId(result.sessionId);
      setLogs(result.outputs);
    }
  }, [code]);

  const onChange = (v: any, event: any) => {
    setCode(v)
  }
  
  const monaco = useMonaco();
  
  useEffect(() => {
    if (!monaco) return
    // Register a new language
    monaco.languages.register({ id: 'tracta' })
    // Register a tokens provider for the language
    monaco.languages.setMonarchTokensProvider('tracta', languageDef)
    // Set the editing configuration for the language
    monaco.editor.defineTheme('tracta', configuration)
    monaco.editor.setTheme('tracta')
  }, [monaco])

  // Maneja el ingreso de un comando en la consola
  const handleCommandSubmit = (e: any) => {
    e.preventDefault();
    if (input.trim()) {
      setLogs([...logs, `> ${input}`]); // Agrega el comando al log
      processCommand(input); // Procesa el comando
      setInput(''); // Limpia el input después de enviar el comando
    }
  };

  // Función para procesar el comando
  const processCommand = async (command: string) => {
    // Aquí puedes añadir lógica para ejecutar diferentes comandos
    switch (command.toLowerCase()) {
      case 'help':
        setLogs((prevLogs) => [...prevLogs, 'Available commands: help, clear']);
        break;
      case 'clear':
        setLogs([]);
        break;
      default:
        const result = await sendInput(command, sessionId);
        console.log(result)
        if(result !== -1) {
          setSessionId(result.sessionId);
          setLogs((prevLogs) => [...prevLogs, ...result.outputs]);
        }
    }
  };

  return (
    <>
      <Header onSubmit={onSubmit}/>
      <div style={{ marginTop: "50px", display: "flex", "flexDirection": "column" }}>
        <Editor
          height="65vh"
          defaultLanguage="tracta"
          theme="tracta"
          value={code}
          onChange={onChange}
          loading={false}
          line={30}
          options={{
            lineNumbers: 'relative',
            minimap: { enabled: false },
            readOnly: false,
            scrollbar: { vertical: 'hidden', horizontal: 'hidden' },
            wordWrap: 'on',
            wrappingIndent: 'same',
            fontFamily: 'Geist',
            fontSize: 14,
            lineHeight: 20,
            lineDecorationsWidth: 5,
            glyphMargin: false,
            renderLineHighlight: 'none',
            overviewRulerBorder: false,
            folding: false,
            rulers: [],
          }}
        />    
        <div style={styles.consoleContainer}>
          <div style={styles.logsContainer}>
            {logs.map((log, index) => (
              <div key={index} style={styles.log}>{log}</div>
            ))}
          </div>
          <form onSubmit={handleCommandSubmit} style={styles.inputContainer}>
            <span style={styles.prompt}>$</span>
            <input
              type="text"
              value={input}
              onChange={(e) => setInput(e.target.value)}
              style={styles.input}
              autoFocus
            />
          </form>
        </div>
      </div>
    </>
  );
}


const styles: any = {
  consoleContainer: {
    backgroundColor: '#1e1e1e',
    color: '#dcdcdc',
    padding: '10px',
    width: '100%',
    height: '26vh',
    overflowY: 'auto',
    fontFamily: 'monospace',
    display: 'flex',
    flexDirection: 'column',
  },
  logsContainer: {
    flex: 1,
    overflowY: 'auto',
  },
  log: {
    padding: '2px 0',
  },
  inputContainer: {
    display: 'flex',
    alignItems: 'center',
    marginTop: '10px',
  },
  prompt: {
    marginRight: '5px',
  },
  input: {
    backgroundColor: 'transparent',
    color: '#dcdcdc',
    border: 'none',
    outline: 'none',
    flex: 1,
  },
};
