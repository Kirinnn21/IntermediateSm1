import com.dutaram.intermediatesm1.api.response.BasicResponse
import com.dutaram.intermediatesm1.api.response.ListStoriesResponse
import com.dutaram.intermediatesm1.api.response.LoginResponse
import com.dutaram.intermediatesm1.data.pref.UserLoginModel
import com.dutaram.intermediatesm1.data.pref.UserRegisterModel
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface ApiService {
    @POST("register")
    fun registerUser(
        @Body user: UserRegisterModel
    ): Call<BasicResponse>

    @POST("login")
    fun loginUser(
        @Body user: UserLoginModel
    ): Call<LoginResponse>

    @GET("stories")
    fun getAllStoriesWithLocation(
        @Header("Authorization") token: String,
        @Query("location") location: Int
    ): Call<ListStoriesResponse>

    @GET("stories")
    suspend fun getAllStoriesWithPaging(
        @Header("Authorization") token: String,
        @Query("page") page: Int,
        @Query("size") size: Int,
    ): ListStoriesResponse


    @Multipart
    @POST("stories")
    fun uploadStory(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part,
        @Part("description") description: String,
    ): Call<BasicResponse>


    @Multipart
    @POST("stories")
    fun uploadStoryWithLocation(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part,
        @Part("description") description: String,
        @Part("lat") lat: Float,
        @Part("lon") lon: Float
    ): Call<BasicResponse>
}