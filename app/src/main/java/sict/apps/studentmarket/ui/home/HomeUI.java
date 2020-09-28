package sict.apps.studentmarket.ui.home;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import sict.apps.studentmarket.R;
import sict.apps.studentmarket.activities.MessengerManageActiviity;
import sict.apps.studentmarket.activities.OrderActivity;
import sict.apps.studentmarket.activities.InfoPostActivity;
import sict.apps.studentmarket.activities.PostsActivity;
import sict.apps.studentmarket.activities.SearchActivity;
import sict.apps.studentmarket.adapters.ListCategoriesAdapter;
import sict.apps.studentmarket.adapters.ListPostsAdapter;
import sict.apps.studentmarket.apihelper.ApiConnect;
import sict.apps.studentmarket.models.Category;
import sict.apps.studentmarket.models.Content;
import sict.apps.studentmarket.models.Notification;
import sict.apps.studentmarket.models.Post;
import sict.apps.studentmarket.models.React;
import sict.apps.studentmarket.models.User;
import sict.apps.studentmarket.services.APIService;
import sict.apps.studentmarket.ui.personal.PersonalUI;
import sict.apps.studentmarket.ultil.MessageNotification;
import sict.apps.studentmarket.ultil.MySocket;
import sict.apps.studentmarket.ultil.MySqlite;
import sict.apps.studentmarket.ultil.Server;
import sict.apps.studentmarket.vm.MainShareViewModel;

import static android.app.Activity.RESULT_OK;

public class HomeUI extends Fragment implements View.OnClickListener, ListPostsAdapter.OnItemClickListener {
    private static final String TAG = HomeUI.class.getSimpleName();
    private List<Category> lsCategories = new ArrayList<Category>();
    private List<Post> lsPosts = new ArrayList<Post>();
    public static ListCategoriesAdapter categoriesAdapter;
    public ListPostsAdapter productsAdapter;
    private RecyclerView categoryRecyclerView;
    private static RecyclerView newProductRecyclerView;
    private Toolbar toolbar;
    private  TextView mTitle;
    private LinearLayout searchBox;
    private ApiLoad apiListener;
    private User user;
    private int num_like = 0;
    private MainShareViewModel viewModel;
    private String objUser;
    private Post post;
    private Category category;
    private int current_position;
    private String content;
    private String p_id;
    private Socket mSocket;
    private String post_id;
    private static final int REQUEST_CODE = 123;

    public static HomeUI newInstance() {
       return new HomeUI();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.home_layout, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadData();
        mapping(view);
        initToolbar();
        initRecyclerView();
        catchEvent();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        MySocket socket = (MySocket) getActivity().getApplication();
        mSocket = socket.getSocket();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = new ViewModelProvider(getActivity()).get(MainShareViewModel.class);
    }


    private void mapping(View v){
        searchBox = (LinearLayout) v.findViewById(R.id.search_box);
        toolbar = (Toolbar) v.findViewById(R.id.home_toolbar);
        categoryRecyclerView = (RecyclerView) v.findViewById(R.id.categories_recyclerview);
        newProductRecyclerView = (RecyclerView) v.findViewById(R.id.new_product_recyclerview);
        mTitle = (TextView) v.findViewById(R.id.toolbar_title);

    }

    private void loadData(){
        connectApi();
        receiveData();
    }

    private void connectApi(){
        apiListener = new ApiLoad();
        apiListener.getListNewProducts();
    }
    private void receiveData(){
        Gson gson = new Gson();
        String lsCategoriesAsString = getActivity().getIntent().getStringExtra("lsCategories");
        Log.d(TAG, "list categories: " + lsCategoriesAsString);
        Type type_1 = new TypeToken<List<Category>>(){}.getType();
        lsCategories = gson.fromJson(lsCategoriesAsString, type_1);
        if(user==null){
            loadSqlite(gson);
        }
    }
    private void loadSqlite(Gson gson){
        MySqlite mySqlite = new MySqlite(getContext());
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
    private void catchEvent(){
        searchBox.setOnClickListener(this);
    }
    private void initToolbar(){
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        ActionBar actionbar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(false);
        actionbar.setDisplayShowTitleEnabled(false);
        mTitle.setText(R.string.app_name);
    }
    private void initRecyclerView(){
        //list categories
        categoriesAdapter =  new ListCategoriesAdapter(getContext(), lsCategories);
        categoryRecyclerView.setAdapter(categoriesAdapter);
        categoryRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        categoriesAdapter.setOnItemClickListener(new ListCategoriesAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Gson gson = new Gson();
                category = lsCategories.get(position);
                String currentCategory = gson.toJson(category);
                Intent intent = new Intent(getActivity(), PostsActivity.class);
                intent.putExtra("current_category", currentCategory);
                startActivity(intent);
            }
        });
        //list new products
        productsAdapter = new ListPostsAdapter(getContext(), lsPosts, user.get_id());
        newProductRecyclerView.setAdapter(productsAdapter);
        StaggeredGridLayoutManager gridLayoutManager =
                new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        gridLayoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
        newProductRecyclerView.setLayoutManager(gridLayoutManager);
        productsAdapter.setOnItemClickListener(this);
    }
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_shoppingcart,menu);
        super.onCreateOptionsMenu(menu, inflater);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.nav_shopping_cart:
                Intent intentCart = new Intent(getContext(), OrderActivity.class);
                startActivity(intentCart);
                break;
            case R.id.nav_messenger:
                Intent intentMessenger = new Intent(getContext(), MessengerManageActiviity.class);
                startActivityForResult(intentMessenger, REQUEST_CODE);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data !=null){
           Gson gson = new Gson();
           Type type = new TypeToken<User>(){}.getType();
           user = gson.fromJson(data.getStringExtra("objUser"), type);
           viewModel.setObjUser(data.getStringExtra("objUser"));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.search_box:
                Intent intent = new Intent(getActivity(), SearchActivity.class);
                startActivity(intent);
                break;
        }
    }

    private Emitter.Listener onNewReact = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            if(getActivity()!=null){
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject data = (JSONObject) args[0];
                            p_id = data.getString("postId");
                            if(p_id.equals(post_id)){
                                num_like = data.getInt("num");
                                lsPosts.get(current_position).setLikes(num_like);
                                productsAdapter.notifyDataSetChanged();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }else{
                return;
            }
        }
    };

    private Emitter.Listener onNotification = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            if(getActivity()!=null) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject data = (JSONObject) args[0];
                            String sellerId = data.getString("userId");
                            content = data.getString("content");
                            if(sellerId.equals(user.get_id())){
                                Toast.makeText(getContext(), content, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }else{
                return;
            }
        }
    };
    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            if(getActivity()!=null){
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject data = (JSONObject) args[0];
                            String content = data.getString("message");
                            Toast.makeText(getContext(), content, Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }else{
                return;
            }
        }
    };
    @Override
    public void onStart() {
        super.onStart();
        mSocket.connect();
        mSocket.on("emit-numLike", onNewReact);
        mSocket.on("emit-notify", onNotification);
        mSocket.on("server-send-message", onNewMessage);
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

    @Override
    public void onItemClick(int position) {
        Gson gson = new Gson();
        post = lsPosts.get(position);
        String currentPost = gson.toJson(post);
        Intent intent = new Intent(getActivity(), InfoPostActivity.class);
        intent.putExtra("current_post", currentPost);
        startActivity(intent);
    }

    @Override
    public void onFavorite(int position) {
        current_position = position;
        post_id = lsPosts.get(current_position).get_id();
        apiListener.protectFeature(user.getToken(), position);
    }

    class ApiLoad extends ApiConnect {
        private final String TAG = ApiLoad.class.getSimpleName();
        private Retrofit retrofit = getRetrofit();
        private APIService apiService = retrofit.create(APIService.class);

        public void getListNewProducts() {
            Call<List<Post>> call = apiService.getNewPosts();
            call.enqueue(new Callback<List<Post>>() {
                @Override
                public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                    if (response.isSuccessful()) {
                        for (int i = 0; i < response.body().size(); i++) {
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
                            post = new Post(postId, userId, categoryId, lsImage, image, name, price, description, address, timeStamp, likes);
                            lsPosts.add(post);
                        }
                        productsAdapter.notifyDataSetChanged();
                    }
                    call.cancel();
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
                            Toast.makeText(getContext(), "Vui lòng đăng nhập để thực hiện tính năng!", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(getContext(), "Vui lòng đăng nhập để thực hiện tính năng!...", Toast.LENGTH_SHORT).show();
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
