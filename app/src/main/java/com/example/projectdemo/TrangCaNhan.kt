package com.example.projectdemo

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.MutableData
import com.google.firebase.database.Transaction
import com.google.firebase.database.ValueEventListener

class TrangCaNhan : AppCompatActivity(),OnImageClickListener {
    private lateinit var postsTextView:TextView
    private lateinit var followersTextView:TextView
    private lateinit var followingTextView:TextView
    private lateinit var Name:TextView
    private lateinit var biographyTextView:TextView
    private lateinit var editProfileButton:Button
    private lateinit var NhanTinButton:Button
    private lateinit var postsRecyclerView: RecyclerView
    private lateinit var avatarImageView: ShapeableImageView
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var UserIDCurrent:String
    private lateinit var imageAdapter: ImageAdapter
    private lateinit var progressBar3: ProgressBar
    private var imageList = mutableListOf<Post>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_trang_ca_nhan)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        postsTextView=findViewById(R.id.postsTextView)
        followersTextView=findViewById(R.id.followersTextView)
        followingTextView=findViewById(R.id.followingTextView)
        biographyTextView=findViewById(R.id.biographyTextView)
        editProfileButton=findViewById(R.id.editProfileButton)
        NhanTinButton=findViewById(R.id.NhanTinButton)
        postsRecyclerView=findViewById(R.id.postsRecyclerView)
        avatarImageView=findViewById(R.id.avatarImageView)
        firebaseAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().getReference("Users")
        Name=findViewById(R.id.Name)
        progressBar3=findViewById(R.id.progressBar3)

        val IDUser = intent.getSerializableExtra("post_data")
        postsRecyclerView=findViewById(R.id.postsRecyclerView)
        postsRecyclerView.layoutManager = GridLayoutManager(this, 3)
        imageAdapter = ImageAdapter(imageList, this)
        postsRecyclerView.adapter = imageAdapter
        loadImageUser(IDUser.toString())


        UserIDCurrent=firebaseAuth.currentUser?.uid.toString()
        NhanTinButton.setOnClickListener(){
            val intent = Intent(this, ChatActivity::class.java)
            startActivity(intent)
        }
        getUserFromId(IDUser.toString()) { user ->
            if (user != null) {
                Name.text = user.name
                followersTextView.text=user.followers.toString()+" follower"
                followingTextView.text=user.following.toString()+" following"
                postsTextView.text=user.post.toString()+" posts"

                if (!user.img.isNullOrEmpty()) {
                    Glide.with(this).load(user.img).into(avatarImageView)
                } else {

                }
            } else {

            }
        }
        isUserFollowed(currentUserID = UserIDCurrent, targetUserID = IDUser.toString()) { isFollowed ->
            if (isFollowed) {
                editProfileButton.text="Đang theo dỗi"
                println("User đã theo dõi.")
            } else {
                editProfileButton.text="Theo dỗi"
                println("User chưa theo dõi.")
            }
        }

        editProfileButton.setOnClickListener(){

            isUserFollowed(currentUserID = UserIDCurrent, targetUserID = IDUser.toString()) { isFollowed ->
                if (isFollowed) {
                    unfollowUser(currentUserID = UserIDCurrent, targetUserID = IDUser.toString()) { success ->
                        if (success) {
                            Toast.makeText(this, "Hủy theo dõi thành công!", Toast.LENGTH_SHORT).show()
                            getUserFromId(IDUser.toString()) { user ->
                                if (user != null) {
                                    Name.text = user.name
                                    followersTextView.text=user.followers.toString()+" follower"
                                    followingTextView.text=user.following.toString()+" following"
                                    postsTextView.text=user.post.toString()+" posts"

                                    if (!user.img.isNullOrEmpty()) {
                                        Glide.with(this).load(user.img).into(avatarImageView)
                                    } else {

                                    }
                                } else {

                                }
                            }
                        } else {
                            Toast.makeText(this, "Hủy theo dõi thất bại!", Toast.LENGTH_SHORT).show()
                        }
                    }

                    editProfileButton.text="Theo dỗi"
                } else {
                    addFollowUserID(UserIDCurrent,IDUser.toString())
                    getUserFromId(IDUser.toString()) { user ->
                        if (user != null) {
                            Name.text = user.name
                            followersTextView.text=user.followers.toString()+" follower"
                            followingTextView.text=user.following.toString()+" following"
                            postsTextView.text=user.post.toString()+" posts"

                            if (!user.img.isNullOrEmpty()) {
                                Glide.with(this).load(user.img).into(avatarImageView)
                            } else {

                            }
                        } else {

                        }
                    }
                    editProfileButton.text="Đang theo dỗi"
                }
            }
//            if(editProfileButton.text=="Theo dỗi"){
//                Toast.makeText(this, "Theo dỗi", Toast.LENGTH_SHORT).show()
//                addFollowUserID(UserIDCurrent,IDUser.toString())
//            }else{
//                Toast.makeText(this, "Đang Theo dỗi", Toast.LENGTH_SHORT).show()
//            }
        }

    }
    override fun onImageClick(post: Post) {

    }

    fun loadImageUser(Iduser:String) {
        progressBar3.visibility= View.VISIBLE

        val databaseReference = FirebaseDatabase.getInstance().getReference("posts")

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                imageList.clear()

                // Duyệt qua tất cả các userID
                for (userSnapshot in snapshot.children) {
                    if (userSnapshot.key == Iduser) {
                        for (postSnapshot in userSnapshot.children) {
                            val post = postSnapshot.getValue(Post::class.java)
                            post?.let { imageList.add(it) }
                        }
                    }
                }
                imageList.sortByDescending { it.timestamp }


                imageAdapter.notifyDataSetChanged()
                progressBar3.visibility= View.GONE
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
    fun addFollowUserID(currentUserID: String, targetUserID: String) {
        val followRef = FirebaseDatabase.getInstance().getReference("Follow")
        val usersRef = FirebaseDatabase.getInstance().getReference("Users")

        val timestamp = System.currentTimeMillis()
        val followData = mapOf("timestamp" to timestamp)

        // Tham chiếu đến follower và following
        val followerRef = followRef.child(targetUserID).child("followers").child(currentUserID)
        val followingRef = followRef.child(currentUserID).child("following").child(targetUserID)

        // Kiểm tra nếu đã follow hay chưa
        followerRef.get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                println("Người dùng đã follow trước đó, không thực hiện thay đổi!")
            } else {
                // Thêm currentUserID vào danh sách followers của targetUserID
                followerRef.setValue(followData)
                    .addOnSuccessListener {
                        println("Thêm vào danh sách followers thành công!")
                    }
                    .addOnFailureListener { error ->
                        println("Lỗi khi thêm followers: ${error.message}")
                    }

                // Thêm targetUserID vào danh sách following của currentUserID
                followingRef.setValue(followData)
                    .addOnSuccessListener {
                        println("Thêm vào danh sách following thành công!")
                    }
                    .addOnFailureListener { error ->
                        println("Lỗi khi thêm following: ${error.message}")
                    }

                // Tăng số lượng followers cho targetUserID trong bảng Users
                usersRef.child(targetUserID).child("followers").runTransaction(object : Transaction.Handler {
                    override fun doTransaction(currentData: MutableData): Transaction.Result {
                        val currentCount = currentData.getValue(Int::class.java) ?: 0
                        currentData.value = currentCount + 1
                        return Transaction.success(currentData)
                    }

                    override fun onComplete(error: DatabaseError?, committed: Boolean, snapshot: DataSnapshot?) {
                        if (committed) {
                            println("Tăng số lượng followers thành công!")
                        } else {
                            println("Lỗi khi tăng followers: ${error?.message}")
                        }
                    }
                })

                // Tăng số lượng following cho currentUserID trong bảng Users
                usersRef.child(currentUserID).child("following").runTransaction(object : Transaction.Handler {
                    override fun doTransaction(currentData: MutableData): Transaction.Result {
                        val currentCount = currentData.getValue(Int::class.java) ?: 0
                        currentData.value = currentCount + 1
                        return Transaction.success(currentData)
                    }

                    override fun onComplete(error: DatabaseError?, committed: Boolean, snapshot: DataSnapshot?) {
                        if (committed) {
                            println("Tăng số lượng following thành công!")
                        } else {
                            println("Lỗi khi tăng following: ${error?.message}")
                        }
                    }
                })
            }
        }.addOnFailureListener { error ->
            println("Lỗi khi kiểm tra theo dõi: ${error.message}")
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

    fun isUserFollowed(currentUserID: String, targetUserID: String, callback: (Boolean) -> Unit) {
        val followRef = FirebaseDatabase.getInstance().getReference("Follow")
        val followerRef = followRef.child(targetUserID).child("followers").child(currentUserID)

        // Kiểm tra xem currentUserID có nằm trong danh sách followers của targetUserID hay không
        followerRef.get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                // Nếu snapshot tồn tại, có nghĩa là currentUserID đã theo dõi targetUserID
                callback(true)
            } else {
                // Nếu snapshot không tồn tại, có nghĩa là chưa theo dõi
                callback(false)
            }
        }.addOnFailureListener { error ->
            println("Lỗi khi kiểm tra theo dõi: ${error.message}")
            callback(false) // Nếu có lỗi xảy ra, trả về false
        }
    }

    fun unfollowUser(currentUserID: String, targetUserID: String, callback: (Boolean) -> Unit) {
        val followRef = FirebaseDatabase.getInstance().getReference("Follow")
        val usersRef = FirebaseDatabase.getInstance().getReference("Users")

        // Tham chiếu đến followers và following
        val followerRef = followRef.child(targetUserID).child("followers").child(currentUserID)
        val followingRef = followRef.child(currentUserID).child("following").child(targetUserID)

        // Xóa ID khỏi followers của targetUserID và following của currentUserID
        followerRef.removeValue().addOnSuccessListener {
            followingRef.removeValue().addOnSuccessListener {
                // Giảm số lượng followers của targetUserID
                usersRef.child(targetUserID).child("followers").runTransaction(object : Transaction.Handler {
                    override fun doTransaction(currentData: MutableData): Transaction.Result {
                        val currentCount = currentData.getValue(Int::class.java) ?: 0
                        currentData.value = if (currentCount > 0) currentCount - 1 else 0
                        return Transaction.success(currentData)
                    }

                    override fun onComplete(error: DatabaseError?, committed: Boolean, snapshot: DataSnapshot?) {
                        if (committed) {
                            println("Giảm số lượng followers thành công!")
                        } else {
                            println("Lỗi khi giảm followers: ${error?.message}")
                        }
                    }
                })

                // Giảm số lượng following của currentUserID
                usersRef.child(currentUserID).child("following").runTransaction(object : Transaction.Handler {
                    override fun doTransaction(currentData: MutableData): Transaction.Result {
                        val currentCount = currentData.getValue(Int::class.java) ?: 0
                        currentData.value = if (currentCount > 0) currentCount - 1 else 0
                        return Transaction.success(currentData)
                    }

                    override fun onComplete(error: DatabaseError?, committed: Boolean, snapshot: DataSnapshot?) {
                        if (committed) {
                            println("Giảm số lượng following thành công!")
                            callback(true) // Hủy follow thành công
                        } else {
                            println("Lỗi khi giảm following: ${error?.message}")
                            callback(false) // Lỗi khi hủy follow
                        }
                    }
                })
            }.addOnFailureListener { error ->
                println("Lỗi khi xóa khỏi following: ${error.message}")
                callback(false)
            }
        }.addOnFailureListener { error ->
            println("Lỗi khi xóa khỏi followers: ${error.message}")
            callback(false)
        }
    }




}