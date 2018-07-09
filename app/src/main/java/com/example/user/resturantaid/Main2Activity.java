package com.example.user.resturantaid;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.EventLogTags;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

public class Main2Activity extends AppCompatActivity {

    private static final String TAG = "Main2Activity";
   private ImageView imageView;
   private EditText editText;
   private Button button;
    private LinearLayout linearLayout;
    private ProgressBar progressBar;
    private StorageReference storageReference;
    private DatabaseReference databaseReference;

    private StorageTask storageTask;
    private Uri imageUri;

    private final static int SELECT_PHOTO = 12345;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        imageView = findViewById(R.id.image);
        editText = findViewById(R.id.name);
        button = findViewById(R.id.upload);
        progressBar = findViewById(R.id.progress);
        linearLayout =findViewById(R.id.parent);

        storageReference = FirebaseStorage.getInstance().getReference("Upload");
        databaseReference = FirebaseDatabase.getInstance().getReference("Upload");

        pick();

        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                InputMethodManager methodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                methodManager.hideSoftInputFromWindow(editText.getWindowToken(), 0);
            }
        });


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(storageTask!=null && storageTask.isInProgress()){

                    Toast.makeText(getBaseContext(), "Upload is in progress", Toast.LENGTH_SHORT).show();
                }

                else {

                    Upload();
                }

            }
        });
    }

    private String fileExtension(Uri uri){

        ContentResolver contentResolver = getContentResolver();

        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();

        // Returning the file Extension.
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri)) ;

    }


    private void Upload() {

        if(imageUri!=null && !(editText.getText().toString().trim().equals(""))){

            StorageReference fileReference = storageReference.
                    child(System.currentTimeMillis()+"."+fileExtension(imageUri));

           storageTask = fileReference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    Handler handler = new Handler();
                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                      progressBar.setProgress(0);
                        }
                    };

                    handler.postDelayed(runnable, 500);

                    Toast.makeText(getBaseContext(), "Upload Successfully", Toast.LENGTH_SHORT).show();

                    ImageUploadInfo imageUploadInfo = new ImageUploadInfo(editText.getText().
                            toString().trim(), taskSnapshot.getDownloadUrl().toString());

                    String upload_id =databaseReference.push().getKey();
                    databaseReference.child(upload_id).setValue(imageUploadInfo);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    Log.d("Error", e.getMessage());
                    Toast.makeText(getBaseContext(),e.getMessage(), Toast.LENGTH_LONG).show();

                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                    double progress = ((100*taskSnapshot.getBytesTransferred())/taskSnapshot.getTotalByteCount());
                    progressBar.setProgress((int)progress);
                }
            });

        }
        else {

            Toast.makeText(getBaseContext(), "No File Selected or Item Name is Blank", Toast.LENGTH_LONG).show();
        }

    }


    public void pick(){
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent photoPickerIntent = new Intent(Intent.ACTION_GET_CONTENT);
                photoPickerIntent.setType("image/*");
               Intent intent5 = Intent.createChooser(photoPickerIntent, "Choose image");
                startActivityForResult(intent5, SELECT_PHOTO);
            }
        });

    }

    public void showUpload(){

        // write code to show the uploads

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SELECT_PHOTO && resultCode == RESULT_OK && data != null) {

            Log.d("Data : ", data.getData().toString());

            imageUri = data.getData();
            imageView.setImageURI(imageUri);
        }
    }
}
