'use client'
import Image from "next/image";
import { Header } from './components/shared/header';
import { useCallback, useEffect, useState } from "react";
import { executeCode } from "./actions";
import { Editor, useMonaco } from "@monaco-editor/react";
import { configuration, languageDef } from "./config";

export default function Home() {

  const [code, setCode] = useState('');

  const onSubmit = useCallback(async () => {
    console.log(code)
    const result = await executeCode(code);
    if(result !== -1 && result.sessionId && result.requiresInput) {
      console.log(result.sessionId);
    }
  }, [code]);

  const onChange = (v: any, event: any) => {
    setCode(v)
    console.log(v)
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

  return (
    <>
      <Header onSubmit={onSubmit}/>
      <div style={{ marginTop: "50px"}}>
        <Editor
          height="95vh"
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
      </div>
    </>
  );
}
