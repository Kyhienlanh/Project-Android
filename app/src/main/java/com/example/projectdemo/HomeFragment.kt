package com.example.projectdemo

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.MutableData
import com.google.firebase.database.Transaction
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */

class HomeFragment : Fragment(), OnPostActionListener {
    private lateinit var recyclerView: RecyclerView
    private lateinit var mainLayout: ConstraintLayout
    private lateinit var postAdapter: PostAdapter
    private lateinit var firebaseAuth:FirebaseAuth
    private val postList = mutableListOf<Post>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // Khởi tạo RecyclerView
        recyclerView = view.findViewById(R.id.postsRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)
        mainLayout = view.findViewById(R.id.mainlayout)
        firebaseAuth = FirebaseAuth.getInstance()

        var userID=firebaseAuth.currentUser?.uid.toString()
        // Khởi tạo Adapter
        postAdapter = PostAdapter(postList,this@HomeFragment,userID)
        recyclerView.adapter = postAdapter

        // Load dữ liệu từ Firebase
        loadPostsFromFirebase()

        return view
    }
    private fun loadPostsFromFirebase() {
        val databaseReference = FirebaseDatabase.getInstance().getReference("posts")

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                postList.clear() // Xóa dữ liệu cũ trước khi thêm mới

                // Duyệt qua tất cả các userID
                for (userSnapshot in snapshot.children) {
                    // Duyệt qua tất cả các postID của user
                    for (postSnapshot in userSnapshot.children) {
                        val post = postSnapshot.getValue(Post::class.java)
                        post?.let { postList.add(it) } // Thêm bài viết vào danh sách nếu không null
                    }
                }

                // Cập nhật adapter sau khi dữ liệu thay đổi
                postAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                // Xử lý lỗi nếu có
                Toast.makeText(context, "Lỗi: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }




    override fun onLikeClicked(post: Post) {
        var UserID=firebaseAuth.currentUser?.uid.toString()
        addUserIDToLikedBy(post.postID,UserID)

    }
    override fun onCommentClicked(post: Post) {
        Toast.makeText(context, "Comment on post by: ${post.userID}", Toast.LENGTH_SHORT).show()
    }

    override fun onShareClicked(post: Post) {
        Toast.makeText(context, "Shared post by: ${post.userID}", Toast.LENGTH_SHORT).show()
    }

    override fun onOptionsClicked(post: Post) {
        Toast.makeText(context, "Options clicked for post by: ${post.userID}", Toast.LENGTH_SHORT).show()
    }
    fun addUserIDToLikedBy(postID: String, currentUserID: String) {
        // Truy cập danh sách tất cả bài viết
        val postsRef = FirebaseDatabase.getInstance().getReference("posts")

        postsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Tìm bài viết có postID trong tất cả bài viết
                for (userSnapshot in snapshot.children) {
                    for (postSnapshot in userSnapshot.children) {
                        if (postSnapshot.key == postID) {
                            val post = postSnapshot.getValue(Post::class.java)
                            if (post != null) {
                                val postRef = postSnapshot.ref // Lấy tham chiếu trực tiếp đến bài viết

                                if (post.likedBy.contains(currentUserID)) {
                                    // Nếu user đã thích, bỏ thích (unlike)
                                    post.likedBy = post.likedBy.filter { it != currentUserID }
                                    post.likes = post.likedBy.size
                                } else {
                                    // Nếu user chưa thích, thêm vào danh sách likedBy
                                    post.likedBy = post.likedBy + currentUserID
                                    post.likes = post.likedBy.size
                                }

                                // Cập nhật lại bài viết trong Firebase
                                postRef.setValue(post).addOnSuccessListener {
                                    println("User updated in likedBy and post updated successfully.")
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

