package sict.apps.studentmarket.ui.post;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import sict.apps.studentmarket.R;
import sict.apps.studentmarket.adapters.ViewPagerAdapter;

public class PostUI extends Fragment{
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private  ViewPagerAdapter adapter;
    public static PostUI newInstance() {
        return new PostUI();
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.create_post_layout, container, false);
        mapping(rootView);
        setupViewPager();
        return rootView;
    }
    private void mapping(View v){
        viewPager = (ViewPager) v.findViewById(R.id.view_pager);
        tabLayout = (TabLayout) v.findViewById(R.id.tabs);
    }
    private void setupViewPager() {
        adapter = new ViewPagerAdapter(getActivity().getSupportFragmentManager());
        adapter.addFragment(Info.newInstance(), "Thông tin sản phẩm");
        adapter.addFragment(Contact.newInstance(), "Thông tin Liên hệ");
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }

}
