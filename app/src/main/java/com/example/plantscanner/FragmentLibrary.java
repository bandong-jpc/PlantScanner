package com.example.plantscanner;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;


public class FragmentLibrary extends Fragment {

    RecyclerView recyclerView;

    FirebaseFirestore firebaseFirestore;

    HashMap<String, Plant> plantHashMap;

    ArrayList<String> ids;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        plantHashMap = new HashMap<String, Plant>();

        ids = new ArrayList<String>();

        firebaseFirestore = FirebaseFirestore.getInstance();

        /*Query query = firebaseFirestore.collection("plant");

        options = new FirestoreRecyclerOptions.Builder<Plant>()
                .setQuery(query, Plant.class)
                .build();

       firestoreRecyclerAdapter = new FirestoreRecyclerAdapter<Plant, PlantViewHolder>(options) {
            @Override
            public void onError(@NonNull FirebaseFirestoreException e) {
                super.onError(e);
                Toast.makeText(getContext(), "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            protected void onBindViewHolder(@NonNull PlantViewHolder holder, int position, @NonNull Plant model) {

                holder.tv.setText(model.getLocalName());

            }

            @NonNull
            @Override
            public PlantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);

                return new PlantViewHolder(view);
            }
        };*/


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View viewFrag =  inflater.inflate(R.layout.fragment_library, container, false);



        recyclerView = viewFrag.findViewById(R.id.recycler);




        getData(new MyCallback() {
            @Override
            public void onCallback(HashMap<String, Plant> map, ArrayList<String> names) {
                PlantAdapter plantAdapter = new PlantAdapter(getContext(), names, map);

                recyclerView.setAdapter(plantAdapter);
                GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity().getApplicationContext(), 2);
                recyclerView.setLayoutManager(gridLayoutManager);
            }
        });

        return viewFrag;
    }

    public void getData(final MyCallback callback){
        firebaseFirestore.collection("plants")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){

                            HashMap<String, Plant> map = new HashMap<String, Plant>();
                            ArrayList<String> names = new ArrayList<String>();

                            for (QueryDocumentSnapshot document: task.getResult()){
                                Plant plant = document.toObject(Plant.class);
                                //Log.d("DATATAG", document.getId() + " => " + plant.getLocalName());
                                map.put(document.getId(), plant);
                                names.add(document.getId());
                            }

                            callback.onCallback(map, names);
                        }else {
                            Toast.makeText(getContext(), "Server Error", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    /*@Override
    public void onStart() {
        super.onStart();

        if(firestoreRecyclerAdapter == null) firestoreRecyclerAdapter.startListening();
    }

    @Override
    public void onStop() {
        if(firestoreRecyclerAdapter != null) firestoreRecyclerAdapter.stopListening();
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(firestoreRecyclerAdapter == null) firestoreRecyclerAdapter.startListening();
    }*/
}

