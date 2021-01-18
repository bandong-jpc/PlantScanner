package com.example.plantscanner;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class FragmentProfileGuest extends Fragment {
    Button btnLogin, btnRegister;
    EditText etEmail, etPassword;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    Activity currentActivity;
    DialogLoading dialogLoading;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        currentActivity = getActivity();
        dialogLoading = new DialogLoading();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_profile_guest, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        btnLogin = view.findViewById(R.id.btnLogin);
        btnRegister = view.findViewById(R.id.btnRegister);

        etEmail = view.findViewById(R.id.editTextEmailAddress);
        etPassword = view.findViewById(R.id.editTextPassword);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openRegisterDialog();
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogLoading.show(getFragmentManager(), "LOADING");
                String email = etEmail.getText().toString().trim();
                String password = etPassword.getText().toString().trim();

                if(TextUtils.isEmpty(email)){
                    etEmail.setError("Email is required.");
                    return;
                }

                if(TextUtils.isEmpty(password)){
                    etPassword.setError("Password is required.");
                    return;
                }

                fAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful()){
                            Toast.makeText(getContext(), "Logged In Successfully", Toast.LENGTH_SHORT).show();
                            getFragmentManager().beginTransaction().replace(R.id.fragment_container, new FragmentProfileUser()).commit();

                            DocumentReference ref = fStore.collection("users").document(fAuth.getCurrentUser().getUid());

                            dialogLoading.dismiss();

                            ref.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    Map<String, Object> user = documentSnapshot.getData();
                                    ((Home)currentActivity).tvUser.setText(user.get("name").toString());

                                }
                            });


                        }else{
                            try{
                                throw task.getException();
                            } catch (FirebaseAuthInvalidCredentialsException e) {
                                etEmail.setError(e.getMessage());
                                etPassword.setError(e.getMessage());
                            } catch (FirebaseAuthInvalidUserException e) {
                                etEmail.setError(e.getMessage());
                                etPassword.setError(e.getMessage());
                            } catch (Exception e){
                                Log.e(TAG, e.getMessage());
                            }
                            dialogLoading.dismiss();
                        }
                    }
                });

            }
        });

        super.onViewCreated(view, savedInstanceState);
    }

    public void openRegisterDialog(){
        final DialogRegister dialogRegister = new DialogRegister();

        dialogRegister.show(getFragmentManager(), "register dialog");
    }

}