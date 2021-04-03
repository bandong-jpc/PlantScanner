package com.example.plantscanner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

public class Home extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    DrawerLayout drawer;
    /*public TextView tvUser;
    FirebaseAuth firebaseAuth;*/
    FirebaseFirestore fStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        /*firebaseAuth = FirebaseAuth.getInstance();*/
        fStore = FirebaseFirestore.getInstance();

        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.color.colorPrimary);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = LayoutInflater.from(this).inflate(R.layout.nav_header, navigationView, false);
        navigationView.addHeaderView(headerView);
        /*tvUser = headerView.findViewById(R.id.tvUser);*/


        /*if(firebaseAuth.getCurrentUser() != null) {
            DocumentReference ref = fStore.collection("users").document(firebaseAuth.getCurrentUser().getUid());
            ref.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    tvUser.setText(documentSnapshot.getString("name"));
                }
            });
        }
        else tvUser.setText("Guest");*/

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawer.addDrawerListener(toggle);
        toggle.syncState();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new FragmentLibrary()).commit();
            navigationView.setCheckedItem(R.id.nav_collection);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_collection:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new FragmentLibrary()).commit();
                break;
            case R.id.nav_snap:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new FragmentSnap()).commit();
                break;
            /*case R.id.nav_profile:
                if(FirebaseAuth.getInstance().getCurrentUser() != null){
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new FragmentProfileUser()).commit();
                }else{
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new FragmentProfileGuest()).commit();
                }

                break;*/
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) drawer.closeDrawer(GravityCompat.START);
        else super.onBackPressed();
    }

}