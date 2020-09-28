package sict.apps.studentmarket.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;

import sict.apps.studentmarket.R;
import sict.apps.studentmarket.adapters.ViewPagerAdapter;
import sict.apps.studentmarket.ui.auth.LoginUI;
import sict.apps.studentmarket.ui.auth.SignupUI;

public class AuthenticationActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private TextView mTitle;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.authentication_layout);
        mapping();
        initToolbar();
        setupViewPager();
    }
    private void mapping(){
        toolbar = (Toolbar) findViewById(R.id.auth_toolbar);
        mTitle = (TextView) findViewById(R.id.toolbar_title);
        viewPager = (ViewPager) findViewById(R.id.view_pager);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
    }
    private void initToolbar(){
        toolbar.setNavigationIcon(R.drawable.ic_clear);
        mTitle.setText("Đăng nhập/Đăng ký");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    private void setupViewPager() {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(LoginUI.newInstance(), "Login");
        adapter.addFragment(SignupUI.newInstance(), "Signup");
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }
}
