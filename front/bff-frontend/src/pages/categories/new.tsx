import React, { useState } from "react";
import { useRouter } from "next/router";
import { categoryService } from "../../services/category.service"; // Importando o serviço
import styles from "../../styles/categories/New-Category.module.css"; // Importando o arquivo de estilo
import { CreateCategory } from "@/types/create-category";


export default function NewCategoryPage() {
  const router = useRouter();
  const [category, setCategory] = useState<CreateCategory>({ name: '' });
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    
    const token = localStorage.getItem('token');
    if (!token) {
      router.push('/login');
      return;
    }

    if (!category.name.trim()) {
      setError('Por favor, preencha o nome da categoria');
      return;
    }

    setIsLoading(true);
    setError(null);

    try {
      await categoryService.create(category);
      router.push('/categories');
    } catch (err) {
      console.error('Erro ao criar categoria:', err);
      setError(err instanceof Error ? err.message : 'Erro ao criar categoria');
    } finally {
      setIsLoading(false);
    }
  };

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setCategory(prev => ({ ...prev, [name]: value }));
  };

  return (
    <div className={styles.container}>      
      {error && (
        <div className={styles.errorMessage}>
          {error}
        </div>
      )}

      <form onSubmit={handleSubmit} className={styles.form}>
        <div className={styles.formGroup}>
          <label htmlFor="name">Nome:</label>
          <input
            type="text"
            id="name"
            name="name"
            value={category.name}
            onChange={handleChange}
            placeholder="Digite o nome da categoria"
            className={styles.input}
            required
          />
        </div>
        
        <div className={styles.buttonGroup}>
          <button 
            type="submit" 
            disabled={isLoading}
            className={styles.actionButton}
          >
            {isLoading ? 'Salvando...' : 'Salvar'}
          </button>
          <button 
            type="button" 
            onClick={() => router.push('/categories')}
            className={styles.cancelButton}
          >
            Cancelar
          </button>
        </div>
      </form>
    </div>
  );
}