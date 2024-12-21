import android.util.Log
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.Call
import retrofit2.Callback
import retrofit2.http.Query



// Request model
data class RequestBody(
    val contents: List<Contents>
)

data class Contents(
    val parts: List<Parts>
)

data class Parts(
    val text: String
)

// Response model
data class Response(
    val candidates: List<Candidate>,
    val usageMetadata: UsageMetadata,
    val modelVersion: String
)

data class Candidate(
    val content: Content,
    val finishReason: String,
    val avgLogprobs: Double
)

data class Content(
    val parts: List<Part>,
    val role: String
)

data class Part(
    val text: String
)

data class UsageMetadata(
    val promptTokenCount: Int,
    val candidatesTokenCount: Int,
    val totalTokenCount: Int
)

// Retrofit interface
interface ApiService {
    @GET("")
    suspend fun getResponse(): Response

    @POST("models/gemini-1.5-flash:generateContent") // Endpoint đầy đủ sẽ có thêm query string
    fun sendRequest(
        @Body requestBody: RequestBody,
        @Query("key") apiKey: String = "AIzaSyAxegf1lTHCEehTxfCU3xnELpQypaLyaqE"
    ): Call<Response>

}

// Retrofit instance

object RetrofitInstance {

    val loggingInterceptor = HttpLoggingInterceptor()
    val client = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()

    // BASE_URL chỉ chứa phần chính của URL mà không có query string
    private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/"

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)  // Đảm bảo BASE_URL kết thúc bằng dấu '/'
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val apiService: ApiService = retrofit.create(ApiService::class.java)
}

suspend fun getParsedData(): Response? {
    val response = RetrofitInstance.apiService.getResponse()

    // In ra kết quả để kiểm tra
    println(response)

    return response
}

// Example function to send request
//fun sendRequest() {
//    val parts = listOf(Parts("Write a story about a magic backpack."))
//    val contents = listOf(Contents(parts))
//    val requestBody = RequestBody(contents)
//     RetrofitInstance.apiService.sendRequest(requestBody).enqueue(object : Callback<Response> {
//        override fun onResponse(call: Call<Response>, response: retrofit2.Response<Response>) {
//            if (response.isSuccessful) {
//                // Xử lý kết quả thành công
//                println("Response: ${response.body()}")
//            } else {
//                // Xử lý lỗi
//                println("Error: ${response.message()}")
//            }
//        }
//
//        override fun onFailure(call: Call<Response>, t: Throwable) {
//            // Xử lý lỗi kết nối
//            println("Failure: ${t.message}")
//        }
//    })
//
//    return
//}



fun sendRequest(Text:String,callback: (Response?) -> Unit) {
    val parts = listOf(Parts(Text)) // Sử dụng giá trị truyền vào
    val contents = listOf(Contents(parts))
    val requestBody = RequestBody(contents)

    RetrofitInstance.apiService.sendRequest(requestBody).enqueue(object : Callback<Response> {
        override fun onResponse(call: Call<Response>, response: retrofit2.Response<Response>) {
            if (response.isSuccessful) {
                // Pass the response body to the callback
                callback(response.body())
            } else {
                // Handle error and pass null to the callback
                println("Error: ${response.message()}")
                callback(null)
            }
        }

        override fun onFailure(call: Call<Response>, t: Throwable) {
            // Handle connection error and pass null to the callback
            println("Failure: ${t.message}")
            callback(null)
        }
    })
}



// Example function to parse JSON
fun parseJson(jsonString: String): Response {
    return Gson().fromJson(jsonString, Response::class.java)
}

