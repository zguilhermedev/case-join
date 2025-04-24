import { useState, useEffect } from "react";
import { useRouter } from "next/router";
import { useAuth } from "../contexts/AuthContext";
import styles from "../styles/Login.module.css";

export default function LoginPage() {
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const [error, setError] = useState("");
    const [isLoading, setIsLoading] = useState(false);
    const router = useRouter();
    const { isAuthenticated, login } = useAuth();

    // Redireciona se já estiver autenticado
    useEffect(() => {
      if (isAuthenticated) {
        router.push('/home');
      }
    }, [isAuthenticated, router]);

    const { query } = useRouter();
    const [successMessage, setSuccessMessage] = useState("");

    useEffect(() => {
    if (query.registered) {
        setSuccessMessage("Registro concluído com sucesso! Faça login para continuar.");
    }
    }, [query]);

    {successMessage && <p className={styles.loginSuccess}>{successMessage}</p>}


    const handleLogin = async (e: React.FormEvent) => {
        e.preventDefault();
        setIsLoading(true);
        setError("");
        
        try {
            await login(username, password);
        } catch (err: any) {
            setError(err.message || "Erro ao fazer login");
        } finally {
            setIsLoading(false);
        }
    };

    if (isAuthenticated) {
        return null; // Ou <Loading /> enquanto redireciona
    }

    return (
        <div className={styles.loginContainer}>
            <div className={styles.loginCard}>
                <h1 className={styles.loginTitle}>Login</h1>
                
                {error && <p className={styles.loginError}>{error}</p>}
                
                <form onSubmit={handleLogin} className={styles.loginForm}>
                    <div className={styles.formGroup}>
                        <label htmlFor="username" className={styles.loginLabel}>
                            Usuário
                        </label>
                        <input
                            id="username"
                            type="text"
                            placeholder="Digite seu usuário"
                            value={username}
                            onChange={(e) => setUsername(e.target.value)}
                            className={styles.loginInput}
                            required
                            autoComplete="username"
                            disabled={isLoading}
                        />
                    </div>

                    <div className={styles.formGroup}>
                        <label htmlFor="password" className={styles.loginLabel}>
                            Senha
                        </label>
                        <input
                            id="password"
                            type="password"
                            placeholder="Digite sua senha"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                            className={styles.loginInput}
                            required
                            autoComplete="current-password"
                            disabled={isLoading}
                        />
                    </div>

                    <button 
                        type="submit" 
                        className={styles.loginButton}
                        aria-label="Entrar no sistema"
                        disabled={isLoading}
                    >
                        {isLoading ? "Carregando..." : "Entrar"}
                    </button>
                </form>
                
                <p className={styles.loginFooter}>
                    Não tem uma conta?{' '}
                    <a href="/register" className={styles.loginLink}>
                        Cadastre-se
                    </a>
                </p>
            </div>
        </div>
    );
}