import { useEffect, useState } from 'react';
import useNotificationStream, { Notification } from '../hooks/useNotificationStream';
import styles from '../styles/NotificationPopup.module.css';

const NotificationPopup = () => {
  const { notifications } = useNotificationStream('http://localhost:9090/bff-service/api/notifications');
  const [currentNotification, setCurrentNotification] = useState<Notification | null>(null);
  const [isVisible, setIsVisible] = useState(false);

  useEffect(() => {
    if (notifications.length > 0) {
      const lastNotification = notifications[notifications.length - 1];
      setCurrentNotification(lastNotification);
      setIsVisible(true);

      const timer = setTimeout(() => {
        setIsVisible(false);
      }, 5000);

      return () => clearTimeout(timer);
    }
  }, [notifications]);

  if (!isVisible || !currentNotification) return null;

  return (
    <div className={styles.notificationContainer}>
      <div className={styles.notification}>
        <div className={styles.notificationContent}>
          <div className={styles.notificationMessage}>
            {currentNotification.message}
          </div>
        </div>
        <div className={styles.notificationProgress} />
      </div>
    </div>
  );
};

export default NotificationPopup;