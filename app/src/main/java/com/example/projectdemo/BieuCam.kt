package com.example.projectdemo

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class BieuCam : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_bieu_cam)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val itemList = listOf(
            Item(R.drawable.haohung, "Hào hứng"),
            Item(R.drawable.buon, "Buồn bã"),
            Item(R.drawable.damduoi, "Đắm đuối"),
            Item(R.drawable.lanhlung, "Lạnh lùng"),
            Item(R.drawable.suynghi, "Suy nghĩ"),
            Item(R.drawable.vuive, "Vui vẻ"),
            Item(R.drawable.binhthuong, "Bình thường"),
            Item(R.drawable.gianhduong, "Giận dữ"),
            Item(R.drawable.wow, "Ngạc nhiên"),
            Item(R.drawable.thuongthuong, "Thương thương"),
            Item(R.drawable.daudau, "Đau đầu"),
            Item(R.drawable.thich, "Thích"),
            Item(R.drawable.tiectung, "Tiệc tùng"),
            Item(R.drawable.nenbuon, "Nén buồn"),
            Item(R.drawable.buonngu, "Buồn ngủ"),
            Item(R.drawable.mimcuoi, "Mỉm cười")
        )

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerview)
        recyclerView.layoutManager = GridLayoutManager(this, 2)

        val adapter = MyAdapter(itemList) { item ->

            val intent = Intent()
            intent.putExtra("text", "-Đang cảm thấy ${item.text}")
            intent.putExtra("imageResId", item.imageResId)
            setResult(RESULT_OK, intent)
            finish()
        }


        recyclerView.adapter = adapter
    }
}