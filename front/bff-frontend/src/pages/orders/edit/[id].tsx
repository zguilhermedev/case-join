import { useState, useEffect } from 'react';
import { useRouter } from 'next/router';
import { useAuth } from '@/contexts/AuthContext';
import { orderService } from '@/services/order.service';
import { productService } from '@/services/product.service';
import styles from '../../../styles/orders/Edit-Order.module.css';
import { OrderCreationDTO, ProductResponse } from '@/types/order';
import Loading from '@/components/loading';

export default function EditOrderPage() {
  const router = useRouter();
  const { id } = router.query;
  const { isAuthenticated, user } = useAuth();
  const [order, setOrder] = useState<OrderCreationDTO>({
    userId: user?.id || 0,
    products: [],
    amount: 0
  });
  const [availableProducts, setAvailableProducts] = useState<ProductResponse[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [isLoadingProducts, setIsLoadingProducts] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (!isAuthenticated) {
      router.push('/login');
      return;
    }

    if (id) {
      fetchOrder();
      fetchProducts();
    }
  }, [id, isAuthenticated, router, user]);

  const fetchOrder = async () => {
    try {
      const orderData = await orderService.getById(Number(id));
      setOrder({
        userId: orderData.userId,
        products: orderData.products.map(p => ({
          productId: p.product.id,
          quantity: p.quantity
        })),
        amount: orderData.amount
      });
    } catch (err) {
      console.error('Erro ao buscar pedido:', err);
      setError('Erro ao carregar pedido');
    } finally {
      setIsLoading(false);
    }
  };

  const fetchProducts = async () => {
    try {
      const { content } = await productService.getAll({
        page: 0,
        size: 100,
        sort: ['name,asc']
      });
      setAvailableProducts(content);
    } catch (err) {
      console.error('Erro ao buscar produtos:', err);
      setError('Erro ao carregar produtos');
    } finally {
      setIsLoadingProducts(false);
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    
    if (!user?.id) {
      setError('Usuário não identificado. Faça login novamente.');
      return;
    }

    if (order.products.length === 0) {
      setError('Adicione pelo menos um produto ao pedido');
      return;
    }

    // Calcula o total do pedido
    const amount = order.products.reduce((total, item) => {
      const product = availableProducts.find(p => p.id === item.productId);
      return total + (product?.value || 0) * item.quantity;
    }, 0);

    setIsLoading(true);
    setError(null);

    try {
      await orderService.update(Number(id), {
        ...order,
        userId: user.id,
        amount
      });
      router.push('/orders');
    } catch (err) {
      console.error('Erro ao atualizar pedido:', err);
      setError(err instanceof Error ? err.message : 'Erro ao atualizar pedido');
    } finally {
      setIsLoading(false);
    }
  };

  const handleAddProduct = (productId: number) => {
    const product = availableProducts.find(p => p.id === productId);
    if (!product) return;

    setOrder(prev => {
      const existingProduct = prev.products.find(p => p.productId === productId);
      if (existingProduct) {
        return {
          ...prev,
          products: prev.products.map(p => 
            p.productId === productId 
              ? { ...p, quantity: p.quantity + 1 } 
              : p
          )
        };
      }
      return {
        ...prev,
        products: [...prev.products, { productId, quantity: 1 }]
      };
    });
  };

  const handleRemoveProduct = (productId: number) => {
    setOrder(prev => {
      const existingProduct = prev.products.find(p => p.productId === productId);
      if (!existingProduct) return prev;

      if (existingProduct.quantity > 1) {
        return {
          ...prev,
          products: prev.products.map(p => 
            p.productId === productId 
              ? { ...p, quantity: p.quantity - 1 } 
              : p
          )
        };
      }
      return {
        ...prev,
        products: prev.products.filter(p => p.productId !== productId)
      };
    });
  };

  if (!isAuthenticated || isLoading || isLoadingProducts) {
    return <Loading />;
  }

  if (error) {
    return (
      <div className={styles.container}>
        <div className={styles.errorMessage}>{error}</div>
        <button 
          onClick={() => router.push('/orders')}
          className={styles.cancelButton}
        >
          Voltar para lista de pedidos
        </button>
      </div>
    );
  }

  return (
    <div className={styles.container}>
      <h1>Editar Pedido #{id}</h1>
      <p className={styles.userInfo}>Usuário: {user?.username} (ID: {user?.id})</p>
      
      {error && (
        <div className={styles.errorMessage}>
          {error}
        </div>
      )}

      <form onSubmit={handleSubmit} className={styles.form}>
        <div className={styles.formGroup}>
          <label>Produtos Disponíveis:</label>
          <select
            onChange={(e) => handleAddProduct(parseInt(e.target.value))}
            className={styles.select}
            defaultValue=""
          >
            <option value="" disabled>Selecione um produto</option>
            {availableProducts.map(product => (
              <option key={product.id} value={product.id}>
                {product.name} - {new Intl.NumberFormat('pt-BR', {
                  style: 'currency',
                  currency: 'BRL'
                }).format(product.value)}
              </option>
            ))}
          </select>
        </div>

        <div className={styles.productsList}>
          <h3>Produtos no Pedido:</h3>
          {order.products.length === 0 ? (
            <p className={styles.noProducts}>Nenhum produto adicionado</p>
          ) : (
            <>
              <ul>
                {order.products.map(item => {
                  const product = availableProducts.find(p => p.id === item.productId);
                  if (!product) return null;
                  
                  return (
                    <li key={item.productId} className={styles.productItem}>
                      <div className={styles.productInfo}>
                        {item.quantity}x {product.name}
                        <span className={styles.productValue}>
                          - {new Intl.NumberFormat('pt-BR', {
                            style: 'currency',
                            currency: 'BRL'
                          }).format(product.value * item.quantity)}
                        </span>
                      </div>
                      <div className={styles.productActions}>
                        <button 
                          type="button"
                          onClick={() => handleRemoveProduct(item.productId)}
                          className={styles.quantityButton}
                          aria-label="Remover uma unidade"
                        >
                          -
                        </button>
                        <span className={styles.quantityDisplay}>{item.quantity}</span>
                        <button 
                          type="button"
                          onClick={() => handleAddProduct(item.productId)}
                          className={styles.quantityButton}
                          aria-label="Adicionar uma unidade"
                        >
                          +
                        </button>
                      </div>
                    </li>
                  );
                })}
              </ul>
              <div className={styles.orderTotal}>
                <span>Total do Pedido:</span>
                <span>
                  {new Intl.NumberFormat('pt-BR', {
                    style: 'currency',
                    currency: 'BRL'
                  }).format(
                    order.products.reduce((total, item) => {
                      const product = availableProducts.find(p => p.id === item.productId);
                      return total + (product?.value || 0) * item.quantity;
                    }, 0)
                  )}
                </span>
              </div>
            </>
          )}
        </div>
        
        <div className={styles.buttonGroup}>
          <button 
            type="submit" 
            disabled={isLoading}
            className={styles.actionButton}
          >
            {isLoading ? 'Salvando...' : 'Salvar Alterações'}
          </button>
          <button 
            type="button" 
            onClick={() => router.push('/orders')}
            className={styles.cancelButton}
          >
            Cancelar
          </button>
        </div>
      </form>
    </div>
  );
}