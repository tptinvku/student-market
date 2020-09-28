package sict.apps.studentmarket.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import sict.apps.studentmarket.R;
import sict.apps.studentmarket.apihelper.ApiConnect;
import sict.apps.studentmarket.models.User;
import sict.apps.studentmarket.services.APIService;
import sict.apps.studentmarket.ui.post.GuestUI;
import sict.apps.studentmarket.ui.post.PostUI;
import sict.apps.studentmarket.ultil.MySqlite;
import sict.apps.studentmarket.vm.PostSharedViewModel;

public class PostActivity extends AppCompatActivity {
    public static final String TAG = PostActivity.class.getSimpleName();
    private Toolbar toolbar;
    private TextView mTitle;
    public  PostSharedViewModel viewModel;
    private Cursor cursor;
    private MySqlite database;
    private String token = "";
    private User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        viewModel = new ViewModelProvider(this).get(PostSharedViewModel.class);
        database = new MySqlite(this);
        cursor = database.get("SELECT * FROM authorization");
        loadData(cursor);
        mapping();
        initToolbar();
        catchEvent();
    }
    private void mapping(){
        toolbar = (Toolbar) findViewById(R.id.post_toolbar);
        mTitle = (TextView) findViewById(R.id.toolbar_title);
    }
    private void loadData(Cursor cs){
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
        apiListener.checkAuth(token);
        cs.close();
        database.close();
    }
    private void initToolbar(){
        mTitle.setText(R.string.post);
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
    class ApiListener extends ApiConnect{
        private final String TAG = ApiListener.class.getSimpleName();
        private Retrofit retrofit = getRetrofit();
        private APIService apiService = retrofit.create(APIService.class);
        public void checkAuth(String token){
            apiService.getAccessToken("Bearer " + token).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if(response.isSuccessful()){
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.frame_post_container, PostUI.newInstance())
                                .commit();
                    }else{
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.frame_post_container, GuestUI.newInstance())
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
