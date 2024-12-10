package com.example.projectdemo

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() {
    private lateinit var postAdapter: PostAdapter
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // Khởi tạo RecyclerView
//        recyclerView = view.findViewById(R.id.postsRecyclerView)
//        recyclerView.layoutManager = LinearLayoutManager(context)

        // Tạo danh sách mẫu cho các bài đăng
//        val postList = listOf(
//            Post("user1", "avatar_url", "post_url", 120, "First post content"),
//            Post("user2", "avatar_url", "post_url", 85, "Second post content"),
//            Post("user3", "avatar_url", "post_url", 105, "Thirst post content")
//        )

        // Gán adapter cho RecyclerView
//        postAdapter = PostAdapter(postList)
//        recyclerView.adapter = postAdapter

        return view
    }
}
