import axios from "axios";

const API_BASE_URL = import.meta.env.VITE_API_URL;
const AUTH_STORAGE_KEY = "restlab.jwt";
const AUTH_CREDENTIALS = {
  username: "admin",
  password: "admin123",
};

const apiClient = axios.create({
  baseURL: API_BASE_URL,
  headers: { "Content-Type": "application/json" },
  timeout: 10000,
});

const authClient = axios.create({
  baseURL: API_BASE_URL,
  headers: { "Content-Type": "application/json" },
  timeout: 10000,
});

const getStoredToken = () => {
  if (typeof window === "undefined") {
    return null;
  }

  return window.localStorage.getItem(AUTH_STORAGE_KEY);
};

const setStoredToken = (token) => {
  if (typeof window === "undefined") {
    return;
  }

  window.localStorage.setItem(AUTH_STORAGE_KEY, token);
};

const loginForToken = async () => {
  const response = await authClient.post("/auth/login", AUTH_CREDENTIALS);
  const token = response.data?.token;

  if (!token) {
    throw new Error("Lipsește token-ul de autentificare.");
  }

  setStoredToken(token);
  return token;
};

const ensureToken = async () => {
  const token = getStoredToken();
  if (token) {
    return token;
  }

  return loginForToken();
};

const requestWithAuth = async (config) => {
  const token = await ensureToken();

  try {
    return await apiClient.request({
      ...config,
      headers: {
        ...(config.headers || {}),
        Authorization: `Bearer ${token}`,
      },
    });
  } catch (error) {
    if (error?.response?.status === 401 || error?.response?.status === 403) {
      if (typeof window !== "undefined") {
        window.localStorage.removeItem(AUTH_STORAGE_KEY);
      }
      const freshToken = await loginForToken();
      return apiClient.request({
        ...config,
        headers: {
          ...(config.headers || {}),
          Authorization: `Bearer ${freshToken}`,
        },
      });
    }

    throw error;
  }
};

export const productApi = {
  getAll: async (config) => {
    const response = await requestWithAuth({ method: "get", url: "/products", ...config });
    return response.data;
  },
  getById: async (id, config) => {
    const response = await requestWithAuth({
      method: "get",
      url: `/products/${id}`,
      ...config,
    });
    return response.data;
  },
  getCategories: async (config) => {
    const response = await requestWithAuth({
      method: "get",
      url: "/categories",
      ...config,
    });
    return response.data;
  },
  create: async (payload, config) => {
    const response = await requestWithAuth({
      method: "post",
      url: "/products",
      data: payload,
      ...config,
    });
    return response.data;
  },
  update: async (id, payload, config) => {
    const response = await requestWithAuth({
      method: "put",
      url: `/products/${id}`,
      data: payload,
      ...config,
    });
    return response.data;
  },
  remove: async (id, config) => {
    const response = await requestWithAuth({
      method: "delete",
      url: `/products/${id}`,
      ...config,
    });
    return response.data;
  },
};
