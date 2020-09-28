package sict.apps.studentmarket.services;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import sict.apps.studentmarket.models.Category;
import sict.apps.studentmarket.models.Notification;
import sict.apps.studentmarket.models.Post;
import sict.apps.studentmarket.models.React;
import sict.apps.studentmarket.models.User;

public interface APIService {
    @GET("/api/categories")
    Call<List<Category>> getCategories();
    @GET("/api/get-category/{category_name}")
    Call<Category> getCategory(@Path("category_name") String name);
    @GET("/api/posts/new")
    Call<List<Post>> getNewPosts();
    @GET("/api/posts/{categoryId}")
    Call<List<Post>> getPosts(@Path("categoryId") String id);

    @GET("/user")
    Call<ResponseBody> getAccessToken(@Header("Authorization") String token);

    @GET("/user/profile/{userId}")
    Call<User> getUserProfile(@Header("Authorization") String token,
                              @Path("userId") String userId);

    @GET("/user/posts/{userId}")
    Call<List<Post>> getUserPosts(@Header("Authorization") String token,
                                  @Path("userId") String id);

    @GET("/api/post/react/likes/{postId}")
    Call<List<React>> getReacts(@Path("postId") String postId);

    @GET("/user/notifications/{userId}")
    Call<List<Notification>> getNotification(@Header("Authorization") String token,
                                        @Path("userId") String id);

    @GET("/api/post/{postId}")
    Call<List<Post>> getPost(@Path("postId") String postId);

    @POST("/auth/signin")
    Call<ResponseBody> postSignin(@Body User user);

    @POST("/auth/signup")
    Call<ResponseBody> postSignup(@Body User user);

    @Multipart
    @POST("/user/create/post")
    Call<Post> postCreatePost(
            @Header("Authorization") String token,
            @Part List<MultipartBody.Part> productImage,
            @Part("categoryId") RequestBody categoryId,
            @Part("product_name") RequestBody product_name,
            @Part("product_price") RequestBody product_price,
            @Part("product_description") RequestBody product_description,
            @Part("userId") RequestBody userId,
            @Part("address") RequestBody address,
            @Part("email") RequestBody email,
            @Part("phone") RequestBody phone);

    @DELETE("/user/delete/post/{postId}")
    Call<ResponseBody> deletePost(@Header("Authorization") String token, @Path("postId") String postId);

    @PUT("/user/update/post/{postId}")
    Call<ResponseBody> updatePost(
            @Header("Authorization") String token,
            @Path("postId") String postId,
            @Body Post post);
}
