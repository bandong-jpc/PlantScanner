package com.example.plantscanner;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;
import java.io.IOException;

public class ActivityPlantDetails extends AppCompatActivity {
    ImageButton btnBack;
    Button btnContribute, btnCancel, btnSubmit;
    EditText etMedicinalUse, etLocalName;
    TextView accuracy, sciName, tvContribute, imageName;
    ImageView imageView;

    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;

    String fileName, filePath;

    boolean isEditing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plant_details);

        Intent intent = getIntent();

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        btnBack = findViewById(R.id.btnBack);
        btnContribute = findViewById(R.id.btnContribute);
        btnCancel = findViewById(R.id.btnCancel);
        btnSubmit = findViewById(R.id.btnSubmit);

        etLocalName = findViewById(R.id.etLocalName);
        etMedicinalUse = findViewById(R.id.etMedicinalUse);

        accuracy = findViewById(R.id.accuracy);
        sciName = findViewById(R.id.sciName);
        tvContribute = findViewById(R.id.tvLoginToContributeTV);
        imageName = findViewById(R.id.imageName);

        imageView = findViewById(R.id.imageView);

        etMedicinalUse.setEnabled(false);
        etLocalName.setEnabled(false);

        btnSubmit.setVisibility(View.INVISIBLE);
        btnCancel.setVisibility(View.INVISIBLE);

        if(firebaseAuth.getCurrentUser() != null){
            btnContribute.setVisibility(View.VISIBLE);
            tvContribute.setVisibility(View.INVISIBLE);
        }else{
            btnContribute.setVisibility(View.INVISIBLE);
            tvContribute.setVisibility(View.VISIBLE);
        }

        accuracy.setText(intent.getStringExtra("accuracy"));
        sciName.setText(intent.getStringExtra("sciName"));
        fileName = intent.getStringExtra("fileName");
        filePath = intent.getStringExtra("filePath");

        imageViewInit();

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleButtons();
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleButtons();
            }
        });

        btnContribute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleButtons();
            }
        });


    }

    private void toggleButtons(){
        if(isEditing){
            etLocalName.setEnabled(false);
            etMedicinalUse.setEnabled(false);
            btnCancel.setVisibility(View.INVISIBLE);
            btnSubmit.setVisibility(View.INVISIBLE);
            btnContribute.setVisibility(View.VISIBLE);
            isEditing = false;
        }else{
            etLocalName.setEnabled(true);
            etMedicinalUse.setEnabled(true);
            btnCancel.setVisibility(View.VISIBLE);
            btnSubmit.setVisibility(View.VISIBLE);
            btnContribute.setVisibility(View.INVISIBLE);
            isEditing = true;
        }
    }

    private void imageViewInit(){
        Bitmap img = BitmapFactory.decodeFile(filePath);

        File curFile = new File(filePath); // ... This is an image file from my device.
        Bitmap rotatedBitmap = null;

        try {
            ExifInterface exif = new ExifInterface(curFile.getPath());
            int rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            int rotationInDegrees = exifToDegrees(rotation);
            Matrix matrix = new Matrix();
            if (rotation != 0f) {matrix.preRotate(rotationInDegrees);}
            rotatedBitmap = Bitmap.createBitmap(img,0,0, img.getWidth(), img.getHeight(), matrix, true);


        }catch(IOException ex){
            Log.e("TAG", "Failed to get Exif data", ex);
        }
        if(rotatedBitmap == null)imageView.setImageBitmap(img);
        else imageView.setImageBitmap(rotatedBitmap);
    }

    private static int exifToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) { return 90; }
        else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {  return 180; }
        else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {  return 270; }
        return 0;
    }
}