from typing import List

from fastapi import APIRouter, HTTPException, status, Form
from pydantic import BaseModel

import database.database as db
from security.JwtUtil import create_token, verify_token
router = APIRouter()
database = db.Database("database.db")


class User(BaseModel):
    id: int
    username: str
    password: str
    first_name: str
    last_name: str
    email: str


class UserDTO(BaseModel):
    id: int
    username: str
    first_name: str
    last_name: str
    email: str


class LoginDto(BaseModel):
    username: str
    password: str


@router.post('/users', status_code=status.HTTP_201_CREATED)
async def create_user(user: User):
    try:
        database.add_user(None, user.username, user.password, user.first_name, user.last_name, user.email)
        return user
    except Exception as e:
        raise HTTPException(status_code=status.HTTP_400_BAD_REQUEST, detail=str(e))


@router.get('/users/{user_id}', response_model=UserDTO)
async def get_users(user_id: int):
    user = database.get_user(user_id)
    if not user:
        raise HTTPException(status_code=404, detail="User not found")
    user_dto = UserDTO(id=user[0], username=user[1], first_name=user[3], last_name=user[4], email=user[5])
    return user_dto


@router.get('/users', response_model=List[UserDTO])
async def get_users():
    users = database.get_all_users()
    user_dtos = [UserDTO(id=user[0], username=user[1], first_name=user[3], last_name=user[4], email=user[5]) for user in users]
    return user_dtos


@router.post('/login')
def login(dto: LoginDto):
    print(dto.username)
    user = database.get_user_by_username(dto.username)
    if not user:
        raise HTTPException(status_code=404, detail="User not found")
    if dto.password != user[2]:
        raise HTTPException(status_code=401, detail="Unauthorized")
    data = {"username": dto.username, "role": "user"}
    return create_token(data)
