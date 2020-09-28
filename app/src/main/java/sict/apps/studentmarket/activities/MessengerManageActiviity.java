package sict.apps.studentmarket.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

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
import sict.apps.studentmarket.ui.messenger.GuestUI;
import sict.apps.studentmarket.ui.messenger.MessengerUI;
import sict.apps.studentmarket.ultil.MySqlite;
import sict.apps.studentmarket.vm.MessengerShareViewModel;

public class MessengerManageActiviity extends AppCompatActivity {
    private Toolbar toolbar;
    private TextView mTitle;
    private Cursor cs;
    private MySqlite database;
    private String token = "";
    private User user;
    private String jsonUser;
    private MessengerShareViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messenger_manage_activiity);
        database = new MySqlite(this);
        viewModel = new ViewModelProvider(this).get(MessengerShareViewModel.class);
        viewModel.getObjUser().observe(this, new Observer<String>() {
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
        loadData();
        mapping();
        initToolbar();
        catchEvent();
    }
    private void mapping(){
        toolbar = (Toolbar) findViewById(R.id.messenger_toolbar);
        mTitle = (TextView) findViewById(R.id.toolbar_title);
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
            jsonUser = gson.toJson(user);
            viewModel.setObjUser(jsonUser);
        }
        if(user==null){
            user = new User("", "GUEST", "", 0, true, "");
        }
        apiListener.checkAuth(token);
        cs.close();
        database.close();
    }
    private void initToolbar(){
        mTitle.setText("Tin nhắn");
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setDisplayShowTitleEnabled(false);

    }
    private void catchEvent(){
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Gson gson = new Gson();
                String objUser = gson.toJson(user);
                Intent intent = new Intent();
                intent.putExtra("objUser", objUser);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }
    class ApiListener extends ApiConnect {
        private final String TAG = PostActivity.ApiListener.class.getSimpleName();
        private Retrofit retrofit = getRetrofit();
        private APIService apiService = retrofit.create(APIService.class);
        public void checkAuth(String token){
            apiService.getAccessToken("Bearer " + token).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if(response.isSuccessful()){
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.frame_messenger, MessengerUI.newInstance())
                                .commit();
                    }else{
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.frame_messenger, GuestUI.newInstance())
                                .commit();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable throwable) {
                    Log.e(TAG, throwable.toString());
                }
            });
        }
    }
}
