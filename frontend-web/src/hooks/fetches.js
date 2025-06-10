export async function login(username, password) {
  try {
    const response = await fetch('http://localhost:8080/auth', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({ username, password }),
    });

    if (!response.ok) {
      throw new Error('Błędne dane logowania');
    }

    // Tu jest różnica: odbieramy token jako tekst
    const token = await response.text();

    if (token) {
      localStorage.setItem('token', token);
      return { success: true };
    } else {
      throw new Error('Brak tokenu w odpowiedzi');
    }
  } catch (error) {
    console.error('Błąd logowania:', error.message);
    return { success: false, message: error.message };
  }
}

export async function getGroups() {
    try {
        const response = await fetch('http://localhost:8080/groups', {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${localStorage.getItem('token')}`
            }
        });

        if (!response.ok) {
            throw new Error('Nie udało się pobrać grup');
        }

        return response.json();
    } catch (error) {
        console.error('Błąd pobierania grup:', error.message);
        return { success: false, message: error.message };
    }
}

export async function getGroupMessages(groupId) {
    try {
        const response = await fetch(`http://localhost:8080/messages/${groupId}`, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${localStorage.getItem('token')}`
            }
        });

        if (!response.ok) {
            throw new Error('Nie udało się pobrać wiadomości');
        }

        return response.json();
    } catch (error) {
        console.error('Błąd pobierania wiadomości:', error.message);
        return { success: false, message: error.message };
    }
}

export async function sendGroupMessage(groupId, messageContent) {
    try {
        const response = await fetch('http://localhost:8080/messages', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${localStorage.getItem('token')}`
        },
        body: JSON.stringify({ group: { id: groupId }, content: messageContent, status: "Sent" }),
    });

    if (!response.ok) {
            throw new Error('Nie udało się pobrać wiadomości');
    }
    
    return response.json();
    } catch {
        console.error('Błąd wysyłania wiadomości:', error.message);
        return { success: false, message: error.message };
    }
}