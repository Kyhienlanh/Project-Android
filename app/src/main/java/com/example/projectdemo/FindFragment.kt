package com.example.projectdemo

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
 * Use the [FindFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class FindFragment : Fragment(),OnClickActionListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var recyclerView: RecyclerView
    private lateinit var searchEditText: EditText
    private lateinit var userAdapter: UserAdapter
    private lateinit var firebaseAuth: FirebaseAuth
    private val userList = mutableListOf<User>()
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
        var view= inflater.inflate(R.layout.fragment_find, container, false)
        searchEditText = view.findViewById(R.id.etSearch)
        recyclerView = view.findViewById(R.id.recyclerViewUsers)
        userAdapter = UserAdapter(userList,this)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = userAdapter
        firebaseAuth = FirebaseAuth.getInstance()
        var tvSearchHistory=view.findViewById<TextView>(R.id.tvSearchHistory)
        var UserID=firebaseAuth.currentUser?.uid.toString()
        getUserSearchHistory(UserID) { historyList ->
            for (user in historyList) {
              userList.add(user)
            }
            tvSearchHistory.visibility=View.VISIBLE
            userAdapter.updateList(userList)
        }

        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                tvSearchHistory.visibility=View.GONE
                val query = s.toString().trim()
                if (query.isNotEmpty()) {
                    searchUsers(query)
                } else {
                    userList.clear()
                    userAdapter.updateList(userList)
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
        return view
    }

    override fun onClicked(user: User) {
        var UserID=firebaseAuth.currentUser?.uid.toString()
        if(UserID==user.userID){
            var ProfileFragment=ProfileFragment()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.framelayout, ProfileFragment)
                .addToBackStack(null)
                .commit()
        }
        else{
            val bundle = Bundle()
            bundle.putSerializable("post_data", user.userID)
            val intent = Intent(requireContext(), TrangCaNhan::class.java)
            intent.putExtras(bundle)
            saveUserSearchHistory(UserID,user)
            startActivity(intent)
        }
    }
    private fun searchUsers(keyword: String) {
        val usersRef = FirebaseDatabase.getInstance().getReference("Users")

        usersRef.orderByChild("name")
            .startAt(keyword)
            .endAt(keyword + "\uf8ff")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    userList.clear()
                    if (snapshot.exists()) {
                        for (userSnapshot in snapshot.children) {
                            val user = userSnapshot.getValue(User::class.java)
                            user?.let { userList.add(it) }
                        }
                        userAdapter.updateList(userList)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(requireContext(), "Lỗi: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }
    fun saveUserSearchHistory(currentUserID: String, searchedUser: User) {
        // Tham chiếu Firebase Database
        val database: DatabaseReference = FirebaseDatabase.getInstance().reference

        // Tạo khóa duy nhất bằng timestamp
        val searchKey = System.currentTimeMillis().toString()

        // Lưu đối tượng User vào SearchHistory -> currentUserID -> searchKey
        database.child("SearchHistory")
            .child(currentUserID)
            .child(searchKey)
            .setValue(searchedUser)
            .addOnSuccessListener {
                println("Đã lưu lịch sử tìm kiếm thành công!")
            }
            .addOnFailureListener { exception ->
                println("Lỗi khi lưu lịch sử tìm kiếm: ${exception.message}")
            }
    }
    fun getUserSearchHistory(currentUserID: String, callback: (List<User>) -> Unit) {
        val database: DatabaseReference = FirebaseDatabase.getInstance().reference

        database.child("SearchHistory").child(currentUserID)
            .get()
            .addOnSuccessListener { snapshot ->
                val searchList = mutableListOf<User>()
                for (child in snapshot.children) {
                    val user = child.getValue(User::class.java)
                    user?.let { searchList.add(it) }
                }
                callback(searchList)
            }
            .addOnFailureListener { exception ->
                println("Lỗi khi đọc lịch sử tìm kiếm: ${exception.message}")
            }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FindFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FindFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}