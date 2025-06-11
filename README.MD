# 🔐 Encrypted Chat

The main goal of this project is to create a mobile chat application with end-to-end encryption. It supports sending voice messages, images, and file attachments. Group chats may be included as well.

# 📚 Table of Contents

- [📁 Project Structure](#-project-structure)
- [🛠️ Technologies Used](#-technologies-used)
- [⚙️ Prerequisites](#️-prerequisites)
- [🧪 Testing](#-testing)
- [📘 API Documentation](#-api-documentation)
- [🔐 Authentication](#-authentication)
- [🗃️ Database Schema](#️-database-schema)
- [🔧 Environment Configuration](#-environment-configuration)
- [👥 Authors](#-authors)
- [📈 Roadmap](#-roadmap)

# 📁 Project Structure

The application follows a three-layer architecture and is divided into three main components:
- **Frontend** – mobile client developed in Kotlin.
- **Backend** – REST API built with Java and Spring Boot.
- **Database** – managed using PostgreSQL.

# 🛠️ Technologies Used

- **Frontend:** Kotlin (Android), React (Web)
- **Backend:** Java, Spring Boot, Spring Web, Spring Security, Hibernate
- **Database:** PostgreSQL
- **Tools:** Docker (for containerization)

# ⚙️ Prerequisites

To run this project locally, it is recommended to use:

- **Android Studio** (for mobile frontend)
- **Visual Studio Code** (for web frontend)
- **IntelliJ IDEA** (for backend)
- **Docker** (for running services in containers)

Using these tools ensures compatibility with the development environment.

# 🧪 Testing

In progress...

# 📘 API Documentation

## 🔐 Authentication

> 💡 All endpoints requiring authentication expect a JWT token in the `Authorization` header:
> ```
> Authorization: Bearer <your_token>
> ```


### `POST /auth`
Authenticate user and receive a JWT token.  
**Request body:**
```json
{
  "username": "username",
  "password": "password"
}
```

**Response:**
```json
{
    "token": "JWT_token"
}
```

## 👤 Users

### `GET /users`
Returns a list of all users.

**Response:**
```json
[
    {
        "id": 1,
        "username": "admin",
        "role": "admin",
        "last_activity": null
    }
]
```

### `POST /users`
Create a new user.  
If called with an admin token, user role can be assigned, otherwise user role is assigned by default.

**Request body:**
```json
{
  "username": "username",
  "password": "password",
  "role": "user" // optional, settable by admin
}
```

**Response:**
```json
{
    "id": 1,
    "username": "admin",
    "role": "admin",
    "last_activity": null
}
```

### `DELETE /users/{username}`
Delete user by username.  
Requires proper authorization (admin or self-delete).

## 👥 Groups

### `GET /groups`
Get a list of all groups.

**Response:**
```json
[
    {
        "id": 1,
        "name": "Group name"
    }
]
```

### `POST /groups`
Create a new group.

**Request body:**
```json
{
  "name": "Group Name"
}
```

**Response:**
```json
{
    "id": 1,
    "name": "Group name"
}
```

### `DELETE /groups/{group_id}`
Delete a group by ID, deletes all messages bound to group.

### `POST /groups/{group_id}/add_user`
Add a user to a group.

**Request body:**
```json
{
  "username": "username",
  "role": "USER" // only USER and ADMIN are allowed
}
```

## 💬 Messages

### `GET /messages`
Get all messages supports filtering and pagination.

### `POST /messages`
Send a message via REST.  
Message will also be broadcast via WebSocket.

**Request body:**
```json
{
    "group": {
        "id": 1
    },
    "content": "Some message content",
    "attachment": "Link to attachment" // Not yet implemented (leave null)
}
```

### `GET /messages/{group_id}`
Get messages for a specific group.  
Supports pagination with optional query params:
```
?page=0&size=100&sort=sentAt,asc
```

### `DELETE /messages/{message_id}`
Delete a message by ID, requires admin role or ownership.

## 🔌 WebSocket

### `ws://<host>/ws`
WebSocket endpoint for real-time communication.   
Requires JWT token via headers or query parameter (e.g., `?token=...`).

**Subscribe to:**  
```
/topic/group.{group_id}
```

# 🗃️ Database Schema

# 🔧 Enviornment Configuration

# 👥 Authors

# 📈 Roadmap