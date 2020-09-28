package sict.apps.studentmarket.ui.dashboard_seller;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import sict.apps.studentmarket.R;
import sict.apps.studentmarket.activities.EditPostActivity;
import sict.apps.studentmarket.adapters.ListSellingAdapter;
import sict.apps.studentmarket.apihelper.ApiConnect;
import sict.apps.studentmarket.models.Category;
import sict.apps.studentmarket.models.Contact;
import sict.apps.studentmarket.models.Post;
import sict.apps.studentmarket.models.User;
import sict.apps.studentmarket.services.APIService;
import sict.apps.studentmarket.ultil.Server;
import sict.apps.studentmarket.vm.MainShareViewModel;

public class Selling extends Fragment {
    private static final String TAG = Selling.class.getSimpleName();
    private ListSellingAdapter adapter;
    private List<Post> lsPosts = new ArrayList<>();
    private RecyclerView sellingRecyclerview;
    private ApiLoad apiLoad;
    private MainShareViewModel viewModel;
    private User user;
    private List<Category> categories;
    private String objUser;
    private Post post;
    private SwipeRefreshLayout swipeRefreshLayout;
    public static Selling newInstance(){
        return new Selling();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.selling_layout, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        apiLoad = new ApiLoad();
        mapping(view);
        catchEvent();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        receiveData();
        viewModel = new ViewModelProvider(getActivity()).get(MainShareViewModel.class);
        viewModel.getObjUser().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                objUser = s;
                Log.d(TAG, "object user: "+s);
                Gson gson = new Gson();
                Type type = new TypeToken<User>(){}.getType();
                user = gson.fromJson(s, type);
                if(user==null){
                    user = new User("", "GUEST", "", 0, true, "");
                }
            }
        });
        viewModel.getPosts().observe(getViewLifecycleOwner(), new Observer<List<Post>>() {
            @Override
            public void onChanged(List<Post> posts) {
                lsPosts = posts;
//                Toast.makeText(getContext(), ""+lsPosts.size(), Toast.LENGTH_SHORT).show();
                initRecyclerView(lsPosts);
            }
        });
    }
    private void receiveData(){
        Gson gson = new Gson();
        String lsCategoriesAsString = getActivity().getIntent().getStringExtra("lsCategories");
        Log.d(TAG, "list categories: " + lsCategoriesAsString);
        Type type = new TypeToken<List<Category>>(){}.getType();
        categories = gson.fromJson(lsCategoriesAsString, type);
        Log.d(TAG, "receiveData: "+lsCategoriesAsString);

    }
    private void catchEvent() {
    }

    private void mapping(View v){
        sellingRecyclerview = (RecyclerView) v.findViewById(R.id.selling_recyclerview);
    }
    private void initRecyclerView(List<Post> posts) {
//        Toast.makeText(getContext(), ""+posts.size(), Toast.LENGTH_SHORT).show();
        adapter = new ListSellingAdapter(getContext(), posts);
        sellingRecyclerview.setAdapter(adapter);
        sellingRecyclerview.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter.setOnItemClickListener(new ListSellingAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {

            }

            @Override
            public void onDeleteItem(int position) {
                apiLoad.deletePost(position, user.getToken());
            }

            @Override
            public void onEditItem(int position) {
                apiLoad.LoadPost(posts.get(position).get_id());
            }
        });

    }
    class ApiLoad extends ApiConnect {
        private final String TAG = ApiLoad.class.getSimpleName();
        private Retrofit retrofit = getRetrofit();
        private APIService apiService = retrofit.create(APIService.class);
        public void getUserPost(String token, String _id){
            Call<List<Post>> call = apiService.getUserPosts("Bearer "+token, _id);
            call.enqueue(new Callback<List<Post>>() {
                @Override
                public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                    if(response.isSuccessful()) {
                        lsPosts.clear();
                        for (int i = 0; i < response.body().size(); i++) {
                            String _id = response.body().get(i).get_id();
                            String categoryId = response.body().get(i).getCategoryId();
                            String product_imagePath = Server.API_URL+response.body().get(i).getProduct_imagePath();
                            String product_name = response.body().get(i).getProduct_name();
                            double product_price = response.body().get(i).getProduct_price();
                            lsPosts.add(new Post(_id, categoryId, product_imagePath, product_name, product_price));
                        }
                        adapter.notifyDataSetChanged();
//                        viewPager.setCurrentItem(0);
                    }
                    call.cancel();
                }

                @Override
                public void onFailure(Call<List<Post>> call, Throwable throwable) {
                    Log.e(TAG, throwable.toString());
                }
            });

        }
        public void deletePost(int position, String token){
            String postId = lsPosts.get(position).get_id();
            Call<ResponseBody> call = apiService.deletePost("Bearer "+token, postId);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if(response.isSuccessful()){
                        lsPosts.remove(position);
                        adapter.notifyItemRemoved(position);
                        Toast.makeText(getActivity(), "Xóa bài đăng thành công", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(getActivity(), token, Toast.LENGTH_SHORT).show();
                    }
                    call.cancel();
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    call.cancel();
                }
            });
        }
        public void LoadPost(String postId){
            Call<List<Post>> call = apiService.getPost(postId);
            call.enqueue(new Callback<List<Post>>() {
                @Override
                public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                    if(response.isSuccessful()){
                        String userId = response.body().get(0).getUserId();
                        String postId = response.body().get(0).get_id();
                        String name = response.body().get(0).getProduct_name();
                        List<String> lsImage = response.body().get(0).getProduct_imageList();
                        String image = Server.API_URL + response.body().get(0).getProduct_imagePath();
                        String categoryId = response.body().get(0).getCategoryId();
                        double price = response.body().get(0).getProduct_price();
                        String description = response.body().get(0).getProduct_description();
                        String address = response.body().get(0).getAddress();
                        String email = response.body().get(0).getContact().getEmail();
                        int phone = response.body().get(0).getContact().getPhone();
                        Contact contact = new Contact(email, phone);
                        long timeStamp = response.body().get(0).getTimestamp();
                        int likes = response.body().get(0).getLikes();
                        post = new Post(postId, userId, categoryId, lsImage, image, name, price, description, address, timeStamp, likes, contact);
                        Intent intent = new Intent(getActivity(), EditPostActivity.class);
                        intent.putExtra("categories", (Serializable) categories);
                        intent.putExtra("objUser", objUser);
                        intent.putExtra("current_post", post);
                        startActivity(intent);
                        call.cancel();
                    }
                }

                @Override
                public void onFailure(Call<List<Post>> call, Throwable t) {
                    call.cancel();
                }
            });
        }
    }
}
