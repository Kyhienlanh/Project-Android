package com.example.projectdemo

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

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
        val colorStateList = ColorStateList(
            arrayOf(
                intArrayOf(android.R.attr.state_checked), // Trạng thái được chọn
                intArrayOf(-android.R.attr.state_checked) // Trạng thái không được chọn
            ),
            intArrayOf(
                Color.parseColor("#4FC3F7"), // Màu cho icon khi được chọn
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
                    replaceFragment(NewsFeedFragment())
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
}