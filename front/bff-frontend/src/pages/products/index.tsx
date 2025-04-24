import { useEffect, useState } from 'react';
import { useRouter } from 'next/router';
import { useAuth } from '@/contexts/AuthContext';
import { productService } from '@/services/product.service';
import styles from '../../styles/products/Products.module.css';
import { ProductResponse } from '@/types/product';

export default function ProductsPage() {
  const [products, setProducts] = useState<ProductResponse[]>([]);
  const [filter, setFilter] = useState('');
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const router = useRouter();
  const { isAuthenticated } = useAuth();

  useEffect(() => {
    if (!isAuthenticated) {
      router.push('/login');
      return;
    }
    fetchProducts();
  }, [isAuthenticated, router]);

  const fetchProducts = async (categoryFilter = '') => {
    try {
      setIsLoading(true);
      setError(null);
      const { content } = await productService.getAll({ 
        category: categoryFilter,
        page: 0,
        size: 10,
        sort: ['name,asc']
      });
      setProducts(content);
    } catch (err) {
      console.error('Erro ao buscar produtos:', err);
      setError('Erro ao carregar produtos');
    } finally {
      setIsLoading(false);
    }
  };

  const handleDelete = async (id: number) => {
    if (!confirm('Tem certeza que deseja excluir este produto?')) return;
    try {
      await productService.delete(id);
      fetchProducts(filter);
    } catch (err) {
      console.error('Erro ao excluir produto:', err);
      setError('Erro ao excluir produto');
    }
  };

  const handleFilterChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setFilter(e.target.value);
  };

  const handleFilterSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    fetchProducts(filter);
  };

  return (
    <div className={styles.container}>
      <div className={styles.header}>
        <h1>Produtos</h1>
        <button 
          onClick={() => router.push('/products/new')} 
          className={styles.primaryButton}
        >
          Novo Produto
        </button>
      </div>
      
      <form onSubmit={handleFilterSubmit} className={styles.filterContainer}>
        <input
          type="text"
          placeholder="Filtrar por nome"
          value={filter}
          onChange={handleFilterChange}
          className={styles.filterInput}
        />
        <button 
          type="submit"
          className={styles.filterButton}
        >
          Buscar
        </button>
      </form>

      {error && <div className={styles.errorMessage}>{error}</div>}

      {isLoading ? (
        <div className={styles.loading}>Carregando...</div>
      ) : products.length === 0 ? (
        <div className={styles.emptyState}>
          {filter ? 'Nenhum produto encontrado com esse filtro' : 'Nenhum produto cadastrado'}
        </div>
      ) : (
        <div className={styles.tableWrapper}>
          <table className={styles.table}>
            <thead>
              <tr>
                <th className={styles.idColumn}>ID</th>
                <th className={styles.nameColumn}>Nome</th>
                <th className={styles.priceColumn}>Preço</th>
                <th className={styles.categoryColumn}>Categoria</th>
                <th className={styles.actionsColumn}>Ações</th>
              </tr>
            </thead>
            <tbody>
              {products.map((product) => (
                <tr key={product.id}>
                  <td className={styles.idCell}>{product.id}</td>
                  <td className={styles.nameCell}>{product.name}</td>
                  <td className={styles.priceCell}>
                    {new Intl.NumberFormat('pt-BR', {
                      style: 'currency',
                      currency: 'BRL'
                    }).format(product.value)}
                  </td>
                  <td className={styles.categoryCell}>{product.category.name}</td>
                  <td className={styles.actionsCell}>
                    <div className={styles.actionsGroup}>
                      <button 
                        onClick={() => router.push(`/products/edit/${product.id}`)} 
                        className={styles.editButton}
                      >
                        Editar
                      </button>
                      <button 
                        onClick={() => handleDelete(product.id)} 
                        className={styles.deleteButton}
                      >
                        Excluir
                      </button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
}