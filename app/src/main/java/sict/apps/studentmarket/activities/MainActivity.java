package sict.apps.studentmarket.activities;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.github.nkzawa.socketio.client.Socket;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.List;

import sict.apps.studentmarket.R;
import sict.apps.studentmarket.models.Category;
import sict.apps.studentmarket.models.User;
import sict.apps.studentmarket.ui.dashboard_seller.MainDashBoardSeller;
import sict.apps.studentmarket.ui.home.HomeUI;
import sict.apps.studentmarket.ui.notification.MainNotification;
import sict.apps.studentmarket.ui.personal.PersonalUI;
import sict.apps.studentmarket.ultil.MySocket;
import sict.apps.studentmarket.vm.MainShareViewModel;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int REQUEST_CODE = 123 ;
    private FloatingActionButton floatingActionButton;
    private BottomNavigationView bottomNavigationView;
    private List<Category> categories;
    private User user;
    private MainShareViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewModel = new ViewModelProvider(this).get(MainShareViewModel.class);
        viewModel.getObjUser().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                Gson gson = new Gson();
                Type type_3 = new TypeToken<User>(){}.getType();
                Log.d(TAG, "User object: " + s);
                user = gson.fromJson(s, type_3);
            }
        });
        receiveData();
        mapping();
        catchEvent();
        loadFragment(HomeUI.newInstance());
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: "+"Start");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onStart: "+"Pause");

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onStart: "+"resume");

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onStart: "+"destroy");

    }

    private void mapping(){
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        floatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
    }
    private void receiveData(){
        Gson gson = new Gson();
        String lsCategoriesAsString = getIntent().getStringExtra("lsCategories");
        Log.d(TAG, "list categories: " + lsCategoriesAsString);
        Type type = new TypeToken<List<Category>>(){}.getType();
        categories = gson.fromJson(lsCategoriesAsString, type);
        String objUser = getIntent().getStringExtra("objUser");
        Log.d(TAG, "object user: " + objUser);
        viewModel.setObjUser(objUser);

    }
    private void catchEvent(){
        bottomNavigationView.setOnNavigationItemSelectedListener(this);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, PostActivity.class);
                intent.putExtra("categories", (Serializable) categories);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data !=null){
            Gson gson = new Gson();
            Type type = new TypeToken<User>(){}.getType();
            user = gson.fromJson(data.getStringExtra("objUser"), type);
            viewModel.setObjUser(data.getStringExtra("objUser"));
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment;
        switch (item.getItemId()) {
            case R.id.navigation_home:
                fragment = HomeUI.newInstance();
                loadFragment(fragment);
                return true;
            case R.id.navigation_selfSelling:
                fragment = MainDashBoardSeller.newInstance();
                loadFragment(fragment);
                return true;
            case R.id.navigation_notifications:
                fragment = MainNotification.newInstance();
                loadFragment(fragment);
                return true;
            case R.id.navigation_personal:
                fragment = PersonalUI.newInstance();
                loadFragment(fragment);
                return true;
        }
        return true;
    }

    private void loadFragment(Fragment fragment) {
        // load fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.commit();
    }
    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }

}
