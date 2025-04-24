import axios from "axios";

const API_BASE_URL = process.env.NEXT_PUBLIC_AUTH_URL || "http://localhost:9090";

export async function login(username: string, password: string): Promise<string> {
  try {
    const response = await axios.post(`${API_BASE_URL}/auth/login`, {
      username,
      password,
    });
    return response.data.token; // supondo que o token vem nesse formato
  } catch (error: any) {
    throw new Error(error.response?.data?.message || "Erro ao fazer login");
  }
}