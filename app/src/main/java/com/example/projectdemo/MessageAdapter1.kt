package com.example.projectdemo

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
class MessageAdapter1(private val messages: List<Message>, private val currentUserId: String) : RecyclerView.Adapter<MessageAdapter1.MessageViewHolder>() {

    // Tạo ViewHolder cho mỗi mục trong RecyclerView
    class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val senderTextView: TextView = itemView.findViewById(R.id.tv_sender)
        val messageTextView: TextView = itemView.findViewById(R.id.tv_message)
        val timestampTextView: TextView = itemView.findViewById(R.id.tv_timestamp)
        val messageContainer: LinearLayout = itemView.findViewById(R.id.message_container)
    }

    // Tạo ViewHolder mới cho mỗi mục
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_message1, parent, false)
        return MessageViewHolder(itemView)
    }

    // Liên kết dữ liệu với ViewHolder
    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = messages[position]

        // Đặt dữ liệu vào các TextView
        holder.senderTextView.text = message.senderID
        holder.messageTextView.text = message.text
        holder.timestampTextView.text = formatTimestamp(message.timestamp)

        // Kiểm tra xem tin nhắn là của người dùng hiện tại hay bot
        if (message.senderID == currentUserId) {
            // Nếu là tin nhắn của người dùng hiện tại, căn bên phải và đổi màu văn bản thành xanh
            holder.messageTextView.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.ChuDao)) // Màu xanh
            holder.messageContainer.gravity = Gravity.END
        } else {
            // Nếu là tin nhắn của bot hoặc người khác, căn bên trái và đổi màu văn bản thành đen
            holder.messageTextView.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.colorStart)) // Màu đen
            holder.messageContainer.gravity = Gravity.START
        }
    }


    // Trả về số lượng tin nhắn trong danh sách
    override fun getItemCount(): Int {
        return messages.size
    }

    // Hàm chuyển đổi timestamp thành dạng dễ đọc
    private fun formatTimestamp(timestamp: String): String {
        val date = Date(timestamp.toLong())
        val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return format.format(date)
    }
}
