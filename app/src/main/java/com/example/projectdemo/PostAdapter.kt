package com.example.projectdemo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PostAdapter(private val postList: List<Post>) : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val avatarImageView: ImageView = itemView.findViewById(R.id.avatarImageView)
        val usernameTextView: TextView = itemView.findViewById(R.id.usernameTextView)
        val postImageView: ImageView = itemView.findViewById(R.id.postImageView)
        val likeIcon: ImageView = itemView.findViewById(R.id.likeIcon)
        val commentIcon: ImageView = itemView.findViewById(R.id.commentIcon)
        val shareIcon: ImageView = itemView.findViewById(R.id.shareIcon)
        val likeCountTextView: TextView = itemView.findViewById(R.id.likeCountTextView)
        val postContentTextView: TextView = itemView.findViewById(R.id.postContentTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.post, parent, false)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = postList[position]
        // Set avatar, username, post image, likes, content here
        holder.usernameTextView.text = post.username
        holder.postContentTextView.text = post.content
        holder.likeCountTextView.text = "${post.likeCount} likes"
        // Load images using Glide or Picasso for avatar and post image
    }

    override fun getItemCount() = postList.size
}
