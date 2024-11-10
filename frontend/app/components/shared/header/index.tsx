'use client'
 
import { useCallback, useEffect, useState } from "react";
import { DesynthButton, DesynthHeader } from "@desynth/web-components-react/dist/components";
import { applyPolyfills, defineCustomElements } from "@desynth/web-components/loader";
import { executeCode } from "../../../actions";

const changeSessionId = (code: string) => {
  return {
    type: "CHANGE_SESSION_ID",
    payload: code
  };
};

export const Header = ({ onSubmit }: any) => {
  const [code, setCode] = useState('');
  const [loaded, setLoaded] = useState(false);

  useEffect(() => {
    setLoaded(true);
  }, []);

  if (loaded === false) 
    return 0
  
  if (typeof window !== "undefined") {     
    applyPolyfills().then(() => {
      defineCustomElements(window);
    });
  }
  return (
    <DesynthHeader id="header">
      <div slot="main">
        <DesynthButton id="open-drawer-button" onClick={onSubmit}>
          Run
        </DesynthButton>
      </div>
    </DesynthHeader>
  );
};

