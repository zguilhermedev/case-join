import { createContext, useContext, useEffect, useState } from "react";
import { useRouter } from "next/router";
import { api } from '@/services/api';

interface AuthContextProps {
  token: string | null;
  isAuthenticated: boolean;
  isLoading: boolean;
  user: UserData | null;  // Adicione esta linha
  login: (username: string, password: string) => Promise<void>;
  logout: () => void;
}

interface UserData {
  id: number;
  username: string;
  role: string;
}

const AuthContext = createContext<AuthContextProps>({} as AuthContextProps);

export const AuthProvider = ({ children }: { children: React.ReactNode }) => {
  const [token, setToken] = useState<string | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const router = useRouter();
  const [user, setUser] = useState<UserData | null>(null);

  useEffect(() => {
    const initializeAuth = async () => {
      const savedToken = localStorage.getItem('token');
      const savedUser = localStorage.getItem('user');
      
      if (savedToken && savedUser) {
        setToken(savedToken);
        setUser(JSON.parse(savedUser));
        api.setToken(savedToken);
      }
      setIsLoading(false);
    };
  
    initializeAuth();
  }, []);

  const login = async (username: string, password: string) => {
    setIsLoading(true);
    try {
      const response = await fetch('http://localhost:9090/bff-service/auth/login', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ username, password }),
      });
  
      if (!response.ok) {
        throw new Error('Credenciais inválidas');
      }
  
      const data = await response.json();
      localStorage.setItem('token', data.token);
      localStorage.setItem('user', JSON.stringify(data.user)); // Armazena o objeto user completo
      
      setToken(data.token);
      setUser(data.user); // Assume que o backend retorna { token, user: { id, name } }
      api.setToken(data.token);
      
      router.push('/home');
    } finally {
      setIsLoading(false);
    }
  }

  const logout = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    setToken(null);
    setUser(null);
    api.setToken(null);
    router.push('/login');
  };

  return (
    <AuthContext.Provider value={{ 
      token, 
      isAuthenticated: !!token,
      isLoading,
      user,
      login, 
      logout 
    }}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => useContext(AuthContext);