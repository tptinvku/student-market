package sict.apps.studentmarket.activities;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import sict.apps.studentmarket.R;
import sict.apps.studentmarket.adapters.ListPostsAdapter;
import sict.apps.studentmarket.apihelper.ApiConnect;
import sict.apps.studentmarket.models.Category;
import sict.apps.studentmarket.models.Content;
import sict.apps.studentmarket.models.Notification;
import sict.apps.studentmarket.models.Post;
import sict.apps.studentmarket.models.React;
import sict.apps.studentmarket.models.User;
import sict.apps.studentmarket.services.APIService;
import sict.apps.studentmarket.ultil.MessageNotification;
import sict.apps.studentmarket.ultil.MySocket;
import sict.apps.studentmarket.ultil.MySqlite;
import sict.apps.studentmarket.ultil.Server;

public class PostsActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = PostsActivity.class.getSimpleName();
    private Toolbar toolbar;
    private LinearLayout searchBox;
    private List<Post> lsPosts = new ArrayList<>();
    private ListPostsAdapter adapter;
    private RecyclerView productRecyclerview;
    private String objCategory;
    private String categoryId;
    ApiListener apiListener;
    private int num_like = 0;
    private User user;
    private Post post;
    private int current_position;
    private String content;
    private String objUser;
    private Socket mSocket;
    private String post_id;
    private String sellerId;
    private String p_id;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.posts_layout);
        apiListener = new ApiListener();
        Gson gson = new Gson();
        loadSqlite(gson);
        MySocket socket = (MySocket) getApplication();
        mSocket = socket.getSocket();
        mapping();
        initToolbar();
        objCategory = getIntent().getStringExtra("current_category");
        if(objCategory!=null){
            Type type = new TypeToken<Category>(){}.getType();
            Category category = gson.fromJson(objCategory, type);
            categoryId = category.get_id();
            apiListener.getListProducts(categoryId);
            initRecyclerView();
            catchEvent();
        }
    }
    private void mapping(){
        searchBox = (LinearLayout) findViewById(R.id.search_box);
        toolbar = (Toolbar) findViewById(R.id.product_toolbar);
        productRecyclerview = (RecyclerView) findViewById(R.id.products_recyclerview);
    }
    private void loadSqlite(Gson gson){
        MySqlite mySqlite = new MySqlite(this);
        Cursor cs = mySqlite.get("SELECT * FROM authorization");
        if(cs.moveToNext()){
            String tk = cs.getString(0);
            String _id = cs.getString(1);
            String name = cs.getString(2);
            int phone = cs.getInt(3);
            boolean gender = cs.getInt(4) > 0 ? true: false;
            String email = cs.getString(5);
            user = new User(tk, _id, name, phone, gender, email);
        }else{
            user = new User("", "GUEST", "", 0, true, "");
        }
        objUser = gson.toJson(user);
        Log.d(TAG, "loadSqlie: "+objUser);
        cs.close();
        mySqlite.close();
    }

    private void initRecyclerView(){
        adapter = new ListPostsAdapter(this, lsPosts, user.get_id());
        productRecyclerview.setAdapter(adapter);
        StaggeredGridLayoutManager gridLayoutManager =
                new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        gridLayoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
        productRecyclerview.setLayoutManager(gridLayoutManager);
        adapter.setOnItemClickListener(new ListPostsAdapter.OnItemClickListener(){
            @Override
            public void onItemClick(int position) {
                Gson gson = new Gson();
                post = lsPosts.get(position);
                String currentPost = gson.toJson(post);
                Intent intent = new Intent(PostsActivity.this, InfoPostActivity.class);
                intent.putExtra("current_post", currentPost);
                startActivity(intent);
            }

            @Override
            public void onFavorite(int position) {
                current_position = position;
                post_id = lsPosts.get(current_position).get_id();
                apiListener.protectFeature(user.getToken(), position);
            }
        });
    }
    private void initToolbar(){
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setDisplayShowTitleEnabled(false);
    }
    private void catchEvent(){
        searchBox.setOnClickListener(this);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.menu_products,menu);
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.search_box:
                Intent intentSearch = new Intent(PostsActivity.this, SearchActivity.class);
                startActivity(intentSearch);
                break;
        }
    }

    private Emitter.Listener onNewReact = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        JSONObject data = (JSONObject) args[0];
                        p_id = data.getString("postId");
                        num_like = data.getInt("num");
                        if(p_id.equals(post_id)){
                            lsPosts.get(current_position).setLikes(num_like);
                            adapter.notifyDataSetChanged();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

    private Emitter.Listener onNotification = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        JSONObject data = (JSONObject) args[0];
                        sellerId = data.getString("userId");
                        content = data.getString("content");
                        if(sellerId.equals(user.get_id())){
                            Toast.makeText(PostsActivity.this, content, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

    @Override
    public void onStart() {
        super.onStart();
        mSocket.connect();
        mSocket.on("emit-numLike", onNewReact);
        mSocket.on("emit-notify", onNotification);
        Log.d(TAG, "onStart: "+"START");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: "+"PAUSE");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSocket.disconnect();
        mSocket.off("like-post", onNewReact);
        mSocket.off("notification", onNotification);
        Log.d(TAG, "onDestroy: "+"DESTROY");
    }

    @Override
    public void onResume() {
        super.onResume();
        mSocket.connect();
        mSocket.on("emit-numLike", onNewReact);
        mSocket.on("emit-notify", onNotification);
        Log.d(TAG, "onResume: "+"RESUME");
    }

    class ApiListener extends ApiConnect{
        private final String TAG = ApiListener.class.getSimpleName();
        private Retrofit retrofit = getRetrofit();
        private APIService apiService = retrofit.create(APIService.class);

        public void getListProducts(String categoryId){
            apiService.getPosts(categoryId).enqueue(new Callback<List<Post>>() {
                @Override
                public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                    if(response!=null){
                        for (int i = 0; i < response.body().size() ; i++){
                            String userId = response.body().get(i).getUserId();
                            String postId = response.body().get(i).get_id();
                            String name = response.body().get(i).getProduct_name();
                            List<String> lsImage = response.body().get(i).getProduct_imageList();
                            String image = Server.API_URL + response.body().get(i).getProduct_imagePath();
                            String categoryId = response.body().get(i).getCategoryId();
                            double price = response.body().get(i).getProduct_price();
                            String description = response.body().get(i).getProduct_description();
                            String address = response.body().get(i).getAddress();
                            long timeStamp = response.body().get(i).getTimestamp();
                            int likes = response.body().get(i).getLikes();
                            Log.d(TAG, timeStamp + "");
                            lsPosts.add(new Post(postId, userId, categoryId, lsImage, image, name, price, description, address, timeStamp, likes));
                        }
                        adapter.notifyDataSetChanged();
                        call.cancel();
                    }
                }

                @Override
                public void onFailure(Call<List<Post>> call, Throwable throwable) {
                    Log.e(TAG, throwable.toString());
                }
            });
        }
        public void protectFeature(String token, int position){
            Call<ResponseBody> call = apiService.getAccessToken("Bearer "+token);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if(response.isSuccessful()){
                        Gson gson = new Gson();
                        String postId= lsPosts.get(position).get_id();
                        String sellerId= lsPosts.get(position).getUserId();
                        content = user.getUsername() + MessageNotification.LIKE;
                        if(user.get_id().equals("GUEST")){
                            Toast.makeText(PostsActivity.this, "Vui lòng đăng nhập để thực hiện tính năng!", Toast.LENGTH_SHORT).show();
                        }else{
                            React react = new React(postId, user.get_id());
                            List<Content> lsCt = new ArrayList<>();
                            lsCt.add(new Content(postId, user.get_id(), content));
                            Notification notification = new Notification(sellerId, lsCt);
                            String like = gson.toJson(react);
                            String notify = gson.toJson(notification);
                            mSocket.emit("like-post", like);
                            if(!sellerId.equals(user.get_id())){
                                mSocket.emit("notification", notify);
                            }
                        }
                    }else{
                        Toast.makeText(PostsActivity.this, "Vui lòng đăng nhập để thực hiện tính năng!...", Toast.LENGTH_SHORT).show();
                    }
                    call.cancel();
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable throwable) {
                    Log.e(TAG, throwable.toString());
                    call.cancel();
                }
            });
        }
    }
}
