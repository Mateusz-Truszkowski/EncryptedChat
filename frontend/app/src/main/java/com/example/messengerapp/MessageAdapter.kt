package com.example.messengerapp

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.messengerapp.data.model.Message

class MessageAdapter(
    private var messages: List<Message>,
    private val currentUserUsername: String?
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val VIEW_TYPE_SENT = 1
    private val VIEW_TYPE_RECEIVED = 2

    override fun getItemViewType(position: Int): Int {
        val message = messages[position]
        val senderUsername = message.sender?.username
        return if (senderUsername == currentUserUsername) VIEW_TYPE_SENT else VIEW_TYPE_RECEIVED
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutId = if (viewType == VIEW_TYPE_SENT)
            R.layout.message_item_sent
        else
            R.layout.message_item_received

        val view = LayoutInflater.from(parent.context).inflate(layoutId, parent, false)

        return if (viewType == VIEW_TYPE_SENT) {
            SentMessageViewHolder(view)
        } else {
            ReceivedMessageViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messages[position]
        if (holder is SentMessageViewHolder) {
            holder.bind(message)
        } else if (holder is ReceivedMessageViewHolder) {
            holder.bind(message)
        }
    }

    override fun getItemCount() = messages.size

    fun updateMessages(newMessages: List<Message>) {
        (messages as? MutableList)?.let {
            Log.d("ADAPTER", "Aktualizacja wiadomości, nowe: ${newMessages.size}")
            it.clear()
            it.addAll(newMessages)
            notifyDataSetChanged()
        }
    }

    class SentMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val messageTextView: TextView = itemView.findViewById(R.id.message_text_sent)

        fun bind(message: Message) {
            messageTextView.text = message.content
        }
    }

    class ReceivedMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val messageTextView: TextView = itemView.findViewById(R.id.message_text_received)

        fun bind(message: Message) {
            messageTextView.text = message.content
        }
    }
}

