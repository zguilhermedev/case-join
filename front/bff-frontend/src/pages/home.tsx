import styles from '../styles/Home.module.css';
import { useAuth } from '../contexts/AuthContext';
import { useRouter } from 'next/router';
import { useEffect } from 'react';
import Loading from '@/components/loading';

export default function Home() {
    const { isAuthenticated, isLoading } = useAuth();
    const router = useRouter();
  
    useEffect(() => {
      if (!isLoading && !isAuthenticated) {
        router.push('/login');
      }
    }, [isAuthenticated, isLoading, router]);
  
    if (isLoading || !isAuthenticated) {
      return <Loading />;
    }

  return (
    <div className={styles.homeContainer}>
      <div className={styles.homeContent}>
        <h1 className={styles.homeTitle}>Painel Administrativo</h1>
        <div className={styles.dashboardGrid}>
          <div className={styles.dashboardCard}>
            <h2 className={styles.cardTitle}>Categorias</h2>
            <p className={styles.cardDescription}>Gerencie suas categorias de produtos</p>
            <button 
              onClick={() => router.push('/categories')}
              className={styles.cardButton}
              aria-label="Acessar categorias"
            >
              Acessar
            </button>
          </div>
          
          <div className={styles.dashboardCard}>
            <h2 className={styles.cardTitle}>Produtos</h2>
            <p className={styles.cardDescription}>Gerencie seu catálogo de produtos</p>
            <button 
              onClick={() => router.push('/products')}
              className={styles.cardButton}
              aria-label="Acessar produtos"
            >
              Acessar
            </button>
          </div>
          
          <div className={styles.dashboardCard}>
            <h2 className={styles.cardTitle}>Pedidos</h2>
            <p className={styles.cardDescription}>Visualize e gerencie pedidos</p>
            <button 
              onClick={() => router.push('/orders')}
              className={styles.cardButton}
              aria-label="Acessar pedidos"
            >
              Acessar
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}