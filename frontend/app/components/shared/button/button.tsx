'use client'
import { DesynthButton } from "@desynth/web-components-react/dist/components";

export const Button = ({ id, text, onClick }: any) => {
  return (
        <DesynthButton id={id} onClick={onClick}>
          {text}
        </DesynthButton>
  );
};

