import sqlite3
from datetime import datetime


class Database:
    def __init__(self, database_path):
        self.connection = sqlite3.connect(database_path, check_same_thread=False)
        self.cursor = self.connection.cursor()

    def create_tables(self):
        res = self.cursor.execute("SELECT * FROM sqlite_master WHERE name='users'")
        if res.fetchone() is None:
            self.cursor.execute("CREATE TABLE users("
                                "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                                "username TEXT NOT NULL, "
                                "password TEXT NOT NULL, "
                                "first_name TEXT NOT NULL, "
                                "last_name TEXT NOT NULL, "
                                "email TEXT NOT NULL )")

        res = self.cursor.execute("SELECT * FROM sqlite_master WHERE name='messages'")
        if res.fetchone() is None:
            self.cursor.execute("CREATE TABLE messages("
                                "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                                "sender_id INTEGER REFERENCES users(id), "
                                "receiver_id INTEGER REFERENCES users(id), "
                                "message TEXT NOT NULL, "
                                "key TEXT NOT NULL, "
                                "timestamp DATETIME NOT NULL, "
                                "status TEXT NOT NULL )")

    def add_user(self, id, username, password, first_name, last_name, email):
        self.cursor.execute("INSERT INTO users VALUES (?, ?, ?, ?, ?, ?)",
                            (id, username, password, first_name, last_name, email))
        self.connection.commit()

    def get_user(self, id):
        res = self.cursor.execute("SELECT * FROM users WHERE id=?", (id,))
        return res.fetchone()

    def get_user_by_username(self, username):
        res = self.cursor.execute("SELECT * FROM users WHERE username=?", (username,))
        return res.fetchone()

    def get_all_users(self):
        res = self.cursor.execute("SELECT * FROM users")
        return res.fetchall()

    def add_message(self, id, sender_id, receiver_id, message, key, status):
        timestamp = datetime.now()
        self.cursor.execute("INSERT INTO messages VALUES (?, ?, ?, ?, ?, ?, ?)",
                            (id, sender_id, receiver_id, message, key, timestamp, status))
        self.connection.commit()
        return timestamp

    def get_message(self, id):
        res = (self.cursor.execute("SELECT * FROM messages WHERE id=?", (id,)))
        return res.fetchone()

    def get_all_messages(self):
        res = self.cursor.execute("SELECT * FROM messages")
        return res.fetchall()