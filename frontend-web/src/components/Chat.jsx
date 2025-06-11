import { useState, useEffect } from 'react';
import { login, getGroups, getGroupMessages, addUserToGroup, addNewGroup } from "../hooks/fetches"
import { connectToChat, sendMessageToGroup, disconnectFromChat } from '../hooks/chat';
import "../assets/Chat.css"

function Chat() {
    const [error, setError] = useState('');
    const [selectedContact, setSelectedContact] = useState('');
    const [message, setMessage] = useState('');
    const [activeUsers, setActiveUsers] = useState([]);
    const [messages, setMessages] = useState([]);

    const handleSubmit = async (e) => {
        const username = document.getElementById("username_input");
        const password = document.getElementById("password_input");
        const result = await login(username.value, password.value);
        if (result.success) {
            window.location.reload();
        }
        else {
            setError(result.message || "Logowanie nieudane")
        }
    }

    const logout = () => {
        localStorage.setItem('token', '');
        disconnectFromChat();
        window.location.reload();
    }

    const updateGroups = async (e) => {
        const response = await getGroups();
        const groups = response.map(group => group.name);
        setActiveUsers(groups);
    }

    const updateMessages = async (groupName) => {
        const currentGroupId = activeUsers.findIndex(contact => groupName == contact) + 1;
        const response = await getGroupMessages(currentGroupId);
        const msgs = response.content;
        setMessages(msgs);
    }

    const handleAddUserToGroup = async () => {
        const currentGroupId = activeUsers.findIndex(contact => selectedContact == contact) + 1;
        const userToAdd = document.getElementById("search_user_input").value;
        await addUserToGroup(currentGroupId, userToAdd);
    }

    const handleAddNewGroup = async () => {
        const userToAdd = document.getElementById("search_user_input").value;
        await addNewGroup("Czat z: " + userToAdd, userToAdd);
        window.location.reload();
    }

    useEffect(() => {
        updateGroups();
    }, []);

    useEffect(() => {
        if (!selectedContact) return;

        const currentGroupId = activeUsers.findIndex(contact => selectedContact === contact) + 1;
        disconnectFromChat();

        connectToChat(currentGroupId, (msg) => {
            setMessages((prev) => [...prev, msg]);
        });

        return () => {
            disconnectFromChat();
        };
    }, [selectedContact]);

    const handleSend = () => {
        const currentGroupId = activeUsers.findIndex(contact => selectedContact === contact) + 1;
        const content = document.getElementById('message_input').value;
        if (content.trim() !== '') {
            sendMessageToGroup(content, currentGroupId);
        }
    };

    return (
        <>
            {localStorage.getItem('token') == "" ? 
            <>
              <div className="login-box">
                <h2>Zaloguj się lub zarejestruj:</h2>
                <label>Nazwa użytkownika:</label>
                <input id="username_input" type="text" /><br />
                <label>Hasło:</label>
                <input id="password_input" type="password" /><br />
                <button onClick={handleSubmit}>Zaloguj się</button>
              </div>
            </> : 
            <>
              <div id="chat_wrapper">
                <div id="chat_topbar">
                  <input type="text" placeholder="Wpisz nazwę użytkownika..." id="search_user_input"/>
                  <button onClick={() => handleAddUserToGroup()}>Dodaj do czatu</button>
                  <button onClick={() => handleAddNewGroup()}>Utwórz nowy czat</button>
                </div>
              <div id="chat_app">
                <div id="contacts">
                  <h3>Kontakty</h3>
                  <ul>
                    {activeUsers.map((name) => (
                    <li
                      key={name}
                      className={selectedContact === name ? 'selected' : ''}
                      onClick={() => { 
                        setSelectedContact(name); 
                        updateMessages(name);
                      }}
                    >
                      {name}
                    </li>
                    ))}
                  </ul>
                </div>

                <div id="current_chat">
                  <h3>{selectedContact}</h3>
                  <div className="messages">
                    {
                        messages.map(msg => 
                        <div key={msg.id}>
                            { msg.sender.username + ": " + msg.content }
                        </div>
                        )
                    }
                </div>

              <div className="message-input">
                <input
                  type="text"
                  placeholder="Napisz wiadomość..."
                  value={message}
                  id='message_input'
                  onChange={(e) => setMessage(e.target.value)}
                />
                <button onClick={handleSend}>Wyślij</button>
              </div>
            </div>
          </div>
          </div>
          <button className="logout-button" onClick={logout}>Wyloguj się</button>
            </>}
        </>
    )
}

export default Chat