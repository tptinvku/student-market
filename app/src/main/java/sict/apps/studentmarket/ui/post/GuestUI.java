package sict.apps.studentmarket.ui.post;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import sict.apps.studentmarket.R;
import sict.apps.studentmarket.activities.AuthenticationActivity;

import static android.app.Activity.RESULT_OK;

public class GuestUI extends Fragment {
    private static final int REQUEST_CODE = 123 ;
    private Button btn_auth;
    public static GuestUI newInstance() {
        
        Bundle args = new Bundle();
        
        GuestUI fragment = new GuestUI();
        fragment.setArguments(args);
        return fragment;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.no_user_layout, container, false);
        mapping(rootView);
        catchEvent();
        return rootView;
    }
    private void mapping(View v){
        btn_auth = (Button) v.findViewById(R.id.btn_auth);
    }
    private void catchEvent(){
        btn_auth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), AuthenticationActivity.class);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data !=null){
            FragmentManager fm = getFragmentManager();
            if (fm != null) {
                FragmentTransaction ft = fm.beginTransaction();
                ft.replace(R.id.frame_post_container, PostUI.newInstance());
                ft.commit();
            }
        }
    }
}
