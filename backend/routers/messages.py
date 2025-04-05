from datetime import datetime
from typing import List

from fastapi import APIRouter, HTTPException, status
from pydantic import BaseModel
from database.database import Database

router = APIRouter()
database = Database("database.db")


class Message(BaseModel):
    id: int
    sender_id: int
    receiver_id: int
    message: str
    key: str
    status: str


class MessageDTO(BaseModel):
    id: int
    sender_id: int
    receiver_id: int
    message: str
    key: str
    timestamp: datetime
    status: str


@router.post('/messages', response_model=MessageDTO, status_code=status.HTTP_201_CREATED)
async def create_message(message: Message):
    try:
        ts = database.add_message(message.id, message.sender_id, message.receiver_id, message.message, message.key, message.status)
        return MessageDTO(id=message.id, sender_id=message.sender_id, receiver_id=message.receiver_id, message=message.message, key=message.key, timestamp=ts, status=message.status)
    except Exception as e:
        raise HTTPException(status_code=400, detail=str(e))


@router.get('/messages/{msg_id}', response_model=MessageDTO)
async def get_message(msg_id: int):
    message = database.get_message(msg_id)
    if not message:
        raise HTTPException(status_code=404, detail="Message not found")
    message_dto = MessageDTO(id=message[0], sender_id=message[1], receiver_id=message[2], message=message[3], key=message[4], timestamp=message[5], status=message[6])
    return message_dto


@router.get('/messages', response_model=List[MessageDTO])
async def get_messages():
    messages = database.get_all_messages()
    message_dtos = [MessageDTO(id=message[0], sender_id=message[1], receiver_id=message[2], message=message[3], key=message[4], timestamp=message[5], status=message[6]) for message in messages]
    return message_dtos
