package sict.apps.studentmarket.ui.auth;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import sict.apps.studentmarket.R;
import sict.apps.studentmarket.models.User;
import sict.apps.studentmarket.services.APIService;
import sict.apps.studentmarket.ultil.Server;
import sict.apps.studentmarket.ultil.MySqlite;
import sict.apps.studentmarket.vm.MainShareViewModel;

public class LoginUI extends Fragment {
    private static final String TAG = LoginUI.class.getSimpleName();
    private Button btn_login;
    private EditText email;
    private EditText password;
    private Retrofit retrofit = null;
    private MySqlite database;
    private MainShareViewModel viewModel;
    public static LoginUI newInstance() {

        return new LoginUI();
    }
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.login_layout, container, false);
        database = new MySqlite(getContext());
        mapping(rootView);
        catchEvent();
        return rootView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(MainShareViewModel.class);

    }

    private void mapping(View v){
        btn_login = (Button) v.findViewById(R.id.btn_login);
        email = (EditText) v.findViewById(R.id.username);
        password = (EditText) v.findViewById(R.id.password);
    }
    private void catchEvent(){
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connectAPI();
//              database.post("DROP TABLE authorization");

            }
        });
    }
    private void connectAPI(){
        if(retrofit==null){
            retrofit = new Retrofit.Builder()
                    .baseUrl(Server.API_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            APIService apiService = retrofit.create(APIService.class);
            String em = email.getText().toString();
            String pw = password.getText().toString();
            User user = new User(em, pw);
            apiService.postSignin(user).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if(response.isSuccessful()){
                        try {
                            String json = response.body().string();
                            JSONObject jsonObject = new JSONObject(json);
                            String message = jsonObject.getString("message");
                            Log.d(TAG,json + "\n" + message);
                            if (message.equals("Auth successful")) {
                                String token = jsonObject.getString("token");
                                String userObject = jsonObject.getString("user");
                                JSONObject jsonUser = new JSONObject(userObject);
                                String user_id = jsonUser.getString("_id");
                                String user_name = jsonUser.getString("username");
                                long user_phone = jsonUser.getLong("phone");
                                boolean gender = jsonUser.getBoolean("gender");
                                int user_gender = gender ? 1: 0;
                                String user_email = jsonUser.getString("email");
                                User user = new User(token, user_id, user_name, user_phone, gender, user_email);
                                Gson gson = new Gson();
                                String objInfoUser = gson.toJson(user);
                                Cursor cursor = database.get("SELECT * FROM authorization");
                                if(cursor.moveToNext()){
                                    database.post("DELETE FROM authorization");
                                }
                                database.post(String.format("INSERT INTO authorization VALUES('%s', '%s','%s', %d, %d,'%s')",
                                        token, user_id, user_name, user_phone, user_gender, user_email));
                                cursor.close();
                                database.close();
                                Intent i = new Intent();
                                i.putExtra("objUser", objInfoUser);
                                i.putExtra("token", token);
                                getActivity().setResult(Activity.RESULT_OK, i);
                                getActivity().finish();
                            }
                                else{
                                    email.setText("");
                                    password.setText("");
                                    Toast.makeText(getContext(), "Đăng nhập thất bại", Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (IOException e){
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable throwable) {
                    Log.e(TAG, throwable.toString());
                }
            });
            retrofit = null;
        }
    }
}
