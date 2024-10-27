package com.example.projectdemo

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class changeProfilenewMainActivity3 : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var storage: FirebaseStorage
    private lateinit var storageReference: StorageReference
    private val IMAGE_PICK_CODE = 1000
    private lateinit var avatarImageView: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_change_profilenew_main3)

        // Đặt padding cho view để tránh bị che bởi thanh trạng thái
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Khởi tạo FirebaseAuth và DatabaseReference
        firebaseAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().getReference("Users")
        storage = FirebaseStorage.getInstance()
        storageReference = storage.reference
        avatarImageView = findViewById(R.id.avatarImageView) // Giả sử bạn đã định nghĩa ImageView này trong layout
        val editPhotoTextView = findViewById<TextView>(R.id.textView3)

        // Thiết lập sự kiện cho TextView để chọn ảnh
        editPhotoTextView.setOnClickListener {
            chooseImageFromGallery()
        }
        // Lấy user ID
        val userID = firebaseAuth.currentUser?.uid
        val usernameTextView = findViewById<TextView>(R.id.editTextText)
        val tieusu = findViewById<TextView>(R.id.editTextText2)
        val gt = findViewById<TextView>(R.id.gt)

        // Lấy dữ liệu người dùng từ Firebase
        if (userID != null) {
            database.child(userID).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        val user = dataSnapshot.getValue(User::class.java)
                        if (user != null) {
                            usernameTextView.text = user.name
                            tieusu.text = user.biography
                            gt.text = user.gender
                        }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.e("Firebase", "Lỗi khi truy cập dữ liệu người dùng: ${databaseError.message}")
                }
            })
        } else {
            Log.e("Firebase", "Người dùng chưa đăng nhập.")
        }


        usernameTextView.setOnClickListener { showEditDialog("Tên", usernameTextView) }
        tieusu.setOnClickListener { showEditDialog("Tiểu sử", tieusu) }
        gt.setOnClickListener { showGenderDialog() }

        // Thiết lập sự kiện cho nút quay lại
        val imageView2 = findViewById<ImageView>(R.id.imageView2)
        imageView2.setOnClickListener { finish() }
    }
    private fun chooseImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE) {
            val imageUri: Uri? = data?.data
            if (imageUri != null) {
                avatarImageView.setImageURI(imageUri) // Hiển thị ảnh trong ImageView
                uploadImageToFirebase(imageUri) // Gọi phương thức để tải ảnh lên Firebase
            }
        }
    }
    private fun uploadImageToFirebase(imageUri: Uri) {
        val userID = firebaseAuth.currentUser?.uid ?: return
        val fileReference = storageReference.child("avatars/$userID.jpg") // Đường dẫn lưu trữ ảnh

        fileReference.putFile(imageUri)
            .addOnSuccessListener {
                fileReference.downloadUrl.addOnSuccessListener { uri ->
                    Log.d("Firebase", "Ảnh đã được tải lên thành công: $uri")
                    updateAvatarUrlInDatabase(uri.toString()) // Cập nhật URL vào Firebase Realtime Database
                }
            }
            .addOnFailureListener { e ->
                Log.e("Firebase", "Tải lên ảnh thất bại: ${e.message}")

            }
    }
    private fun updateAvatarUrlInDatabase(avatarUrl: String) {
        val userID = firebaseAuth.currentUser?.uid ?: return
        database.child(userID).child("img").setValue(avatarUrl)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("Firebase", "URL avatar đã được cập nhật.")
                } else {
                    Log.e("Firebase", "Cập nhật URL avatar thất bại: ${task.exception?.message}")
                }
            }
    }
    private fun showEditDialog(fieldName: String, textView: TextView) {
        // Tạo AlertDialog
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Chỉnh sửa $fieldName")

        // Tạo EditText trong dialog
        val editText = EditText(this)
        editText.setText(textView.text)
        builder.setView(editText)

        // Thiết lập nút OK
        builder.setPositiveButton("OK") { dialog, _ ->
            val newValue = editText.text.toString()
            textView.text = newValue // Cập nhật TextView

            // Cập nhật vào Firebase
            updateUserField(fieldName, newValue) // Gọi phương thức cập nhật
            dialog.dismiss()
        }

        // Thiết lập nút Hủy
        builder.setNegativeButton("Hủy") { dialog, _ -> dialog.cancel() }

        builder.show()
    }

    private fun showGenderDialog() {
        val options = arrayOf("Nam", "Nữ", "Khác")


        val builder = AlertDialog.Builder(this)
        builder.setTitle("Chọn giới tính")
            .setItems(options) { dialog, which ->
                val selectedGender = options[which]
                val genderTextView = findViewById<TextView>(R.id.gt)
                genderTextView.text = selectedGender
                updateUserField("Giới tính", selectedGender)
            }
            .setNegativeButton("Hủy") { dialog, _ -> dialog.dismiss() }

        builder.show()
    }

    private fun updateUserField(fieldName: String, value: String) {
        val userID = firebaseAuth.currentUser?.uid ?: return
        val fieldKey = when (fieldName) {
            "Tên" -> "name"
            "Tiểu sử" -> "biography"
            "Giới tính" -> "gender"
            else -> return
        }

        database.child(userID).child(fieldKey).setValue(value).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d("Firebase", "$fieldKey đã được cập nhật.")
            } else {
                Log.e("Firebase", "Cập nhật $fieldKey thất bại: ${task.exception?.message}")
            }
        }
    }
}
