// src/pages/register.tsx
import { useState } from "react";
import { useRouter } from "next/router";
import { userService } from "../services/user.service";
import styles from "../styles/Login.module.css";
import Loading from "../components/loading";

export default function RegisterPage() {
    const [formData, setFormData] = useState({
        username: "",
        password: "",
        confirmPassword: ""
    });
    const [error, setError] = useState("");
    const [isLoading, setIsLoading] = useState(false);
    const [success, setSuccess] = useState(false);
    const router = useRouter();

    const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const { id, value } = e.target;
        setFormData(prev => ({
            ...prev,
            [id]: value
        }));
    };

    const handleRegister = async (e: React.FormEvent) => {
        e.preventDefault();
        
        if (formData.password !== formData.confirmPassword) {
            setError("As senhas não coincidem");
            return;
        }

        setIsLoading(true);
        setError("");
        
        try {
            await userService.create({
                username: formData.username,
                password: formData.password,
                role: 'USER'
            });
            
            setSuccess(true);
            // Redireciona após 2 segundos para mostrar a mensagem de sucesso
            setTimeout(() => {
                router.push('/login?registered=true');
            }, 2000);
            
        } catch (err: any) {
            setError(err.message || "Erro ao registrar usuário");
        } finally {
            setIsLoading(false); // Garante que o loading sempre será desativado
        }
    };

    if (isLoading) {
        return <Loading />; // Mostra o componente Loading enquanto carrega
    }

    if (success) {
        return (
            <div className={styles.loginContainer}>
                <div className={styles.loginCard}>
                    <h1 className={styles.loginTitle}>Sucesso!</h1>
                    <p className={styles.loginSuccess}>Usuário registrado com sucesso. Redirecionando para login...</p>
                </div>
            </div>
        );
    }

    return (
        <div className={styles.loginContainer}>
            <div className={styles.loginCard}>
                <h1 className={styles.loginTitle}>Cadastro</h1>
                
                {error && <p className={styles.loginError}>{error}</p>}
                
                <form onSubmit={handleRegister} className={styles.loginForm}>
                <div className={styles.formGroup}>
                        <label htmlFor="username" className={styles.loginLabel}>
                            Usuário
                        </label>
                        <input
                            id="username"
                            type="text"
                            placeholder="Digite seu usuário"
                            value={formData.username}
                            onChange={handleChange}
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
                            value={formData.password}
                            onChange={handleChange}
                            className={styles.loginInput}
                            required
                            autoComplete="new-password"
                            disabled={isLoading}
                        />
                    </div>

                    <div className={styles.formGroup}>
                        <label htmlFor="confirmPassword" className={styles.loginLabel}>
                            Confirmar Senha
                        </label>
                        <input
                            id="confirmPassword"
                            type="password"
                            placeholder="Confirme sua senha"
                            value={formData.confirmPassword}
                            onChange={handleChange}
                            className={styles.loginInput}
                            required
                            autoComplete="new-password"
                            disabled={isLoading}
                        />
                    </div>

                    <button 
                        type="submit" 
                        className={styles.loginButton}
                        aria-label="Registrar usuário"
                        disabled={isLoading}
                    >
                        {isLoading ? (
                            <>
                                <span className={styles.spinner} /> Registrando...
                            </>
                        ) : "Registrar"}
                    </button>
                </form>
                
                <p className={styles.loginFooter}>
                    Já tem uma conta?{' '}
                    <a href="/login" className={styles.loginLink}>
                        Faça login
                    </a>
                </p>
            </div>
        </div>
    );
}