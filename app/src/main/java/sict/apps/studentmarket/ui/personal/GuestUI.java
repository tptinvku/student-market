package sict.apps.studentmarket.ui.personal;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;


import sict.apps.studentmarket.R;
import sict.apps.studentmarket.activities.AuthenticationActivity;
import sict.apps.studentmarket.vm.MainShareViewModel;

import static android.app.Activity.RESULT_OK;

public class GuestUI extends Fragment {
    private static final String TAG = GuestUI.class.getSimpleName();
    private LinearLayout auth;
    private static final int REQUEST_CODE = 123;
    private MainShareViewModel viewModel;
    public static GuestUI newInstance() {
        return new GuestUI();
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.guest_layout, container, false);
        mapping(rootView);
        catchEvent();
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = new ViewModelProvider(getActivity()).get(MainShareViewModel.class);
    }

    private void mapping(View v){
        auth = (LinearLayout) v.findViewById(R.id.nav_auth);
    }
    private void catchEvent(){
        auth.setOnClickListener(new View.OnClickListener() {
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
            Log.d(TAG, "onActivityResult: " + data.getStringExtra("objUser"));
            viewModel.setObjUser(data.getStringExtra("objUser"));
            FragmentManager fm = getFragmentManager();
            if (fm != null) {
                String objUser = data.getStringExtra("objUser");
                viewModel.setObjUser(objUser);
                FragmentTransaction ft = fm.beginTransaction();
                ft.replace(R.id.frame_personal_container, UserUI.newInstance());
                ft.commit();
            }
        }
    }
}
