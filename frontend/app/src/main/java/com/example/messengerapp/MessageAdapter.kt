package com.example.messengerapp

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.messengerapp.data.model.Message

class MessageAdapter(private val messages: List<Message>, private val currentUserUsername: String?) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val VIEW_TYPE_SENT = 1
    private val VIEW_TYPE_RECEIVED = 2

    override fun getItemViewType(position: Int): Int {
        if (messages[position].sender == null)
            return VIEW_TYPE_RECEIVED
        return if (messages[position].sender?.username == currentUserUsername) VIEW_TYPE_SENT else VIEW_TYPE_RECEIVED
    }

    // Zmieniamy typ ViewHoldera, by przechowywał również odwołanie do TextView
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutId = if (viewType == VIEW_TYPE_SENT) R.layout.message_item_sent else R.layout.message_item_received
        val view = LayoutInflater.from(parent.context).inflate(layoutId, parent, false)

        // Zwracamy odpowiedni ViewHolder
        return if (viewType == VIEW_TYPE_SENT) {
            SentMessageViewHolder(view)
        } else {
            ReceivedMessageViewHolder(view)
        }
    }

    // Metoda do bindowania danych w odpowiednich widokach
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messages[position]

        // Zastosowanie odpowiedniego ViewHoldera w zależności od typu wiadomości
        if (holder is SentMessageViewHolder) {
            holder.bind(message)
        } else if (holder is ReceivedMessageViewHolder) {
            holder.bind(message)
        }
    }

    override fun getItemCount() = messages.size

    // ViewHolder dla wiadomości wysłanych
    class SentMessageViewHolder(itemView: android.view.View) : RecyclerView.ViewHolder(itemView) {
        private val messageTextView: TextView = itemView.findViewById(R.id.message_text_sent)

        fun bind(message: Message) {
            messageTextView.text = message.content
        }
    }

    // ViewHolder dla wiadomości odebranych
    class ReceivedMessageViewHolder(itemView: android.view.View) : RecyclerView.ViewHolder(itemView) {
        private val messageTextView: TextView = itemView.findViewById(R.id.message_text_received)

        fun bind(message: Message) {
            messageTextView.text = message.content
        }
    }
}

