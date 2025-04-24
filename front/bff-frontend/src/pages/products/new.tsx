import { useState, useEffect } from 'react';
import { useRouter } from 'next/router';
import { useAuth } from '@/contexts/AuthContext';
import { productService } from '@/services/product.service';
import { categoryService } from '@/services/category.service';
import styles from '../../styles/products/New-Product.module.css';
import { ProductCreationDTO } from '@/types/product';

export default function NewProductPage() {
  const router = useRouter();
  const [product, setProduct] = useState<ProductCreationDTO>({
    name: '',
    value: 0,
    categoryId: 0
  });
  const [categories, setCategories] = useState<{id: number, name: string}[]>([]);
  const [isLoading, setIsLoading] = useState(false);
  const [isLoadingCategories, setIsLoadingCategories] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const { isAuthenticated } = useAuth();

  useEffect(() => {
    if (!isAuthenticated) {
      router.push('/login');
      return;
    }
    fetchCategories();
  }, [isAuthenticated, router]);

  const fetchCategories = async () => {
    try {
      const { content } = await categoryService.getAll({
        page: 0,
        size: 100,
        sort: ['name,asc']
      });
      setCategories(content);
    } catch (err) {
      console.error('Erro ao buscar categorias:', err);
      setError('Erro ao carregar categorias');
    } finally {
      setIsLoadingCategories(false);
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    
    if (!product.name.trim()) {
      setError('Por favor, preencha o nome do produto');
      return;
    }

    if (product.categoryId <= 0) {
      setError('Por favor, selecione uma categoria');
      return;
    }

    setIsLoading(true);
    setError(null);

    try {
      await productService.create(product);
      router.push('/products');
    } catch (err) {
      console.error('Erro ao criar produto:', err);
      setError(err instanceof Error ? err.message : 'Erro ao criar produto');
    } finally {
      setIsLoading(false);
    }
  };

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>) => {
    const { name, value } = e.target;

    setProduct(prev => {
      if (name === 'price') {
        // Garante que o valor seja um número válido ou 0
        const numericValue = value === '' ? 0 : parseFloat(value);
        return { ...prev, [name]: isNaN(numericValue) ? 0 : numericValue };
      }
      
      if (name === 'categoryId') {
        return { ...prev, [name]: parseInt(value) || 0 };
      }
      
      return { ...prev, [name]: value };
    });
  };

  if (!isAuthenticated || isLoadingCategories) {
    return <div className={styles.container}>Carregando...</div>;
  }

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
            value={product.name}
            onChange={handleChange}
            placeholder="Digite o nome do produto"
            className={styles.input}
            required
          />
        </div>

        <div className={styles.formGroup}>
          <label htmlFor="price">Preço:</label>
          <input
            type="number"
            id="price"
            name="value"
            value={product.value || ''}
            onChange={handleChange}
            className={styles.input}
            step="0.01"
            min="0"
            placeholder="0,00"
            required
          />
        </div>

        <div className={styles.formGroup}>
          <label htmlFor="categoryId">Categoria:</label>
          <select
            id="categoryId"
            name="categoryId"
            value={product.categoryId}
            onChange={handleChange}
            className={styles.select}
            required
          >
            <option value="">Selecione uma categoria</option>
            {categories.map(category => (
              <option key={category.id} value={category.id}>
                {category.name}
              </option>
            ))}
          </select>
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
            onClick={() => router.push('/products')}
            className={styles.cancelButton}
          >
            Cancelar
          </button>
        </div>
      </form>
    </div>
  );
}