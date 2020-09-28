package sict.apps.studentmarket.ui.order;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import sict.apps.studentmarket.R;

public class OrderEmptyUI extends Fragment {
    private Button btnContinueBuy;
    public static OrderEmptyUI newInstance() {
        return new OrderEmptyUI();
    }
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.order_empty, container, false);
        mapping(rootView);
        catchEvent();
        return rootView;
    }

    private void catchEvent() {
        btnContinueBuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();

            }
        });
    }

    private void mapping(View v) {
        btnContinueBuy = (Button) v.findViewById(R.id.btn_continue_buy);

    }

}
