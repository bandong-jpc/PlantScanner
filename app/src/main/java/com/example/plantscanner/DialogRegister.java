package com.example.plantscanner;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class DialogRegister extends AppCompatDialogFragment {
    private EditText etFullName, etEmail, etPassword;
    private Button register, cancel;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    DialogLoading dialogLoading;


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        dialogLoading = new DialogLoading();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_register, null);

        etFullName = view.findViewById(R.id.registerETFullName);
        etEmail = view.findViewById(R.id.registerETEmailAddress);
        etPassword = view.findViewById(R.id.registerETPassword);

        cancel = view.findViewById(R.id.registerBTNCancel);
        register = view.findViewById(R.id.registerBTNRegister);


        fAuth = FirebaseAuth.getInstance();

        fStore = FirebaseFirestore.getInstance();


        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String fullname = etFullName.getText().toString().trim();
                String email = etEmail.getText().toString().trim();
                String password = etPassword.getText().toString().trim();

                if(TextUtils.isEmpty(fullname)){
                    etFullName.setError("Full Name is required.");
                    return;
                }

                if(TextUtils.isEmpty(email)){
                    etEmail.setError("Email is required.");
                    return;
                }

                if(TextUtils.isEmpty(password)){
                    etPassword.setError("Password is required.");
                    return;
                }

                if(password.length() < 6){
                    etPassword.setError("Password must be more than 6 characters.");
                    return;
                }

                dialogLoading.show(getFragmentManager(), "LOADING");

                fAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    Map<String, Object> userMap = new HashMap<String, Object>();
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful()){

                            userMap.put("name", fullname);

                            fStore.collection("users").document(fAuth.getCurrentUser().getUid()).set(userMap);
                            Toast.makeText(getContext(), "Successfully Registered!", Toast.LENGTH_SHORT).show();

                            dialogLoading.dismiss();

                            DialogRegister.super.dismiss();
                            getFragmentManager().beginTransaction().replace(R.id.fragment_container, new FragmentProfileUser()).commit();
                            /*((Home)getActivity()).tvUser.setText(fullname);*/

                        }else{
                            try{
                                throw task.getException();
                            } catch (FirebaseAuthUserCollisionException e) {
                                etEmail.setError(e.getMessage());
                            } catch (FirebaseAuthWeakPasswordException e){
                                etPassword.setError(e.getReason());
                            } catch (Exception e){
                                Log.e(TAG, e.getMessage());
                            }
                            dialogLoading.dismiss();
                        }
                    }

                });



            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogRegister.super.dismiss();
            }
        });

        builder.setView(view);

        return builder.create();
    }


}
