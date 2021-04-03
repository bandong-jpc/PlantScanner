package com.example.plantscanner;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.widget.ImageViewCompat;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class FragmentSnap extends Fragment {
    DialogLoading dialogLoading;
    ImageView imageView;
    TextView imageName, sciNameField, sciName, accuracyField, accuracy;
    ImageButton btnSearch, btnClear, btnSnap;
    Button btnDetails;

    String currentImagePath = null;
    String imgFileName = "";

    FirebaseAuth fAuth;

    MediaType MEDIA_TYPE = MediaType.parse("image/jpg");

    private final OkHttpClient client = new OkHttpClient();

    private static final int IMAGE_REQUEST = 1;

    double numericAccuracy;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        fAuth = FirebaseAuth.getInstance();

        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
            //your codes here

        }
        return inflater.inflate(R.layout.fragment_snap, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        dialogLoading = new DialogLoading();

        imageView = view.findViewById(R.id.imageView);
        imageName = view.findViewById(R.id.imageName);
        sciName = view.findViewById(R.id.sciName);
        sciNameField = view.findViewById(R.id.sciNameField);
        accuracy = view.findViewById(R.id.accuracy);
        accuracyField = view.findViewById(R.id.accuracyField);

        btnDetails = view.findViewById(R.id.btnDetails);
        btnClear = view.findViewById(R.id.btnClear);
        btnSnap = view.findViewById(R.id.btnSnap);
        btnSearch = view.findViewById(R.id.btnSearch);

        btnSnap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                captureImage(v);
            }
        });

        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File toDelete = new File(currentImagePath);
                toDelete.delete();
                currentImagePath = "";
                imgFileName = "";
                imageView.setImageResource(R.mipmap.ic_launcher_icon_foreground);
                imageName.setText("No image selected.");
                accuracy.setVisibility(View.INVISIBLE);
                accuracyField.setVisibility(View.INVISIBLE);
                sciName.setVisibility(View.INVISIBLE);
                sciNameField.setVisibility(View.INVISIBLE);
                btnDetails.setVisibility(View.INVISIBLE);
            }
        });

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(imgFileName.equals("")){
                    Toast.makeText(getContext(), "No image selected!", Toast.LENGTH_SHORT).show();
                }else postToPlantNet();

            }
        });

        btnDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ActivityPlantDetails.class);
                intent.putExtra("fileName", imgFileName);
                intent.putExtra("filePath", currentImagePath);
                intent.putExtra("sciName", sciName.getText().toString());
                intent.putExtra("accuracy", accuracy.getText().toString());

                /*String uName = ((Home)getActivity()).tvUser.getText().toString();*/

                /*intent.putExtra("uName", uName);*/
                intent.putExtra("numericAccuracy", numericAccuracy);

                startActivity(intent);
            }
        });


        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if(requestCode == 1 && resultCode == getActivity().RESULT_OK){
            Bitmap img = BitmapFactory.decodeFile(currentImagePath);

            File curFile = new File(currentImagePath); // ... This is an image file from my device.
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
            imageName.setText(imgFileName);
        }
    }

    private void captureImage(View view){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if(intent.resolveActivity(getActivity().getPackageManager()) != null){
            File imageFile = null;

            try {
                imageFile = getImageFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if(imageFile != null){
                Uri imageUri = FileProvider.getUriForFile(getContext(), "com.example.plantscanner.provider", imageFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);

                startActivityForResult(intent, IMAGE_REQUEST);
            }

        }
    }

    private File getImageFile()throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String uid = "";
        if(fAuth.getCurrentUser() != null) uid = fAuth.getCurrentUser().getUid();
        String imageName = "PS_"+ uid +"_"+timeStamp;
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        File imageFile = File.createTempFile(imageName, ".jpg", storageDir);
        imgFileName = imageFile.getName();
        currentImagePath = imageFile.getAbsolutePath();
        return imageFile;

    }

    private static int exifToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) { return 90; }
        else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {  return 180; }
        else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {  return 270; }
        return 0;
    }

    private void postToPlantNet(){
        dialogLoading.show(getFragmentManager(), "Loading");

        RequestBody body = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("organs", "leaf")
                .addFormDataPart("images", imgFileName, RequestBody.create(new File(currentImagePath), MEDIA_TYPE))
                .build();

        Request request = new Request.Builder()
                .url("https://my-api.plantnet.org/v2/identify/all?api-key=2a103FOXaE5ftre7VxDb4kQoOO")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            Runnable dismissDialog = new Runnable() {
                @Override
                public void run() {
                    dialogLoading.dismiss();
                    Toast.makeText(getContext(), "Error cannot connect to server." , Toast.LENGTH_SHORT).show();
                }
            };

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
                getActivity().runOnUiThread(dismissDialog);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull final Response response) throws IOException {
                if(response.isSuccessful()){
                    String jsonData = response.body().string();
                    try {
                        JSONObject resObject = new JSONObject(jsonData);
                        JSONArray results = resObject.getJSONArray("results");

                        JSONObject resultObject = results.getJSONObject(1);

                        final double acc = resultObject.getDouble("score");

                        JSONObject species = resultObject.getJSONObject("species");

                        final String sciN = species.getString("scientificNameWithoutAuthor");

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                numericAccuracy = acc;

                                sciName.setText(sciN);
                                accuracy.setText(toPercentage(acc));

                                sciName.setVisibility(View.VISIBLE);
                                sciNameField.setVisibility(View.VISIBLE);
                                accuracy.setVisibility(View.VISIBLE);
                                accuracyField.setVisibility(View.VISIBLE);

                                btnDetails.setVisibility(View.VISIBLE);

                                dialogLoading.dismiss();

                            }
                        });

                    } catch (JSONException e) {
                        e.printStackTrace();
                        getActivity().runOnUiThread(dismissDialog);
                    }
                }else{
                    Log.i("RESPONSE FAIL", response.body().string());
                    getActivity().runOnUiThread(dismissDialog);
                }
            }
        });
    }

    public static String toPercentage(double n){
        return String.format("%.0f",n*100)+"%";
    }



}