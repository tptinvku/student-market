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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import sict.apps.studentmarket.R;
import sict.apps.studentmarket.activities.EditPostActivity;
import sict.apps.studentmarket.adapters.ViewPagerAdapter;
import sict.apps.studentmarket.apihelper.ApiConnect;
import sict.apps.studentmarket.models.Contact;
import sict.apps.studentmarket.models.Post;
import sict.apps.studentmarket.models.User;
import sict.apps.studentmarket.services.APIService;
import sict.apps.studentmarket.ultil.Server;
import sict.apps.studentmarket.vm.MainShareViewModel;

public class DashBoard extends Fragment {
    private static final String TAG = DashBoard.class.getSimpleName();
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private  ViewPagerAdapter adapter;
    private ApiLoad apiLoad;
    private MainShareViewModel viewModel;
    private User user;
    private List<Post> lsPosts = new ArrayList<>();

    public static DashBoard newInstance() {
        return new DashBoard();
    }

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dash_board_layout, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        apiLoad = new ApiLoad();
        mapping(view);
        setupViewPager();
        catchEvent();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = new ViewModelProvider(getActivity()).get(MainShareViewModel.class);
        viewModel.getObjUser().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                Log.d(TAG, "object user: "+s);
                Gson gson = new Gson();
                Type type = new TypeToken<User>(){}.getType();
                user = gson.fromJson(s, type);
                if(user==null){
                    user = new User("", "GUEST", "", 0, true, "");
                }
                apiLoad.getUserPost(user.getToken(), user.get_id());
            }
        });
    }

    private void catchEvent(){
    }
    private void mapping(View v){
        viewPager = (ViewPager) v.findViewById(R.id.view_pager);
        tabLayout = (TabLayout) v.findViewById(R.id.tabs);
    }
    private void setupViewPager() {
        adapter = new ViewPagerAdapter(getActivity().getSupportFragmentManager());
        adapter.addFragment(Selling.newInstance(), getString(R.string.dang_ban));
        adapter.addFragment(Nothing.newInstance(), getString(R.string.bi_tu_choi));
        adapter.addFragment(Nothing.newInstance(), getString(R.string.can_thanh_toan));
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(0);
        tabLayout.setupWithViewPager(viewPager);
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
                        viewModel.setPosts(lsPosts);
                    }
                    call.cancel();
                }

                @Override
                public void onFailure(Call<List<Post>> call, Throwable throwable) {
                    Log.e(TAG, throwable.toString());
                }
            });

        }
    }
}
