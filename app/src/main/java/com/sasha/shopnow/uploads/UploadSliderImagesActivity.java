package com.sasha.shopnow.uploads;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.sasha.shopnow.R;

import java.util.ArrayList;
import java.util.HashMap;

public class UploadSliderImagesActivity extends AppCompatActivity {

    private Button chooseBtn, uploadBtn;
    private ActivityResultLauncher<Intent> resultLauncher;
    private ArrayList<Uri> imageList = new ArrayList<>();
    private ArrayList<String> urlString;
    private ProgressBar progressBar;
    private TextView preview_text;
    private int count = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_slider_images);

        preview_text = findViewById(R.id.preview_text);
        progressBar = findViewById(R.id.progressBar);
        chooseBtn = findViewById(R.id.choose_btn);
        uploadBtn = findViewById(R.id.upload_btn);

        resultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result != null){
                    if (result.getResultCode() == RESULT_OK){
                        if (result.getData().getClipData() != null){
                            int imageCount = result.getData().getClipData().getItemCount();
                            int selectImage = 0;

                            while (selectImage < imageCount){
                                Uri imageUri = result.getData().getClipData().getItemAt(selectImage).getUri();
                                imageList.add(imageUri);
                                selectImage = selectImage + 1;
                            }
                            preview_text.setVisibility(View.VISIBLE);
                            preview_text.setText(imageList.size() +"images selected");
                        }
                        else{
                            Toast.makeText(UploadSliderImagesActivity.this, "Please select multiple images", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });

        chooseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                resultLauncher.launch(intent);
            }
        });

        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageList == null){
                    Toast.makeText(UploadSliderImagesActivity.this, "Please select images", Toast.LENGTH_SHORT).show();
                }
                else{
                    startUploadImages();
                }
            }
        });
    }

    private void startUploadImages() {

        progressBar.setVisibility(View.VISIBLE);

        urlString = new ArrayList<>();

        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Slider") ;

        for (count = 0; count < imageList.size(); count++){
            Uri imageUri = imageList.get(count);

            StorageReference imageRef = storageReference.child("Slider Images" + imageUri.getLastPathSegment());

            imageRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            urlString.add(String.valueOf(uri));
                            if (urlString.size() == imageList.size()){
                                saveSliderImages(urlString);
                            }
                        }
                    });
                }
            });
        }
    }

    private void saveSliderImages(ArrayList<String> urlString) {

        progressBar.setVisibility(View.VISIBLE);

        final DatabaseReference imageRef = FirebaseDatabase.getInstance().getReference().child("Slider");

        HashMap<String, Object> hashMap = new HashMap<>();

        for (int i = 0; i < urlString.size(); i++){
            hashMap.put("imageUrl"+ i, urlString.get(i));
        }

        imageRef.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(UploadSliderImagesActivity.this, "Images upload successfull", Toast.LENGTH_SHORT).show();
                    startActivity(getIntent());
                }
            }
        });
    }
}