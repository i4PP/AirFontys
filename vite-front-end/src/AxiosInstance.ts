import axios, { AxiosInstance } from "axios";
import user from "./localStorage/userStorage.ts";
import router from "./router";

export const createApi = (baseURL: string): AxiosInstance => {
    return axios.create({
        baseURL: baseURL,
        withCredentials: true,
    });
};

const api = createApi(import.meta.env.VITE_AUTH_API_URL)

api.interceptors.response.use((response) => {
    return response;
}, (error) => {
    if (error.response.status === 401  && error.response.config.url === '/auth/refresh') {
        api.post("/auth/logout", {}, {
        }).then(() => {
            router.push({
                path: '/login',
                query: {logout: 'expired'},
            });
            user.value = null;
        }).catch((error) => {
            console.error(error);
        });
    }
    if (error.response.status === 401  && error.response.config.url !== '/auth/login' && error.response.config.url !== '/auth/logout') {
        api.post("/auth/refresh", {}, {
        }).then(() => {

        }).catch((error) => {
            console.error(error);
        });
    }
    return Promise.reject(error);
});
