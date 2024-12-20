package com.example.projectdemo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Date


class CommentAdapter(private val commentList: List<Comment>, var userID: String) : RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.itemcomment, parent, false)
        return CommentViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val comment = commentList[position]
        holder.bind(comment, this) // Truyền adapter vào ViewHolder
    }

    override fun getItemCount(): Int = commentList.size

    // ViewHolder để ánh xạ các view trong item
    class CommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivUserAvatar: ImageView = itemView.findViewById(R.id.ivUserAvatar)
        private val tvUserName: TextView = itemView.findViewById(R.id.tvUserName)
        private val tvCommentContent: TextView = itemView.findViewById(R.id.tvCommentContent)
        private val tvTimestamp: TextView = itemView.findViewById(R.id.tvTimestamp)

        fun bind(comment: Comment, adapter: CommentAdapter) {
            tvCommentContent.text = comment.content

            fun convertTimestampToDate(timestamp: Long): String {
                // Chuyển timestamp (mili giây) thành đối tượng Date
                val date = Date(timestamp)

                // Định dạng ngày giờ
                val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                return formatter.format(date)
            }
            tvTimestamp.text = convertTimestampToDate(comment.timestamp)

            // Gọi phương thức getUserFromId từ CommentAdapter để lấy thông tin người dùng
            adapter.getUserFromId(comment.userID) { user ->
                if (user != null) {
                    tvUserName.text = user.name
                    if (!user.img.isNullOrEmpty()) {
                        Glide.with(itemView.context).load(user.img).into(ivUserAvatar)
                    }
                } else {
                    tvUserName.text = "Unknown User"
                }
            }

        }
    }


    private fun getUserFromId(userId: String, callback: (User?) -> Unit) {
        val database = FirebaseDatabase.getInstance()
        val usersRef = database.getReference("Users")

        usersRef.child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val user = snapshot.getValue(User::class.java)
                    callback(user)
                } else {
                    callback(null) // Không tìm thấy user
                }
            }

            override fun onCancelled(error: DatabaseError) {
                callback(null) // Lỗi xảy ra
            }
        })
    }
}
