package com.example.calorietracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPasswordActivity extends AppCompatActivity {

    private EditText send_email;
    private Button reset_btn;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Reset Password");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        send_email=findViewById(R.id.send_email);
        reset_btn=findViewById(R.id.reset_btn);

        auth=FirebaseAuth.getInstance();
        reset_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String email=send_email.getText().toString();
                if(email.equals(""))
                    Toast.makeText(getApplicationContext(),"Email cannot be empty",Toast.LENGTH_SHORT).show();
                else{
                    auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if(task.isSuccessful()){
                                Toast.makeText(ResetPasswordActivity.this,"An email has been sent to your registered email id",Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(ResetPasswordActivity.this,MainActivity.class));
                            }
                            else{
                                String error=task.getException().toString();
                                Toast.makeText(ResetPasswordActivity.this,error,Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }

            }
        });

    }
}