package sict.apps.studentmarket.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import sict.apps.studentmarket.R;
import sict.apps.studentmarket.apihelper.ApiConnect;
import sict.apps.studentmarket.models.Category;
import sict.apps.studentmarket.models.Contact;
import sict.apps.studentmarket.models.Post;
import sict.apps.studentmarket.models.User;
import sict.apps.studentmarket.services.APIService;
import sict.apps.studentmarket.ui.capture.MoreCapture;
import sict.apps.studentmarket.ultil.Server;
import sict.apps.studentmarket.vm.PostSharedViewModel;

public class EditPostActivity extends AppCompatActivity {
    private static final String TAG = EditPostActivity.class.getSimpleName();
    private static final int REQUEST_CODE = 123;
    private Spinner spinnerCategories;
    private Spinner spinnerAddress;
    private PostSharedViewModel postSharedViewModel;
    private Button btn_confirm;
    private List<Category> categories = new ArrayList<>();
    private List<String> lsCategoryName = new ArrayList<>();
    private List<String> lsAddress = new ArrayList<>();
    private String categoryId;
    private EditText productName;
    private EditText productPrice;
    private EditText productDescription;
    private EditText email;
    private EditText phone;
    private String categoryName;
    private String address;
    private User user;
    private String detail_description = "";
    private Retrofit retrofit;
    private String objUser;
    private ApiLoad apiLoad;
    private Post post;
    private List<String> lsCategoryId = new ArrayList<>();
    private Toolbar toolbar;
    private TextView mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_post);
        apiLoad = new ApiLoad();
        postSharedViewModel = new ViewModelProvider(this).get(PostSharedViewModel.class);
        mapping();
        setView();
        initToolbar();
        catchEvent();
    }
    private void mapping(){
        toolbar = (Toolbar) findViewById(R.id.edit_post_toolbar);
        mTitle = (TextView) findViewById(R.id.toolbar_title);
        spinnerCategories = (Spinner)findViewById(R.id.spinner_categories);
        spinnerAddress = (Spinner) findViewById(R.id.spinner_address);
        btn_confirm = (Button) findViewById(R.id.btn_confirm);
        productName = (EditText) findViewById(R.id.productName);
        productPrice = (EditText) findViewById(R.id.productPrice);
        productDescription = (EditText) findViewById(R.id.productDetail);
        email = (EditText) findViewById(R.id.post_email);
        phone = (EditText) findViewById(R.id.post_phone);
    }
    private void initToolbar(){
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setDisplayShowTitleEnabled(false);
        toolbar.setTitle(R.string.edit_post);

    }
    private void setView(){
        Gson gson = new Gson();
        categories  = (List<Category>) getIntent().getSerializableExtra("categories");
        lsAddress = Arrays.asList(Server.provinces_city);
        objUser = getIntent().getStringExtra("objUser");
        Type type = new TypeToken<User>(){}.getType();
        user = gson.fromJson(objUser, type);
        post = (Post) getIntent().getSerializableExtra("current_post");
//        MoreCapture capture = MoreCapture.newInstance();
//
//        getSupportFragmentManager().beginTransaction()
//                .add(R.id.frame_capture_container, capture)
//                .commit();
        lsCategoryName.add("Chọn danh mục...");
        for(int i= 0; i< categories.size(); i++){
            lsCategoryName.add(categories.get(i).getCategory_name());
            lsCategoryId.add(categories.get(i).get_id());
        }

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_spinner_item,
                lsCategoryName
        );
        ArrayAdapter<String> arrayAdapter1 = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                Server.provinces_city
        );
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        arrayAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategories.setAdapter(arrayAdapter);
        spinnerAddress.setAdapter(arrayAdapter1);

        productName.setText(post.getProduct_name());
        productPrice.setText(Double.toString(post.getProduct_price()));
        productDescription.setText(post.getProduct_description());
        email.setText(post.getContact().getEmail());
        phone.setText(String.valueOf(post.getContact().getPhone()));
        int categoryPosition = lsCategoryId.indexOf(post.getCategoryId());
        int addressPosition = lsAddress.indexOf(post.getAddress());
        Log.d(TAG, "setView: "+post.getCategoryId());
        spinnerCategories.setSelection(categoryPosition+1);
        spinnerAddress.setSelection(addressPosition+1);
    }
    private void catchEvent(){
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        productDescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditPostActivity.this, DetailedDescriptionActivity.class);
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
        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                apiLoad.updatePost(user.getToken());
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
    class ApiLoad extends ApiConnect {
        private final String TAG = ApiLoad.class.getSimpleName();
        private Retrofit retrofit = getRetrofit();
        private APIService apiService = retrofit.create(APIService.class);
        public void updatePost(String token){
            String tk = "Bearer "+token;
            String postId = post.get_id();
            String category_Id = categoryId;
            String name = productName.getText().toString();
            double price = Double.parseDouble(productPrice.getText().toString());
            String description = detail_description;
            String addrss  = address;
            String em = email.getText().toString();
            int numPhone = Integer.parseInt(phone.getText().toString());
            Contact contact = new Contact(em, numPhone);
            post = new Post(categoryId, name, price, description, addrss, contact);
            Call<ResponseBody> call = apiService.updatePost(
                    tk,
                    postId,
                    post);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        finish();
                    }else{
                        Toast.makeText(EditPostActivity.this, "Edit failed", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable throwable) {
                    Log.e(TAG, throwable.toString());
                }
            });
        }
    }
}
