from fastapi import APIRouter, WebSocket, WebSocketDisconnect
from database.database import Database

router = APIRouter()
database = Database("database.db")


class ConnectionManager:
    def __init__(self):
        self.connections: dict[int, WebSocket] = {}

    async def connect(self, user_id: int, websocket: WebSocket):
        user = database.get_user(user_id)
        if not user:
            await websocket.close(code=1003)
            return
        await websocket.accept()
        self.connections[user_id] = websocket

    def disconnect(self, user_id: int):
        self.connections.pop(user_id)

    async def send_to_user(self, user_id: int, message):
        websocket = self.connections[user_id]
        if websocket:
            await websocket.send_json(message)
            print(f'[CHAT] Sent {message}')
            message['status'] = 'delivered'
        else:
            print('[CHAT] Receiver unavailable')


manager = ConnectionManager()


@router.websocket('/chat/{user_id}')
async def chat(websocket: WebSocket, user_id: int):
    await manager.connect(user_id, websocket)
    try:
        while True:
            data = await websocket.receive_json()
            data['sender_id'] = user_id
            data['key'] = ""
            data['status'] = "sent"

            await manager.send_to_user(int(data['receiver_id']), data)

            database.add_message(None, data['sender_id'], data['receiver_id'], data['message'], data['key'],
                                 data['status'])
    except WebSocketDisconnect:
        manager.disconnect(user_id)
