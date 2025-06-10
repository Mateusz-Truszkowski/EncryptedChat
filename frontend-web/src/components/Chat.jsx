import { useState, useEffect } from 'react';
import { login, getGroups, getGroupMessages, sendGroupMessage } from "../hooks/fetches"
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

    const handleSendMessage = async (e) => {
        const content = document.getElementById('message_input').value;
        const currentGroupId = activeUsers.findIndex(contact => selectedContact == contact) + 1;
        const response = await sendGroupMessage(currentGroupId, content);
        updateMessages(selectedContact);
    }

    useEffect(() => {
        updateGroups();
    }, []);

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
                  <h3>Czat z {selectedContact}</h3>
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
                <button onClick={handleSendMessage}>Wyślij</button>
              </div>
            </div>
          </div>
          <button className="logout-button" onClick={logout}>Wyloguj się</button>
            </>}
        </>
    )
}

export default Chat