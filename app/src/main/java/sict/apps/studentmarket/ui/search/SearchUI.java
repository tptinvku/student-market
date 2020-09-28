package sict.apps.studentmarket.ui.search;

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

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
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
import retrofit2.converter.gson.GsonConverterFactory;
import sict.apps.studentmarket.R;
import sict.apps.studentmarket.activities.InfoPostActivity;
import sict.apps.studentmarket.adapters.ListResultsFoundAdapter;
import sict.apps.studentmarket.apihelper.ApiConnect;
import sict.apps.studentmarket.models.Post;
import sict.apps.studentmarket.models.Result;
import sict.apps.studentmarket.services.APIService;
import sict.apps.studentmarket.ultil.Server;
import sict.apps.studentmarket.vm.SearchShareViewModel;

public class SearchUI extends Fragment {
    private static final String TAG = SearchUI.class.getSimpleName();
    private RecyclerView recyclerView;
    private ListResultsFoundAdapter adapter;
    private SearchShareViewModel viewModel;
    private List<Result> lsResult = new ArrayList<>();
    private Retrofit retrofit;
    private Post post;

    public static SearchUI newInstance(){
        return  new SearchUI();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.search, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mapping(view);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = new ViewModelProvider(getActivity()).get(SearchShareViewModel.class);
        viewModel.getResults().observe(getViewLifecycleOwner(), new Observer<List<Result>>() {
            @Override
            public void onChanged(List<Result> results) {
                lsResult = results;
//              Toast.makeText(getContext(), ""+lsResult.size(), Toast.LENGTH_SHORT).show();
                initRecyclerView();
            }
        });
    }

    private void mapping(View v){
        recyclerView = (RecyclerView) v.findViewById(R.id.search_recyclerview);
    }

    private void initRecyclerView(){
        adapter = new ListResultsFoundAdapter(getContext(), lsResult);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter.setOnItemClickListener(new ListResultsFoundAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                  ApiLoad apiLoad = new ApiLoad();
                  apiLoad.LoadPost(lsResult.get(position).get_id());
            }
        });
    }

    class ApiLoad extends ApiConnect {
        private final String TAG = ApiLoad.class.getSimpleName();
        private Retrofit retrofit = getRetrofit();
        private APIService apiService = retrofit.create(APIService.class);
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
                            long timeStamp = response.body().get(0).getTimestamp();
                            int likes = response.body().get(0).getLikes();
                            post = new Post(postId, userId, categoryId, lsImage, image, name, price, description, address, timeStamp, likes);
                            Gson gson = new Gson();
                            String currentPost = gson.toJson(post);
                            Intent intent = new Intent(getActivity(), InfoPostActivity.class);
                            intent.putExtra("current_post", currentPost);
                            getActivity().startActivity(intent);
                            getActivity().finish();
                            call.cancel();
                        }else{
                            Toast.makeText(getContext(), "Không tìm thấy sản phẩm!", Toast.LENGTH_SHORT).show();
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
