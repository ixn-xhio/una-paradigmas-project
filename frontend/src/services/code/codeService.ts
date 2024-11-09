import { HttpClient } from '../../config/httpClient'; // Asegúrate de que HttpClient esté importado correctamente

interface ExecuteCodeRequest {
  code: string;
}

export interface ExecuteCodeResponse {
	"outputs": string[],
	"requiresInput": boolean,
	"sessionId": string,
	"returnValue": null
}

interface InputCodeRequest {
  input: string;
  sessionId: string;
}

export interface InputCodeResponse {
	"outputs": string[],
	"requiresInput": boolean,
	"sessionId": string,
	"returnValue": null
}

class CodeService {
    private HttpClient: HttpClient;

    constructor() {
        this.HttpClient = HttpClient.getInstance(); // Obtener la instancia de HttpClient
    }

    // Método para ejecutar el código (POST /api/code/execute)
    public async executeCode(request: ExecuteCodeRequest): Promise<ExecuteCodeResponse> {
        try {
        const response = await this.HttpClient.request({
            method: 'POST',
            url: 'http://localhost:8085/api/code/execute',
            data: request,
        });
        return response.data; // Retornar la respuesta del servidor
        } catch (error) {
        console.error('Error ejecutando el código:', error);
        throw error; // Lanzar el error para que sea manejado por el consumidor del servicio
        }
    }

    // Método para enviar la entrada (POST /api/code/input)
    public async sendInput(request: InputCodeRequest): Promise<InputCodeResponse> {
        try {
        const response = await this.HttpClient.request({
            method: 'POST',
            url: 'http://localhost:8085/api/code/input',
            data: request,
        });
        return response.data; // Retornar la respuesta del servidor
        } catch (error) {
        console.error('Error enviando la entrada:', error);
        throw error; // Lanzar el error para que sea manejado por el consumidor del servicio
        }
    }
}

// Exportar el servicio para su uso en otros lugares
export const codeService = new CodeService();
