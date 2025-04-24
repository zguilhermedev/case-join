import styles from '../styles/Loading.module.css';

const Loading = () => {
  return (
    <div className={styles.loadingContainer}>
      <div className={styles.loadingSpinner}></div>
      <p className={styles.loadingText}>Carregando...</p>
    </div>
  );
};

export default Loading;