package com.example.projectdemo

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
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


        // Mở album ngay khi Activity được tạo
        openGallery()
        loadUserData()
        // Các thiết lập khác
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        ChonAnh.setOnClickListener(){
            openGallery()
        }
        Camera.setOnClickListener(){
            requestCameraPermission()
        }
        Vitri.setOnClickListener(){
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



    }



    companion object {
        const val PICK_IMAGE_REQUEST = 1001 // Mã yêu cầu để nhận kết quả chọn ảnh
        const val REQUEST_CAMERA_PERMISSION = 1002

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
        val userID = firebaseAuth.currentUser?.uid

        // Lấy dữ liệu người dùng từ Firebase
        if (userID != null) {
            database.child(userID).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        val user = dataSnapshot.getValue(User::class.java)
                        user?.let { updateUI(it) } // Cập nhật UI nếu người dùng không null
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

}

