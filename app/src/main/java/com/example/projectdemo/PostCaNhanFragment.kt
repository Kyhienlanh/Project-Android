package com.example.projectdemo

import android.app.ActivityOptions
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.io.File

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class PostCaNhanFragment : Fragment(),OnPostActionListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var recyclerView: RecyclerView
    private lateinit var mainLayout: ConstraintLayout
    private lateinit var postAdapter: PostAdapter
    private lateinit var firebaseAuth: FirebaseAuth
    private val postList = mutableListOf<Post>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_post_ca_nhan, container, false)

        // Khởi tạo RecyclerView
        recyclerView = view.findViewById(R.id.postsRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)
        firebaseAuth = FirebaseAuth.getInstance()

        var userID=firebaseAuth.currentUser?.uid.toString()
        // Khởi tạo Adapter
        postAdapter = PostAdapter(postList,this@PostCaNhanFragment,userID)
        recyclerView.adapter = postAdapter

        // Load dữ liệu từ Firebase
        loadPostsFromFirebase()

        return view
    }
    private fun loadPostsFromFirebase() {
        var UserID=firebaseAuth.currentUser?.uid.toString()
        val databaseReference = FirebaseDatabase.getInstance().getReference("posts")

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                postList.clear() // Xóa dữ liệu cũ trước khi thêm mới


                for (userSnapshot in snapshot.children) {
                    // Duyệt qua tất cả các postID của user
                    if(userSnapshot.key==UserID){
                        for (postSnapshot in userSnapshot.children) {
                            val post = postSnapshot.getValue(Post::class.java)
                            post?.let { postList.add(it) }
                        }
                    }

                }
                postList.sortByDescending { it.timestamp }

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

        val intent = Intent(context, commentActivity::class.java).apply {
            putExtra("post_data", post.postID)
        }
        val options = ActivityOptions.makeCustomAnimation(
            requireActivity(),
            R.anim.slide_in_up,
            R.anim.slide_out_down
        )
        startActivity(intent, options.toBundle())
    }


    override fun onNameClicked(post: Post) {
        Toast.makeText(context, "click name", Toast.LENGTH_SHORT).show()
    }
    override fun onShareClicked(post: Post) {
        sharePost(post)
    }
    private fun sharePost(post: Post) {
        // Tạo nội dung cần chia sẻ
        val shareText = "Bài đăng từ ${post.userID}:\n\n${post.content}"

        // Kiểm tra nếu hình ảnh tồn tại
        if (!post.imageURL.isNullOrEmpty()) {
            // Tạo Intent chia sẻ với hình ảnh và văn bản
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain" // Loại nội dung là văn bản
                putExtra(Intent.EXTRA_SUBJECT, "Chia sẻ bài đăng") // Tiêu đề chia sẻ
                putExtra(Intent.EXTRA_TEXT, "$shareText\n\nHình ảnh: ${post.imageURL}") // Nội dung bài đăng với URL hình ảnh
            }

            // Mở hộp thoại chia sẻ
            startActivity(Intent.createChooser(shareIntent, "Chia sẻ bài đăng qua:"))
        } else {
            // Nếu không có ảnh, chỉ chia sẻ nội dung văn bản
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_SUBJECT, "Chia sẻ bài đăng")
                putExtra(Intent.EXTRA_TEXT, shareText)
            }

            // Mở hộp thoại chia sẻ
            startActivity(Intent.createChooser(shareIntent, "Chia sẻ bài đăng qua:"))
        }
    }


    // Hàm lưu ảnh vào bộ nhớ cache và trả về URI
    private fun saveImageToCache(bitmap: Bitmap): Uri {
        val file = File(requireContext().cacheDir, "shared_image.png") // Tạo file trong bộ nhớ cache
        file.outputStream().use {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) // Lưu ảnh dưới định dạng PNG
        }
        return FileProvider.getUriForFile(
            requireContext(),
            "${requireContext().packageName}.fileprovider", // Cập nhật với tên package của bạn
            file
        )
    }




    override fun onOptionsClicked(post: Post) {

        val dialogView = layoutInflater.inflate(R.layout.bottom_sheet_dialog, null)
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        bottomSheetDialog.setContentView(dialogView)

        val tvEdit = dialogView.findViewById<TextView>(R.id.tvEdit)
        val tvDelete = dialogView.findViewById<TextView>(R.id.tvDelete)
        var UserID=firebaseAuth.currentUser?.uid.toString()
        if(post.userID!=UserID){
            tvDelete.visibility=View.GONE
        }
        tvEdit.setOnClickListener {
            // Xử lý logic sửa bài đăng
            Toast.makeText(context, "Chọn sửa bài đăng", Toast.LENGTH_SHORT).show()
            bottomSheetDialog.dismiss()
        }
        tvDelete.setOnClickListener {
            deletePost(post)
            Toast.makeText(context, "Chọn xóa bài đăng", Toast.LENGTH_SHORT).show()
            bottomSheetDialog.dismiss()
        }


        bottomSheetDialog.show()

    }
    private fun countPosts(userId: String) {
        val postsRef = FirebaseDatabase.getInstance().getReference("posts").child(userId)

        // Lấy số lượng bài đăng
        postsRef.get().addOnSuccessListener { snapshot ->
            // snapshot chứa tất cả các bài đăng của người dùng
            val postCount = snapshot.childrenCount // Đếm số lượng bài đăng

            // Cập nhật số lượng bài đăng vào User
            val userRef = FirebaseDatabase.getInstance().getReference("Users").child(userId)

            // Cập nhật số lượng bài đăng vào thuộc tính "postCount" của người dùng
            userRef.child("post").setValue(postCount).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Cập nhật thành công
                    Toast.makeText(context, "Số lượng bài đăng đã được cập nhật", Toast.LENGTH_SHORT).show()
                } else {
                    // Xử lý lỗi nếu có
                    Toast.makeText(context, "Lỗi khi cập nhật số lượng bài đăng", Toast.LENGTH_SHORT).show()
                }
            }

            Toast.makeText(context, "Số lượng bài đăng của người dùng: $postCount", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener { exception ->
            // Xử lý khi có lỗi trong việc đọc dữ liệu
            Toast.makeText(context, "Lỗi: ${exception.message}", Toast.LENGTH_SHORT).show()
        }
    }
    fun deletePost(post: Post) {
        val postsRef = FirebaseDatabase.getInstance().getReference("posts")
        var UserID=firebaseAuth.currentUser?.uid.toString()
        if(post.userID==UserID){
            postsRef.child(post.userID).child(post.postID).removeValue()
                .addOnSuccessListener {
                    countPosts(UserID)
                    Toast.makeText(context, "Xóa bài đăng thành công", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { error ->
                    // Xử lý lỗi
                    Toast.makeText(context, "Lỗi: ${error.message}", Toast.LENGTH_SHORT).show()
                }
        }

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
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment PostCaNhanFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            PostCaNhanFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}