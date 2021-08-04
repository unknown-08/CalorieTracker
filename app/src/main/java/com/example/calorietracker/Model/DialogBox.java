package com.example.calorietracker.Model;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.calorietracker.CaloriePredicitingActivity;
import com.example.calorietracker.MainActivity;
import com.example.calorietracker.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class DialogBox extends AppCompatDialogFragment {

    EditText targetCalorie;
    FirebaseDatabase firebaseDatabase1;
    DatabaseReference databaseReference1;

    String userid;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
        LayoutInflater inflater=getActivity().getLayoutInflater();
        View view=inflater.inflate(R.layout.layout_dialog,null);
        firebaseDatabase1=FirebaseDatabase.getInstance();
        databaseReference1=firebaseDatabase1.getReference();
        final FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
        userid=user.getUid();
        builder.setView(view).setTitle("").setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String Target=targetCalorie.getText().toString();
                Map mp =  new HashMap<>();
                mp.put("Tar",Target);
                if(Target.length()==0)
                {

                }
                else
                {
                    DatabaseReference mchild=databaseReference1.child("target").child(user.getUid());
                    mchild.setValue(mp, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull  DatabaseReference databaseReference) {
                           // Toast.makeText(getActivity(),"Added Successfully",Toast.LENGTH_LONG).show();

                            dialog.cancel();
                        }
                    });
                }



            }
        });

        targetCalorie=view.findViewById(R.id.targetcalorie);
        return builder.create();

    }




}
