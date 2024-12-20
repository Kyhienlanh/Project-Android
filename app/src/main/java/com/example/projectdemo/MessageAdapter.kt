package com.example.projectdemo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth

class MessageAdapter(private val messages: MutableList<Message>) : RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {

    // Đảm bảo đã khai báo ViewHolder
    inner class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val left: TextView = itemView.findViewById(R.id.left)
        val right: TextView = itemView.findViewById(R.id.right)
        val leftParams = left.layoutParams as LinearLayout.LayoutParams
        val rightParams = right.layoutParams as LinearLayout.LayoutParams
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_message, parent, false)
        return MessageViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = messages[position]
        val currentUserID = FirebaseAuth.getInstance().currentUser?.uid.toString()


        if (message.senderID == currentUserID) {

            holder.left.text=message.text
//
            holder.right.visibility=View.INVISIBLE

        } else {

            holder.right.text=message.text
//            holder.rightParams.weight = 5f
            holder.left.visibility=View.INVISIBLE
        }
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    // Thêm tin nhắn vào adapter
    fun addMessage(message: Message) {
        messages.add(message)
        notifyItemInserted(messages.size - 1)
    }
}
