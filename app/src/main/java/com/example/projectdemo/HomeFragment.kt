package com.example.projectdemo

import android.app.Activity
import android.app.ActivityOptions
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.Transition
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.MutableData
import com.google.firebase.database.Transaction
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.launch
import java.io.File
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
        listenForNotifications(userID, requireContext())
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
        var UserID=firebaseAuth.currentUser?.uid.toString()
        if(UserID==post.userID){
            var ProfileFragment=ProfileFragment()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.framelayout, ProfileFragment)
                .addToBackStack(null)
                .commit()
        }
        else{
            val bundle = Bundle()
            bundle.putSerializable("post_data", post.userID)
            val intent = Intent(requireContext(), TrangCaNhan::class.java)
            intent.putExtras(bundle)
            startActivity(intent)
        }
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

                                    post.likedBy = post.likedBy.filter { it != currentUserID }
                                    post.likes = post.likedBy.size
                                } else {
                                    // Nếu user chưa thích, thêm vào danh sách likedBy
                                    post.likedBy = post.likedBy + currentUserID
                                    post.likes = post.likedBy.size
                                    sendLikeNotification(currentUserID, post.userID, post.postID)
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

    data class Notification(
        val message: String? = null,
        val timestamp: Long? = null,
        val postID: String? = null,
        val type: String? = null,
        val sent: Boolean = false // Mới thêm thuộc tính sent để theo dõi trạng thái đã gửi
    )


    fun sendLikeNotification(likerID: String, postOwnerID: String, postID: String) {
        val database = FirebaseDatabase.getInstance().reference

        // Kiểm tra nếu đã gửi thông báo trước đó
        database.child("likes").child(postID).child(likerID).child("notified").get()
            .addOnSuccessListener { snapshot ->
                val notified = snapshot.getValue(Boolean::class.java) ?: false
                if (!notified) {
                    // Chưa thông báo, tiếp tục xử lý
                    getUserFromId(likerID) { user ->
                        val name = user?.name ?: "Người dùng"

                        // Tạo thông báo
                        val notification = Notification(
                            message = "$name đã thích bài viết của bạn!",
                            timestamp = System.currentTimeMillis(),
                            postID = postID,
                            type = "like",
                            sent = false // Mới tạo, chưa gửi thông báo
                        )

                        // Lưu thông báo vào Firebase
                        database.child("notifications")
                            .child(postOwnerID)
                            .push()
                            .setValue(notification)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    Log.d("Notification", "Thông báo đã được gửi thành công.")

                                    // Đánh dấu sự kiện là đã thông báo
                                    database.child("likes").child(postID).child(likerID)
                                        .child("notified").setValue(true)
                                        .addOnCompleteListener {
                                            // Sau khi cập nhật "notified", bạn có thể tiếp tục các thao tác khác nếu cần
                                        }
                                } else {
                                    Log.d("Notification", "Gửi thông báo không thành công: ${task.exception}")
                                }
                            }
                    }
                } else {
                    Log.d("Notification", "Thông báo đã được gửi trước đó, không gửi lại.")
                }
            }
            .addOnFailureListener { exception ->
                Log.e("Notification", "Lỗi khi kiểm tra trạng thái thông báo: ${exception.message}")
            }
    }



    fun listenForNotifications(userID: String, context: Context) {
        val database = FirebaseDatabase.getInstance().reference

        database.child("notifications").child(userID)
            .addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    val notification = snapshot.getValue(Notification::class.java)
                    if (notification != null && notification.message != null && !notification.sent) {
                        // Hiển thị thông báo
                        showNotification(context, notification.message)

                        // Sau khi hiển thị thông báo, đánh dấu thông báo là đã gửi
                        snapshot.ref.child("sent").setValue(true)
                    } else {
                        Log.d("Notification", "Thông báo không hợp lệ hoặc đã gửi trước đó.")
                    }
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
                override fun onChildRemoved(snapshot: DataSnapshot) {}
                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
                override fun onCancelled(error: DatabaseError) {
                    Log.d("Notification", "Lỗi lắng nghe thông báo: ${error.message}")
                }
            })
    }





    fun showNotification(context: Context, message: String?) {
        if (message == null) {
            Log.d("Notification", "Message is null, không hiển thị thông báo.")
            return
        }

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationChannelId = "user_notifications_channel"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                notificationChannelId,
                "User Notifications",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(context, notificationChannelId)
            .setContentTitle("Thông báo mới")
            .setContentText(message)
            .setSmallIcon(R.drawable.timdo)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }








}

