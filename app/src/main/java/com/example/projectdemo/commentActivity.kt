package com.example.projectdemo

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class commentActivity : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var databaseReference: FirebaseDatabase
    private val CmtList = mutableListOf<Comment>()
    private lateinit var CommentAdapter: CommentAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_comment)

        // Initialize FirebaseAuth here
        firebaseAuth = FirebaseAuth.getInstance()
        val userID=firebaseAuth.currentUser?.uid.toString()
        val etComment = findViewById<EditText>(R.id.etComment)
        val rvComments = findViewById<RecyclerView>(R.id.rvComments)
        var tvCommentCount= findViewById<TextView>(R.id.tvCommentCount)
        val ivUserAvatar=findViewById<ShapeableImageView>(R.id.ivUserAvatar)
        val postId = intent.getStringExtra("post_data")

        val btnSendComment = findViewById<ImageButton>(R.id.btnSendComment)

        etComment.requestFocus() // Đặt focus cho EditText
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(etComment, InputMethodManager.SHOW_IMPLICIT)

        CommentAdapter = CommentAdapter(CmtList,userID) // Giả sử CommentAdapter nhận danh sách comment
        rvComments.layoutManager = LinearLayoutManager(this)
        rvComments.adapter = CommentAdapter

        loadcmtFromFirebase(postId.toString())
        getUserFromId(userID) { user ->
            if (user != null) {

                if (!user.img.isNullOrEmpty()) {
                    Glide.with(this).load(user.img).into(ivUserAvatar)
                }
            }
        }

        btnSendComment.setOnClickListener {
            val userID = firebaseAuth.currentUser?.uid
            if (etComment.text.toString().trim().isNotEmpty() && userID != null) {
                val newComment = Comment(
                    userID = userID,
                    content = etComment.text.toString(),
                    timestamp = System.currentTimeMillis()
                )
                addUserIDToCmtBy(postId.toString(), newComment)

            } else {
                Toast.makeText(this, "Vui lòng nhập bình luận", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun loadcmtFromFirebase(postID: String) {
        val databaseReference = FirebaseDatabase.getInstance().getReference("posts")

        // Lắng nghe sự thay đổi của dữ liệu
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                CmtList.clear()  // Xóa danh sách comment cũ trước khi thêm comment mới

                // Duyệt qua từng user
                for (userSnapshot in snapshot.children) {
                    // Duyệt qua từng post của user
                    for (postSnapshot in userSnapshot.children) {
                        // Kiểm tra nếu post này có postID trùng khớp
                        if (postSnapshot.key == postID) {
                            // Lấy danh sách comment
                            val commentsSnapshot = postSnapshot.child("commentBy")

                            // Duyệt qua các bình luận trong "commentBy"
                            for (cmtSnapshot in commentsSnapshot.children) {
                                // Chuyển đổi mỗi snapshot thành đối tượng Comment
                                val comment = cmtSnapshot.getValue(Comment::class.java)
                                comment?.let {
                                    // Thêm bình luận vào danh sách
                                    CmtList.add(it)

                                }
                            }
                        }
                    }
                }

                CmtList.sortByDescending { it.timestamp }

                // Thông báo adapter đã có thay đổi dữ liệu
                CommentAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                // Xử lý lỗi
                Toast.makeText(this@commentActivity, "Lỗi: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
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


    fun addUserIDToCmtBy(postID: String, newComment: Comment) {
        val postsRef = FirebaseDatabase.getInstance().getReference("posts")

        postsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (userSnapshot in snapshot.children) {
                    for (postSnapshot in userSnapshot.children) {
                        if (postSnapshot.key == postID) {
                            val post = postSnapshot.getValue(Post::class.java)
                            if (post != null) {
                                val postRef = postSnapshot.ref
                                val updatedComments = post.CommentBy.toMutableList()

                                // Tạo commentID dựa trên số lượng hiện tại
                                val newCommentID = updatedComments.size.toString()
                                val commentWithID = newComment.copy(commentID = newCommentID)

                                // Thêm bình luận mới
                                updatedComments.add(commentWithID)
                                post.CommentBy = updatedComments

                                // Tăng commentsCount
                                post.commentsCount += 1

                                // Cập nhật Firebase
                                postRef.setValue(post).addOnSuccessListener {
                                    println("Comment added successfully with ID: $newCommentID")
                                    println("Updated comments count: ${post.commentsCount}")
                                }.addOnFailureListener {
                                    println("Failed to update post: ${it.message}")
                                }
                                return
                            }
                        }
                    }
                }
                println("Post with ID $postID not found.")
            }

            override fun onCancelled(error: DatabaseError) {
                println("Error fetching posts: ${error.message}")
            }
        })
    }
}
