'use client'
 
import { useEffect, useState } from "react";
import { DesynthButton, DesynthHeader } from "@desynth/web-components-react/dist/components";
import { applyPolyfills, defineCustomElements } from "@desynth/web-components/loader";

export const Header = ({ onSubmit }: any) => {
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

