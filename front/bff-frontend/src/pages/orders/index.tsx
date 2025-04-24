import { useEffect, useState } from 'react';
import { useRouter } from 'next/router';
import { useAuth } from '@/contexts/AuthContext';
import { orderService } from '@/services/order.service';
import styles from '../../styles/orders/Orders.module.css';
import { OrderDTO } from '@/types/order';

export default function OrdersPage() {
  const [orders, setOrders] = useState<OrderDTO[]>([]);
  const [filterUserId, setFilterUserId] = useState('');
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const router = useRouter();
  const { isAuthenticated, user } = useAuth();

  const fetchOrders = async (userId?: string) => {
    try {
      const filterUser = user?.role == 'ADMIN' ? userId : user?.id + '';
      console.log(filterUser)
      setIsLoading(true);
      setError(null);
      const { content } = await orderService.getAll({ 
        userId: filterUser ? parseInt(filterUser) : undefined,
        page: 0,
        size: 10
      });
      setOrders(content);
    } catch (err) {
      console.error('Erro ao buscar pedidos:', err);
      setError('Erro ao carregar pedidos');
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    if (!isAuthenticated) {
      router.push('/login');
      return;
    }

    fetchOrders();
  }, [isAuthenticated, router]);

  const handleDelete = async (id: number) => {
    if (!confirm('Tem certeza que deseja excluir este pedido?')) return;
    try {
      await orderService.delete(id);
      fetchOrders(filterUserId);
    } catch (err) {
      console.error('Erro ao excluir pedido:', err);
      setError('Erro ao excluir pedido');
    }
  };

  const handleFilterChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setFilterUserId(e.target.value);
  };

  const handleFilterSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    fetchOrders(filterUserId);
  };

  return (
    <div className={styles.container}>
      <div className={styles.header}>
        <h1>Pedidos</h1>
        <button 
          onClick={() => router.push('/orders/new')} 
          className={styles.primaryButton}
        >
          Novo Pedido
        </button>
      </div>
      
      <form onSubmit={handleFilterSubmit} className={styles.filterContainer}>
        <input
          type="number"
          placeholder="Filtrar por ID do usuário"
          value={filterUserId}
          onChange={handleFilterChange}
          className={styles.filterInput}
          min="1"
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
      ) : orders.length === 0 ? (
        <div className={styles.emptyState}>
          {filterUserId ? 'Nenhum pedido encontrado para este usuário' : 'Nenhum pedido cadastrado'}
        </div>
      ) : (
        <div className={styles.tableWrapper}>
          <table className={styles.table}>
            <thead>
              <tr>
                <th className={styles.idColumn}>ID</th>
                <th className={styles.amountColumn}>Valor Total</th>
                <th className={styles.productsColumn}>Produtos</th>
                <th className={styles.actionsColumn}>Ações</th>
              </tr>
            </thead>
            <tbody>
              {orders.map((order) => (
                <tr key={order.id}>
                  <td className={styles.idCell}>{order.id}</td>
                  <td className={styles.amountCell}>
                    {new Intl.NumberFormat('pt-BR', {
                      style: 'currency',
                      currency: 'BRL'
                    }).format(order.amount)}
                  </td>
                  <td className={styles.productsCell}>
                    {order.products.map(p => (
                      <div key={p?.product?.id}>
                        {p.quantity}x {p.product?.name} ({p.product?.category.name})
                      </div>
                    ))}
                  </td>
                  <td className={styles.actionsCell}>
                    <div className={styles.actionsGroup}>
                      <button 
                        onClick={() => router.push(`/orders/edit/${order.id}`)} 
                        className={styles.editButton}
                      >
                        Editar
                      </button>
                      <button 
                        onClick={() => handleDelete(order.id)} 
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