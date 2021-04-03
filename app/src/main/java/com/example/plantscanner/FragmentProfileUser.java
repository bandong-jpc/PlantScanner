package com.example.plantscanner;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;

public class FragmentProfileUser extends Fragment {
    Button btnLogout;
    TextView tvEmail;
    FirebaseAuth fAuth;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        fAuth = FirebaseAuth.getInstance();

        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_profile_user, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        btnLogout = view.findViewById(R.id.profileBTNLogout);

        tvEmail = view.findViewById(R.id.profileTVEmail);

        tvEmail.setText(fAuth.getCurrentUser().getEmail());

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fAuth.signOut();
                getFragmentManager().beginTransaction().replace(R.id.fragment_container, new FragmentProfileGuest()).commit();
                /*((Home)getActivity()).tvUser.setText("Guest");*/
            }
        });

        super.onViewCreated(view, savedInstanceState);
    }
}
