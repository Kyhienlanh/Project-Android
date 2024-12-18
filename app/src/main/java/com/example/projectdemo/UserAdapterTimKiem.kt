package com.example.projectdemo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView
interface OnClickActionListener {
    fun onClicked(user: User)
}

class UserAdapter(
    private var userList: List<User>,
    private val listener: OnClickActionListener
) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tvUserName)
        val tvEmail: TextView = itemView.findViewById(R.id.tvUserEmail)
        val ivUserAvatar: ShapeableImageView = itemView.findViewById(R.id.ivUserAvatar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_usertimkiem, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = userList[position]
        holder.tvName.text = user.name
        holder.tvEmail.text = user.email

        // Load avatar image
        Glide.with(holder.itemView.context)
            .load(user.img) // Đường dẫn URL ảnh từ đối tượng User
            .placeholder(R.drawable.baseline_account_circle_24) // Ảnh mặc định nếu không có
            .error(R.drawable.ic_launcher_background) // Ảnh lỗi nếu load thất bại
            .into(holder.ivUserAvatar)

        // Xử lý click item
        holder.itemView.setOnClickListener {
            listener.onClicked(user)
        }
    }

    override fun getItemCount() = userList.size

    fun updateList(newList: List<User>) {
        userList = newList
        notifyDataSetChanged()
    }
}
