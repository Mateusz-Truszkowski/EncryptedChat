import SockJS from 'sockjs-client/dist/sockjs';
import { Client } from '@stomp/stompjs';

let stompClient = null;

export function connectToChat(groupId, refreshMessages) {
    const token = localStorage.getItem('token');
    const socket = new SockJS(`http://localhost:8080/ws?token=${token}`);
    const client = new Client({
    webSocketFactory: () => socket,
    reconnectDelay: 5000,
    onConnect: () => {
      console.log('Połączono z WebSocket');
      client.subscribe(`/topic/group.${groupId}`, () => refreshMessages(groupId));
    }
  });

  client.activate();
  stompClient = client;
}

export function disconnectFromChat() {
  if (stompClient) {
    stompClient.deactivate();
    stompClient = null;
  }
}