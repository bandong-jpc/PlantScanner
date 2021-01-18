package com.example.plantscanner;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

public class DialogLoading extends AppCompatDialogFragment {

    AnimationDrawable animationDrawable;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_loading, null);

        final ImageView animation = (ImageView) view.findViewById(R.id.animateIV);
        animation.setImageResource(R.drawable.animation_loading);

        animation.post(new Runnable() {
            @Override
            public void run() {
                animationDrawable = (AnimationDrawable) animation.getDrawable();
                animationDrawable.start();
            }
        });

        setCancelable(false);

        builder.setView(view);

        return builder.create();
    }


}
