﻿// WebSocketService.ts

import { createApi } from "./AxiosInstance.ts";
import router from "./router";
import user from "./localStorage/userStorage.ts";

export class WebSocketService {
    private websocket: WebSocket | null = null;
    private retryCount = 0;
    private maxRetries = 3;
    private endpoint: string;
    private api = createApi(import.meta.env.VITE_AUTH_API_URL);

    constructor(endpoint: string) {
        this.endpoint = endpoint;
    }

    connectWebSocket(
        onMessage: (data: any) => void,
        onError: (error: Event) => void,
        onOpen?: () => void
    ) {
        this.websocket = new WebSocket(this.endpoint);

        this.websocket.onopen = () => {
            console.log('WebSocket connection opened');
            this.retryCount = 0;
            console.log('retryCount:', this.retryCount);
        };

        this.websocket.onmessage = (event: MessageEvent) => {
            try {
                onMessage(event.data);
            } catch (err) {
                console.error('Failed to parse message data:', err);
            }
        };

        this.websocket.onerror = async (error: Event) => {
            console.error('WebSocket error:', error);
            onError(error);
            console.log('retryCount:', this.retryCount);
            this.retryCount++;
            await this.handleTokenRefresh();
        };

        this.websocket.onclose = () => {
            console.log('WebSocket connection closed');
            if (this.retryCount < this.maxRetries) {
                setTimeout(() => this.connectWebSocket(onMessage, onError, onOpen), 2000);
            } else {
                console.error('Max retries reached. Closing WebSocket connection');
                this.api.post('/auth/logout');
                user.value = null;
                router.push('/login');
            }
        };
    }

    async handleTokenRefresh() {
        try {
            await this.api.post('/auth/refresh');
            console.log('Token refreshed successfully');
            this.retryCount++;
        } catch (err) {
            console.error('Failed to refresh token:', err);
            await this.api.post('/auth/logout');
            user.value = null;
            router.push('/login');
        }
    }

    closeWebSocket() {
        if (this.websocket) {
            this.websocket.close();
        }
    }
}