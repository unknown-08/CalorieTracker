package com.example.calorietracker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import com.example.calorietracker.Model.Stats;
import com.example.calorietracker.Model.Stats2;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Collections;

public class StatCalorie extends AppCompatActivity {
    DatabaseReference mydbref1;
    FirebaseDatabase mydb1;
    RecyclerView recyclerView;
    StatsAdapter statsAdapter;
    ArrayList<Stats2> arr = new ArrayList<>();
    String userid;
    String calorie,name,time;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stat_calorie);
        getWindow().setStatusBarColor(ContextCompat.getColor(StatCalorie.this,R.color.background_color));

        mydb1= FirebaseDatabase.getInstance();
        mydbref1=mydb1.getReference();
        final FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
        userid=user.getUid();
        recyclerView = (RecyclerView)findViewById(R.id.recyclestat);
        recyclerView.setLayoutManager(new LinearLayoutManager(StatCalorie.this));
        mydbref1.child("food").child(user.getUid()).orderByChild("timestamp").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable  String previousChildName) {

                if (snapshot.exists()) {

                    for(DataSnapshot snapshot1 : snapshot.getChildren()){
                    if(snapshot1.getKey().equals("cal"))
                        calorie=snapshot1.getValue().toString().trim();
                    else if(snapshot1.getKey().equals("name"))
                        name=snapshot1.getValue().toString().trim();
                    else
                        time=snapshot1.getValue().toString().trim();
                    }
                    arr.add(new Stats2(name,calorie,time));
                    Collections.reverse(arr);
                    statsAdapter = new StatsAdapter(arr,StatCalorie.this);
                    recyclerView.setAdapter(statsAdapter);
                    Toast.makeText(StatCalorie.this,"pw",Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot,  String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull  DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull  DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull  DatabaseError error) {

            }
        });


    }
}