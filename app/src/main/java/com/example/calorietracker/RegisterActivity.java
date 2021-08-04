package com.example.calorietracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hbb20.CountryCodePicker;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    EditText usernameEditText, passwordEditText, emailEditText, phoneNoEditText;
    Button signUpButton;
    FirebaseAuth auth;
    DatabaseReference databaseReference;
    CountryCodePicker ccp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resgister);
        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Register");
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        usernameEditText = findViewById(R.id.usernameedittext);
        emailEditText = findViewById(R.id.emaileditetxt);
        passwordEditText = findViewById(R.id.passwordedittext);
        phoneNoEditText = findViewById(R.id.phoneNoeditText);
        signUpButton = findViewById(R.id.signupbutton);
        databaseReference = FirebaseDatabase.getInstance().getReference("User");
        auth = FirebaseAuth.getInstance();

        ccp=(CountryCodePicker) findViewById(R.id.ccp);

        signUpButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {


                final String email = emailEditText.getText().toString();
                final String password = passwordEditText.getText().toString();
                final String username = usernameEditText.getText().toString();
                String ans=ccp.getSelectedCountryCode();

                final String phoneno;
                if(ccp.getSelectedCountryCode().equals(""))
                    phoneno="+91"+phoneNoEditText.getText().toString();
                else
                    phoneno="+"+ccp.getSelectedCountryCode()+phoneNoEditText.getText().toString();

                final String imageURL="default";
                Log.i("name",username);
                Log.i("phoneno",phoneno);
                Log.i("email",email);

                if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(username)
                        || TextUtils.isEmpty(phoneno)) {
                    Toast.makeText(RegisterActivity.this, "Please enter all the Details", Toast.LENGTH_LONG).show();
                }
                else if(password.length()<6)
                {
                    Toast.makeText(RegisterActivity.this,"Password length must be of at least 6 characters",Toast.LENGTH_LONG).show();
                }
                else {


                    auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {

                                FirebaseUser firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
                                String id=firebaseUser.getUid();
                                HashMap<String,String> info=new HashMap<>();
                                info.put("username",username);
                                info.put("phoneno",phoneno);
                                info.put("email",email);
                                info.put("imageurl",imageURL);
                                info.put("id",id);
                                info.put("search",username.toLowerCase());
                                FirebaseDatabase.getInstance().getReference("User")
                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .setValue(info).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        Toast.makeText(RegisterActivity.this, "Registration Complete Succesfully", Toast.LENGTH_LONG).show();
                                        Intent intent =new Intent(getApplicationContext(),MainActivity.class);
                                        startActivity(intent);
                                    }
                                });


                            }
                            else{
                                Toast.makeText(RegisterActivity.this, "Registration Failed", Toast.LENGTH_LONG).show();
                            }

                        }
                    });
                }
            }
        });
    }
}