import { useEffect, useState } from 'react';
import { useAuth } from '../contexts/AuthContext';

export interface Notification {
  id: string;
  message: string;
  timestamp: Date;
}

export const useNotificationStream = (url: string) => {
  const { token } = useAuth();
  const [notifications, setNotifications] = useState<Notification[]>([]);

  useEffect(() => {
    if (!token) return;

    const controller = new AbortController();
    const { signal } = controller;

    const fetchData = async () => {
      try {
        const response = await fetch(url, {
          headers: {
            Authorization: `Bearer ${token}`
          },
          signal
        });

        const reader = response.body?.getReader();
        const decoder = new TextDecoder();

        while (reader) {
          const { done, value } = await reader.read();
          if (done) break;
          
          const message = decoder.decode(value);
          console.log(message)
          setNotifications(prev => [...prev, {
            id: Date.now().toString(),
            message,
            timestamp: new Date()
          }]);
        }
      } catch (err) {
        if (!signal.aborted) {
          console.error('Fetch error:', err);
        }
      }
    };

    fetchData();

    return () => controller.abort();
  }, [url, token]);

  return { notifications };
};

export default useNotificationStream;