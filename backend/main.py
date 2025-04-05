from fastapi import FastAPI
import database.database as db
from routers.users import router as users_router
from routers.messages import router as messages_router

database = db.Database("database.db")
database.create_tables()

app = FastAPI()

app.include_router(users_router, tags=["users"])
app.include_router(messages_router, tags=["messages"])