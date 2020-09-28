package sict.apps.studentmarket.ui.post;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import sict.apps.studentmarket.R;
import sict.apps.studentmarket.models.Post;
import sict.apps.studentmarket.services.APIService;
import sict.apps.studentmarket.ultil.Server;
import sict.apps.studentmarket.vm.PostSharedViewModel;

public class Contact extends Fragment {
    private static final String TAG = Contact.class.getSimpleName();
    private Button btn_post;
    private EditText email;
    private EditText phone;
    private List<String> lsPaths = new ArrayList<>();
    private Post post;
    private PostSharedViewModel postSharedViewModel;
    private Retrofit retrofit = null;
    private ViewPager viewPager;
    private String token = "";
    private List<MultipartBody.Part> image = new ArrayList<>();
    public static Contact newInstance() {
        return new Contact();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fill_contact_info_layout, container, false);
        mapping(rootView);
        catchEvent();
        return rootView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        postSharedViewModel = new ViewModelProvider(getActivity()).get(PostSharedViewModel.class);
        postSharedViewModel.getPost().observe(getViewLifecycleOwner(), new Observer<Post>() {
            @Override
            public void onChanged(Post p) {
                post = p;
                lsPaths = post.getProduct_imageList();
            }
        });
        postSharedViewModel.getObjUser().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    token = jsonObject.getString("token");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private  void mapping(View v){
        btn_post = (Button) v.findViewById(R.id.btn_post);
        viewPager = (ViewPager) getActivity().findViewById(R.id.view_pager);
        email = (EditText) v.findViewById(R.id.post_email);
        phone = (EditText) v.findViewById(R.id.post_phone);

    }
    private void catchEvent(){
        btn_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(post !=null){
                    createProduct(token, post);
                } else {
                    Toast.makeText(getContext(), "Vui lòng điền đầy đủ thông tin.", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
    private void createProduct(String token, Post post){
        if(retrofit==null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(Server.API_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            APIService apiService = retrofit.create(APIService.class);
            String tk = "Bearer "+token;
            RequestBody requestCategoryId = RequestBody.create(post.getCategoryId(), MultipartBody.FORM);
            RequestBody requestProductDescription = RequestBody.create(post.getProduct_description(), MultipartBody.FORM);
            RequestBody requestProductName = RequestBody.create(post.getProduct_name(), MultipartBody.FORM);
            RequestBody requestProductPrice = RequestBody.create(Double.toString(post.getProduct_price()), MultipartBody.FORM);
            RequestBody requestUserId = RequestBody.create(post.getUserId(), MultipartBody.FORM);
            RequestBody requestAddress = RequestBody.create(post.getAddress(), MultipartBody.FORM);
            RequestBody requestEmail = RequestBody.create(email.getText().toString(), MultipartBody.FORM);
            RequestBody requestPhone = RequestBody.create(phone.getText().toString(), MultipartBody.FORM);
            for (int i = 0; i < lsPaths.size(); i++) {
                String pathName = lsPaths.get(i);
                image.add(prepareFilePart(pathName));
            }
            Call<Post> call = apiService.postCreatePost(
                    tk,
                    image,
                    requestCategoryId,
                    requestProductName,
                    requestProductPrice,
                    requestProductDescription,
                    requestUserId,
                    requestAddress,
                    requestEmail,
                    requestPhone);
                    call.enqueue(new Callback<Post>() {
                @Override
                public void onResponse(Call<Post> call, Response<Post> response) {
                    if (response.isSuccessful()) {
                        getActivity().finish();
                    }else{
                        viewPager.setCurrentItem(0);
                    }
                    call.cancel();
                }

                @Override
                public void onFailure(Call<Post> call, Throwable throwable) {
                    Log.e(TAG, throwable.toString());
                }
            });
            retrofit=null;
        }
    }
    private MultipartBody.Part prepareFilePart(String pathName){
        File file = new File(pathName);
        RequestBody requestFile = RequestBody.create(file, MediaType.parse("image/png"));
        return MultipartBody.Part.createFormData("productImages", pathName, requestFile);
    }
}
