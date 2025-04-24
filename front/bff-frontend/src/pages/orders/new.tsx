import { useState, useEffect } from 'react';
import { useRouter } from 'next/router';
import { useAuth } from '@/contexts/AuthContext';
import { orderService } from '@/services/order.service';
import { productService } from '@/services/product.service';
import styles from '../../styles/orders/New-Order.module.css';
import { OrderCreationDTO, ProductResponse } from '@/types/order';
import Loading from '@/components/loading';

export default function NewOrderPage() {
  const router = useRouter();
  const { isAuthenticated, user } = useAuth();
  const [order, setOrder] = useState<OrderCreationDTO>({
    userId: user?.id || 0,
    products: [],
    amount: 0, 
  });
  const [availableProducts, setAvailableProducts] = useState<ProductResponse[]>([]);
  const [isLoading, setIsLoading] = useState(false);
  const [isLoadingProducts, setIsLoadingProducts] = useState(true);
  const [error, setError] = useState<string | null>(null);

  
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

  useEffect(() => {
    if (!isAuthenticated) {
      router.push('/login');
      return;
    }
  
    if (user?.id) {
      setOrder(prev => ({ ...prev, userId: user.id }));
    }
    
    fetchProducts();
  }, [isAuthenticated, router, user, isLoading]);

  if (isLoading || isLoadingProducts) {
    return <Loading />;
  }


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
      await orderService.create({
        userId: user.id,
        amount,
        products: order.products
      });
      router.push('/orders');
    } catch (err) {
      console.error('Erro ao criar pedido:', err);
      setError(err instanceof Error ? err.message : 'Erro ao criar pedido');
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

  if (!isAuthenticated || isLoadingProducts) {
    return <Loading />; // Use seu componente Loading
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
                        −
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
          )}
        </div>

        <div className={styles.orderSummary}>
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
        </div>
        
        <div className={styles.buttonGroup}>
          <button 
            type="submit" 
            disabled={isLoading}
            className={styles.actionButton}
          >
            {isLoading ? 'Salvando...' : 'Salvar Pedido'}
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