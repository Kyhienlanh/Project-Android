package com.example.projectdemo

import android.app.Activity
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainActivity : AppCompatActivity() {
    private lateinit var bottomNavigationView :BottomNavigationView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        setOnlineStatus()
        val colorStateList = ColorStateList(
            arrayOf(
                intArrayOf(android.R.attr.state_checked), // Trạng thái được chọn
                intArrayOf(-android.R.attr.state_checked) // Trạng thái không được chọn
            ),
            intArrayOf(
                Color.parseColor("#1877F2"), // Màu cho icon khi được chọn
                Color.parseColor("#000000") // Màu cho icon khi không được chọn
            )
        )
        bottomNavigationView=findViewById(R.id.bottomNavigationView)
        bottomNavigationView.setItemIconTintList(colorStateList)
        bottomNavigationView.setOnItemSelectedListener { menuItem->
            when(menuItem.itemId){
                R.id.home->{

                    replaceFragment(HomeFragment())
                    true
                }
                R.id.search->{
                    replaceFragment(FindFragment())
                    true
                }
                R.id.NewsFeed->{

                    val intent = Intent(this, AddNewsFeed::class.java)
                    startActivity(intent)
                    true
                }
                R.id.nofications->{
                    replaceFragment(NotificationFragment())
                    true
                }
                R.id.Profile->{
                    replaceFragment(ProfileFragment())
                    true
                }
                else ->false

            }
        }
        replaceFragment(HomeFragment())


    }
    private fun replaceFragment(Fragment:Fragment){
        supportFragmentManager.beginTransaction().replace(R.id.framelayout,Fragment).commit()
    }

    fun setOnlineStatus() {
        val currentUserID = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val database = FirebaseDatabase.getInstance()
        val userStatusRef = database.getReference("Users/$currentUserID/avatar")

        // Lắng nghe trạng thái kết nối tới Firebase
        val connectedRef = database.getReference(".info/connected")
        connectedRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val connected = snapshot.getValue(Boolean::class.java) ?: false
                if (connected) {
                    // Nếu người dùng kết nối tới Firebase, đặt trạng thái online
                    userStatusRef.setValue("online")

                    // Thiết lập trạng thái offline khi người dùng ngắt kết nối
                    userStatusRef.onDisconnect().setValue("offline")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Xử lý lỗi nếu có
            }
        })
    }
}