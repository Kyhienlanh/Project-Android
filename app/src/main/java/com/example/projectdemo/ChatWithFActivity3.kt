package com.example.projectdemo

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
class ChatWithFActivity3 : AppCompatActivity() {
    private lateinit var avatarImageView: ImageView
    private lateinit var usernameTextView: TextView
    private lateinit var messagesRecyclerView: RecyclerView
    private lateinit var messageEditText: EditText
    private lateinit var sendButton: ImageView
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var userID:String
    private lateinit var messageAdapter:MessageAdapter
    private  var messages = mutableListOf<Message>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_chat_with_factivity3)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        firebaseAuth=FirebaseAuth.getInstance()
        avatarImageView = findViewById(R.id.avatarImageView)
        usernameTextView = findViewById(R.id.usernameTextView)
        messagesRecyclerView = findViewById(R.id.messagesRecyclerView)
        messageEditText = findViewById(R.id.messageEditText)
        sendButton = findViewById(R.id.sendButton)
        userID = intent.getStringExtra("UserID_data").toString()
        if (userID == null) {
            Log.d("UserInfo", "User ID is null")
            return
        }

        messagesRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        messageAdapter = MessageAdapter(messages)
        val userIDCurrent=firebaseAuth.currentUser?.uid.toString()
        messagesRecyclerView.adapter = messageAdapter
        getUserById(userID.toString())

        val chatID = generateChatID(userIDCurrent,userID)
        listenForMessages(chatID)
        sendButton.setOnClickListener {
            sendMessage()
        }
    }
    fun generateChatID(userID1: String, userID2: String): String {
        val sortedIDs = listOf(userID1, userID2).sorted()
        return sortedIDs[0] + "_" + sortedIDs[1]  // Sắp xếp và tạo chatID cố định
    }

    fun sendMessage() {
        val message = messageEditText.text.toString().trim()
        val userIDCurrent = firebaseAuth.currentUser?.uid.toString()
        if (message.isNotEmpty()) {
            val userID = userID  // Lấy userID từ đăng nhập hoặc intent
            val timestamp = System.currentTimeMillis().toString()

            // Sử dụng generateChatID để tạo chatID cố định
            val chatID = generateChatID(userIDCurrent, userID)

            val messageData = mapOf(
                "senderID" to userID,
                "text" to message,
                "timestamp" to timestamp
            )

            // Thêm tin nhắn vào Firebase Database
            val database = FirebaseDatabase.getInstance()
            val chatRef = database.getReference("chats").child(chatID)
            val messageRef = chatRef.push()  // Tạo ID ngẫu nhiên cho tin nhắn
            messageRef.setValue(messageData)

            // Xóa nội dung EditText sau khi gửi
            messageEditText.text.clear()
        }
    }


    fun listenForMessages(chatID: String) {
        val database = FirebaseDatabase.getInstance()
        val chatRef = database.getReference("chats").child(chatID)

        chatRef.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val message = snapshot.getValue(Message::class.java)
                if (message != null) {
                    messageAdapter.addMessage(message)
                    messagesRecyclerView.scrollToPosition(messages.size - 1)  // Cuộn đến tin nhắn mới nhất
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildRemoved(snapshot: DataSnapshot) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {}
        })
    }


    fun getUserById(userID: String) {
        // Khởi tạo tham chiếu tới Firebase Database
        val database = FirebaseDatabase.getInstance()
        val userRef = database.getReference("Users").child(userID)

        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val name = snapshot.child("name").getValue(String::class.java)
                    usernameTextView.text = name.toString()

                    val Linkimg = snapshot.child("img").getValue(String::class.java)
                    // Kiểm tra URL của ảnh và tải ảnh từ URL vào ImageView
                    if (!Linkimg.isNullOrEmpty()) {
                        Glide.with(this@ChatWithFActivity3)
                            .load(Linkimg)  // Tải ảnh từ URL
                            .placeholder(R.drawable.ic_launcher_background)
                            .error(R.drawable.circle_background)
                            .into(avatarImageView)
                    }
                } else {
                    Log.d("UserInfo", "User not found")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Xử lý lỗi nếu có
                Log.d("UserInfo", "Error: ${error.message}")
            }
        })
    }
}