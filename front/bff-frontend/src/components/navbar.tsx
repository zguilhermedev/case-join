import Link from 'next/link';
import { useRouter } from 'next/router';
import styles from '../styles/Navbar.module.css';
import { useAuth } from '../contexts/AuthContext';

const Navbar = () => {
  const router = useRouter();
  const { logout } = useAuth();

  const isActive = (path: string) => {
    return router.pathname === path ? styles.active : '';
  };

  return (
    <nav className={styles.navbar}>
      <div className={styles.navItems}>
        <Link href="/" className={`${styles.navItem} ${isActive('/')}`}>
          Home
        </Link>
        <Link href="/categories" className={`${styles.navItem} ${isActive('/categories')}`}>
          Categorias
        </Link>
        <Link href="/products" className={`${styles.navItem} ${isActive('/products')}`}>
          Produtos
        </Link>
        <Link href="/orders" className={`${styles.navItem} ${isActive('/orders')}`}>
          Pedidos
        </Link>
      </div>

      <div className={styles.navRight}>
        <button onClick={logout} className={styles.logoutButton}>
          Logout
        </button>
      </div>
    </nav>
  );
};

export default Navbar;