import { useEffect, useState } from 'react';
import { useRouter } from 'next/router';
import { useAuth } from '@/contexts/AuthContext';
import { categoryService } from '@/services/category.service';
import styles from '../../styles/categories/Categories.module.css';
import { CategoryDTO } from '@/types/category';

export default function CategoriesPage() {
  const [categories, setCategories] = useState<CategoryDTO[]>([]);
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
    fetchCategories();
  }, [isAuthenticated, router]);

  const fetchCategories = async (nameFilter = '') => {
    try {
      setIsLoading(true);
      setError(null);
      const { content } = await categoryService.getAll({ 
        name: nameFilter,
        page: 0,
        size: 10,
        sort: ['name,asc']
      });
      setCategories(content);
    } catch (err) {
      console.error('Erro ao buscar categorias:', err);
      setError('Erro ao carregar categorias');
    } finally {
      setIsLoading(false);
    }
  };

  const handleDelete = async (id: number) => {
    if (!confirm('Tem certeza que deseja excluir esta categoria?')) return;
    try {
      await categoryService.delete(id);
      fetchCategories(filter);
    } catch (err) {
      console.error('Erro ao excluir categoria:', err);
      setError('Erro ao excluir categoria');
    }
  };

  const handleFilterChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setFilter(e.target.value);
  };

  const handleFilterSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    fetchCategories(filter);
  };

  return (
    <div className={styles.container}>
      <div className={styles.header}>
        <h1>Categorias</h1>
        <button 
          onClick={() => router.push('/categories/new')} 
          className={styles.primaryButton}
        >
          Nova Categoria
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
      ) : categories.length === 0 ? (
        <div className={styles.emptyState}>
          {filter ? 'Nenhuma categoria encontrada com esse filtro' : 'Nenhuma categoria cadastrada'}
        </div>
      ) : (
        <div className={styles.tableWrapper}>
          <table className={styles.table}>
            <thead>
              <tr>
                <th className={styles.idColumn}>ID</th>
                <th className={styles.nameColumn}>Nome</th>
                <th className={styles.actionsColumn}>Ações</th>
              </tr>
            </thead>
            <tbody>
              {categories.map((cat) => (
                <tr key={cat.id}>
                  <td className={styles.idCell}>{cat.id}</td>
                  <td className={styles.nameCell}>{cat.name}</td>
                  <td className={styles.actionsCell}>
                    <div className={styles.actionsGroup}>
                      <button 
                        onClick={() => router.push(`/categories/edit/${cat.id}`)} 
                        className={styles.editButton}
                      >
                        Editar
                      </button>
                      <button 
                        onClick={() => handleDelete(cat.id)} 
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