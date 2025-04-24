import { useState, useEffect } from 'react';
import { useRouter } from 'next/router';
import styles from '../../../styles/categories/Edit-Category.module.css';
import { categoryService } from "../../..//services/category.service";
import { CategoryDTO } from '@/types/category';

export default function EditCategoryPage() {
  const router = useRouter();
  const { id } = router.query;
  const [category, setCategory] = useState<CategoryDTO>({ id: 0, name: '' });
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    const token = localStorage.getItem('token');
    if (!token) {
      router.push('/login');
      return;
    }

    if (id) {
      fetchCategory();
    }
  }, [id]);

  const fetchCategory = async () => {
    try {
      const resp = await categoryService.getById(Number(id))
      if (!resp) {
        throw new Error('Categoria não encontrada');
      }
      const data = resp;
      setCategory(data);
      setIsLoading(false);
    } catch (err) {
      console.error('Erro ao buscar categoria:', err);
      setError('Erro ao carregar categoria');
      setIsLoading(false);
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    
    const token = localStorage.getItem('token');

    if (!token) {
        router.push('/login');
        return;
    }

    try {
        await categoryService.update(Number(id), category);
        router.push('/categories');
      } catch (err) {
        setError(err instanceof Error ? err.message : 'Erro ao atualizar categoria');
      }
  };

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setCategory(prev => ({ ...prev, [name]: value }));
  };

  if (isLoading) {
    return <div className={styles.container}>Carregando...</div>;
  }

  if (error) {
    return <div className={styles.container}>{error}</div>;
  }

  return (
    <div className={styles.container}>
      
      <form onSubmit={handleSubmit} className={styles.form}>
        <div className={styles.formGroup}>
          <label htmlFor="name">Nome:</label>
          <input
            type="text"
            id="name"
            name="name"
            value={category.name}
            onChange={handleChange}
            className={styles.input}
            required
          />
        </div>
        
        <div className={styles.buttonGroup}>
          <button type="submit" className={styles.actionButton}>
            Salvar
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