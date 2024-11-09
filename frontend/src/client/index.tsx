//Electrode React
import { React, ReactSubApp } from "@xarc/react";
//Redux
import { reduxFeature, connect } from "@xarc/react-redux";
import { changeCode, setExecutionId } from "../actions";
export { reduxReducers } from '../reducers';
//Configuration for IDE
import { configuration, languageDef } from "../config/monaco";
import Editor, { useMonaco,  } from '@monaco-editor/react';



const Client = (props) => {
  const { value, dispatch } = props;
  const monaco = useMonaco()
  console.log(monaco)

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

  const onChange = (value: string, event) => {
    dispatch(changeCode(value))
  }

  return (
    <Editor
      height="90vh"
      defaultLanguage="tracta"
      theme="tracta"
      value={value}
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
  )
}

const mapStateToProps = (state) => {
  return { 
    text: state.ide.text.value,
    id: state.ide.id.value
  };
};

export const subapp: ReactSubApp = {
  Component: connect(mapStateToProps, (dispatch) => ({ dispatch }))(Client),
  wantFeatures: [
    reduxFeature({
      React,
      shareStore: true,
      reducers: true, // true => read the reduxReducers export from this file
      prepare: async (initialState) => {
        return { 
          initialState: initialState || 
          { 
            ide: {
              text: { 
                value: "" 
              },
              id: { 
                value: 0
              }
            }
          } 
        };
      },
    }),
  ],
};
