import SockJS from 'sockjs-client/dist/sockjs';
import { Client } from '@stomp/stompjs';

let stompClient = null;

export function connectToChat(groupId, onMessageReceived) {
    const token = localStorage.getItem('token');
    const socket = new SockJS(`http://localhost:8080/ws?token=${token}`);
    const client = new Client({
    webSocketFactory: () => socket,
    reconnectDelay: 5000,
    onConnect: () => {
      console.log('Połączono z WebSocket');
      client.subscribe(`/topic/group.${groupId}`, (message) => {
        const body = JSON.parse(message.body);
        onMessageReceived(body);
      });
    }
  });

  client.activate();
  stompClient = client;
}

export function sendMessageToGroup(content, groupId) {
  if (stompClient && stompClient.connected) {
    stompClient.publish({
      destination: '/app/chat.send',
      body: JSON.stringify({
        content: content,
        groupId: groupId
      })
    });
  } else {
    console.warn("Brak połączenia WebSocket");
  }
}

export function disconnectFromChat() {
  if (stompClient) {
    stompClient.deactivate();
    stompClient = null;
  }
}