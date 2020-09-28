package sict.apps.studentmarket.ui.capture;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import sict.apps.studentmarket.R;
import sict.apps.studentmarket.vm.PostSharedViewModel;

import static android.app.Activity.RESULT_OK;
import static android.os.Environment.getExternalStoragePublicDirectory;

public class Capture extends Fragment {
    private static final String TAG = Capture.class.getSimpleName();
    private static final int REQUEST_TAKE_PHOTO = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int PERMISSION_CODE = 2;
    private static final String FILE_PROVIDER_AUTHORITY = "sict.apps.studentmarket.fileprovider";
    private RelativeLayout layout;
    private String currentPhotoPath;
    private PostSharedViewModel viewModel;
    public static Capture newInstance() {

        return new Capture();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.take_photo_layout, container, false);
        mapping(rootView);
        catchEvent();
        return rootView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
                requestPermissions(new String[]{
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                        },
                        PERMISSION_CODE);
        }
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = new ViewModelProvider(getActivity()).get(PostSharedViewModel.class);
    }

    private void mapping(View v){
        layout = (RelativeLayout) v.findViewById(R.id.capture_layout);
    }
    private void catchEvent(){
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            viewModel.setPath(currentPhotoPath);
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_capture_container, MoreCapture.newInstance())
                    .commit();
        }else{
            displayMessage(getContext(),"Request cancelled or something went wrong.");
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

}
