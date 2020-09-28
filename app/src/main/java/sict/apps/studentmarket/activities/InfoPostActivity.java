package sict.apps.studentmarket.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.text.Format;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import sict.apps.studentmarket.R;
import sict.apps.studentmarket.adapters.ListImagesAdapter;
import sict.apps.studentmarket.apihelper.ApiConnect;
import sict.apps.studentmarket.models.Content;
import sict.apps.studentmarket.models.Notification;
import sict.apps.studentmarket.models.React;
import sict.apps.studentmarket.models.User;
import sict.apps.studentmarket.services.APIService;
import sict.apps.studentmarket.ultil.MessageNotification;
import sict.apps.studentmarket.ultil.MySocket;
import sict.apps.studentmarket.ultil.MySqlite;
import sict.apps.studentmarket.ultil.Server;

public class InfoPostActivity extends AppCompatActivity implements View.OnClickListener, ListImagesAdapter.OnClickImageShow {
    private static final String TAG = InfoPostActivity.class.getSimpleName();
    private static final int REQUEST_CODE = 123;
    private TextView productName;
    private TextView productPrice;
    private ImageView productImage;
    private TextView u_address;
    private TextView time_stamp;
    private ListImagesAdapter adapter;
    private RecyclerView recyclerView;
    private FloatingActionButton fab_messenger;
    private ImageView btn_favorite;
    private Toolbar toolbar;
    private Button btnBuy;
    private String name;
    private String u_id;
    private String p_id;
    private String c_id;
    private String path;
    private byte quantity=1;
    private double price;
    private double total_price;
    private MySqlite database;
    private Cursor cs;
    private String token = "";
    private String userId = "";
    private String paths = "";
    private String username= "";
    private long phone;
    private boolean gender;
    private String email = "";
    private long timestamp;
    private String address;
    private List<String> lsPaths = new ArrayList<>();
    private String sellerName;
    private String objPost = null;
    private Socket mSocket;
    private String content;
    private int num_like;
    private String post_id;
    private TextView numLike;
    private User user = null;
    private ApiLoad apiListener;
    private int likes;
    private boolean love = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_info);
        apiListener = new ApiLoad();
        loadData();
        MySocket socket = (MySocket) getApplication();
        mSocket = socket.getSocket();
        mSocket.connect();
        mSocket.on("emit-numLike", onNewReact);
        mSocket.on("emit-notify", onNotification);
        mapping();
        initToolbar();
        objPost = getIntent().getStringExtra("current_post");
        if(objPost!=null){
            try {
                Gson gson = new Gson();
                JSONObject object = new JSONObject(objPost);
                u_id = object.getString("userId");
                p_id = object.getString("_id");
                c_id = object.getString("categoryId");
                name = object.getString("product_name");
                price = object.getDouble("product_price");
                path = object.getString("product_imagePath");
                paths = object.getString("product_imageList");
                timestamp = object.getLong("timestamp");
                address = object.getString("address");
                likes = object.getInt("likes");
                Type type = new TypeToken<List<String>>(){}.getType();
                lsPaths = gson.fromJson(paths, type);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.d(TAG, "onCreate: "+u_id);
            total_price = quantity*price;
            setView();
            catchEvent();
        }
    }

    private void initToolbar() {
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setDisplayShowTitleEnabled(false);
    }

    private void setView() {
        NumberFormat formatter = new DecimalFormat("#,###");
        Format format = new SimpleDateFormat("EEE, dd/MM/yyyy");
        String time = format.format(timestamp);
        u_address.setText(address);
        time_stamp.setText(time);
        productName.setText(name);
        numLike.setText(String.valueOf(likes));
        productPrice.setText(formatter.format(price));
        Picasso.get()
                .load(path)
                .into(productImage);
        initRecyclerView();
    }

    private void initRecyclerView() {
        //list categories
        adapter = new ListImagesAdapter(this, lsPaths);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
    }
    private void catchEvent() {
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        btn_favorite.setOnClickListener(this);
        adapter.setOnClickShowListener(this);
        if(u_id!=null) {
            if (u_id.equals(user.get_id())) {
                fab_messenger.hide();
                btnBuy.setVisibility(View.INVISIBLE);
            } else {
                btnBuy.setOnClickListener(this);
                fab_messenger.setOnClickListener(this);
            }
        }
    }

    private void loadData(){
        if(user == null){
            database = new MySqlite(this);
            cs = database.get("SELECT * FROM authorization");
            if (cs.moveToNext()) {
                token = cs.getString(0);
                userId = cs.getString(1);
                username = cs.getString(2);
                phone = cs.getInt(3);
                gender = cs.getInt(4) > 0 ? true: false;
                email = cs.getString(5);
                user = new User(token, userId, username, phone, gender, email);
                Log.d(TAG, "loadData: "+username);
            }else{
                user = new User("", "GUEST", "", 0, true, "");
                Log.d(TAG, "loadData: "+username);
            }
            cs.close();
            database.close();
        }
    }

    private void mapping() {
        toolbar = (Toolbar) findViewById(R.id.product_detail_toolbar);
        productName = (TextView) findViewById(R.id.productName);
        productPrice = (TextView) findViewById(R.id.productPrice);
        productImage = (ImageView) findViewById(R.id.img_view);
        btnBuy = (Button) findViewById(R.id.btn_buy);
        recyclerView = (RecyclerView) findViewById(R.id.images_recyclerview);
        fab_messenger = (FloatingActionButton) findViewById(R.id.fab_messenger);
        time_stamp = (TextView) findViewById(R.id.timeStamp);
        u_address = (TextView) findViewById(R.id.address);
        btn_favorite = (ImageView) findViewById(R.id.btn_favorite);
        numLike = (TextView) findViewById(R.id.num_like);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_product_detail, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.nav_shopping_cart:
                Intent intentCart = new Intent(this, OrderActivity.class);
                startActivity(intentCart);
                break;
            case R.id.nav_home:
                finish();
                break;
            case R.id.nav_search:
                Intent intentSearch = new Intent(this, SearchActivity.class);
                startActivity(intentSearch);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void addToCart(){
        cs = database.get(String.format("SELECT * FROM cart WHERE  userId = '%s' AND productId = '%s'",user.get_id(), p_id));
        if(!cs.moveToNext()) {
            database.post(String.format("INSERT INTO cart VALUES(null, '%s', '%s', '%s', '%s', '%s', '%d','%f', '%f')",
                    user.get_id(), p_id, c_id, name, path, quantity, price, total_price));
            Log.d(TAG, String.format("INSERT INTO cart VALUES(null, '%s', '%s', '%s', '%s', '%s', '%d','%f', '%f')",
                                        user.get_id(), p_id, c_id, name, path, quantity, price, total_price));
            Toast.makeText(this, "This product has been added to the cart.", Toast.LENGTH_SHORT).show();
        }
        else{
            Intent intent = new Intent(this, OrderActivity.class);
            startActivity(intent);
            Toast.makeText(this, "This product already exists in the cart.", Toast.LENGTH_SHORT).show();
        }
        cs.close();
        database.close();
    }

    @Override
    public void showImage(int position) {
        String imgPath = Server.API_URL+ lsPaths.get(position);
        Picasso.get()
                .load(imgPath)
                .into(productImage);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data !=null){
            String objUser = data.getStringExtra("objUser");
            Log.d(TAG, "onActivityResult: "+objUser);
                Gson gson = new Gson();
                Type type = new TypeToken<User>(){}.getType();
                user = gson.fromJson(objUser, type);
                if(u_id.equals(user.get_id())){
                    fab_messenger.hide();
                }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_buy:
                addToCart();
                break;

            case R.id.btn_favorite:
                if(love == true){
                    apiListener.protectFeatureReact(user.getToken());
                    btn_favorite.setImageResource(R.drawable.ic_favorite_red_32dp);
                    love = false;
                }else {
                    apiListener.protectFeatureReact(user.getToken());
                    btn_favorite.setImageResource(R.drawable.ic_favorite_border_red_32dp);
                    love = true;
                }
                break;

            case R.id.fab_messenger:
                apiListener.protectFeatureMessage(user.getToken(), u_id, user.get_id());
                break;

            default:
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
                            post_id = data.getString("postId");
                            num_like = data.getInt("num");
                            if(p_id.equals(post_id)){
                                numLike.setText(String.valueOf(num_like));
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
                            String sellerId = data.getString("userId");
                            content = data.getString("content");
                            if(sellerId.equals(user.get_id())){
                                Toast.makeText(InfoPostActivity.this, content, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
        }
    };
    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSocket.off("like-post", onNewReact);
        mSocket.off("notification", onNotification);
    }

    @Override
    public void onResume() {
        super.onResume();
        mSocket.on("emit-numLike", onNewReact);
        mSocket.on("emit-notify", onNotification);
    }
    class ApiLoad extends ApiConnect {
        private final String TAG = ApiLoad.class.getSimpleName();
        private Retrofit retrofit = getRetrofit();
        private APIService apiService = retrofit.create(APIService.class);

        public void protectFeatureMessage(String token, String sl_id, String u_id){
            Call<User> call = apiService.getUserProfile("Bearer " + token, sl_id);
            call.enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    if(response.isSuccessful()){
                        sellerName = response.body().getUsername();
                        Intent i = new Intent(InfoPostActivity.this, MessengerActivity.class);
                        i.putExtra("sellerName", sellerName);
                        i.putExtra("sl_id", sl_id);
                        i.putExtra("userId", u_id);
                        startActivity(i);
                    }else{
                        Intent i = new Intent(InfoPostActivity.this, AuthenticationActivity.class);
                        startActivityForResult(i, REQUEST_CODE);
                    }
                    call.cancel();
                }

                @Override
                public void onFailure(Call<User> call, Throwable throwable) {
                    Log.e(TAG, throwable.toString());
                    call.cancel();
                }
            });
        }
        public void protectFeatureReact(String token){
            Call<ResponseBody> call = apiService.getAccessToken("Bearer "+token);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if(response.isSuccessful()){
                        Gson gson = new Gson();
                        String postId= p_id;
                        String sellerId= u_id;
                        content = user.getUsername() + MessageNotification.LIKE;
                        if(user.get_id().equals("GUEST")){
                            Toast.makeText(InfoPostActivity.this, "Vui lòng đăng nhập để thực hiện tính năng!", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(InfoPostActivity.this, "Vui lòng đăng nhập để thực hiện tính năng!...", Toast.LENGTH_SHORT).show();
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
