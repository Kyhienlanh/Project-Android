package com.example.projectdemo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView

interface OnPostActionListener {
    fun onLikeClicked(post: Post)
    fun onCommentClicked(post: Post)
    fun onShareClicked(post: Post)
    fun onOptionsClicked(post: Post)
}

class PostAdapter(
    private val posts: List<Post>,
    private val listener: OnPostActionListener // Thêm listener vào adapter
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

        fun bind(post: Post) {
            // Set user data
            userName.text = post.userID // Use actual user name from your data
            location.text = post.location ?: ""
            postContent.text = post.content

            // Handle post image visibility and loading
            if (!post.imageURL.isNullOrEmpty()) {
                postImage.visibility = View.VISIBLE
                Glide.with(itemView.context).load(post.imageURL).into(postImage)
            } else {
                postImage.visibility = View.GONE
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
}

