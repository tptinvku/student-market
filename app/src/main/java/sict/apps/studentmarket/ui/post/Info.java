package sict.apps.studentmarket.ui.post;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MultipartBody;
import sict.apps.studentmarket.R;
import sict.apps.studentmarket.activities.DetailedDescriptionActivity;
import sict.apps.studentmarket.models.Category;
import sict.apps.studentmarket.models.Post;
import sict.apps.studentmarket.models.User;
import sict.apps.studentmarket.ui.capture.Capture;
import sict.apps.studentmarket.ultil.Server;
import sict.apps.studentmarket.ultil.MySqlite;
import sict.apps.studentmarket.vm.PostSharedViewModel;

import static android.app.Activity.RESULT_OK;

public class Info extends Fragment {
    private static final String TAG = Info.class.getSimpleName();
    private static final int REQUEST_CODE = 123;
    private Spinner spinnerCategories;
    private Spinner spinnerAddress;
    private PostSharedViewModel postSharedViewModel;
    private Button btn_continue;
    private ViewPager viewPager;
    private List<Category> categories = new ArrayList<>();
    private List<String> lsCategoryName = new ArrayList<>();
    private String userId;
    private String categoryId;
    private Post post;
    private EditText productName;
    private EditText productPrice;
    private EditText productDescription;
    private String categoryName;
    private String address;
    private List<String> lsPaths = null;
    private MySqlite database;
    private User user;
    private String detail_description = "";
    public static Info newInstance() {

        return new Info();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fill_info_post_layout, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mapping(view);
        setView();
        catchEvent();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        database = new MySqlite(getContext());
        categories  = (List<Category>) getActivity().getIntent().getSerializableExtra("categories");
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame_capture_container, new Capture())
                .commit();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        postSharedViewModel = new ViewModelProvider(getActivity()).get(PostSharedViewModel.class);
        postSharedViewModel.getPathImgs().observe(getViewLifecycleOwner(), new Observer<List<String>>() {
            @Override
            public void onChanged(List<String> strings) {
                lsPaths = strings;
                for(String s: lsPaths){
                    Log.d(TAG, "Paths: "+s);
                }
            }
        });
        postSharedViewModel.getObjUser().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                Log.d(TAG, "onChanged: "+s);
                Gson gson = new Gson();
                String objUser  = s;
                Type type_3 = new TypeToken<User>(){}.getType();
                user = gson.fromJson(objUser, type_3);
            }
        });
    }

    private void mapping(View v){
        spinnerCategories = (Spinner) v.findViewById(R.id.spinner_categories);
        spinnerAddress = (Spinner) v.findViewById(R.id.spinner_address);
        btn_continue = (Button) v.findViewById(R.id.btn_continue);
        viewPager = (ViewPager) getActivity().findViewById(R.id.view_pager);
        productName = (EditText) v.findViewById(R.id.productName);
        productPrice = (EditText) v.findViewById(R.id.productPrice);
        productDescription = (EditText) v.findViewById(R.id.productDetail);
    }
    private void setView(){
        lsCategoryName.add("Chọn danh mục...");
        for(int i= 0; i< categories.size(); i++){
                lsCategoryName.add(categories.get(i).getCategory_name());
        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                getContext(),
                android.R.layout.simple_spinner_item,
                lsCategoryName
        );
        ArrayAdapter<String> arrayAdapter1 = new ArrayAdapter<>(
                getContext(),
                android.R.layout.simple_spinner_item,
                Server.provinces_city
        );
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        arrayAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategories.setAdapter(arrayAdapter);
        spinnerAddress.setAdapter(arrayAdapter1);
    }
    private void catchEvent(){
        productDescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), DetailedDescriptionActivity.class);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });
        spinnerCategories.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0){
                    return;
                }
                position--;
                categoryName = spinnerCategories.getSelectedItem().toString();
                categoryId = categories.get(position).get_id();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinnerAddress.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0){
                    return;
                }
                position--;
                address = spinnerAddress.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        btn_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(post ==null) {
                    String name = productName.getText().toString();
                    String description = detail_description;
                    String post_price = productPrice.getText().toString();
                    String userId = user.get_id();
                    if (lsPaths!=null && name!=null && description!=null && post_price!=null && userId!=null) {
                        double price = Double.parseDouble(post_price);
                        post = new Post(categoryId, name, price, description, lsPaths, userId, address);
                        postSharedViewModel.setPost(post);
                        viewPager.setCurrentItem(1);
                    }
                    if(post == null){
                            Toast.makeText(getContext(), "Vui lòng điền đầy đủ thông tin.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data !=null){
            detail_description = data.getStringExtra("description");
            productDescription.setText(detail_description);
        }
    }
}
