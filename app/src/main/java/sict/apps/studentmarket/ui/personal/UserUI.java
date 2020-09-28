package sict.apps.studentmarket.ui.personal;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import sict.apps.studentmarket.R;
import sict.apps.studentmarket.activities.ProfileActivity;
import sict.apps.studentmarket.apihelper.ApiConnect;
import sict.apps.studentmarket.models.User;
import sict.apps.studentmarket.services.APIService;
import sict.apps.studentmarket.ultil.MySqlite;
import sict.apps.studentmarket.vm.MainShareViewModel;

import static android.app.Activity.RESULT_OK;

public class UserUI extends Fragment {
    private static final String TAG = UserUI.class.getSimpleName();
    private LinearLayout nav_profile;
    private FragmentTransaction fragmentTransaction;
    private TextView userId;
    private TextView userName;
    private MySqlite db;
    private Cursor cursor;
    private String u_id;
    private String u_name;
    private String u_email;
    private int u_phone;
    private int u_gender;
    private String token = "";
    private User user;
    private boolean gender;
    private int REQUEST_CODE = 123;
    private MainShareViewModel viewModel;
    public static UserUI newInstance() {
        return new UserUI();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.user_layout, container, false);
        fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        db = new MySqlite(getContext());
        cursor = db.get("SELECT * FROM authorization");
        mapping(rootView);
        loadData(cursor);
        catchEvent();
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = new ViewModelProvider(getActivity()).get(MainShareViewModel.class);
        viewModel.getObjUser().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                Log.d(TAG, "object user: "+s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    token = jsonObject.getString("token");
                    u_id = jsonObject.getString("_id");
                    u_name = jsonObject.getString("username");
                    u_phone = jsonObject.getInt("phone");
                    gender = jsonObject.getBoolean("gender");
                    u_gender = gender ? 1: 0;
                    u_email = jsonObject.getString("email");
                    user = new User(token, u_id, u_name, u_phone, gender, u_email);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void loadData(Cursor cs){
        if(user != null){
            setView();
        }
        else {
            if(cs.moveToNext()){
                token = cs.getString(0);
                u_id = cs.getString(1);
                u_name = cs.getString(2);
                u_phone = cs.getInt(3);
                u_gender = cs.getInt(4);
                u_email = cs.getString(5);
                gender = u_gender>0? true: false;
                user = new User(token, u_id, u_name, u_phone, gender, u_email);
                setView();
            }
        }
    }
    private void catchEvent() {
        nav_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("user_id", u_id);
                bundle.putString("user_name", u_name);
                bundle.putString("user_email", u_email);
                Intent intent = new Intent(getActivity(), ProfileActivity.class);
                intent.putExtras(bundle);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });
    }

    private void mapping(View v){
        userId = (TextView) v.findViewById(R.id.user_id);
        userName = (TextView) v.findViewById(R.id.user_name);
        nav_profile = (LinearLayout) v.findViewById(R.id.nav_profile);
    }
    private void setView(){
        userId.setText(u_id);
        userName.setText(u_name);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data !=null){
            Log.d(TAG, "onActivityResult: " + data.getStringExtra("objUser"));
            viewModel.setObjUser(data.getStringExtra("objUser"));
            FragmentManager fm = getFragmentManager();
            if (fm != null) {
                FragmentTransaction ft = fm.beginTransaction();
                ft.replace(R.id.frame_personal_container, GuestUI.newInstance());
                ft.commit();
            }
        }
    }
}
