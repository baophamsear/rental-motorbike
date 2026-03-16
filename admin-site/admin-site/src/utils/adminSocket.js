import SockJS from 'sockjs-client';
import { Client } from '@stomp/stompjs';

export const connectAdminSocket = (onReceiveMessage) => {
  

  console.log("in ra onreicemessage", `${onReceiveMessage}`);

  const client = new Client({
    webSocketFactory: () => new WebSocket("ws://localhost:8080/ws"),
    reconnectDelay: 5000,
    debug: (str) => console.log('[STOMP DEBUG]', str),

    onConnect: () => {
      console.log('✅ WebSocket connected!');
      client.subscribe('/topic/admin-bike-notifications', (message) => {
        console.log('📥 Received WebSocket message:', message.body);
        onReceiveMessage(JSON.parse(message.body));
      });
    },

    onStompError: (frame) => console.error('❌ STOMP Error', frame),
    onWebSocketError: (error) => console.error('❌ WebSocket Error', error),
    onWebSocketClose: (evt) => {
      console.error('❌ WebSocket closed:', evt.code, evt.reason);
    },
  });

  client.activate();
  return client;
};
