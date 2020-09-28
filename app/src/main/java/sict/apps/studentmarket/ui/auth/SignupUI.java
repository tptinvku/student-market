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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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

public class SignupUI extends Fragment {
    private static final String TAG = SignupUI.class.getSimpleName();
    private EditText username;
    private EditText phone;
    private EditText email;
    private EditText password;
    private RadioButton gender_male;
    private RadioButton gender_female;
    private RadioGroup radioGroup;
    private Button btn_signup;
    private Retrofit retrofit = null;
    private static boolean gender;
    private MySqlite database;
    public static SignupUI newInstance(){
        return new SignupUI();
    }
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.signup_layout, container, false);
        database = new MySqlite(getContext());
        mapping(rootView);
        eventCatch();
        return rootView;
    }

    private void mapping(View v) {
        username = (EditText) v.findViewById(R.id.username);
        phone = (EditText) v.findViewById(R.id.numberPhone);
        email = (EditText) v.findViewById(R.id.email);
        password = (EditText) v.findViewById(R.id.password);
        gender_male = (RadioButton) v.findViewById(R.id.radioButton_male);
        gender_female = (RadioButton) v.findViewById(R.id.radioButton_female);
        radioGroup = (RadioGroup) v.findViewById(R.id.radio);
        btn_signup = (Button) v.findViewById(R.id.btn_signup);
    }

    private void eventCatch(){
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId){
                    case R.id.radioButton_male:
                        Log.d(TAG, gender_male.getText().toString());
                        gender = true;
                        break;
                    case R.id.radioButton_female:
                        Log.d(TAG, gender_female.getText().toString());
                        gender = false;
                        break;
                }
            }
        });
        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connectAPI();
            }
        });
    }
    private void connectAPI() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(Server.API_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            APIService apiService = retrofit.create(APIService.class);
            String name = username.getText().toString();
            long np = Long.parseLong(phone.getText().toString());
            String em = email.getText().toString();
            String pw = password.getText().toString();
            User user = new User(name, np, em, pw, gender);
            apiService.postSignup(user).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                   if(response.isSuccessful()){
                       try {
                           JSONObject jsonObject = new JSONObject(response.body().string());
                           String message = jsonObject.getString("message");
                           Log.d(TAG, message);
                           if(message.equals("Sign Up Success")){
                               String token = jsonObject.getString("token");
                               String userObject = jsonObject.getString("user");
                               JSONObject jsonUser = new JSONObject(userObject);
                               String user_id = jsonUser.getString("_id");
                               String user_name = jsonUser.getString("username");
                               long user_phone = jsonUser.getLong("phone");
                               int user_gender = jsonUser.getBoolean("gender") ? 1: 0;
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
                               username.setText("");
                               phone.setText("");
                               radioGroup.clearCheck();
                               Toast.makeText(getContext(), "Đăng ký thất bại", Toast.LENGTH_SHORT).show();
                           }
                       } catch (JSONException e) {
                           e.printStackTrace();
                       } catch (IOException e) {
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
