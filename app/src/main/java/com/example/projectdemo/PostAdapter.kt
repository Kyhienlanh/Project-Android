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

interface OnPostActionListener {
    fun onLikeClicked(post: Post)
    fun onCommentClicked(post: Post)
    fun onShareClicked(post: Post)
    fun onOptionsClicked(post: Post)
}



// Trong PostAdapter:
class PostAdapter(
    private var posts: MutableList<Post>,
    private val listener: OnPostActionListener,
    private var userId:String

) : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.post, parent, false)

        return PostViewHolder(view)
    }
    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = posts[position]
        holder.bind(post)
    }

    override fun getItemCount(): Int {
        return posts.size
    }

    fun updatePosts(newPosts: MutableList<Post>) {
        this.posts = newPosts
        notifyDataSetChanged()
    }

    inner class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val userAvatar: ShapeableImageView = itemView.findViewById(R.id.userAvatar)
        private val userName: TextView = itemView.findViewById(R.id.userName)
        private val location: TextView = itemView.findViewById(R.id.location)
        private val postContent: TextView = itemView.findViewById(R.id.postContent)
        private val postImage: ImageView = itemView.findViewById(R.id.postImage)
        private val likeCount: TextView = itemView.findViewById(R.id.likeCount)
        private val commentCount: TextView = itemView.findViewById(R.id.commentCount)
        private val likeButton: ImageView = itemView.findViewById(R.id.likeButton)
        private val commentButton: ImageView = itemView.findViewById(R.id.commentButton)
        private val shareButton: ImageView = itemView.findViewById(R.id.shareButton)
        private val optionsIcon: ImageView = itemView.findViewById(R.id.optionsIcon)
        private val context = itemView.context
        fun bind(post: Post) {
            // Set user data
          
            location.text = post.location ?: ""
            postContent.text = post.content
            getUserFromId(post.userID) { user ->
                if (user != null) {
                    userName.text = user.name
                    if (!user.img.isNullOrEmpty()) {
                        Glide.with(context).load(user.img).into(userAvatar)
                    } else {
                        userAvatar.setImageResource(R.drawable.circle_background)
                    }
                } else {
                    userName.text = "Unknown User"
                    userAvatar.setImageResource(R.drawable.circle_background)
                }
            }
            // Handle post image visibility and loading
            if (!post.imageURL.isNullOrEmpty()) {
                postImage.visibility = View.VISIBLE
                Glide.with(itemView.context).load(post.imageURL).into(postImage)
            } else {
                postImage.visibility = View.GONE
            }
            val isLiked = post.likedBy.contains(userId)
            if (isLiked) {
                likeButton.setImageResource(R.drawable.timdo)
            } else {
                likeButton.setImageResource(R.drawable.timtrang)
            }
            // Set like and comment counts
            likeCount.text = "Likes: ${post.likes}"
            commentCount.text = "Comments: ${post.commentsCount}"

            // Set click listeners for buttons
            likeButton.setOnClickListener {
              listener.onLikeClicked(post)
            }

            commentButton.setOnClickListener {
                listener.onCommentClicked(post)
            }

            shareButton.setOnClickListener {
                listener.onShareClicked(post)
            }

            optionsIcon.setOnClickListener {
                listener.onOptionsClicked(post)
            }
        }
    }
    // Trong PostAdapter
    private fun getUserFromId(userId: String, callback: (User?) -> Unit) {
        val database = FirebaseDatabase.getInstance()
        val usersRef = database.getReference("Users") // Thay "users" bằng tên node của bạn trong database

        usersRef.child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    // Chuyển đổi snapshot thành User
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
