package sict.apps.studentmarket.ui.capture;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import sict.apps.studentmarket.R;
import sict.apps.studentmarket.adapters.ListCapturesAdapter;
import sict.apps.studentmarket.vm.PostSharedViewModel;

import static android.app.Activity.RESULT_OK;
import static android.os.Environment.getExternalStoragePublicDirectory;

public class MoreCapture extends Fragment implements ListCapturesAdapter.OnItemClickListener {
    private static final String TAG = MoreCapture.class.getSimpleName();
    private static final int REQUEST_TAKE_PHOTO = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int PERMISSION_CODE = 2;
    private static final String FILE_PROVIDER_AUTHORITY = "sict.apps.studentmarket.fileprovider";
    private RelativeLayout btn_capture;
    private String currentPhotoPath;
    private RecyclerView recyclerView;
    private ListCapturesAdapter adapter;
    private List<String> paths = new ArrayList<>();
    private PostSharedViewModel viewModel;
    private String objPaths;
    public static MoreCapture newInstance() {
        return  new MoreCapture();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.list_image, container, false);
        Bundle bundle = getArguments();
        if(bundle!=null){
            Gson gson = new Gson();
            objPaths = bundle.getString("ls_path");
            Type type = new TypeToken<List<String>>(){}.getType();
            paths = gson.fromJson(objPaths, type);
        }
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mapping(view);
        initRecyclerView();
        catchEvent();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            String permission[] = {
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            };
            requestPermissions(permission, PERMISSION_CODE);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = new ViewModelProvider(getActivity()).get(PostSharedViewModel.class);
        viewModel.getPath().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String strings) {
                Log.d(TAG, "onChanged: " + strings);
                paths.add(strings);
                viewModel.setPathImgs(paths);
            }
        });
    }

    private void mapping(View v) {
        btn_capture = (RelativeLayout) v.findViewById(R.id.btn_capture);
        recyclerView = (RecyclerView) v.findViewById(R.id.capture_recyclerview);
    }
    private void initRecyclerView(){
        adapter = new ListCapturesAdapter(getContext(), paths);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
    }
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
                displayMessage(getContext(),photoFile.getAbsolutePath());
                // Continue only if the File was successfully created
                if(photoFile!=null){
                    Uri photoUri = FileProvider.getUriForFile(
                            getContext(),
                            FILE_PROVIDER_AUTHORITY,
                            photoFile);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                    startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
                }
            } catch (Exception e) {
                displayMessage(getContext(),e.getMessage());
            }
        }else{
            displayMessage(getContext(),"Null");
        }
    }
    private void catchEvent(){
        adapter.setOnItemClickListener(this);
        btn_capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            paths.add(currentPhotoPath);
            viewModel.setPathImgs(paths);
            adapter.notifyDataSetChanged();
        }
    }
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File  image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir);
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }
    private void displayMessage(Context context, String message)
    {
        Toast.makeText(context,message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onDeleteItem(int position) {
        File file = new File(paths.get(position));
        boolean deleted = file.delete();
        paths.remove(position);
        if(deleted) {
            adapter.notifyDataSetChanged();
            if (paths.isEmpty()) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frame_capture_container, Capture.newInstance())
                        .commit();
            }
        }
    }
}
