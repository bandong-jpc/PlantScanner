package com.example.plantscanner;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

public class PlantAdapter extends RecyclerView.Adapter<PlantAdapter.PlantViewHolder> {
    StorageReference storageRef;
    Context context;
    ArrayList<String> ids;
    HashMap<String, Plant> plantMap;

    public PlantAdapter(Context c, ArrayList<String> ids, HashMap<String, Plant> plantmap) {
        context = c;
        this.ids = ids;
        this.plantMap = plantmap;

        storageRef = FirebaseStorage.getInstance().getReference();
    }

    public class PlantViewHolder extends RecyclerView.ViewHolder {
        TextView tv;
        ImageView im;
        Plant plant;
        String sciName;


        public PlantViewHolder(@NonNull View itemView) {
            super(itemView);
            im = itemView.findViewById(R.id.img);
            tv = itemView.findViewById(R.id.item_name);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ActivityPlant.class);
                    intent.putExtra("sciName", sciName);
                    intent.putExtra("file", plant.file);
                    /*intent.putExtra("contributor", plant.contributor);*/
                    intent.putExtra("medicinalUse", plant.medicinalUse);
                    intent.putExtra("localName", plant.localName);
                    intent.putExtra("benefits", plant.benefits);
                    context.startActivity(intent);
                }
            });
        }


    }

    @NonNull
    @Override
    public PlantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item, parent, false);

        return new PlantViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final PlantViewHolder holder, int position) {
        holder.tv.setText(ids.get(position));

        storageRef.child("images/"+plantMap.get(ids.get(position)).getFile()).getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                Picasso.get().load(task.getResult()).placeholder(R.drawable.logo_leaves).into(holder.im);
            }
        });

        holder.plant = plantMap.get(ids.get(position));
        holder.sciName = ids.get(position);

    }

    @Override
    public int getItemCount() {
        return ids.size();
    }


}
