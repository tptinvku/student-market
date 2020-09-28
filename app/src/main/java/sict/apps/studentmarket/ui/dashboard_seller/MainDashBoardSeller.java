package sict.apps.studentmarket.ui.dashboard_seller;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
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

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import sict.apps.studentmarket.R;
import sict.apps.studentmarket.apihelper.ApiConnect;
import sict.apps.studentmarket.models.User;
import sict.apps.studentmarket.services.APIService;
import sict.apps.studentmarket.ultil.MySqlite;
import sict.apps.studentmarket.vm.MainShareViewModel;

public class MainDashBoardSeller extends Fragment {
    public static final String TAG = MainDashBoardSeller.class.getSimpleName();
    private Toolbar toolbar;
    private TextView mTitle;
    private MainShareViewModel viewModel;
    private MySqlite database;
    private Cursor cs;
    private String token = "";
    private User user;
    public static MainDashBoardSeller newInstance(){
        return new MainDashBoardSeller();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dash_board_container, container, false);
        return rootView;
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = new ViewModelProvider(getActivity()).get(MainShareViewModel.class);
        viewModel.getObjUser().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                Gson gson = new Gson();
                Type type = new TypeToken<User>(){}.getType();
                user = gson.fromJson(s, type);
                if(user==null){
                    loadData();
                }else{
                    ApiListener apiListener = new ApiListener();
                    apiListener.checkAuth(user.getToken());
                }
            }
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        database = new MySqlite(getContext());
        mapping(view);
        initToolbar();
    }

    private void mapping(View v){
        toolbar = (Toolbar) v.findViewById(R.id.selling_toolbar);
        mTitle = (TextView) v.findViewById(R.id.toolbar_title);
    }
    private void initToolbar(){
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        ActionBar actionbar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(false);
        actionbar.setDisplayShowTitleEnabled(false);
        toolbar.setTitle(R.string.manage_post);

    }
    private void loadData(){
        cs = database.get("SELECT * FROM authorization");
        ApiListener apiListener = new ApiListener();
        Gson gson = new Gson();
        if (cs.moveToNext()) {
            token = cs.getString(0);
            String _id = cs.getString(1);
            String name = cs.getString(2);
            int phone = cs.getInt(3);
            boolean gender = cs.getInt(4) > 0 ? true: false;
            String email = cs.getString(5);
            user = new User(token, _id, name, phone, gender, email);
            String jsonUser = gson.toJson(user);
            viewModel.setObjUser(jsonUser);
        }
        if(user==null){
            user = new User("", "GUEST", "", 0, true, "");
        }
        apiListener.checkAuth(user.getToken());
        cs.close();
        database.close();
    }
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_post_management, menu);
    }
    class ApiListener extends ApiConnect {
        private final String TAG = ApiListener.class.getSimpleName();
        private Retrofit retrofit = getRetrofit();
        private APIService apiService = retrofit.create(APIService.class);


        public void checkAuth(String token){
            Call<ResponseBody> call = apiService.getAccessToken("Bearer " + token);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if(response.isSuccessful()){
                        getActivity().getSupportFragmentManager().beginTransaction()
                                .replace(R.id.frame_dashboard_container, DashBoard.newInstance())
                                .commit();
                    }else{
                        getActivity().getSupportFragmentManager().beginTransaction()
                                .replace(R.id.frame_dashboard_container, GuestUI.newInstance())
                                .commit();
                    }
                    call.cancel();
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable throwable) {
                    Log.e(TAG, throwable.toString());
                }
            });
        }
    }
}
