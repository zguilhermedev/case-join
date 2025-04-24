import { useState, useEffect } from 'react';
import { useRouter } from 'next/router';
import { useAuth } from '@/contexts/AuthContext';
import { productService } from '@/services/product.service';
import { categoryService } from '@/services/category.service';
import styles from '../../../styles/products/Edit-Product.module.css';
import { ProductResponse } from '@/types/product';

export default function EditProductPage() {
  const router = useRouter();
  const { id } = router.query;
  const [product, setProduct] = useState<ProductResponse>({
    id: 0,
    name: '',
    description: '',
    value: 0,
    category: { id: 0, name: '' }
  });
  const [categories, setCategories] = useState<{id: number, name: string}[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState('');
  const { isAuthenticated } = useAuth();

  useEffect(() => {
    if (!isAuthenticated) {
      router.push('/login');
      return;
    }

    if (id) {
      fetchProduct();
      fetchCategories();
    }
  }, [id, isAuthenticated, router]);

  const fetchProduct = async () => {
    try {
      setIsLoading(true);
      const data = await productService.getById(Number(id));
      setProduct(data);
    } catch (err) {
      console.error('Erro ao buscar produto:', err);
      setError('Erro ao carregar produto');
    } finally {
      setIsLoading(false);
    }
  };

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
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    
    try {
      await productService.update(product.id, {
        name: product.name,
        value: product.value,
        categoryId: product.category.id
      });
      router.push('/products');
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Erro ao atualizar produto');
    }
  };

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>) => {
    const { name, value } = e.target;
    
    if (name === 'categoryId') {
      const selectedCategory = categories.find(cat => cat.id === Number(value));
      if (selectedCategory) {
        setProduct(prev => ({
          ...prev,
          category: {
            id: selectedCategory.id,
            name: selectedCategory.name
          }
        }));
      }
      return;
    }

    setProduct(prev => ({ 
      ...prev, 
      [name]: name === 'price' ? parseFloat(value) || 0 : value 
    }));
  };

  if (isLoading) {
    return <div className={styles.container}>Carregando...</div>;
  }

  if (error) {
    return <div className={styles.container}>{error}</div>;
  }

  return (
    <div className={styles.container}>
      <h1>Editar Produto</h1>
      
      <form onSubmit={handleSubmit} className={styles.form}>
        <div className={styles.formGroup}>
          <label htmlFor="name">Nome:</label>
          <input
            type="text"
            id="name"
            name="name"
            value={product.name}
            onChange={handleChange}
            className={styles.input}
            required
          />
        </div>

        <div className={styles.formGroup}>
          <label htmlFor="price">Preço:</label>
          <input
            type="number"
            id="price"
            name="price"
            value={product.value}
            onChange={handleChange}
            className={styles.input}
            step="0.01"
            min="0"
            required
          />
        </div>

        <div className={styles.formGroup}>
          <label htmlFor="categoryId">Categoria:</label>
          <select
            id="categoryId"
            name="categoryId"
            value={product.category.id}
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
          <button type="submit" className={styles.actionButton}>
            Salvar
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