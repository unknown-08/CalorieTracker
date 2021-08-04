package com.example.calorietracker;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;
import com.example.calorietracker.Model.Stats;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CaloriePredicitingActivity extends AppCompatActivity {

    ImageView FoodImage,addCalorie;
    Button trackCalorie;
    ProgressBar progressBar;
    TextView Food_Name,Calories;
    TextView QuantityTextView,Quantity;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    String userid;
    Storetarget target;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calorie_prediciting);
        getWindow().setStatusBarColor(ContextCompat.getColor(CaloriePredicitingActivity.this,R.color.background_color));
        FoodImage=findViewById(R.id.food_image);
        Bundle bundle=getIntent().getExtras();
        target=new Storetarget(CaloriePredicitingActivity.this);
        if(bundle!=null && bundle.containsKey("image"))
            FoodImage.setImageURI(Uri.parse(bundle.getString("image")));
        trackCalorie=findViewById(R.id.trackCalorie);
        progressBar=findViewById(R.id.progressBar);
        Food_Name=findViewById(R.id.Food_Name);
        Calories=findViewById(R.id.Calories);
        QuantityTextView=findViewById(R.id.quantityTextView);
        Quantity=findViewById(R.id.qty);
        addCalorie=findViewById(R.id.add_calorie);
        final FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
        userid=user.getUid();

        firebaseDatabase=FirebaseDatabase.getInstance();
        databaseReference=firebaseDatabase.getReference();


        addCalorie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uniqueID = UUID.randomUUID().toString();

                Map map = new HashMap();
                map.put("timestamp", ServerValue.TIMESTAMP);
                map.put("name",Food_Name.getText().toString());
                map.put("cal",Calories.getText().toString());
                DatabaseReference mchild=databaseReference.child("food").child(user.getUid());
                mchild.child(uniqueID).setValue(map, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable  DatabaseError databaseError, @NonNull  DatabaseReference databaseReference) {
                        Toast.makeText(CaloriePredicitingActivity.this,"Added Successfully",Toast.LENGTH_LONG).show();
                        startActivity(new Intent(CaloriePredicitingActivity.this,MainActivity.class));
                    }
                });
            }
        });




        if (!Python.isStarted())
            Python.start(new AndroidPlatform(this));
        final Python py = Python.getInstance();


        trackCalorie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progressBar.setVisibility(View.VISIBLE);
                PyObject pyObject=py.getModule("calorie");

                Bitmap bm=((BitmapDrawable)FoodImage.getDrawable()).getBitmap();

                ByteArrayOutputStream out = new ByteArrayOutputStream();
                bm.compress(Bitmap.CompressFormat.JPEG, 100, out); // '100' is quality
                byte[] byteArray = out.toByteArray();
                String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);
                PyObject obj=pyObject.callAttr("funt",encoded);
                progressBar.setVisibility(View.GONE);

                if(obj!=null){
                    trackCalorie.setVisibility(View.GONE);
                    QuantityTextView.setVisibility(View.GONE);
                    Quantity.setVisibility(View.GONE);
                    String name = obj.toString();
                    PyObject obj2 = pyObject.callAttr("readCal", name);
                    String calorie = obj2.toString();
                    int len = calorie.length();
                    calorie = calorie.substring(1, len - 1);
                    int calorieValue = Integer.parseInt(calorie);
                    int qty=Integer.parseInt(Quantity.getText().toString());
                    calorieValue=calorieValue*qty;
                    Calories.setText(calorieValue+" Calorie");
                    Food_Name.setText(name);
                    Calories.setVisibility(View.VISIBLE);
                    Food_Name.setVisibility(View.VISIBLE);
                    Toast.makeText(CaloriePredicitingActivity.this, "calorie " + calorie, Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(CaloriePredicitingActivity.this, "Couldn't find the object" , Toast.LENGTH_LONG).show();
                }

            }
        });
    }

    private String getDate() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy.mm.dd");
        Date date = new Date();
        return dateFormat.format(date);
    }
    private String getTime() {
        DateFormat dateFormat = new SimpleDateFormat("hh:mm:ss a");
        Date date = new Date();
        return dateFormat.format(date);
    }
}