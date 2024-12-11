package com.example.projectdemo

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream

class AddNewsFeed : AppCompatActivity() {

    private var selectedImageUri: Uri? = null // Biến lưu URI của ảnh được chọn
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var avatarImageView: ShapeableImageView
    private lateinit var NameUser: TextView
    private lateinit var TrangThai: TextView
    private lateinit var back:ImageView
    private lateinit var Post:Button
    private lateinit var NoiDung:EditText
    private lateinit var imageView10:ImageView
    private lateinit var ChonAnh:TextView
    private lateinit var GanThe:TextView
    private lateinit var CamXuc:TextView
    private lateinit var Vitri:TextView
    private lateinit var MauNen:TextView
    private lateinit var Camera:TextView
    private lateinit var imageView6:ImageView
    private lateinit var progressBar2: ProgressBar
    var textIcon:String=""
    var IDIcon:Int=0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_news_feed)
        database = FirebaseDatabase.getInstance().reference.child("Users")
        firebaseAuth = FirebaseAuth.getInstance()
        avatarImageView=findViewById(R.id.avatarImageView)
        NameUser=findViewById(R.id.NameUser)
        TrangThai=findViewById(R.id.TrangThai)
        back=findViewById(R.id.back)
        Post=findViewById(R.id.Post)
        NoiDung=findViewById(R.id.NoiDung)
        imageView10=findViewById(R.id.imageView10)
        ChonAnh=findViewById(R.id.ChonAnh)
        GanThe=findViewById(R.id.GanThe)
        CamXuc=findViewById(R.id.CamXuc)
        Vitri=findViewById(R.id.Vitri)
        MauNen=findViewById(R.id.MauNen)
        Camera=findViewById(R.id.Camera)
        imageView6=findViewById(R.id.imageView6)
        progressBar2=findViewById(R.id.progressBar2)


        // Mở album ngay khi Activity được tạo
        openGallery()
        loadUserData()
        // Các thiết lập khác
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        imageView6.setOnClickListener(){
            val options = arrayOf("Công khai", "Riêng tư")
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Chọn trạng thái")
            builder.setItems(options) { dialog, which ->

                TrangThai.text = options[which]
            }
            builder.show()
        }
        ChonAnh.setOnClickListener(){
            openGallery()
        }
        Camera.setOnClickListener(){
            requestCameraPermission()
        }
        Vitri.setOnClickListener(){
        }
        back.setOnClickListener(){
            var intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        val fontList = listOf("Default", "Bold", "Italic", "Serif", "Sans Serif", "Monospace")

        MauNen.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Chọn kiểu chữ")

            builder.setItems(fontList.toTypedArray()) { dialog, which ->
                when (which) {
                    0 -> {
                        NoiDung.typeface = Typeface.DEFAULT
                    }
                    1 -> {
                        NoiDung.setTypeface(null, Typeface.BOLD)
                    }
                    2 -> {
                        NoiDung.setTypeface(null, Typeface.ITALIC)
                    }
                    3 -> {
                        NoiDung.typeface = Typeface.SERIF
                    }
                    4 -> {
                        NoiDung.typeface = Typeface.SANS_SERIF
                    }
                    5 -> {
                        NoiDung.typeface = Typeface.MONOSPACE
                    }

                }
            }

            builder.show()
        }
        CamXuc.setOnClickListener {
            val intent = Intent(this, BieuCam::class.java)
            startActivityForResult(intent, 1)  // Sử dụng mã request code 1
        }
        Post.setOnClickListener(){
            uploadImageFromImageView()
        }




    }



    companion object {
        const val PICK_IMAGE_REQUEST = 1001
        const val REQUEST_CAMERA_PERMISSION = 1002
        const val CROP_IMAGE_REQUEST = 1003
    }

    // Mở album để chọn ảnh
    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*" // Chỉ chọn ảnh
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    // Nhận kết quả từ album
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.data // Lưu URI của ảnh đã chọn vào biến
            // Bạn có thể sử dụng URI này để tải ảnh vào ImageView hoặc thực hiện các thao tác khác
            // Ví dụ: hiển thị ảnh vào một ImageView
            val imageView = findViewById<ImageView>(R.id.imageView10)
            imageView.setImageURI(selectedImageUri)
        }
        if (requestCode == REQUEST_CAMERA_PERMISSION && resultCode == RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
            val imageView = findViewById<ImageView>(R.id.imageView10)
            imageView.setImageBitmap(imageBitmap)
        }
        if (requestCode == 1 && resultCode == RESULT_OK) {
            // Lấy dữ liệu trả về từ Intent
            textIcon = data?.getStringExtra("text").toString()  // Lấy giá trị text
            IDIcon = data?.getIntExtra("imageResId", -1)!!.toInt()  // Lấy giá trị imageResId, mặc định là -1 nếu không có giá trị
            loadUserData()
            // Xử lý dữ liệu nhận được (ví dụ: hiển thị thông báo)
            Toast.makeText(this, "Text: $textIcon, Image ID: $IDIcon", Toast.LENGTH_SHORT).show()
        }

    }

    private fun loadUserData() {
        progressBar2.visibility = View.VISIBLE
        val userID = firebaseAuth.currentUser?.uid

        // Lấy dữ liệu người dùng từ Firebase
        if (userID != null) {
            database.child(userID).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        val user = dataSnapshot.getValue(User::class.java)
                        user?.let { updateUI(it) } // Cập nhật UI nếu người dùng không null
                        progressBar2.visibility = View.GONE
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.e("Firebase", "Lỗi khi truy cập dữ liệu người dùng: ${databaseError.message}")
                }
            })
        } else {
            Log.e("Firebase", "Người dùng chưa đăng nhập.")
        }
    }
    private fun updateUI(user: User) {

           NameUser.text = user.name +"${textIcon}"

//         Hiển thị avatar
        val img = user.img
        if (!img.isNullOrEmpty()) {
            Glide.with(this@AddNewsFeed)
                .load(img)
                .placeholder(R.drawable.circle_background)
                .into(avatarImageView)

        }
    }

    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(packageManager) != null) {
            startActivityForResult(intent, REQUEST_CAMERA_PERMISSION)
        }
    }
    private fun requestCameraPermission() {
        if (checkSelfPermission(android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(android.Manifest.permission.CAMERA), REQUEST_CAMERA_PERMISSION)
        } else {
            openCamera()
        }
    }
    private fun uploadImageFromImageView() {
        // Lấy drawable từ ImageView
        progressBar2.visibility = View.VISIBLE
        val drawable = imageView10.drawable
        if (drawable == null) {
            Toast.makeText(this, "Hình ảnh không tồn tại!", Toast.LENGTH_SHORT).show()
            return
        }

        // Chuyển drawable thành Bitmap
        val bitmap = (drawable as BitmapDrawable).bitmap

        // Chuyển Bitmap thành ByteArray
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream) // Nén ảnh
        val imageData = byteArrayOutputStream.toByteArray()

        // Tạo đường dẫn lưu trữ trong Firebase Storage
        val userID = FirebaseAuth.getInstance().currentUser?.uid ?: "unknown_user"
        val storageRef = FirebaseStorage.getInstance().getReference("post_images/$userID/${System.currentTimeMillis()}.jpg")

        // Upload ByteArray lên Firebase Storage
        val uploadTask = storageRef.putBytes(imageData)
        uploadTask.addOnSuccessListener {
            // Lấy URL của hình ảnh sau khi upload thành công
            storageRef.downloadUrl.addOnSuccessListener { uri ->
                val imageUrl = uri.toString()
                savePostToDatabase(imageUrl)
                Toast.makeText(this, "Upload thành công: $imageUrl", Toast.LENGTH_SHORT).show()

                progressBar2.visibility = View.GONE
            }
        }.addOnFailureListener { e ->
            Toast.makeText(this, "Upload thất bại: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    // Lưu bài đăng vào Firebase Realtime Database
    private fun savePostToDatabase(imageUrl: String) {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val databaseRef = FirebaseDatabase.getInstance().getReference("posts/$currentUserId")

        // Tạo postID duy nhất
        val postID = databaseRef.push().key ?: return

        // Tạo một đối tượng bài đăng với thông tin người dùng nhập
        val post = Post(
            postID = postID,
            userID = currentUserId,
            content = NoiDung.text.toString(),
            CamSucHoatDong = "$textIcon", // Ví dụ, bạn có thể thay đổi tùy theo trạng thái
            GanTheNguoiKhac = "taggedUserID", // Gắn thẻ người dùng (tùy chỉnh)
            MauNen = "${NoiDung.typeface.toString()}", // Màu nền mặc định
            imageURL = imageUrl,
            timestamp = System.currentTimeMillis(),
            likes = 0,
            commentsCount = 0,
            likedBy = listOf(),
            sharedCount = 0,
            status = TrangThai.text.toString(),
            location = ""
        )

        // Lưu bài đăng vào Firebase Realtime Database
        databaseRef.child(postID).setValue(post)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Lưu bài đăng vào Database thành công!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Lưu bài đăng thất bại: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }





}

