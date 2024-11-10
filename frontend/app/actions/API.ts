import { codeService } from "../services/codeService";

export const executeCode = async (code: string) => {
    const result = await codeService.executeCode({ code });
    return (result && result !== null) ? result : -1;
}

export const sendInput = async (input: string, sessionId: string) => {
    const result = await codeService.sendInput({ input, sessionId });
    return (result && result !== null) ? result : -1;
}