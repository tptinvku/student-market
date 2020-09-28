package sict.apps.studentmarket.ui.notification;

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
import sict.apps.studentmarket.activities.MessengerManageActiviity;
import sict.apps.studentmarket.activities.OrderActivity;
import sict.apps.studentmarket.apihelper.ApiConnect;
import sict.apps.studentmarket.models.User;
import sict.apps.studentmarket.services.APIService;
import sict.apps.studentmarket.ultil.MySqlite;
import sict.apps.studentmarket.vm.MainShareViewModel;

import static android.app.Activity.RESULT_OK;

public class MainNotification extends Fragment {
    private MainShareViewModel viewModel;
    private Toolbar toolbar;
    private User user;
    private MySqlite database;
    private Cursor cs;
    private String token = "";
    private static  final int REQUEST_CODE = 123;


    public static MainNotification newInstance(){
        return new MainNotification();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.notification_container, container, false);
        database = new MySqlite(getContext());
        mapping(rootView);
        initToolbar();
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
    private void mapping(View v){
        toolbar = (Toolbar) v.findViewById(R.id.notify_toolbar);

    }
    private void initToolbar(){
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        ActionBar actionbar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(false);
        actionbar.setDisplayShowTitleEnabled(false);
        toolbar.setTitle(R.string.lblNotify);
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
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
            viewModel.setObjUser(data.getStringExtra("objUser"));
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_notify_container, NotificationUI.newInstance())
                    .commit();
        }
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
                                .replace(R.id.frame_notify_container, NotificationUI.newInstance())
                                .commit();
                    }else{
                        getActivity().getSupportFragmentManager().beginTransaction()
                                .replace(R.id.frame_notify_container, GuestUI.newInstance())
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
