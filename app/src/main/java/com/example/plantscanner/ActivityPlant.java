package com.example.plantscanner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class ActivityPlant extends AppCompatActivity {

    ImageButton btnBack;

    TextView sciName2, etLocalName2, etMedicinalUse2, /*contributor,*/ benefits2, etProperUsage2;
    ImageView imageView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plant);

        btnBack = findViewById(R.id.btnBack2);

        sciName2 = findViewById(R.id.sciName2);

        etLocalName2 = findViewById(R.id.etLocalName2);
        etLocalName2.setMovementMethod(new ScrollingMovementMethod());

        etMedicinalUse2 = findViewById(R.id.etMedicinalUse2);
        etMedicinalUse2.setMovementMethod(new ScrollingMovementMethod());

        /*contributor = findViewById(R.id.contributor);*/

        benefits2 = findViewById(R.id.etBenefits2);
        benefits2.setMovementMethod(new ScrollingMovementMethod());

        etProperUsage2 = findViewById(R.id.etProperUsage2);
        etProperUsage2.setMovementMethod(new ScrollingMovementMethod());

        imageView2 = findViewById(R.id.imageView2);

        Intent intent = getIntent();

        sciName2.setText(intent.getStringExtra("sciName"));
        etLocalName2.setText(intent.getStringExtra("localName"));
        etMedicinalUse2.setText(intent.getStringExtra("medicinalUse"));
        /*contributor.setText(intent.getStringExtra("contributor"));*/
        benefits2.setText(intent.getStringExtra("benefits"));
        etProperUsage2.setText(intent.getStringExtra("properUsage"));

        StorageReference storageRef = FirebaseStorage.getInstance().getReference();

        storageRef.child("images/"+intent.getStringExtra("file")).getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                Picasso.get().load(task.getResult()).placeholder(R.drawable.logo_leaves).into(imageView2);
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}