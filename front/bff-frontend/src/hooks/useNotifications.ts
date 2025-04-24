// hooks/useNotifications.ts
import { useEffect, useState } from 'react';
import { io, Socket } from 'socket.io-client';

interface Notification {
  id: string;
  title: string;
  message: string;
  timestamp: Date;
  read: boolean;
}

const useNotifications = (bffUrl: string) => {
  const [notifications, setNotifications] = useState<Notification[]>([]);
  const [socket, setSocket] = useState<Socket | null>(null);

  useEffect(() => {
    // Conexão WebSocket
    const newSocket = io(bffUrl, {
      path: '/ws-notifications',
      transports: ['websocket'],
    });

    newSocket.on('connect', () => {
      console.log('Conectado ao serviço de notificações');
    });

    newSocket.on('notification', (newNotification: Notification) => {
      setNotifications(prev => [newNotification, ...prev]);
    });

    newSocket.on('disconnect', () => {
      console.log('Desconectado do serviço de notificações');
    });

    setSocket(newSocket);

    return () => {
      newSocket.disconnect();
    };
  }, [bffUrl]);

  const markAsRead = (id: string) => {
    setNotifications(prev =>
      prev.map(n => (n.id === id ? { ...n, read: true } : n))
    );
  };

  return { notifications, markAsRead };
};

export default useNotifications;