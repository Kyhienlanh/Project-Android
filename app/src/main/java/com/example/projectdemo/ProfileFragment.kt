package com.example.projectdemo

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.android.gms.cast.framework.media.ImagePicker
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfileFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var database: DatabaseReference

    private lateinit var usernameTextView: TextView
    private lateinit var postsTextView: TextView
    private lateinit var followersTextView: TextView
    private lateinit var followingTextView: TextView
    private lateinit var biographyTextView: TextView
    private lateinit var img: ShapeableImageView
    private lateinit var progressBar3: ProgressBar
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
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        // Khởi tạo Firebase
        firebaseAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().getReference("Users")

        // Khởi tạo các View
        usernameTextView = view.findViewById(R.id.usernameTextView)
        postsTextView = view.findViewById(R.id.postsTextView)
        followersTextView = view.findViewById(R.id.followersTextView)
        followingTextView = view.findViewById(R.id.followingTextView)
        biographyTextView = view.findViewById(R.id.biographyTextView)
        img = view.findViewById(R.id.avatarImageView)
        progressBar3=view.findViewById(R.id.progressBar3)
        // Lấy thông tin người dùng
        loadUserData()

        // Thiết lập sự kiện cho nút chỉnh sửa
        val editProfileButton = view.findViewById<Button>(R.id.editProfileButton)
        editProfileButton.setOnClickListener {
            startActivity(Intent(requireContext(), changeProfilenewMainActivity3::class.java))
        }

        return view
    }

    override fun onResume() {
        super.onResume()
        loadUserData() // Tải lại thông tin người dùng khi Fragment hiển thị
    }

    private fun loadUserData() {
        progressBar3.visibility = View.VISIBLE
        val userID = firebaseAuth.currentUser?.uid

        // Lấy dữ liệu người dùng từ Firebase
        if (userID != null) {
            database.child(userID).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        val user = dataSnapshot.getValue(User::class.java)
                        user?.let { updateUI(it) } // Cập nhật UI nếu người dùng không null
                        progressBar3.visibility = View.GONE
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.e("Firebase", "Lỗi khi truy cập dữ liệu người dùng: ${databaseError.message}")
                    progressBar3.visibility = View.GONE
                }
            })
        } else {
            Log.e("Firebase", "Người dùng chưa đăng nhập.")
            progressBar3.visibility = View.GONE
        }
    }

    private fun updateUI(user: User) {
        usernameTextView.text = user.name
        postsTextView.text = "${user.post} post"
        followersTextView.text = "${user.followers} follower"
        followingTextView.text = "${user.following} following"
        biographyTextView.text = user.biography

        // Hiển thị avatar
        val avatarUrl = user.img
        if (!avatarUrl.isNullOrEmpty()) {
            Glide.with(requireActivity())
                .load(avatarUrl)
                .placeholder(R.drawable.circle_background)
                .into(img)

        }
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProfileFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
