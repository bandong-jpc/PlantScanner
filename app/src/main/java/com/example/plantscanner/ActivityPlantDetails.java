package com.example.plantscanner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.model.Document;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ActivityPlantDetails extends AppCompatActivity {
    ImageButton btnBack;
    Button btnContribute, btnCancel, btnSubmit;
    EditText etMedicinalUse, etLocalName, etBenefits;
    TextView accuracy, sciName, tvContribute, imageName;
    ImageView imageView;

    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    StorageReference storageRef;
    UploadTask uploadTask;

    String fileName, filePath, localName = "", medicinalUse="", sName, userName = "";

    boolean isEditing = false;

    double numericAccuracy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plant_details);

        Intent intent = getIntent();

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference();

        btnBack = findViewById(R.id.btnBack);
        btnContribute = findViewById(R.id.btnContribute);
        btnCancel = findViewById(R.id.btnCancel);
        btnSubmit = findViewById(R.id.btnSubmit);

        etLocalName = findViewById(R.id.etLocalName);
        etMedicinalUse = findViewById(R.id.etMedicinalUse);
        etBenefits = findViewById(R.id.etBenefits);

        accuracy = findViewById(R.id.accuracy);
        sciName = findViewById(R.id.sciName);
        tvContribute = findViewById(R.id.tvLoginToContributeTV);
        imageName = findViewById(R.id.imageName);

        imageView = findViewById(R.id.imageView);

        etMedicinalUse.setEnabled(false);
        etLocalName.setEnabled(false);
        etBenefits.setEnabled(false);

        btnSubmit.setVisibility(View.INVISIBLE);
        btnCancel.setVisibility(View.INVISIBLE);

        if(firebaseAuth.getCurrentUser() != null){
            userName = intent.getStringExtra("uName");
            btnContribute.setVisibility(View.VISIBLE);
            tvContribute.setVisibility(View.INVISIBLE);
        }else{
            btnContribute.setVisibility(View.INVISIBLE);
            tvContribute.setVisibility(View.VISIBLE);
        }

        imageName.setText(intent.getStringExtra("fileName"));
        sName = intent.getStringExtra("sciName");
        accuracy.setText(intent.getStringExtra("accuracy"));
        sciName.setText(sName);
        fileName = intent.getStringExtra("fileName");
        filePath = intent.getStringExtra("filePath");
        numericAccuracy = intent.getDoubleExtra("numericAccuracy", 0);

        DocumentReference docRef = firebaseFirestore.collection("plants").document(sName);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    if(document.exists()){
                        btnContribute.setVisibility(View.INVISIBLE);
                        Log.d("DOCUTAG", "DocumentSnapshot data: " + document.getData());

                        etLocalName.setText(document.getString("localName"));
                        etMedicinalUse.setText(document.getString("medicinalUse"));
                        etBenefits.setText(document.getString("benefits"));
                    }
                }
            }
        });

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

            Map<String, Object> plantMap = new HashMap<String, Object>();

            DialogLoading dialogLoading = new DialogLoading();

            @Override
            public void onClick(View v) {


                if(etLocalName.getText().toString().equalsIgnoreCase("")){
                    etLocalName.setError("Field cannot be empty.");
                    return;
                }
                if(etMedicinalUse.getText().toString().equalsIgnoreCase("")){
                    etMedicinalUse.setError("Field cannot be empty.");
                    return;
                }
                if(etBenefits.getText().toString().equalsIgnoreCase("")){
                    etBenefits.setError("Field cannot be empty.");
                    return;
                }
                /*if(numericAccuracy < .5){
                    accuracy.setError("Accuracy must be greater than 50%");
                    return;
                }*/

                dialogLoading.show(getSupportFragmentManager(), "LOADING");

                plantMap.put("approved", false);
                plantMap.put("file", fileName);
                plantMap.put("localName", etLocalName.getText().toString());
                plantMap.put("medicinalUse", etMedicinalUse.getText().toString());
                plantMap.put("benefits", etBenefits.getText().toString());
                plantMap.put("contributorUID", firebaseAuth.getCurrentUser().getUid());
                plantMap.put("contributor", userName);

                firebaseFirestore.collection("plants").document(sName).set(plantMap)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                uploadToFBStorage(dialogLoading);
                                //Toast.makeText(ActivityPlantDetails.this, "Data submitted for review.", Toast.LENGTH_LONG).show();

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(ActivityPlantDetails.this, "Submission failed. " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                Log.i("FIRESTORE DEBUG", e.getMessage());
                                dialogLoading.dismiss();
                            }
                        });


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
            etBenefits.setEnabled(false);
            etMedicinalUse.setEnabled(false);
            btnCancel.setVisibility(View.INVISIBLE);
            btnSubmit.setVisibility(View.INVISIBLE);
            btnContribute.setVisibility(View.VISIBLE);
            isEditing = false;
        }else{
            etLocalName.setEnabled(true);
            etBenefits.setEnabled(true);
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

    private void uploadToFBStorage(final DialogLoading dialogLoading){
        Uri file = Uri.fromFile(new File(filePath));
        StorageReference fileRef = storageRef.child("images/"+file.getLastPathSegment());
        uploadTask = fileRef.putFile(file);

        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(ActivityPlantDetails.this, "Data submitted for review.", Toast.LENGTH_LONG).show();
                dialogLoading.dismiss();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ActivityPlantDetails.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                Log.i("STORAGE DEBUG", e.getMessage());

            }
        });
    }

}