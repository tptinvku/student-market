package sict.apps.studentmarket.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import sict.apps.studentmarket.R;
import sict.apps.studentmarket.ui.personal.PersonalUI;
import sict.apps.studentmarket.ultil.MySqlite;
import sict.apps.studentmarket.vm.MainShareViewModel;

public class ProfileActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private TextView mTitle;
    private TextView userId;
    private TextView userName;
    private TextView userEmail;
    private Button btnLogout;
    private String user_id="";
    private String user_name="";
    private String user_email="";
    private MySqlite database;
    private Bundle bundle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        database = new MySqlite(this);
        bundle = getIntent().getExtras();
        if(bundle!=null){
            user_id = bundle.getString("user_id");
            user_name = bundle.getString("user_name");
            user_email = bundle.getString("user_email");
        }
        mapping();
        initToolbar();
        setView();
        catchEvent();
    }

    private void initToolbar() {
        mTitle.setText(R.string.lblProfile);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setDisplayShowTitleEnabled(false);
    }

    private void mapping() {
        toolbar = (Toolbar) findViewById(R.id.profile_toolbar);
        mTitle = (TextView) findViewById(R.id.toolbar_title);
        userId = (TextView) findViewById(R.id.user_id);
        userName = (TextView) findViewById(R.id.user_name);
        userEmail = (TextView) findViewById(R.id.user_email);
        btnLogout = (Button) findViewById(R.id.btn_logout);
    }
    private void setView(){
        userId.setText(user_id);
        userName.setText(user_name);
        userEmail.setText(user_email);
    }
    private void catchEvent() {
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                database.post("DELETE FROM authorization");
                database.close();
                Intent i = new Intent();
                i.putExtra("objUser", "");
                setResult(Activity.RESULT_OK, i);
                finish();
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_account_management, menu);
        return true;
    }
}
