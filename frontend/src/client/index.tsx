//Electrode React
import { React, ReactSubApp } from "@xarc/react";
//Redux
import { reduxFeature, connect } from "@xarc/react-redux";
//Configuration for IDE
import Editor, { useMonaco } from '@monaco-editor/react';
import { configuration, languageDef } from "../config";
import { executeCode } from "../actions";
export { reduxReducers } from './reducers';

const changeCode = (code: string) => {
  return {
    type: "CHANGE_CODE",
    payload: code
  };
};

const changeSessionId = (code: string) => {
  return {
    type: "CHANGE_SESSION_ID",
    payload: code
  };
};

const Client = (props) => {
  const { code, dispatch } = props;
  
  const monaco = useMonaco();
  React.useEffect(() => {
    if (!monaco) return
    // Register a new language
    monaco.languages.register({ id: 'tracta' })
    // Register a tokens provider for the language
    monaco.languages.setMonarchTokensProvider('tracta', languageDef)
    // Set the editing configuration for the language
    monaco.editor.defineTheme('tracta', configuration)
    monaco.editor.setTheme('tracta')
  }, [monaco])

  const onChange = (v: string, event) => {
    dispatch(changeCode(v))
  }

  const onSubmit = async () => {
    const result = await executeCode(code);
    if(result !== -1 && result.sessionId && result.requiresInput) {
      dispatch(changeSessionId(result.sessionId))
    }
  }

  return (
   <div>
    <button onClick={onSubmit}>Run</button>
    <Editor
      height="90vh"
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
  )
}

const mapStateToProps = (state) => {
  return { code: state.project.code };
};

export const subapp: ReactSubApp = {
  Component: connect(mapStateToProps, (dispatch) => ({ dispatch }))(Client),
  wantFeatures: [
    reduxFeature({
      React,
      shareStore: true,
      reducers: true, // true => read the reduxReducers export from this file
      prepare: async (initialState) => {
        return { initialState: initialState || { project: { code: "" } } };
      },
    }),
  ],
};
