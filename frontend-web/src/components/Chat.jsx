import { useState, useEffect } from 'react';
import { login, getGroups, getGroupMessages, addUserToGroup, addNewGroup, sendGroupMessage } from "../hooks/fetches"
import { connectToChat, disconnectFromChat } from '../hooks/chat';
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
        const groups = response.map(group => ({ id: group.id, name: group.name }));
        setActiveUsers(groups);
    }

    const updateMessages = async (groupId) => {
        const response = await getGroupMessages(groupId);
        const msgs = response.content;
        setMessages(msgs);
    }

    const handleAddUserToGroup = async () => {
        const userToAdd = document.getElementById("search_user_input").value;
        await addUserToGroup(selectedContact.id, userToAdd);
        document.getElementById("search_user_input").value = "";
    }

    const handleAddNewGroup = async () => {
        const userToAdd = document.getElementById("search_user_input").value;
        await addNewGroup("Czat z: " + userToAdd, userToAdd);
        document.getElementById("search_user_input").value = "";
        window.location.reload();
    }

    useEffect(() => {
        updateGroups();
    }, []);

    useEffect(() => {
        if (!selectedContact) return;

        disconnectFromChat();

        connectToChat(selectedContact.id, updateMessages);

        return () => {
            disconnectFromChat();
        };
    }, [selectedContact]);

    const handleSend = () => {
        if (message.trim() !== '') {
            sendGroupMessage(selectedContact.id, message);
        }
        setMessage("");
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
                    {activeUsers.map((group) => (
                    <li
                      key={group.id}
                      className={selectedContact === group.name ? 'selected' : ''}
                      onClick={() => { 
                        setSelectedContact(group); 
                        updateMessages(group.id);
                      }}
                    >
                      {group.name}
                    </li>
                    ))}
                  </ul>
                </div>

                <div id="current_chat">
                  <h3>{selectedContact.name}</h3>
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