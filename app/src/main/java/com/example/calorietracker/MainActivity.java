package com.example.calorietracker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.calorietracker.Model.DialogBox;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    CardView pickFromGallery,clickFromCamera,analytics,trackChanges;
    private static final int IMAGE_PICK_CODE=1000;
    private static final int GALLERYPERMISSION_CODE=1001;
    private static final int CAMERAPERMISSION_CODE=1002;
    private static final int IMAGE_CAPTURE_CODE=1003;
    Uri image_uri;
    DatabaseReference mydbref2;
    DatabaseReference mydbref3;

    TextView txtperc,txtcal;
    FirebaseDatabase mydb2;
    FirebaseDatabase mydb3;
    String userid;
    String target;
    ProgressBar circular;
    TextView txt;
    int caltakentoday=0;
    FirebaseUser user;
    FirebaseAuth auth;
    Storetarget storetarget;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setStatusBarColor(ContextCompat.getColor(MainActivity.this,R.color.background_color));
        storetarget=new Storetarget(MainActivity.this);

        circular=findViewById(R.id.circular_determinative_pb);
        clickFromCamera = findViewById(R.id.clickFromCamera) ;
        pickFromGallery = findViewById(R.id.pickFromGallery);
        analytics = findViewById(R.id.analytics);
        trackChanges = findViewById(R.id.trackChanges);
        txtperc=findViewById(R.id.progress_tv);
        txtcal=findViewById(R.id.cal);
        mydb2= FirebaseDatabase.getInstance();
        mydbref2=mydb2.getReference();
        mydb3= FirebaseDatabase.getInstance();
        mydbref3=mydb3.getReference();
        user= FirebaseAuth.getInstance().getCurrentUser();
        userid=user.getUid();
        txt= findViewById(R.id.targ);
        settarget();

        if(storetarget.getTarget()==0)
            opendialog();


        trackChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                opendialog();
            }
        });
        analytics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,StatCalorie.class));

            }
        });

        pickFromGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M)
                {
                    if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_DENIED)
                    {
                           String [] permissions= {Manifest.permission.READ_EXTERNAL_STORAGE};
                           requestPermissions(permissions,GALLERYPERMISSION_CODE);
                    }
                    else
                    {
                        pickImageFromGallery();
                    }
                }
                else
                {
                     pickImageFromGallery();
                }
            }
        });

        clickFromCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
                    if(checkSelfPermission(Manifest.permission.CAMERA)==
                            PackageManager.PERMISSION_DENIED || checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)==PackageManager.PERMISSION_DENIED){

                        //permission not enabled, request it
                        String[] permissions= {Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE};
                        requestPermissions(permissions,CAMERAPERMISSION_CODE);
                    }
                    else
                    {
                        //permission already granted
                        openCamera();
                    }
                }
                else
                {
                    openCamera();
                }
            }

        });

    }
private void calcal()
{
            mydbref3.child("food").child(user.getUid()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable  String previousChildName) {
                for(DataSnapshot snapshot1 : snapshot.getChildren()){

                    String snp=snapshot1.getValue().toString();
                    if(snapshot1.getKey().equals("timestamp")) {

                        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                        String dateString = formatter.format(new Date(Long.parseLong(snapshot1.getValue().toString())));
                        String todaydate=getDate();
                        if (dateString.equals(todaydate))
                        {
                            String t= snapshot.getValue().toString();
                            String arr[]=t.split("cal=");
                            String a[]=arr[1].split(" Calorie");
                            String ans=a[0];
                            caltakentoday=caltakentoday+Integer.parseInt(ans);
                            Log.d("arr",a[0]);

                        }

                    }
                }

                String b=txt.getText().toString();
                int tar=Integer.parseInt(b);
                int perc=(caltakentoday*100)/tar;
                txtperc.setText(perc+"%");
                circular.setProgress(perc);
                txtcal.setText(caltakentoday+" Cal");
                storetarget.setTarget(tar);

            }

            @Override
            public void onChildChanged(@NonNull  DataSnapshot snapshot, @Nullable  String previousChildName) {


            }

            @Override
            public void onChildRemoved(@NonNull  DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull  DataSnapshot snapshot, @Nullable  String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull  DatabaseError error) {

            }
        });

}
    private void openCamera() {

         ContentValues values=new ContentValues();
         values.put(MediaStore.Images.Media.TITLE,"New Picture");
         values.put(MediaStore.Images.Media.DESCRIPTION,"From the Camera");
         image_uri=getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values);
         // camera intent
         Intent cameraIntent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
         cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,image_uri);
         startActivityForResult(cameraIntent,IMAGE_CAPTURE_CODE);
    }

    private void pickImageFromGallery()
    {
         Intent intent=new Intent(Intent.ACTION_PICK);
         intent.setType("image/*");
         startActivityForResult(intent,IMAGE_PICK_CODE);
    }

  public  void settarget()
  {
    mydbref2.child("target").child(userid).addChildEventListener(new ChildEventListener() {
        @Override
        public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable  String previousChildName) {
            caltakentoday=0;
            target= snapshot.getValue().toString();
            txt.setText(target);
            calcal();
        }

        @Override
        public void onChildChanged(@NonNull  DataSnapshot snapshot, @Nullable String previousChildName) {
            target= snapshot.getValue().toString();
            caltakentoday=0;
            txt.setText(target);
            calcal();
        }

        @Override
        public void onChildRemoved(@NonNull  DataSnapshot snapshot) {

        }

        @Override
        public void onChildMoved(@NonNull  DataSnapshot snapshot, @Nullable  String previousChildName) {

        }

        @Override
        public void onCancelled(@NonNull  DatabaseError error) {

        }
    });

}
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode)
        {
            case GALLERYPERMISSION_CODE: {
                if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    pickImageFromGallery();
                }
                else{
                    Toast.makeText(this,"Permission denied...!",Toast.LENGTH_LONG).show();
                }
            }

            case CAMERAPERMISSION_CODE: {

                if(grantResults.length>0 && grantResults[0]==
                PackageManager.PERMISSION_GRANTED)
                {
                   // permission from popup was granted
                   openCamera();
                }
                else {
                    Toast.makeText(this,"Permission denied",Toast.LENGTH_SHORT).show();
                }
            }

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK && requestCode==IMAGE_PICK_CODE) {
            Intent sendimage = new Intent(Intent.ACTION_VIEW);
            sendimage.setClass(MainActivity.this, CaloriePredicitingActivity.class);
            sendimage.putExtra("image", data.getData().toString());
            startActivity(sendimage);
        }

        if(resultCode==RESULT_OK && requestCode==IMAGE_CAPTURE_CODE) {

            Intent sendimage = new Intent(Intent.ACTION_VIEW);
            sendimage.setClass(MainActivity.this, CaloriePredicitingActivity.class);
            sendimage.putExtra("image", image_uri.toString());
            startActivity(sendimage);
        }
    }


    private void opendialog() {
        DialogBox dialogBox=new DialogBox();
        dialogBox.show(getSupportFragmentManager(),"Dialog box");

    }

    private String getDate() {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date date = new Date();
        return dateFormat.format(date);
    }



}