import axios, { AxiosError, AxiosHeaders, AxiosInstance, AxiosRequestConfig, AxiosResponse, InternalAxiosRequestConfig } from 'axios';

// Extender AxiosRequestConfig para agregar la propiedad `_retry`
interface AxiosRequestConfigWithRetry extends InternalAxiosRequestConfig {
    _retry?: boolean;
}
  
export class HttpClient {
    private static instance: HttpClient;
    private axiosInstance: AxiosInstance;
    private isRefreshing: boolean = false;
    private pendingRequests: Array<(token: string) => void> = [];
  
    private constructor() {
      this.axiosInstance = axios.create();
      this.setupInterceptors();
    }
  
    public static getInstance(): HttpClient {
      if (!HttpClient.instance) {
        HttpClient.instance = new HttpClient();
      }
      return HttpClient.instance;
    }
  
    // Set up Axios interceptors for requests and responses
    private setupInterceptors() {
      // Interceptor de solicitud
      this.axiosInstance.interceptors.request.use(
        (config: AxiosRequestConfigWithRetry) => {
          const token = localStorage.getItem('accessToken'); // Obtener token de un lugar de almacenamiento
          if (token) {
            // Use `AxiosHeaders` to set headers correctly
            if (config.headers instanceof AxiosHeaders) {
                config.headers.set('Authorization', `Bearer ${token}`);
            } else {
                config.headers = new AxiosHeaders({
                  Authorization: `Bearer ${token}`
                });
            }
          }
          return config;
        },
        (error: AxiosError) => Promise.reject(error)
      );
  
      // Interceptor de respuesta
      this.axiosInstance.interceptors.response.use(
        (response: AxiosResponse) => {
          return response; // Devuelve la respuesta si es exitosa
        },
        async (error: AxiosError) => {
          const originalRequest = error.config as AxiosRequestConfigWithRetry;
  
          // Si la respuesta es un error 401 (no autorizado) y aún no estamos refrescando el token
          if (error.response?.status === 401 && !originalRequest._retry) {
            originalRequest._retry = true;
  
            if (!this.isRefreshing) {
              this.isRefreshing = true;
              try {
                // Aquí deberías agregar la lógica para refrescar el token
                const newToken = await this.refreshToken(); // Refrescar el token (simulado)
                localStorage.setItem('accessToken', newToken); // Guardar el nuevo token
  
                // Reintentar las solicitudes en la cola con el nuevo token
                this.pendingRequests.forEach((callback) => callback(newToken));
                this.pendingRequests = []; // Limpiar la cola después de reenviar todas las solicitudes
              } catch (err) {
                return Promise.reject(err); // Si el refresh falla, rechaza la promesa
              } finally {
                this.isRefreshing = false;
              }
            }
  
            // Coloca la solicitud original en la cola para reintentarla después de que se refresque el token
            return new Promise((resolve) => {
              this.pendingRequests.push((token: string) => {
                originalRequest.headers['Authorization'] = `Bearer ${token}`;
                resolve(this.axiosInstance(originalRequest));
              });
            });
          }
  
          return Promise.reject(error); // Si el error no es 401 o la solicitud ya se intentó, se rechaza
        }
      );
    }
  
    // Método para hacer solicitudes
    public async request(config: AxiosRequestConfig): Promise<AxiosResponse> {
      return this.axiosInstance(config);
    }
  
    // Método para refrescar el token (simulado)
    private async refreshToken(): Promise<string> {
      // Aquí se debería hacer una solicitud real para obtener un nuevo token
      console.log('Refrescando el token...');
      return new Promise((resolve) => {
        setTimeout(() => {
          resolve('newAccessToken'); // Simulamos el nuevo token
        }, 1000); // Simulamos un retraso para refrescar el token
      });
    }

    public get(url: string, config?: AxiosRequestConfig) {
        return this.axiosInstance.get(url, config);
    }

    public post(url: string, data?: any, config?: AxiosRequestConfig) {
        return this.axiosInstance.post(url, data, config);
    }

    public put(url: string, data?: any, config?: AxiosRequestConfig) {
        return this.axiosInstance.put(url, data, config);
    }


    public delete(url: string, config?: AxiosRequestConfig) {
        return this.axiosInstance.delete(url, config);
    }

    // Additional methods (put, delete, etc.) can be added here
    }
