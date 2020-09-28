package sict.apps.studentmarket.ui.search;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.flexbox.FlexboxLayout;

import java.util.ArrayList;
import java.util.List;

import sict.apps.studentmarket.R;

public class OriginSearchUI extends Fragment {
    private FlexboxLayout kw_container;
    private List<String> lsKeyWords = new ArrayList<>();
    public static OriginSearchUI newInstance() {

        return  new OriginSearchUI();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.origin_search, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        kw_container = (FlexboxLayout) view.findViewById(R.id.hot_kw);
        initDataDemo();
        inflateLayout();

    }
    private void inflateLayout(){
        LinearLayout.LayoutParams buttonLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        buttonLayoutParams.setMargins(5, 5, 5, 5);
        for(int i = 0; i < lsKeyWords.size(); i++){
            final TextView tv = new TextView(getActivity().getApplicationContext());
            tv.setText(lsKeyWords.get(i));
            tv.setHeight(80);
            tv.setTextSize(16.0f);
            tv.setGravity(Gravity.CENTER);
            tv.setTextColor(Color.parseColor("#000000"));
            tv.setBackground(getResources().getDrawable(R.drawable.round_corner));
            tv.setId(i + 1);
            tv.setLayoutParams(buttonLayoutParams);
            tv.setTag(i);
            tv.isClickable();
            tv.setPadding(10, 2, 10, 2);
            kw_container.addView(tv);
        }
    }
    private void initDataDemo(){
        lsKeyWords.add("Iphone 11");
        lsKeyWords.add("Samsung A10");
        lsKeyWords.add("Khẩu trang");
        lsKeyWords.add("Giấy vệ sinh");
        lsKeyWords.add("Bia");
        lsKeyWords.add("Cỏ Mỹ");
    }
}
