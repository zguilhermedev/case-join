import { ReactNode } from 'react';
import Navbar from './navbar';
import { useAuth } from '../contexts/AuthContext';
import { useRouter } from 'next/router';
import Loading from './loading';
import NotificationPopup from './NotificationPopup';

interface LayoutProps {
  children: ReactNode;
}

const Layout = ({ children }: LayoutProps) => {
  const { isAuthenticated, isLoading } = useAuth();
  const router = useRouter();

  if (isLoading) {
    return <Loading />;
  }

  // Não redireciona se estiver na página de login
  if (!isAuthenticated && router.pathname !== '/login' && router.pathname !== '/register') {
    return <Loading />; // Apenas mostra loading, o redirecionamento será feito pelo useEffect
  }

  return (
    <>
      {isAuthenticated && <Navbar />}
      <NotificationPopup />
      <main>{children}</main>
    </>
  );
};

export default Layout;