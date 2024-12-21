package com.example.projectdemo

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import sendRequest
class ChatBotActivity3 : AppCompatActivity() {
    private lateinit var editTextOutput: TextView

    private lateinit var Text: EditText


    private lateinit var button2: Button

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var database: FirebaseDatabase



    private lateinit var recyclerView: RecyclerView
    private lateinit var messageAdapter: MessageAdapter1
    private lateinit var messageList: MutableList<Message>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_chat_bot3)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize Firebase
        firebaseAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        recyclerView = findViewById(R.id.recyclerView)
        val userIDCurrent=firebaseAuth.currentUser?.uid.toString()
        messageList = mutableListOf()

        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        messageAdapter = MessageAdapter1(messageList,userIDCurrent)
        recyclerView.adapter = messageAdapter
        getMessages()
        Text = findViewById(R.id.ChatAI)
        button2 = findViewById(R.id.button2)


        button2.setOnClickListener {
            test()
        }
    }

    fun test() {
        val text = Text.text.toString()
        val userId = firebaseAuth.currentUser?.uid ?: "unknown_user"

        saveMessage(userId, text)

        sendRequest(text) { response ->
            if (response != null) {
                // Handle the successful response
                runOnUiThread {

                    val botResponse = response.candidates[0].content.parts[0].text
                    println("Got response: $response")

                    saveMessage("Bot", botResponse)
                }
            } else {
                // Handle failure
                runOnUiThread {
                    println("Failed to get a response.")
                }
            }
        }
    }

    private fun saveMessage(sender: String, messageText: String) {
        val userId = firebaseAuth.currentUser?.uid ?: "unknown_user"
        val message = Message(sender, messageText, System.currentTimeMillis().toString())
        val messagesRef = database.getReference("messages").child(userId)

        // Push the message to Firebase Realtime Database
        messagesRef.push().setValue(message)
            .addOnSuccessListener {
                println("Message saved successfully!")
            }
            .addOnFailureListener { e ->
                println("Failed to save message: ${e.message}")
            }
    }

    private fun getMessages() {
        val userId = firebaseAuth.currentUser?.uid ?: "unknown_user"
        val messagesRef = database.getReference("messages").child(userId)

        // Đọc tất cả tin nhắn của người dùng hiện tại
        messagesRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val messages = mutableListOf<Message>()

                // Lặp qua các tin nhắn
                for (messageSnapshot in snapshot.children) {
                    val message = messageSnapshot.getValue(Message::class.java)
                    message?.let {
                        messages.add(it)
                    }
                }

                // Cập nhật danh sách tin nhắn và thông báo adapter cập nhật
                messageList.clear() // Xóa danh sách cũ (nếu có)
                messageList.addAll(messages)
                messageAdapter.notifyDataSetChanged() // Cập nhật RecyclerView
            }

            override fun onCancelled(error: DatabaseError) {
                // Xử lý lỗi khi lấy dữ liệu
                println("Failed to read messages: ${error.message}")
            }
        })
    }





}
