package com.example.projectdemo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView

interface OnUserActionListener {
    fun onUserClicked(user: User)
}



class ChatAdapter(private val userList: List<User>, private val listener: ChatActivity) :
    RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgAvatar: ShapeableImageView = itemView.findViewById(R.id.imgAvatar)
        val viewStatus: View = itemView.findViewById(R.id.viewStatus)
        val tvUsername: TextView = itemView.findViewById(R.id.tvUsername)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_chat, parent, false)
        return ChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val user = userList[position]

        // Load ảnh từ URL sử dụng Glide
        Glide.with(holder.itemView.context)
            .load(user.img)
            .placeholder(R.drawable.lion) // Ảnh mặc định nếu URL lỗi
            .into(holder.imgAvatar)

        if (user.avatar=="online") {
            holder.viewStatus.visibility = View.VISIBLE
            holder.viewStatus.isSelected = true
        } else {
            holder.viewStatus.visibility = View.VISIBLE
            holder.viewStatus.isSelected = false // Xám (offline)
        }
        holder.tvUsername.text = user.name
        holder.imgAvatar.setOnClickListener {
            listener.onUserClicked(user)
        }
//        if (user.isOnline) {
//            holder.viewStatus.visibility = View.VISIBLE
//        } else {
//            holder.viewStatus.visibility = View.GONE
//        }
    }

    override fun getItemCount(): Int = userList.size
}