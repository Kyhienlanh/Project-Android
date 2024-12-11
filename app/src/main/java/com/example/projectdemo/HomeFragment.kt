package com.example.projectdemo

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
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
class HomeFragment : Fragment(), OnPostActionListener {
    private lateinit var postAdapter: PostAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var mainLayout: ConstraintLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // Khởi tạo RecyclerView
        recyclerView = view.findViewById(R.id.postsRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)
        mainLayout =view.findViewById(R.id.mainlayout)

        val samplePosts = listOf(
            Post(
                postID = "1",
                userID = "user_01",
                content = "Hôm nay là một ngày thật đẹp trời! 🌞",
                CamSucHoatDong = "Đi dạo công viên",
                GanTheNguoiKhac = "Bạn bè",
                MauNen = "#FFD700",
                imageURL = "https://th.bing.com/th/id/OIP.avb9nDfw3kq7NOoP0grM4wHaEK?rs=1&pid=ImgDetMain",
                timestamp = System.currentTimeMillis() - 3600000,
                likes = 123,
                commentsCount = 10,
                likedBy = listOf("user_02", "user_03"),
                sharedCount = 5,
                status = "public",
                location = "Công viên thành phố"
            ),
            Post(
                postID = "2",
                userID = "user_02",
                content = "Một bữa ăn thật ngon! 🍕🍔",
                CamSucHoatDong = "Ăn uống",
                imageURL = "https://th.bing.com/th/id/OIP.Uh5kcavlK6oBgFr9AJnSrgHaEK?w=1280&h=720&rs=1&pid=ImgDetMain",
                timestamp = System.currentTimeMillis() - 7200000,
                likes = 56,
                commentsCount = 4,
                likedBy = listOf("user_01", "user_04"),
                sharedCount = 2,
                status = "public",
                location = "Nhà hàng ABC"
            ),
            Post(
                postID = "3",
                userID = "user_03",
                content = "Thử thách leo núi đầu tiên trong đời! 🧗‍♂️",
                CamSucHoatDong = "Thể thao",
                imageURL = "https://th.bing.com/th/id/OIP.LUgS1OBIc6idv8x6hJ_G_wHaNN?w=236&h=421&c=7&o=5&pid=1.20",
                timestamp = System.currentTimeMillis() - 10800000,
                likes = 200,
                commentsCount = 25,
                likedBy = listOf("user_01", "user_02", "user_04"),
                sharedCount = 10,
                status = "private",
                location = "Núi XYZ"
            ),
            Post(
                postID = "4",
                userID = "user_04",
                content = "Chỉ là một ngày bình thường với tách cà phê ☕",
                timestamp = System.currentTimeMillis() - 21600000,
                likes = 32,
                commentsCount = 1,
                likedBy = listOf("user_03"),
                sharedCount = 0,
                status = "public",
                location = "Quán cà phê 123"
            ),
            Post(
                postID = "5",
                userID = "user_05",
                content = "Hãy sống thật tích cực và yêu thương nhau! ❤️",
                CamSucHoatDong = "Chia sẻ cảm xúc",
                timestamp = System.currentTimeMillis() - 43200000,
                likes = 150,
                commentsCount = 30,
                likedBy = listOf("user_01", "user_02", "user_03", "user_04"),
                sharedCount = 20,
                status = "public"
            )
        )

        // Khởi tạo adapter với dữ liệu giả lập và listener
        postAdapter = PostAdapter(samplePosts, this)
        recyclerView.adapter = postAdapter

        return view
    }

    // Implement the callback methods
    override fun onLikeClicked(post: Post) {
        Toast.makeText(context, "Liked post by: ${post.userID}", Toast.LENGTH_SHORT).show()
    }

    override fun onCommentClicked(post: Post) {
        Toast.makeText(context, "Comment on post by: ${post.userID}", Toast.LENGTH_SHORT).show()
    }

    override fun onShareClicked(post: Post) {
        Toast.makeText(context, "Shared post by: ${post.userID}", Toast.LENGTH_SHORT).show()
    }

    override fun onOptionsClicked(post: Post) {
        Toast.makeText(context, "Options clicked for post by: ${post.userID}", Toast.LENGTH_SHORT).show()
    }
}

